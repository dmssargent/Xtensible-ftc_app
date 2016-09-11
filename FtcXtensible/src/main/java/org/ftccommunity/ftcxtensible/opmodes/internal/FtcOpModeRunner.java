package org.ftccommunity.ftcxtensible.opmodes.internal;

import android.support.annotation.NonNull;

import org.ftccommunity.ftcxtensible.AnnotationFtcRegister;
import org.ftccommunity.ftcxtensible.OpModeType;
import org.ftccommunity.ftcxtensible.dagger.SimpleDag;
import org.ftccommunity.ftcxtensible.opmodes.CustomRunner;
import org.ftccommunity.ftcxtensible.opmodes.methods.EntryPoint;
import org.ftccommunity.ftcxtensible.opmodes.methods.Init;
import org.ftccommunity.ftcxtensible.opmodes.methods.Loop;
import org.ftccommunity.ftcxtensible.opmodes.methods.Stop;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by David on 9/11/2016.
 */
public class FtcOpModeRunner<T> extends AttachedOpMode {
    private final Class<T> opClass;
    private final OpModeType type;
    private final Method mainMethod;
    @Nullable
    private final Method initMethod;
    @Nullable
    private final Method stopMethod;
    private Object instance;

    public FtcOpModeRunner(Class<T> opClass) {
        this.opClass = opClass;

        OpModeTypes temp = null;
        if (opClass.isAnnotationPresent(CustomRunner.class)) {
            final Class<? extends OpModeType> value = opClass.getAnnotation(CustomRunner.class).value();
            type = SimpleDag.create(value, null);
        } else {
            for (OpModeTypes opModeType : OpModeTypes.values()) {
                if (opModeType.check(opClass)) {
                    if (temp == null) {
                        temp = opModeType;
                    } else if ((temp == OpModeTypes.LOOP && opModeType == OpModeTypes.FULL) ||
                            (temp == OpModeTypes.FULL && opModeType == OpModeTypes.LOOP)) {
                        temp = OpModeTypes.FULL;
                    } else {
                        throw new UnknownError("Cannot determine type, the class \"" +
                                opClass.getSimpleName() + "\" fits the contract for \"" +
                                temp + "\" and \"" + opModeType + "\"");
                    }
                }
            }
            if (temp == null) {
                throw new UnknownError("Cannot determine type, the class \"" +
                        opClass.getSimpleName() + "\" fits no available contracts.");
            }
            type = temp;
        }

        initMethod = type.initMethod(opClass);
        mainMethod = type.mainMethod(opClass);
        stopMethod = type.stopMethod(opClass);
    }

    @Override
    public void init() {
        super.init();
        recreateInstance();
        type.init(initMethod, instance);
    }

    @Override
    public void loop() {
        super.loop();
        type.loop(mainMethod, instance);
    }

    @Override
    public void stop() {
        type.stop(stopMethod, instance);
        instance = null;
        super.stop();
    }

    private void recreateInstance() {
        instance = SimpleDag.create(opClass, AnnotationFtcRegister.OP_MODE_RUNNER_PROVIDER);
    }

