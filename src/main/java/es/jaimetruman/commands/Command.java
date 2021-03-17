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
    String name();

    /**
     * If you want your command to be able to run in the console you would
     * set this to true
     */
    boolean canBeTypedInConsole() default false;
}
