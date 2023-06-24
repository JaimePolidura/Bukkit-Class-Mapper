package es.bukkitclassmapper._shared.utils;

import java.util.concurrent.Callable;

public final class ExceptionUtils {
    public static <T> T runAndGetOrTerminate(Callable<T> callable) {
        try {
            return callable.call();
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
}
