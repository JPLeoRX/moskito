package net.anotheria.moskito.core.errorhandling;

import net.anotheria.moskito.core.config.errorhandling.ErrorCatcherConfig;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.List;

/**
 * New type of error catcher
 *
 * @author Leo Ertuna
 * @since 21.05.2018 17:52
 */
public class SolrErrorCatcher implements ErrorCatcher {
    @Override
    public void add(Throwable throwable) {
        SolrClient solrClient = SolrClientAdapter.getSolarClient();

        // Convert from throwable
        CaughtError caughtError = new CaughtError(throwable);

        // Convert from caught error
        SolrCaughtError solrCaughtError = new SolrCaughtError(caughtError);

        try {
            // Add to solr
            solrClient.addBean(SolrCaughtError.class.getName(), solrCaughtError);

            // Push changes
            solrClient.commit(SolrCaughtError.class.getName());
        }

        catch (SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CaughtError> getErrorList() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getNumberOfCaughtErrors() {
        return 0;
    }

    @Override
    public ErrorCatcherConfig getConfig() {
        return null;
    }
}