package es.bukkitclassmapper._shared.utils.reflections;

public interface BukkitClassMapperInstanceProvider {
    <I, O extends I> O get(Class<I> baseClass);

    boolean isExcluded(Class<?> clazz);

    static BukkitClassMapperInstanceProvider defaultProvider() {
        return DefaultInstanceProvider.INSTANCE;
    }
}
