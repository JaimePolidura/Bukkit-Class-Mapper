package es.bukkitclassmapper._shared.utils.reflections;

@FunctionalInterface
public interface InstanceProvider {
    <I, O extends I> O get(Class<I> baseClass);

    static InstanceProvider defaultProvider() {
        return DefaultInstanceProvider.INSTANCE;
    }
}
