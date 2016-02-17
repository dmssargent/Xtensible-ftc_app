/*
 * Copyright © 2016 David Sargent
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ftccommunity.ftcxtensible.services;


import android.util.Log;

import com.google.common.util.concurrent.Service;

import org.ftccommunity.ftcxtensible.ServiceRegister;
import org.ftccommunity.ftcxtensible.abstraction.services.LatebindingContextService;
import org.ftccommunity.ftcxtensible.robot.RobotContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages user-defined {@link Service}s. It handles any services defined by the {@link
 * RobotService} annonation and handles passing the {@link RobotContext} to the services who also
 * implement {@link LatebindingContextService}, however if a {@code Service} only implements {@code
 * LatebindingContextService} the {@code Service} will not be handled
 *
 * @author David Sargent
 * @since 0.3.2
 */
public class ServiceManager {
    private static final String TAG = "ROBOT_SERVICE_MANAGER::";
    private static ServiceManager thisRef;
    private com.google.common.util.concurrent.ServiceManager manager;

    private RobotContext context;
    private LinkedList<LatebindingContextService> robotContextServices;
    private ServiceMap robotServices;

    private ServiceManager() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                stopAsync();
            }
        });

        robotContextServices = new LinkedList<>();
        robotServices = new ServiceMap();

        List<Class> services = ServiceRegister.getServices();
        robotServices = getServicesFromClassList(services);
        manager = new com.google.common.util.concurrent.ServiceManager(robotServices.services());

        manager.startAsync();
    }

    /**
     * Gets the current Service Manager for the Xtensible Library
     *
     * @return the current Service Manager
     */
    public static ServiceManager getInstance() {
        if (thisRef == null) {
            thisRef = new ServiceManager();
        }

        return thisRef;
    }

    /**
     * Generates a list of services from a valid class list
     *
     * @param classes a list of classes
     * @return the instantiated services from the given class list
     */
    private ServiceMap getServicesFromClassList(List<Class> classes) {
        final ServiceMap services = new ServiceMap();
        for (Class<?> klazz : classes) {
            try {
                if (klazz.isAssignableFrom(Service.class)) {
                    Service service = (Service) klazz.newInstance();
                    String serviceName = klazz.getAnnotation(RobotService.class).value();
                    services.put(serviceName.equals("") ? klazz.getSimpleName() : serviceName, service);

                    if (klazz.isAssignableFrom(LatebindingContextService.class)) {
                        robotContextServices.add(LatebindingContextService.class.cast(service));
                    }
                } else {
                    Log.w(TAG, klazz.getSimpleName() + " is not a valid service, it must have a super class of Service");
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return services;
    }

    /**
     * Binds the given {@link RobotContext} to the {@link LatebindingContextService} found
     *
     * @param ctx the {@code RobotContext}
     */
    public void bindContext(@NotNull RobotContext ctx) {
        checkNotNull(ctx);
        for (LatebindingContextService service : robotContextServices) {
            service.bindToContext(ctx);
        }
    }

    /**
     * The Guava Service Manager that this manager delegates to
     *
     * @return the backing Guava service manager;
     */
    public com.google.common.util.concurrent.ServiceManager delegate() {
        return manager;
    }

    /**
     * Attempt to stop all services within a timely manner
     */
    public void stopAsync() {
        try {
            manager.stopAsync().awaitStopped(1, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Log.e(TAG, "Some services failed to stop in a timely manner", e);
        }
    }

    /**
     * Gets a service based on the service name from either the name specified in the annotation or
     * the simple name of the class
     *
     * @param key the Service Name
     * @return the corresponding service to the given key
     * @throws IllegalArgumentException thrown if the given name does not represent a service
     */
    public Service getService(String key) {
        checkArgument(robotServices.containsKey(key),
                "Service %s is not valid, did you forget the correct name or to annotate it?", key);

        return robotServices.get(key);
    }

    /**
     * The underlying map for this ServiceManager
     */
    private class ServiceMap extends HashMap<String, Service> {
        private LinkedList<Service> services;

        public ServiceMap() {
            super();
            services = new LinkedList<>();
        }

        @Override
        public Service put(String key, Service service) {
            services.add(super.put(key, service));
            return service;
        }

        @NotNull
        public LinkedList<Service> services() {
            return services;
        }
    }
}
