package net.anotheria.moskito.core.errorhandling;

import net.anotheria.moskito.core.config.errorhandling.ErrorCatcherConfig;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;
import java.util.List;

/**
 * New type of error catcher
 *
 * For now we will extend the builtin in-memory error catcher
 *
 * @author Leo Ertuna
 * @since 21.05.2018 17:52
 */
public class SolrErrorCatcher extends BuiltinErrorCatcher implements ErrorCatcher {
    private SolrClient solrClient = SolrClientAdapter.getSolarClient();
    private SolrCaughtErrorConverter converter = new SolrCaughtErrorConverter();

    /**
     * Constructor to provide {@link BuiltinErrorCatcher} superclass with config
     * @param aConfig
     */
    public SolrErrorCatcher(ErrorCatcherConfig aConfig) {
        super(aConfig);
    }

    /**
     * Since we build this on top of {@link BuiltinErrorCatcher} it will call the super method
     * Then we will store the caught error in solr
     * @param throwable throwable
     */
    @Override
    public void add(Throwable throwable) {
        // Don't forget to add it using the builtin error catcher
        super.add(throwable);

        // Convert from throwable to caught error
        CaughtError caughtError = new CaughtError(throwable);

        // Convert from caught error to solr error
        SolrCaughtError solrCaughtError = converter.toSolr(caughtError);

        try {
            // Add to solr
            solrClient.addBean("SolrCaughtError", solrCaughtError);

            // Push changes
            solrClient.commit("SolrCaughtError");
        }

        catch (SolrServerException | IOException e) {
            // If something went wrong - just crash here
            System.out.println(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * We will query solr and retrieve the full error list
     * @return caught errors list
     */
    @Override
    public List<CaughtError> getErrorList() {
        try {
            // Create new query, select all elements from a collection
            SolrQuery query = new SolrQuery("*:*");
            query.setRows(Integer.MAX_VALUE);

            // Get response
            QueryResponse response = solrClient.query("SolrCaughtError", query);

            // Extract beans
            List<SolrCaughtError> beans = response.getBeans(SolrCaughtError.class);

            // Convert back to caught errors
            return converter.fromSolr(beans);
        }

        catch (SolrServerException | IOException e) {
            // If something went wrong - just crash here
            System.out.println(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}