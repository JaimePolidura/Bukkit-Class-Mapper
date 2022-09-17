package es.bukkitclassmapper;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public abstract class ClassMapper {
    protected final Reflections reflections;
    protected final ClassMapperConfiguration configuration;

    public ClassMapper(ClassMapperConfiguration configuration) {
        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(configuration.getCommonPackage()))
                .setScanners(new TypeAnnotationsScanner(),
                             new SubTypesScanner()));
        this.configuration = configuration;
    }

    public abstract void scan ();
}
