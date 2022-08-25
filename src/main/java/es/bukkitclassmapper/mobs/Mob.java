package es.bukkitclassmapper.mobs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotaton to declare commands. To implement a command you have to
 * annotate your class with this and implement CommandRunner interface
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mob {
    /**
     * X, Y, Z cords of the mob
     */
    int x();
    int y();
    int z();
}
