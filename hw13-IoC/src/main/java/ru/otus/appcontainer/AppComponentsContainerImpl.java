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

    private final Map<Object, Method> returnTypeMethods = new HashMap<>();

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
        //initAppComponents(configClass);
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

    private void initAppComponents(Class<?> configClass) throws Exception {
        for (var declaredMethod : configClass.getDeclaredMethods()) {
            if (declaredMethod.getAnnotation(AppComponent.class) != null) {
                returnTypeMethods.put(declaredMethod.getReturnType(), declaredMethod);
            }
        }

        Constructor<?>[] constructors = configClass.getConstructors();
        Object configInstance = constructors[0].newInstance();
        for (var method : returnTypeMethods.values()) {
            appComponentsByName.put(method.getAnnotation(AppComponent.class).name(),
                    getReturnObject(method.getReturnType(), configInstance));
        }
    }

    private Object getReturnObject(Class<?> returnType, Object configInstance) throws Exception {
        if (appComponents.get(returnType) != null) {
            return appComponents.get(returnType);
        }
        var method = returnTypeMethods.get(returnType);
        List<Object> args = new ArrayList<>();
        for (var paramType : method.getParameterTypes()) {
            args.add(getReturnObject(paramType, configInstance));
        }
        var returnObject = method.invoke(configInstance, args.toArray());
        appComponents.put(returnType, returnObject);
        return returnObject;
    }

}
