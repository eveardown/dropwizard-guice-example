package com.example.helloworld;

import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;

import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

/**
 * The {@link com.google.inject.Module} implementation.
 *
 * <p>A {@link com.google.inject.Module} contributes configuration information, typically interface bindings, which will
 * be used to create an {@link Injector}. A Guice-based application is ultimately made of little more than a set of
 * {@code Module}s and some bootstrapping code.
 *
 * <p>{@code Module} classes can use a more streamlined syntax by extending {@link AbstractModule}
 * rather than implementing the @link com.google.inject.Module} interface directly.</p>
 *
 * <p>In addition to the bindings configured via {@link #configure}, bindings will be created for
 * all methods annotated with {@literal @}{@link Provides}. Use scope and binding annotations on
 * such methods to configure the bindings.
 * @author Steve Brown, Estafet Ltd.
 *
 * @see com.google.inject.Module
 * @see com.google.inject.AbstractModule
 * @see com.google.inject.Provides
 * @see javax.inject.Named
 * @see com.google.inject.Injector
 */
public class HelloWorldModule extends AbstractModule {

    /**
     * Configure the module.
     *
     * <p>Create the {@link Tracer} and register it as the {@link GlobalTracer}.</p>
     *
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
//        bind(HelloWorldConfiguration.class).to(HelloWorldConfiguration.class);
//        final Tracer tracer = Tracing.init("hello-world");
//        final boolean registeredOK = GlobalTracer.registerIfAbsent(tracer);
//
//        if (!registeredOK) {
//            throw new RuntimeException("Failed to register the global tracer.");
//        }
    }

    /**
     * Get the service name.
     * @param configuration
     *         The application configuration.
     * @return
     *         The name of this service.
     */
    @Provides
    @Named("serviceName")
    public String provideServiceName(final HelloWorldConfiguration configuration) {
        return configuration.getServiceName();
    }

    /**
     * Get the message template.
     * @param configuration
     *         The application configuration.
     * @return
     *         The template used to create the message.
     */
    @Provides
    @Named("template")
    public String provideTemplate(final HelloWorldConfiguration configuration) {
        return configuration.getTemplate();
    }

    /**
     * Get the default name to use.
     * @param configuration
     *         The application configuration.
     * @return
     *         The default name to use when no name is supplied with the request.
     */
    @Provides
    @Named("defaultName")
    public String provideDefaultName(final HelloWorldConfiguration configuration) {
        return configuration.getDefaultName();
    }

}
