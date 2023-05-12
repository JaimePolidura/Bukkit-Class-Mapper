package es.bukkitclassmapper._shared.utils.reflections;

@FunctionalInterface
public interface BukkitClassMapperInstanceProvider {
    <I, O extends I> O get(Class<I> baseClass);

    static BukkitClassMapperInstanceProvider defaultProvider() {
        return DefaultInstanceProvider.INSTANCE;
    }
}
