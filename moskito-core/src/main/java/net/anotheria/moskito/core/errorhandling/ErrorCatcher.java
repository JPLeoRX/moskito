package net.anotheria.moskito.core.errorhandling;

import java.util.List;

/**
 * This class is used by the BuiltInErrorProducer to held caught errors.
 *
 * @author lrosenberg
 * @since 04.06.17 20:49
 */
public interface ErrorCatcher {

	void add(Throwable throwable) ;

	List<CaughtError> getErrorList();

	String getName();

	/**
	 * Returns the number of caught errors for the console.
	 * @return
	 */
	int getNumberOfCaughtErrors();

}
