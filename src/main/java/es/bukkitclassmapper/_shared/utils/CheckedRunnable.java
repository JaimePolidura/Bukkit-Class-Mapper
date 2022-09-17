package es.bukkitclassmapper._shared.utils;

@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
