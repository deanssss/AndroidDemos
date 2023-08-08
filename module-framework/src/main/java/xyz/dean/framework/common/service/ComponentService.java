package xyz.dean.framework.common.service;

import android.text.TextUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import xyz.dean.framework.common.annotation.DefaultImpl;
import xyz.dean.framework.common.util.LogUtil;
import xyz.dean.framework.common.util.ReflectUtil;

@SuppressWarnings("unused")
public class ComponentService<T extends IComponentService> {
    private static final String TAG = "ComponentService";
    private static final Map<Class<? extends IComponentService>, IComponentService> serviceMap
            = new ConcurrentHashMap<>();
    private Class<T> serviceClass;
    private Object defaultValue;

    public static <T extends IComponentService> ComponentService<T> with(Class<T> clazz) {
        return new ComponentService<>(clazz);
    }

    public static <T extends IComponentService> T get(Class<T> clazz) {
        return ComponentService.with(clazz).create();
    }

    private ComponentService() { }
    private ComponentService(Class<T> clazz) {
        serviceClass = clazz;
    }

    public ComponentService<T> defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @SuppressWarnings("unchecked")
    public T create() {
        // 命中缓存，则从缓存中获取。
        if (serviceMap.containsKey(serviceClass)) {
            return (T) serviceMap.get(serviceClass);
        }

        T instance = null;
        // 检查是否注册有服务实现，如果有，则获取实现实例。
        Class<? extends IComponentService> registeredImplClazz = ComponentServiceRegistry
                .getComponentService(serviceClass);
        if (registeredImplClazz != null) {
            try {
                instance = (T) registeredImplClazz.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                LogUtil.INSTANCE.e(TAG, "Create instance for registered service impl failed." +
                        " class: " + registeredImplClazz.getName(), e);
            }
        }

        // 没有获取到注册的服务实现实例，尝试获取配置的默认实现。
        if (instance == null) {
            DefaultImpl defaultImpl = serviceClass.getAnnotation(DefaultImpl.class);
            if (defaultImpl != null && !TextUtils.isEmpty(defaultImpl.value())) {
                try {
                    instance = (T) Class.forName(defaultImpl.value()).newInstance();
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    LogUtil.INSTANCE.e(TAG, "Create instance for default service impl failed." +
                            " class: " + defaultImpl.value(), e);
                }
            }
        }

        // 缓存实例。
        if (instance != null) {
            serviceMap.put(serviceClass, instance);
            return instance;
        }

        // 动态代理做默认实现兜底。
        instance = (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass}, defaultInvocationHandler);
        return instance;
    }

    private final Object object = new Object();
    private final InvocationHandler defaultInvocationHandler = (proxy, method, args) -> {
        if (method.getDeclaringClass() ==  Object.class) {
            return method.invoke(object, args);
        }
        if (defaultValue != null) {
            return defaultValue;
        }
        return ReflectUtil.getDefaultValue(method.getReturnType());
    };
}
