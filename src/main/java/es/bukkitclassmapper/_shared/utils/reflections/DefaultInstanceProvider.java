package es.bukkitclassmapper._shared.utils.reflections;

import lombok.SneakyThrows;

public final class DefaultInstanceProvider implements BukkitClassMapperInstanceProvider {
    public static final DefaultInstanceProvider INSTANCE = new DefaultInstanceProvider();

    @Override
    @SneakyThrows
    public <I, O extends I> O get(Class<I> baseClass) {
        return (O) baseClass.newInstance();
    }

    @Override
    public boolean isExcluded(Class<?> clazz) {
        return false;
    }
}
