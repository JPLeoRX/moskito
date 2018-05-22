package net.anotheria.moskito.core.errorhandling;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

/**
 * Helper adapter that will provide {@link SolrErrorCatcher} with the {@link SolrClient} object
 *
 * @author Leo Ertuna
 * @since 21.05.2018 17:52
 */
public class SolrClientAdapter {
    // Builder for solr client with default localhost url
    private static HttpSolrClient.Builder builder = new HttpSolrClient.Builder("http://localhost:8983/solr");

    // Solr client
    private static SolrClient solrClient = builder.build();

    /**
     * Pointer to solr client
     * @return solr client
     */
    public static SolrClient getSolarClient() {
        return solrClient;
    }
}