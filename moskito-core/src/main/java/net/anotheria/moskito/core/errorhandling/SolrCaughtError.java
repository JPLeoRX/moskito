package net.anotheria.moskito.core.errorhandling;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;
import java.util.Map;

/**
 * This class is a representation of {@link CaughtError} object specifically tailored to be used with Solr
 *
 * @author Leo Ertuna
 * @since 21.03.2018 17:30
 */
public class SolrCaughtError implements Serializable {
    @Field
    private long timestamp;

    @Field
    private String throwableClassName;

    @Field
    private String throwableMessage;

    @Field
    private String throwableStackTrace;

    @Field
    private String throwableFullStackTrace;

    @Field("key*")
    private Map<String, String> tags;

    /**
     * This object must be constructed only from an existing error
     * @param caughtError error
     */
    public SolrCaughtError(CaughtError caughtError) {
        this.timestamp = caughtError.getTimestamp();
        this.throwableClassName = caughtError.getThrowable().getClass().getName();
        this.throwableMessage = caughtError.getThrowable().getMessage();
        this.throwableStackTrace = ExceptionUtils.getStackTrace(caughtError.getThrowable());
        this.throwableFullStackTrace = ExceptionUtils.getFullStackTrace(caughtError.getThrowable());
        this.tags = caughtError.getTags();
    }

    /**
     * Getter
     * @return time when error occurred
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Getter
     * @return the name of the throwable class
     */
    public String getThrowableClassName() {
        return throwableClassName;
    }

    /**
     * Getter
     * @return message from the throwable
     */
    public String getThrowableMessage() {
        return throwableMessage;
    }

    /**
     * Getter
     * @return stack trace of the throwable
     */
    public String getThrowableStackTrace() {
        return throwableStackTrace;
    }

    /**
     * Getter
     * @return nested stack trace from the throwable
     */
    public String getThrowableFullStackTrace() {
        return throwableFullStackTrace;
    }

    /**
     * Getter
     * @return tags
     */
    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "SolrCaughtError{" +
                "timestamp=" + timestamp +
                ", throwableClassName='" + throwableClassName + '\'' +
                ", throwableMessage='" + throwableMessage + '\'' +
                ", throwableStackTrace='" + throwableStackTrace + '\'' +
                ", throwableFullStackTrace='" + throwableFullStackTrace + '\'' +
                ", tags=" + tags +
                '}';
    }
}