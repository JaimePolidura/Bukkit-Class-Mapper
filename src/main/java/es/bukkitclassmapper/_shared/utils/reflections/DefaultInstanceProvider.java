package es.bukkitclassmapper._shared.utils.reflections;

import lombok.SneakyThrows;

public final class DefaultInstanceProvider implements BukkitClassMapperInstanceProvider {
    public static final DefaultInstanceProvider INSTANCE = new DefaultInstanceProvider();

    @Override
    @SneakyThrows
    public <I, O extends I> O get(Class<I> baseClass) {
        return (O) baseClass.newInstance();
    }

    @SneakyThrows
    private static <T> T instanciateNoConstructor(Class<T> baseClass) {
        return baseClass.newInstance();
    }
}
