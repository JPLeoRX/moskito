package net.anotheria.moskito.core.errorhandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A converter that will handle transition between {@link CaughtError} and {@link SolrCaughtError}
 * It will also serialize and deserialize {@link Throwable} objects via GSON
 *
 * @author Leo Ertuna
 * @since 22.05.2018 12:07
 */
public class SolrCaughtErrorConverter {
    private final Gson gson;

    /**
     * Default constructor
     */
    public SolrCaughtErrorConverter() {
        gson = new GsonBuilder().create();
    }

    /**
     * Convert single caught error to solr error
     * @param caughtError caught error
     * @return solr error
     */
    public SolrCaughtError toSolr(CaughtError caughtError) {
        // Create new object
        SolrCaughtError solr = new SolrCaughtError();

        // Set all the fields
        solr.setTimestamp(caughtError.getTimestamp());
        solr.setThrowableClassName(caughtError.getThrowable().getClass().getName());
        solr.setThrowableMessage(caughtError.getThrowable().getMessage());
        solr.setThrowableJson(gson.toJson(caughtError.getThrowable()));
        solr.setTags(caughtError.getTags());

        // Return resulting object
        return solr;
    }

    /**
     * Convert single solr error to caught error
     * @param solrCaughtError solr error
     * @return caught error
     */
    public CaughtError fromSolr(SolrCaughtError solrCaughtError) {
        // Restore all the fields
        long timestamp = solrCaughtError.getTimestamp();
        Throwable throwable = gson.fromJson(solrCaughtError.getThrowableJson(), Throwable.class);
        Map<String, String> tags = solrCaughtError.getTags();

        // Return new object with those fields
        return new CaughtError(timestamp, throwable, tags);
    }

    /**
     * Convert multiple caught errors to solr errors
     * @param caughtErrors caught errors
     * @return solr errors
     */
    public List<SolrCaughtError> toSolr(Collection<CaughtError> caughtErrors) {
        // Convert via parallel stream mapping
        return caughtErrors.parallelStream().map(this::toSolr).collect(Collectors.toList());
    }

    /**
     * Convert multiple solr errors to caught errors
     * @param solrCaughtErrors solr errors
     * @return caught errors
     */
    public List<CaughtError> fromSolr(Collection<SolrCaughtError> solrCaughtErrors) {
        // Convert via parallel stream mapping
        return solrCaughtErrors.parallelStream().map(this::fromSolr).collect(Collectors.toList());
    }
}