package net.anotheria.moskito.core.errorhandling;

import net.anotheria.moskito.core.config.errorhandling.ErrorCatcherConfig;
import net.anotheria.moskito.core.config.errorhandling.ErrorCatcherTarget;
import net.anotheria.moskito.core.context.MoSKitoContext;
import net.anotheria.moskito.core.tag.TagType;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * A simple test to demonstrate how new {@link SolrErrorCatcher} works
 *
 * Before running this make sure that you started solr on port 8983 in cloud mode
 *          solr start -c
 *
 * Also make sure that this collection exists in solr, if it doesn't - create it
 *          GET localhost:8983/solr/admin/collections?action=CREATE&name=SolrCaughtError&numShards=1
 *
 * @author Leo Ertuna
 * @since 22.05.2018 16:20
 */
@Ignore
public class SolrErrorCatcherTest {
    private static ErrorCatcher errorCatcher;

    @BeforeClass
    public static void init() {
        // Set some random tags for testing purposes
        MoSKitoContext.addTag("someTag1", "someValue1", TagType.BUILTIN, "someSource1");
        MoSKitoContext.addTag("someTag2", "someValue2", TagType.BUILTIN, "someSource2");

        // Setup config
        ErrorCatcherConfig errorCatcherConfig = new ErrorCatcherConfig();
        errorCatcherConfig.setTarget(ErrorCatcherTarget.LOGANDMEMORY);
        errorCatcherConfig.setExceptionClazz("java.lang.IllegalArgumentException");
        errorCatcherConfig.setParameter("IllegalArgExcLog");

        // Create error catcher
        errorCatcher = new SolrErrorCatcher(errorCatcherConfig);
    }

    @Test
    public void add() {
        // Save last known size
        int previousSize = errorCatcher.getErrorList().size();

        // Add new error
        errorCatcher.add(new IllegalArgumentException("This is a test exception"));

        // Save current size
        int currentSize = errorCatcher.getErrorList().size();

        // Make sure that one exception was added
        assertEquals(previousSize + 1, currentSize);
    }

    @Test
    public void getErrorList() {
        // Get a list of stored errors
        List<CaughtError> errorList = errorCatcher.getErrorList();

        // Print out eat of them
        for (CaughtError error : errorList)
            System.out.println(error);
    }
}