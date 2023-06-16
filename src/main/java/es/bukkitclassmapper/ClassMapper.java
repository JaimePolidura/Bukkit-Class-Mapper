package es.bukkitclassmapper;

import es.bukkitclassmapper._shared.utils.ClassMapperLogger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public abstract class ClassMapper {
    protected final ClassMapperConfiguration configuration;
    protected final ClassMapperLogger logger;
    protected final Reflections reflections;

    public ClassMapper(ClassMapperConfiguration configuration, ClassMapperLogger logger) {
        this.configuration = configuration;
        this.reflections = configuration.getReflections() == null ? new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(configuration.getCommonPackage()))
                .setScanners(new TypeAnnotationsScanner(),
                             new SubTypesScanner())) : configuration.getReflections();
        this.logger = logger;
    }

    public abstract void scan();
}
