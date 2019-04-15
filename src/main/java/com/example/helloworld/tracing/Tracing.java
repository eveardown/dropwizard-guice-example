package com.example.helloworld.tracing;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import com.example.helloworld.lib.Debug;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;


/**
 * This class contains helper methods to initialise tracing (create the {@link Tracer}) and set up the
 * {@link io.opentracing.Span} for each request.
 *
 * <a href=""https://www.jaegertracing.io/docs/1.11/>Jaeger Tracing</a> is configured by
 * <a href="https://www.jaegertracing.io/docs/1.11/client-features/">Environment Variables</a>.</p>
 *
 *
 * <p>The <a href="https://www.jaegertracing.io/docs/1.11/getting-started/">Jaeger all in one container</a> exposes
 * the following ports:</p>
 *
 * <table style="width: 100%;">
 * <thead>
 * <tr>
 * <th style="width:10%">Port</th>
 * <th style="width:10%">Protocol</th>
 * <th style="width:10%">Component</th>
 * <th style="width:70%">Function</th>
 * </tr>
 * </thead>
 *
 * <tbody>
 * <tr>
 * <td><code>5775</code></td>
 * <td>UDP</td>
 * <td>agent</td>
 * <td>accept <code>zipkin.thrift</code> over compact thrift protocol (deprecated, used by legacy clients only)</td>
 * </tr>
 *
 * <tr>
 * <td><code>6831<c/ode></td>
 * <td>UDP</td>
 * <td>agent</td>
 * <td>accept <code>jaeger.thrift</code> over compact thrift protocol</td>
 * </tr>
 *
 * <tr>
 * <td><code>6832</code></td>
 * <td>UDP</td>
 * <td>agent</td>
 * <td>accept <code>jaeger.thrift</code> over binary thrift protocol</td>
 * </tr>
 *
 * <tr>
 * <td><code>5778</code></td>
 * <td>HTTP</td>
 * <td>agent</td>
 * <td>serve configs</td>
 * </tr>
 *
 * <tr>
 * <td><code>16686</code></td>
 * <td>HTTP</td>
 * <td>query</td>
 * <td>serve frontend</td>
 * </tr>
 *
 * <tr>
 * <td><code>14268</code></td>
 * <td>HTTP</td>
 * <td>collector</td>
 * <td>accept <code>jaeger.thrift</code> directly from clients</td>
 * </tr>
 *
 * <tr>
 * <td><code>9411</code></td>
 * <td>HTTP</td>
 * <td>collector</td>
 * <td>Zipkin compatible endpoint (optional)</td>
 * </tr>
 * </tbody>
 * </table>
 *
 */
public final class Tracing {

    /**
     * Initialise tracing for a service.
     *
     * <p>This registers the {@link Tracer} as the {@link GlobalTracer} because the automatic instrumentation requires
     * the {@code GlobalTracer}. If no tracer is registered, the {@code GlobalTracer} will be a noop tracer.</p>
     *
     * @param serviceName
     *          The name of the service to initialise tracing for.
     * @return
     *          The tracing implementation.
     *
     * @see io.jaegertracing.Configuration
     */
    public static Tracer init(final String serviceName) {

        // Check that either a Jaeger agent or a Jaeger collector is defined.
        validateEnvironmentVariables();

        // Check that Apache Thrift and the Jaeger Java client is on the class path.
        validateClasspath();

        // get the Jaeger sender configuration.
        final SenderConfiguration senderConfiguration = SenderConfiguration.fromEnv();

        // The sampler always makes the same decision for all traces. It samples all traces. If the parameter were
        // zero, it would sample no traces.
        //
        // There doesn't seem to be a public class defining sampler type names.
        final SamplerConfiguration samplerConfiguration = SamplerConfiguration.fromEnv()
                                                                              .withType("const")
                                                                              .withParam(new Integer(1));

        // The reporter configuration species what is reported and how it is reported.
        final ReporterConfiguration reporterConfiguration =
                        ReporterConfiguration.fromEnv()
                                             .withSender(senderConfiguration)
                                             .withLogSpans(Boolean.TRUE);

        // The configuration encapsulates the configuration for sampling and reporting.
        final Configuration configuration = new Configuration(serviceName).withSampler(samplerConfiguration)
                                                                          .withReporter(reporterConfiguration);

        // Create the tracer from the configuration.
        final Tracer tracer = configuration.getTracer();
        Debug.debug(serviceName, "init", "Created tracer: " + tracer.toString() + "for service " + serviceName + ".");

        final boolean registeredOK = GlobalTracer.registerIfAbsent(tracer);

        if (!registeredOK) {
            throw new RuntimeException("Failed to register the global tracer.");
        }
        Debug.debug(serviceName, "init", "Registered the Global Tracer OK.");
        return tracer;
    }

