package ru.otus.appcontainer;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final Map<Class<?>, Object> appComponents = new HashMap<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) throws Exception {
        processConfig(initialConfigClass);
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        if (appComponents.get(componentClass) != null) {
            return (C) appComponents.get(componentClass);
        }
        for (var classInterface : componentClass.getInterfaces()) {
            if (appComponents.get(classInterface) != null) {
                return (C) appComponents.get(classInterface);
            }
        }
        return null;
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return (C) appComponentsByName.get(componentName);
    }

    private void processConfig(Class<?> configClass) throws Exception {
        checkConfigClass(configClass);
        initAppComponentsOrder(configClass);
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private void initAppComponentsOrder(Class<?> configClass) throws Exception {
        List<Method> configMethods = new ArrayList<>();
        for (var declaredMethod : configClass.getDeclaredMethods()) {
            if (declaredMethod.getAnnotation(AppComponent.class) != null) {
                configMethods.add(declaredMethod);
            }
        }
        configMethods.sort(Comparator.comparingInt(o -> o.getAnnotation(AppComponent.class).order()));

        Constructor<?>[] constructors = configClass.getConstructors();
        if (constructors.length > 1) {
            throw new RuntimeException("Config class can consist only one constructor!");
        }

        Object configInstance = constructors[0].newInstance();
        for (var configMethod : configMethods) {
            List<Object> args = new ArrayList<>();
            for (var paramType : configMethod.getParameterTypes()) {
                args.add(getAppComponent(paramType));
            }
            var returnObject = configMethod.invoke(configInstance, args.toArray());
            appComponents.put(configMethod.getReturnType(), returnObject);
            appComponentsByName.put(configMethod.getAnnotation(AppComponent.class).name(), returnObject);
        }
    }
}
