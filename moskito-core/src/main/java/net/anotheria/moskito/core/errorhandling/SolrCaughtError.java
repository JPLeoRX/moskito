package net.anotheria.moskito.core.errorhandling;

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
    private String throwableJson;

    @Field("key*")
    private Map<String, String> tags;

    /**
     * Default constructor
     */
    public SolrCaughtError() {

    }

    /**
     * Getter
     * @return time when error occurred
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Setter
     * @param timestamp new timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Getter
     * @return the name of the throwable class
     */
    public String getThrowableClassName() {
        return throwableClassName;
    }

    /**
     * Setter
     * @param throwableClassName new name of the throwable class
     */
    public void setThrowableClassName(String throwableClassName) {
        this.throwableClassName = throwableClassName;
    }

    /**
     * Getter
     * @return message from the throwable
     */
    public String getThrowableMessage() {
        return throwableMessage;
    }

    /**
     * Setter
     * @param throwableMessage new message from the throwable
     */
    public void setThrowableMessage(String throwableMessage) {
        this.throwableMessage = throwableMessage;
    }

    /**
     * Getter
     * @return json string of the throwable
     */
    public String getThrowableJson() {
        return throwableJson;
    }

    /**
     * Setter
     * @param throwableJson new json string of the throwable
     */
    public void setThrowableJson(String throwableJson) {
        this.throwableJson = throwableJson;
    }

    /**
     * Getter
     * @return tags
     */
    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * Setter
     * @param tags new tags
     */
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    /**
     * To string
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "SolrCaughtError{" +
                "timestamp=" + timestamp +
                ", throwableClassName='" + throwableClassName + '\'' +
                ", throwableMessage='" + throwableMessage + '\'' +
                ", throwableJson='" + throwableJson + '\'' +
                ", tags=" + tags +
                '}';
    }
}