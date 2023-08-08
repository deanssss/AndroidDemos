package xyz.dean.framework.common.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xyz.dean.framework.common.util.LogUtil;
import xyz.dean.framework.common.util.ReflectUtil;

@SuppressWarnings("unused")
public class ComponentEvent<T extends IComponentEvent> {
    private static final HashMap<Class<? extends IComponentEvent>, IComponentEvent> cacheEvent =
            new HashMap<>();
    private Class<T> eventClass;
    private EventResultCombiner combiner;
    private Object defaultValue;
    private OnErrorStrategy errorStrategy = OnErrorStrategy.TERMINATE;

    public static <T extends IComponentEvent> ComponentEvent<T> with(Class<T> clazz) {
        return new ComponentEvent<>(clazz);
    }

    public static <T extends IComponentEvent> T get(Class<T> clazz) {
        return with(clazz).create();
    }

    private ComponentEvent() { }
    private ComponentEvent(Class<T> clazz) {
        this.eventClass = clazz;
    }

    @SuppressWarnings("unchecked")
    public T create() {
        if (cacheEvent.containsKey(eventClass)) {
            return (T) cacheEvent.get(eventClass);
        }
        T instance = (T) Proxy.newProxyInstance(eventClass.getClassLoader(), new Class[]{eventClass},
                new EventInvocationHandler());
        cacheEvent.put(eventClass, instance);
        return instance;
    }

    public ComponentEvent<T> combiner(EventResultCombiner<?> combiner) {
        this.combiner = combiner;
        return this;
    }

    public ComponentEvent<T> defaultValue(Object value) {
        this.defaultValue = value;
        return this;
    }

    public ComponentEvent<T> setErrorStrategy(OnErrorStrategy strategy) {
        this.errorStrategy = strategy;
        return this;
    }

    class EventInvocationHandler implements InvocationHandler {
        private static final String TAG = "EventInvocationHandler";

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
            List<IComponentEvent> handlers = ComponentEventRegistry.findEventHandler(eventClass);
            // 没有注册的处理器。
            if (handlers == null) {
                handlers = new ArrayList<>();
            }

            // copy to avoid concurrent modify exception
            List<IComponentEvent> handlersCopy = new ArrayList<>(handlers);
            List<Object> results = new ArrayList<>();
            for (IComponentEvent handler : handlersCopy) {
                try {
                    Object result = method.invoke(handler, args);
                    results.add(result);
                } catch (Exception e) {
                    if (errorStrategy == OnErrorStrategy.TERMINATE) {
                        throw e;
                    } else {
                        LogUtil.INSTANCE.w(TAG, "Execute the handler failed." +
                                " handler: " + handler.getClass().getName() +
                                " method: " + method.getName(), e);
                    }
                }
            }
            if (method.getReturnType() == Void.TYPE) {
                return null;
            }

            // 组合所有结果。
            if (combiner != null) {
                return combiner.combine(results);
            }
            // 如果没有组合器，默认返回最后一个结果。
            if (results.size() > 0) {
                return results.get(results.size() - 1);
            }

            if (defaultValue != null) {
                return defaultValue;
            }
            return ReflectUtil.getDefaultValue(method.getReturnType());
        }
    }
}
