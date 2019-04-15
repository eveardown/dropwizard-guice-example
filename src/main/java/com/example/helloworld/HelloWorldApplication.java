package com.example.helloworld;

import com.example.helloworld.tracing.OpenTracingContextInitializer;
import com.example.helloworld.tracing.Tracing;

import ch.qos.logback.classic.Level;
import io.dropwizard.Application;
import io.dropwizard.Bundle;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ru.vyarus.dropwizard.guice.GuiceBundle;

/**
 * @author Steve Brown, Estafet Ltd.
 *
 * This is a REST web service that formats a greeting message.
 *
 * This service is implemented using the following frameworks:
 *
 * <ul>
 *  <li><a href="https://www.dropwizard.io/">dropwizard</a>: Dropwizard straddles the line between being a library and
 * being a framework. Its goal is to provide performant, reliable implementations of everything a production-ready web
 * application needs.</li>
 *  <li><a href="https://github.com/google/guice">Guice</a>: Guice is a lightweight dependency injection framework for
 *  Java 6 and above, brought to you by Google.</li>
 *  <li><a href="http://xvik.github.io/dropwizard-guicey/">dropwizard-guicy</a>: provides integration between Dropwizard
 *  and Guice.</li>
 * </ul>
 *
 *
 */
public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

    /**
     * The entry point for the service.
     * @param args
     *         The command line arguments. The first argument must be "{@code server}" and the second is the YAML
     *         server configuration file, {@code server.yml}.
     */
    public static void main(final String[] args) {
        try {
            Tracing.init("hello-world");
            new HelloWorldApplication().run(args);
        }
        catch (final Exception e) {
            System.err.println("An error occured in the helllo-world service. " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Initialises the application bootstrap.
     *
     * <p>This method enables auto-configuration for all classes in the {@code com.example.helloworld} package, and is
     * called from the Dropwizard framework.</p>
     *
     * @param bootstrap
     *         The pre-start application environment, containing everything required to bootstrap a Dropwizard
     *         command.
     * @see io.dropwizard.Application#initialize(io.dropwizard.setup.Bootstrap)
     */
    @Override
    public void initialize(final Bootstrap<HelloWorldConfiguration> bootstrap) {

        // Create the Guice bundle for the application,
        final GuiceBundle<HelloWorldConfiguration> guiceBundle =
               GuiceBundle.<HelloWorldConfiguration>builder()
                          .modules(new HelloWorldModule())
                          .enableAutoConfig(getClass().getPackage().getName())
                          .build();

        // Add the Guice bundle to the Dropwizard bootstrap.
        bootstrap.addBundle(guiceBundle);
    }

    /**
     * Set the log level at which to log on application startup.
     */
    @Override
    protected Level bootstrapLogLevel() {
        return Level.ALL;
    }

    /**
     * @return
     *          The name of this application.
     * @see io.dropwizard.Application#getName()
     */
    @Override
    public String getName() {
        return "hello-world";
    }

    /**
     * When the application runs, this method is called after the {@link Bundle}s are run.
     *
     * <p>This method provides an extension point to add providers, resources, etc. for this application.</p>
     *
     * @param configuration
     *          The parsed {@link Configuration} object for this application.
     * @param environment
     *          The application's {@link Environment}.
     * @throws Exception
     *          If an error occurs.
     * @see io.dropwizard.Application#run(io.dropwizard.Configuration, io.dropwizard.setup.Environment)
     */
    @Override
    public void run(final HelloWorldConfiguration configuration,
                    final Environment environment) throws Exception {
        environment.servlets().addServletListeners(new OpenTracingContextInitializer());
    }
}
