package es.bukkitclassmapper._shared.utils;

import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public final class InstanceCreator {
    public static <T> T create(Class<T> baseClass, InstanceProvider instanceProvider){
        Constructor<?>[] constructors = baseClass.getConstructors();
        boolean hasNoConstructor = constructors.length == 0;
        boolean alreadyProvived = instanceProvider.get(baseClass) != null;

        if(alreadyProvived)
            return instanceProvider.get(baseClass);

        return hasNoConstructor ? instanciateNoConstructor(baseClass) : instanciateConstructor(baseClass, instanceProvider);
    }

    @SneakyThrows
    private static <T> T instanciateNoConstructor(Class<T> baseClass) {
        return baseClass.newInstance();
    }

    @SneakyThrows
    private static <T> T instanciateConstructor(Class<T> baseClass, InstanceProvider instanceProvider) {
        Constructor<?> constructor = baseClass.getConstructors()[0];
        Object[] paramsValues = Arrays.stream(constructor.getParameterTypes())
                .map(instanceProvider::get)
                .toArray(Object[]::new);

        return (T) constructor.newInstance(paramsValues);
    }
}
