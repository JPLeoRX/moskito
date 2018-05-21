package net.anotheria.moskito.core.errorhandling;

import org.apache.solr.client.solrj.SolrClient;

/**
 * Helper adapter that will provide {@link SolrErrorCatcher} with the {@link SolrClient} object
 *
 * @author Leo Ertuna
 * @since 21.05.2018 17:52
 */
public class SolrClientAdapter {
    public static SolrClient getSolarClient() {
        return null;
    }
}