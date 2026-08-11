package voiidstudios.wex.core.managers;

import org.bukkit.event.Listener;

import voiidstudios.wex.EXABoot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ListenerManager {
    private static final String LISTENERS_PACKAGE = "voiidstudios.wex.listeners";

    private final EXABoot bootstrap;

    public ListenerManager(EXABoot bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void registerDefaults() {
        List<Class<?>> listenerClasses = findListenerClasses();

        if (listenerClasses.isEmpty()) {
            bootstrap.getLogger().passiveInfo("No listeners were found in the package " + LISTENERS_PACKAGE + ".");
            return;
        }

        for (Class<?> listenerClass : listenerClasses) {
            Listener instance = instantiate(listenerClass);
            if (instance == null) {
                continue;
            }

            bootstrap.getFeatureContext().registerListener(instance);
        }
    }

    public EXABoot getBootstrap() {
        return bootstrap;
    }

    private Listener instantiate(Class<?> listenerClass) {
        try {
            Constructor<?> withBootstrap = findConstructor(listenerClass, EXABoot.class);
            if (withBootstrap != null) {
                return (Listener) withBootstrap.newInstance(bootstrap);
            }

            Constructor<?> noArgs = findConstructor(listenerClass);
            if (noArgs != null) {
                return (Listener) noArgs.newInstance();
            }

            bootstrap.getLogger().passiveInfo("It could not be instantiated " + listenerClass.getName()
                    + ": It does not have a constructor (EXABoot) or an empty one.");
        } catch (ReflectiveOperationException exception) {
            bootstrap.getLogger().passiveInfo("It could not be instantiated " + listenerClass.getName()
                    + ": " + exception.getMessage());
        }

        return null;
    }

    private Constructor<?> findConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException exception) {
            return null;
        }
    }

    private List<Class<?>> findListenerClasses() {
        List<Class<?>> found = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        String packagePath = LISTENERS_PACKAGE.replace('.', '/');

        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                if ("jar".equals(resource.getProtocol())) {
                    scanJar(resource, packagePath, classLoader, found);
                } else if ("file".equals(resource.getProtocol())) {
                    scanDirectory(new File(resource.toURI()), LISTENERS_PACKAGE, classLoader, found);
                }
            }
        } catch (IOException | URISyntaxException exception) {
            bootstrap.getLogger().passiveInfo("It could not scan the listeners package: " + exception.getMessage());
        }

        return found;
    }

    private void scanJar(URL resource, String packagePath, ClassLoader classLoader, List<Class<?>> found) throws IOException {
        JarURLConnection connection = (JarURLConnection) resource.openConnection();

        try (JarFile jarFile = connection.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (!name.startsWith(packagePath) || !name.endsWith(".class") || name.contains("$")) {
                    continue;
                }

                String className = name.substring(0, name.length() - ".class".length()).replace('/', '.');
                addIfListener(className, classLoader, found);
            }
        }
    }

    private void scanDirectory(File directory, String packageName, ClassLoader classLoader, List<Class<?>> found) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classLoader, found);
                continue;
            }

            if (!file.getName().endsWith(".class") || file.getName().contains("$")) {
                continue;
            }

            String className = packageName + "." + file.getName().substring(0, file.getName().length() - ".class".length());
            addIfListener(className, classLoader, found);
        }
    }

    private void addIfListener(String className, ClassLoader classLoader, List<Class<?>> found) {
        try {
            Class<?> candidate = Class.forName(className, false, classLoader);

            if (!Listener.class.isAssignableFrom(candidate)) {
                return;
            }

            int modifiers = candidate.getModifiers();
            if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)) {
                return;
            }

            found.add(candidate);
        } catch (ClassNotFoundException | LinkageError exception) {
            bootstrap.getLogger().passiveInfo("It could not load the class " + className + ": " + exception.getMessage());
        }
    }
}