    private enum OpModeTypes implements OpModeType {
        MAIN {
            @Override
            public void init(Method method, Object instance) {
                try {
                    method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void loop(Method method, Object instance) {

            }

            @Override
            public void stop(Method method, Object instance) {

            }

            @Override
            public Method initMethod(Class<?> instance) {
                return mainMethod(instance);
            }

            @NonNull
            @Override
            public Method mainMethod(Class<?> instance) {
                try {
                    return instance.getMethod("main");
                } catch (NoSuchMethodException e) {
                    throw verify(MAIN, instance);
                }
            }

            @Override
            public Method stopMethod(Class<?> instance) {
                return null;
            }

            @Override
            public boolean check(Class<?> klazz) {
                for (Method method : klazz.getMethods()) {
                    if (Modifier.isStatic(method.getModifiers()) && method.getName().equals("main")) {
                        return true;
                    }
                }

                return false;
            }
        }, ENTRY_POINT {
            @Override
            public void init(Method method, Object instance) {
                try {
                    method.invoke(instance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void loop(Method method, Object instance) {

            }

            @Override
            public void stop(Method method, Object instance) {

            }

            @Override
            public Method initMethod(Class<?> instance) {
                return mainMethod(instance);
            }

            @Override
            @NonNull
            public Method mainMethod(Class<?> instance) {
                for (Method method : instance.getMethods()) {
                    if (method.isAnnotationPresent(EntryPoint.class)) {
                        return method;
                    }
                }

                throw verify(ENTRY_POINT, instance);
            }

            @Override
            public Method stopMethod(Class<?> instance) {
                return null;
            }

            @Override
            public boolean check(Class<?> klazz) {
                boolean entryPointDetected = false;
                for (Method method : klazz.getMethods()) {
                    if (method.isAnnotationPresent(EntryPoint.class)) {
                        if (entryPointDetected) {
                            return false;
                        }
                        entryPointDetected = true;
                    }
                }

                return entryPointDetected;
            }
        }, LOOP {
            @Override
            public void init(Method method, Object instance) {

            }

            @Override
            public void loop(Method method, Object instance) {
                try {
                    method.invoke(instance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void stop(Method method, Object instance) {

            }

            @Override
            public Method initMethod(Class<?> instance) {
                return null;
            }

            @Override
            @NonNull
            public Method mainMethod(Class<?> instance) {
                for (Method method : instance.getMethods()) {
                    if (method.isAnnotationPresent(Loop.class)) {
                        return method;
                    }
                }

                throw verify(LOOP, instance);
            }

            @Override
            public Method stopMethod(Class<?> instance) {
                return null;
            }

            @Override
            public boolean check(Class<?> klazz) {
                boolean entryPointDetected = false;
                for (Method method : klazz.getMethods()) {
                    if (method.isAnnotationPresent(Loop.class)) {
                        if (entryPointDetected) {
                            return false;
                        }
                        entryPointDetected = true;
                    }
                }

                return entryPointDetected;
            }
        }, FULL {
            @Override
            public void init(Method method, Object instance) {
                try {
                    method.invoke(instance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void loop(Method method, Object instance) {
                try {
                    method.invoke(instance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void stop(Method method, Object instance) {
                try {
                    method.invoke(instance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public Method initMethod(Class<?> instance) {
                for (Method method : instance.getMethods()) {
                    if (method.isAnnotationPresent(Init.class)) {
                        return method;
                    }
                }

                return null;
            }

            @Override
            @NonNull
            public Method mainMethod(Class<?> instance) {
                for (Method method : instance.getMethods()) {
                    if (method.isAnnotationPresent(Loop.class)) {
                        return method;
                    }
                }

                throw verify(FULL, instance);
            }

            @Override
            public Method stopMethod(Class<?> instance) {
                for (Method method : instance.getMethods()) {
                    if (method.isAnnotationPresent(Stop.class)) {
                        return method;
                    }
                }

                return null;
            }

            @Override
            public boolean check(Class<?> klazz) {
                if (!LOOP.check(klazz)) {
                    return false;
                }

                boolean initPointDetected = false;
                boolean stopPointDetected = false;
                for (Method method : klazz.getMethods()) {
                    if (method.isAnnotationPresent(Init.class)) {
                        if (initPointDetected) {
                            return false;
                        }
                        initPointDetected = true;
                    }
                    if (method.isAnnotationPresent(Stop.class)) {
                        if (stopPointDetected) {
                            return false;
                        }
                        stopPointDetected = true;
                    }
                }

                return initPointDetected || stopPointDetected;
            }
        };

        private static RuntimeException verify(OpModeType type, Class<?> instance) {
            if (type.check(instance)) {
                return new RuntimeException("Contract passed, missing method! impl bad");
            } else {
                return new UnsupportedOperationException("Class is not qualified for the operation");
            }
        }
    }
}
