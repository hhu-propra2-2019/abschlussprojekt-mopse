package mops.utils;

import java.lang.annotation.*;

/**
 * Interface for marking a class with Aggregate Root for testing.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AggregateRoot {

}
