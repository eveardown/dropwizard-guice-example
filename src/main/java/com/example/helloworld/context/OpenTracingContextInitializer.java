/**
 * Copyright Estafet Ltd. 2019. All rights reserved.
 */
package com.example.helloworld.context;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import com.example.helloworld.lib.Tracing;

import io.opentracing.contrib.jaxrs2.server.SpanFinishingFilter;
import io.opentracing.util.GlobalTracer;

/**
 *
 *
 *
 * @author Steve Brown, Estafet Ltd.
 */
@WebListener
public class OpenTracingContextInitializer implements javax.servlet.ServletContextListener {

    /**
     * The name of the service to trace.
     */
    private final String service;

    /**
     * Construct from the service name.
     * @param serviceName
     *          The name of the service.
     */
    public OpenTracingContextInitializer(final String serviceName) {
        service = serviceName;
    }

    /**
     * @param servletContextEvent
     *          The {@link ServletContextEvent} containing the {@link javax.servlet.ServletContext} to be initialised.
     *
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        final io.opentracing.Tracer tracer = Tracing.init(service);
        GlobalTracer.registerIfAbsent(tracer);

        final ServletContext servletContext = servletContextEvent.getServletContext();
        final Dynamic filterRegistration = servletContext.addFilter("tracingFilter", new SpanFinishingFilter());
        filterRegistration.setAsyncSupported(true);
        filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST,
                                                               DispatcherType.ASYNC),
                                                               false,
                                                               "*");
    }

    /**
     * @param servletContextEvent
     *          The {@link ServletContextEvent} containing the {@link javax.servlet.ServletContext} being destroyed.
     *
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        // Do nothing.

    }
}
