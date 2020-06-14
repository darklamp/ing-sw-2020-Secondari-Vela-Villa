package it.polimi.ingsw.Model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation serves the purpose of making it easy for the model to load available gods without hard-coding them.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface God {
    /**
     * @return name of the god
     */
    String name();
}