package es.jaimetruman.commands.exceptions;

public final class InvalidPermissions extends RuntimeException{
    public InvalidPermissions(String message) {
        super(message);
    }
}
