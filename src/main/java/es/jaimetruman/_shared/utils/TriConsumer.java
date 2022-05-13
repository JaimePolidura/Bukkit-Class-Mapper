package es.jaimetruman._shared.utils;

@FunctionalInterface
public interface TriConsumer<X, Y, Z> {
    void consume(X x, Y y, Z z);
}
