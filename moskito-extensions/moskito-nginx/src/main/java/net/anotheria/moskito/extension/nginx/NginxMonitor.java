package net.anotheria.moskito.extension.nginx;

import net.anotheria.moskito.core.producers.IStatsProducer;
import net.anotheria.moskito.core.registry.ProducerRegistryFactory;
import net.anotheria.moskito.extension.nginx.config.NginxMonitorConfig;
import net.anotheria.moskito.extension.nginx.config.NginxMonitoredInstance;
import net.anotheria.util.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Nginx monitor that handles monitoring of several nginx instances, as
 * configured in 'nginx-monitor.json' file.
 *
 * @author dzhmud
 */
public final class NginxMonitor {

    /** Use shared plugin logger. */
    private static final Logger LOGGER = NginxMonitoringPlugin.LOGGER;

    /** List of all created NginxMonitor objects. Used by reconfiguration method.*/
    private static final List<NginxMonitor> MONITORS = new ArrayList<>();

    /** Executor service that handles calling of update routine.*/
    private ScheduledExecutorService executorService;

    /** NginxMonitorConfig instance. */
    private final NginxMonitorConfig config;

    /** List of created producers. */
    private final List<NginxStatsProducer> producers = new CopyOnWriteArrayList<>();

    /** TimerTask that updates producers upon running.*/
    private TimerTask updateTask;


    static NginxMonitor initialize(NginxMonitorConfig aConfig) {
        NginxMonitor nginxMonitor = new NginxMonitor(aConfig);
        MONITORS.add(nginxMonitor);
        nginxMonitor.setup();
        return nginxMonitor;
    }


    private NginxMonitor(NginxMonitorConfig aConfig) {
        this.config = aConfig;
    }

    private void setup() {
        NginxMonitoredInstance[] instances = config.getMonitoredInstances();
        if (instances == null || instances.length == 0) {
            LOGGER.info("nginx monitor has empty array of monitored instances!");
        } else {
            final Set<String> monitorNames = new HashSet<>();
            for (NginxMonitoredInstance instance : instances) {
                //check for duplicate names, we should not register several producers with same producerId.
                if (!monitorNames.add(instance.getName())) {
                    LOGGER.warn("Found duplicate name in configuration: '" + instance.getName() + "', skipping.");
                    continue;
                }
                //create and register NginxStatsProducer.
                NginxStatsProducer producer = new NginxStatsProducer(instance);
                producers.add(producer);
                ProducerRegistryFactory.getProducerRegistryInstance().registerProducer(producer);
            }
            updateTask = new TimerTask() {
                @Override public void run() {
                    for (NginxStatsProducer producer : producers) {
                        producer.update();
                    }
                }
            };
            if (executorService == null) {
                executorService = Executors.newSingleThreadScheduledExecutor();
            }
            executorService.scheduleAtFixedRate(updateTask, 0, config.getUpdatePeriod(), TimeUnit.SECONDS);
        }
    }

    /**
     * Cancel updater task and unregister producers.
     * However, call of {@link #setup()} method will recreate everything and
     * turn this NginxMonitor back to running state.
     */
    private void stop() {
        //unregister and free all resources;
        if (updateTask != null)
            updateTask.cancel();
        for (NginxStatsProducer producer : producers) {
            producer.nginxStats.destroy();
            ProducerRegistryFactory.getProducerRegistryInstance().unregisterProducer(producer);
        }
        producers.clear();
    }

    /**
     * Completely deinitialize this NginxMonitor.
     * It won't be usable anymore
     */
    void deInitialize() {
        stop();
        if (executorService != null)
            executorService.shutdown();
        MONITORS.remove(this);
    }

    /** Called when NginxMonitorConfig source changed at runtime. */
    public static void reconfigureNginxMonitor(NginxMonitorConfig config) {
        for (NginxMonitor monitor : MONITORS) {
            if (monitor.config == config) {
                monitor.stop();
                monitor.setup();
            }
        }
    }

    private static class NginxStatsProducer implements IStatsProducer<NginxStats> {

        /** Configuration of the monitored nginx instance. */
        private final NginxMonitoredInstance nginx;

        /** NginxStats object that collects metrics. */
        private final NginxStats nginxStats;

        /** Singleton list with NginxStats object. */
        private final List<NginxStats> stats;

        /** This producer ID.*/
        private final String producerId;

        NginxStatsProducer(NginxMonitoredInstance instance) {
            if (instance == null)
                throw new IllegalArgumentException("NginxMonitoredInstance argument is null");
            this.nginx = instance;
            this.producerId = "NginxMonitor-" + instance.getName();
            this.nginxStats = new NginxStats(instance.getName());
            this.stats = Collections.singletonList(nginxStats);
        }

        @Override
        public List<NginxStats> getStats() {
            return stats;
        }

        @Override
        public String getProducerId() {
            return producerId;
        }

        @Override
        public String getCategory() {
            return "Monitor";
        }

        @Override
        public String getSubsystem() {
            return "proxy";
        }

        private void update() {
            NginxStatus nginxStatus = fetchNginxStatus();
            if (nginxStatus != null)
                nginxStats.update(nginxStatus);
        }

        private NginxStatus fetchNginxStatus() {
            NginxStatus nginxStatus = null;
            final UsernamePasswordCredentials credentials = StringUtils.isEmpty(nginx.getUsername()) ? null :
                    new UsernamePasswordCredentials(nginx.getUsername(), nginx.getPassword());
            try {
                final HttpResponse response = HttpHelper.getHttpResponse(nginx.getLocation(), credentials);
                final String content = HttpHelper.getResponseContent(response);
                if (HttpHelper.isScOk(response)) {
                    nginxStatus = NginxStubStatusParser.parseStatus(content);
                } else {
                    LOGGER.warn("Response status code is not 200! Nginx location = "
                            + nginx.getLocation() + ", response=" + content);
                }
            } catch (IOException e) {
                LOGGER.warn("Failed to fetch content of URL ["+nginx.getLocation()+"].", e);
            } catch (NginxStubStatusParser.NginxStatusParserException e) {
                LOGGER.warn("Failed to parse content of URL ["+nginx.getLocation()+"] -> " + e.getMessage(), e);
            }
            return nginxStatus;
        }

    }

}
