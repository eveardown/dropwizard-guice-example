package com.example.helloworld.resources;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.spi.http.HttpContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.helloworld.core.Saying;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;


/**
 * The Web resource class that handles REST requests.
 *
 * <p>In JAX-RS, a Web resource is implemented as a resource class and requests are handled by methods on resource
 * classes.</p>
 *
 * <p>A resource class is a Java class that uses JAX-RS annotations to implement a corresponding Web resource. Resource
 * classes are POJOs that have at least one annotated with {@literal @}{@link Path} or a request method designator.</p>
 *
 *
 * @author Steve Brown, Estafet Ltd.
 */
@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    /**
     * The logger to use.
     */
    final Logger logger = LoggerFactory.getLogger(HelloWorldResource.class);

    /**
     * The message template.
     */
    private final String template;

    /**
     * The name to use if there is no name query parameter.
     */
    private final String defaultName;

    /**
     * The request counter.
     */
    private final AtomicLong counter;

    /**
     * The request provider.
     */
    @Inject
    private Provider<HttpServletRequest> requestProvider;

    /**
     * Constructor.
     * @param theTemplate
     *          The template string to use.
     * @param theDefaultName
     *          The name to use if there is no name query parameter.
     */
    @Inject
    public HelloWorldResource(@Named("template") final String theTemplate,
                              @Named("defaultName") final String theDefaultName) {
        logger.info("Creating a new HelloWorldResource!");
        template = theTemplate;
        defaultName = theDefaultName;
        counter = new AtomicLong();
    }

    /**
     * Process the request from the client.
     * @param name
     *          The name to use.
     * @param context
     *          The {@link HttpContext} that represents a mapping from the root URI path of a web
     *          service to a {@link javax.xml.ws.spi.http.HttpHandler} that is invoked to handle requests
     *          destined for that path on the associated container.
     * @return
     *          The result in a {@link Saying} object.
     */
    @GET
    public Saying sayHello(@QueryParam("name") final Optional<String> name, @Context final HttpContext context) {
        logger.info("User-Agent: " + requestProvider.get().getHeader("User-Agent"));
        return new Saying(counter.incrementAndGet(),
                          String.format(template, name.or(defaultName)));
    }

    /**
     * Called by the container when the resource is being destroyed.
     *
     * @see PreDestroy
     */
    @PreDestroy
    void destroy() {
        logger.info("Destroying HelloWorldResource... :(");
    }
}