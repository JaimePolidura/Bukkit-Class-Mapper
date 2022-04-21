package es.jaimetruman.commands.exceptions;

public final class InvalidSenderType extends RuntimeException{
    public InvalidSenderType(String message) {
        super(message);
    }
}
