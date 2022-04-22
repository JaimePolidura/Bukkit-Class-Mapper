package es.jaimetruman.commands.exceptions;

public final class InvalidUsage extends RuntimeException{
    public InvalidUsage(String message) {
        super(message);
    }
}
