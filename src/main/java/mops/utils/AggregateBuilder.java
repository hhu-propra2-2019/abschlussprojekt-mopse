package mops.utils;

import java.lang.annotation.*;

/**
 * Interface for marking a class as a Aggregate Builder so that
 * tests don't fail because of builders.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AggregateBuilder {

}
