package es.bukkitclassmapper._shared.utils;

@FunctionalInterface
public interface InstanceProvider {
    <I, O extends I> O get(Class<I> baseClass);

    static InstanceProvider defaultEmpty() {
        return new InstanceProvider() {
            @Override
            public <I, O extends I> O get(Class<I> baseClass) {
                return null;
            }
        };
    }
}