    /**
     * Create a scope for the request.
     * @param tracer
     *          The {@io.opentracing.Tracer} to use.
     * @param httpHeaders
     *          The HTTP headers in the request.
     * @param operationName
     *          The name of the operation.
     * @return
     *          A {@link Scope} for tracing the request.
     */
    public static Scope startServerSpan(final Tracer tracer,
                                        final javax.ws.rs.core.HttpHeaders httpHeaders,
                                        final String operationName) {
        // format the headers for extraction
        final MultivaluedMap<String, String> requestHeaders = httpHeaders.getRequestHeaders();

        // Get the first value of each Key.
        final Set<String> headerNames = requestHeaders.keySet();
        final HashMap<String, String> contextHeaders = new HashMap<>(headerNames.size());
        for (final String key : headerNames) {
            final String value = requestHeaders.get(key).get(0);

            try {
                Debug.debug(operationName, "startServerSpan", "Adding context header: " + key + " : " +
                            URLDecoder.decode(value, "UTF-8") + " .");
            }
            catch (@SuppressWarnings("unused") final UnsupportedEncodingException uee) {
                Debug.debug(operationName, "startServerSpan",  "Adding context header: " + key + " : " +
                            " Can't decode value as UTF-8.");
            }
            contextHeaders.put(key, value);
        }

        Tracer.SpanBuilder spanBuilder = null;
        try {
            final SpanContext parentSpanContext = tracer.extract(Format.Builtin.TEXT_MAP_EXTRACT,
                                                                 new TextMapExtractAdapter(contextHeaders));
            if (parentSpanContext == null) {
                Debug.debug(operationName, "startServerSpan", "No parent context .");
                spanBuilder = tracer.buildSpan(operationName);
            }
            else {
                Debug.debug(operationName,
                            "startServerSpan",
                            "Got parent context " + parentSpanContext.toString() + ".");
                spanBuilder = tracer.buildSpan(operationName).asChildOf(parentSpanContext);
            }
        }
        catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
            Debug.debug(operationName, "startServerSpan", "Invalid serialized state (corrupt, wrong version, etc) .");
            spanBuilder = tracer.buildSpan(operationName);
        }

        spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER);

        final Span span = spanBuilder.start();
        final Scope scope = tracer.scopeManager().activate(span);
        Debug.debugScope(operationName, "startServerSpan", "started active scope", scope);

        return scope;
    }

    /**
     * Get the value of a property.
     *
     * <p>A property can be defined as "{@code -Dname=value}" on the command line or by an environment variable, "@code name}".</p>
     * @param propertyName
     *          The name of the property.
     * @return
     *          The value of the named property, or {@code null} if it is not defined.
     */
    private static String getProperty(final String propertyName) {
        final String value = System.getProperty(propertyName, System.getenv(propertyName));
        return value;
    }

    /**
     * Get the value of a property as an {@link Integer}.
     *
     * <p>A property can be defined as "{@code -Dname=value}" on the command line or by an environment variable, "@code name}".</p>
     * @param propertyName
     *          The name of the property.
     * @return
     *          The value of the property or {@code null} if it is not set.
     */
    private static Integer getPropertyAsInteger(final String propertyName) {
        final String value =  getProperty(propertyName);

        if (value == null) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        }
        catch (final NumberFormatException nfe) {
            throw new IllegalStateException("The property " + propertyName +
                                            " is not a valid integer: \"" + value + "\".", nfe);
        }
    }

    /**
     * @param className
     *          The class name to check.
     * @return
     *          {@code true} if the class is on the class path. {@code false} otherwise.
     *
     */
    private static boolean isClassAvailable(final String className) {
        try {
            Class.forName(className, false,  Thread.currentThread().getContextClassLoader());
            return true;
        }
        catch (@SuppressWarnings("unused") final ClassNotFoundException  e) {
            return false;
        }
    }

    /**
     * Check the port number is valid.
     * @param portNumber
     *          The port number to check.
     * @return
     *          {@code true} if the port number is valid. {@code false} otherwise.
     */
    private static boolean isValidPort(final Integer portNumber) {
        if (portNumber != null) {
            final int value = portNumber.intValue();

            return value >= 0 && value < 65_536;
        }
        return false;
    }

    /**
     * Validate the class path.
     *
     * <p>The Jaeger Java client and the Jaeger Thrift sender must be on the class path. Otherwise, no traces will
     * be sent.</p>
     */
    private static void validateClasspath() {
        if (!isClassAvailable("org.apache.thrift.protocol.TProtocol")) {
            throw new IllegalStateException("Apache Thrift is not on the class path.");
        }

        if (!isClassAvailable("io.jaegertracing.client.Version")) {
            throw new IllegalStateException("The Jaeger Java client is not on the class path.");
        }
    }

    /**
     * Check that either a Jaeger agent or a Jaeger collector end point is defined.
     *
     * This is a link to <a href="https://www.jaegertracing.io/docs/1.11/client-features/">Jaeger Environment Variables</a>.</p>
     *
     * <p>A property can be defined as "{@code -Dname=value}" on the command line or by an environment variable, "@code name}".</p>
     * <p>If {@code JAEGER_ENDPOINT} is defined, the Jaeger agent will not be used.
     *
     */
    private static void validateEnvironmentVariables() {

        final String collectorEndpoint = getProperty(Configuration.JAEGER_ENDPOINT);

        if (collectorEndpoint == null) {
            final String agentHost = getProperty(Configuration.JAEGER_AGENT_HOST);
            final Integer agentPort = getPropertyAsInteger(Configuration.JAEGER_AGENT_PORT);

            if (agentHost != null && isValidPort(agentPort)) {
                return;
            }

            throw new IllegalStateException("No Jaeger collector endpoint or agent host and port defined.");
        }
    }

    /**
     * Cannot instantiate.
     */
    private Tracing() {
        super();
    }
}
