package com.example.helloworld.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.Tracer;

/**
 * Implementation of the TracerProvider interface which is used with Guice.
 *
 * @author Jennya Dobreva
 *
 */
public class JaegerTracerProvider implements TracerProvider {

    private final static  Logger logger = LoggerFactory.getLogger(JaegerTracerProvider.class);
    private JaegerTracer jtracer;

    public JaegerTracerProvider() {
        initTracer();
    }

    private void initTracer() {
        new SamplerConfiguration();
        final SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("const").withParam(1);
        new ReporterConfiguration();
        final ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withLogSpans(true);
        final Configuration config = new Configuration("jaeger-tracer-service").withSampler(samplerConfig).withReporter(
                        reporterConfig);
        jtracer = config.getTracer();
        logger.info("________________________Tracer is initialized ________________________");
    }

    @Override
    public Scope buildSpan(final String operationName, final String tagName) {
        logger.info("----------------------------------- Build span has been called -----------------------------------");
        final Scope scope = jtracer.buildSpan(operationName).startActive(true);
        scope.span().setTag(tagName, operationName);
        return scope;
    }

    @Override
    public Tracer getTacer() {
        return jtracer;
    }

}
