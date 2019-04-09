package com.example.helloworld;

import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;

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
     *
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        // Nothing to do.
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
