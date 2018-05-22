package net.anotheria.moskito.core.errorhandling;

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is a representation of {@link CaughtError} object specifically tailored to be used with Solr.
 *
 * Solr always returns a multipart field in the form of an array list, so it requires an additional setter that will extract the first element of this list.
 *
 * We keep timestamp, but we add throwable class name and message as well as JSON serialized version of throwable.
 * Since Solr has problems with storing tags - map of tags is also serialized to JSON.
 *
 * We can create this object from {@link CaughtError} and convert back to caught error from this object.
 * For more details about conversion take a look at {@link SolrCaughtErrorConverter}
 *
 * @author Leo Ertuna
 * @since 21.03.2018 17:30
 */
public class SolrCaughtError implements Serializable {
    private long timestamp;
    private String throwableClassName;
    private String throwableMessage;
    private String throwableJson;
    private String tagsJson;

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
     * Setter used by solr
     * @param timestamp solr list
     */
    @Field
    public void setTimestamp(ArrayList<Long> timestamp) {
        this.timestamp = timestamp.get(0);
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
     * Setter used by solr
     * @param throwableClassName solr list
     */
    @Field
    public void setThrowableClassName(ArrayList<String> throwableClassName) {
        this.throwableClassName = throwableClassName.get(0);
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
     * Setter used by solr
     * @param throwableMessage solr list
     */
    @Field
    public void setThrowableMessage(ArrayList<String> throwableMessage) {
        this.throwableMessage = throwableMessage.get(0);
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
     * Setter used by solr
     * @param throwableJson solr list
     */
    @Field
    public void setThrowableJson(ArrayList<String> throwableJson) {
        this.throwableJson = throwableJson.get(0);
    }

    /**
     * Getter
     * @return tags
     */
    public String getTagsJson() {
        return tagsJson;
    }

    /**
     * Setter
     * @param tagsJson new tags
     */
    public void setTagsJson(String tagsJson) {
        this.tagsJson = tagsJson;
    }

    /**
     * Setter used by solr
     * @param tags solr list
     */
    @Field
    public void setTagsJson(ArrayList<String> tags) {
        this.tagsJson = tags.get(0);
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
                ", tagsJson=" + tagsJson +
                '}';
    }
}