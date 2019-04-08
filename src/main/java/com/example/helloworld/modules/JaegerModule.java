package com.example.helloworld.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.helloworld.providers.JaegerTracerProvider;
import com.example.helloworld.providers.TracerProvider;
import com.google.inject.AbstractModule;

/**
 * "The Module is the basic unit of definition of bindings (or wiring, as it’s known in Spring)... Guice has adopted a
 * code-first approach for dependency injection and management. The application 's initialization can include a list of 
 * Modules which perform the class instances binding.
 * <p/>
 * Check the method {@link com.example.helloworld.HelloWorldApplication#initialize(io.dropwizard.setup.Bootstrap)}.
 * <p/>
 * More information {@link https://www.baeldung.com/guice}
 * 
 * @author Jennya Dobreva
 *
 */
public class JaegerModule extends AbstractModule {

    private final static Logger logger = LoggerFactory.getLogger(JaegerModule.class);

    @Override
    protected void configure() {
        logger.info("###########          Configured Jaeger Module    #############");
        bind(TracerProvider.class).to(JaegerTracerProvider.class);
    }

}
