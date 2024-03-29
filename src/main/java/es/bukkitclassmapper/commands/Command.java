package es.bukkitclassmapper.commands;


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
public @interface Command {
    /**
     * Name of the command. If you want to declare a subcommand,
     * you would do this for example: "profile account"
     */
    String value();

    /**
     * Permissions that the player needs to have to perform the command
     */
    String permissions() default "";

    /**
     * If the command use io type operations it will execute in another thread
     */
    boolean isAsync() default false;

    /**
     * All args names that will be mapped to the classname with the same. Example
     */
    String[] args() default "";

    /**
     * Returns an explanation for the command
     */
    String explanation() default "";

    /**
     * If set to true it will display to the player all the plugins command
     * with its explanation.
     * Helper commands to subcommands is autogenerated
     */
    boolean isHelper() default false;

    boolean needsOp() default false;
}
