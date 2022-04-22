package es.jaimetruman.commands;


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
     * If you want your command to be able to run in the console you would
     * set this to true
     */
    boolean canBeTypedInConsole() default false;

    /**
     * Permissions that the player needs to have to perform the command
     */
    String permissions() default "";

    /**
     * If you want your command to be executed in other thread, set this to true
     */
    boolean isAsync() default false;

    /**
     * All args names that will be mapped to the classname with the same. Example
     */
    String[] args() default "";

    /**
     * Returns the help method for that command
     */
    String helperCommand() default "";

    /**
     * Returns an explanation for the command
     */
    String explanation() default "";
}
