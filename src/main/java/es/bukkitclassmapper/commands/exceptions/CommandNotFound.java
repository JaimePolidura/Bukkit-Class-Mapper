package es.bukkitclassmapper.commands.exceptions;

public final class CommandNotFound extends RuntimeException{
    public CommandNotFound(String message) {
        super(message);
    }
}
