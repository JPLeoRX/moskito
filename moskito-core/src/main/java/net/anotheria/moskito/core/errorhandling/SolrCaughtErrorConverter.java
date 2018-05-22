package net.anotheria.moskito.core.errorhandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
        solr.setTagsJson(gson.toJson(caughtError.getTags()));

        // Return resulting object
        return solr;
    }

    /**
     * Convert single solr error to caught error
     * @param solrCaughtError solr error
     * @return caught error
     */
    @SuppressWarnings("unchecked")
    public CaughtError fromSolr(SolrCaughtError solrCaughtError) {
        // Restore all the fields
        long timestamp = solrCaughtError.getTimestamp();
        Throwable throwable = gson.fromJson(solrCaughtError.getThrowableJson(), Throwable.class);
        Map<String, String> tags = (Map<String, String>) gson.fromJson(solrCaughtError.getTagsJson(), Map.class);

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
        //return caughtErrors.parallelStream().map(this::toSolr).collect(Collectors.toList());

        // This piece will probably never be needed
        throw new IllegalAccessError("This method shouldn't be called");
    }

    /**
     * Convert multiple solr errors to caught errors
     * @param solrCaughtErrors solr errors
     * @return caught errors
     */
    public List<CaughtError> fromSolr(Collection<SolrCaughtError> solrCaughtErrors) {
        // Convert via parallel stream mapping
        //return solrCaughtErrors.parallelStream().map(this::fromSolr).collect(Collectors.toList());

        // We're having troubles with running streams from moskito, it looks like moskito still uses java 1.7, so we have to do this sad list iteration
        List<CaughtError> caughtErrors = new ArrayList<>(solrCaughtErrors.size());
        for (SolrCaughtError solrCaughtError : solrCaughtErrors)
            caughtErrors.add(this.fromSolr(solrCaughtError));
        return caughtErrors;
    }
}