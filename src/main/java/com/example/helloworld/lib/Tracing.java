package com.example.helloworld.lib;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.tag.Tags;


/**
 * This class helper methods to initialise tracing and set up the {@link io.opentracing.Span} for each request.
 *
 */
public final class Tracing {

    /**
     * Cannot instantiate.
     */
    private Tracing() {
        super();
    }

    /**
     * Initialise tracing for a service.
     * @param service
     *          The name of the service to initialise tracing for.
     * @return
     *          The tracing implementation.
     */
    public static Tracer init(final String service) {

        // The sampler always makes the same decision for all traces. It samples all traces. If the parameter were
        // zero, it would sample no traces.
        //
        // There doesn't seem to be a public class defining sampler type names.
        final SamplerConfiguration samplerConfiguration = SamplerConfiguration.fromEnv()
                                                                              .withType("const")
                                                                              .withParam(new Integer(1));

        // The reporter configuration species what is reported. In this case,
        final ReporterConfiguration reporterConfiguration = ReporterConfiguration.fromEnv().withLogSpans(Boolean.TRUE);

        // The configuration encapsulates the configuration for sampling and reporting.
        final Configuration configuration = new Configuration(service).withSampler(samplerConfiguration)
                                                                      .withReporter(reporterConfiguration);

        // Create the tracer from the configuration.
        final Tracer tracer = configuration.getTracer();
        Debug.debug(service, "init", "Created tracer: " + tracer.toString() + "for service " + service + ".");
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
}
