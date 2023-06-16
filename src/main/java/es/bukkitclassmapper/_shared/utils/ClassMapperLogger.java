package es.bukkitclassmapper._shared.utils;

import es.bukkitclassmapper.ClassMapperConfiguration;
import lombok.AllArgsConstructor;

import java.util.logging.Level;

@AllArgsConstructor
public final class ClassMapperLogger {
    private final ClassMapperConfiguration configuration;

    public void debug(String message, Object... args) {
        if(configuration.isUseDebugLogging()){
            configuration.getNativeLogger().log(Level.INFO, String.format(message, args));
        }
    }

    public void info(String message, Object... args) {
        configuration.getNativeLogger().log(Level.INFO, String.format(message, args));
    }

    public void error(String message, Object... args) {
        configuration.getNativeLogger().log(Level.SEVERE, String.format(message, args));
    }
}
