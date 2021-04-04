package es.jaimetruman;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class Mapper {
    protected final Reflections reflections;

    public Mapper(String packageToStartScanning) {
        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageToStartScanning))
                .setScanners(new TypeAnnotationsScanner(),
                             new SubTypesScanner()));
    }
}
