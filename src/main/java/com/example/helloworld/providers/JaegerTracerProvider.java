package com.example.helloworld.providers;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.Tracer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provides;

/**
 * Implementation of the TracerProvider interface which is used with Guice.
 * 
 * @author Jennya Dobreva
 *
 */
public class JaegerTracerProvider implements TracerProvider {

    private final static Logger logger = LoggerFactory.getLogger(JaegerTracerProvider.class);
    private JaegerTracer jtracer;

    public JaegerTracerProvider() {
        initTracer();
    }

    private void initTracer() {
        SamplerConfiguration samplerConfig = new SamplerConfiguration().fromEnv().withType("const").withParam(1);
        ReporterConfiguration reporterConfig = new ReporterConfiguration().fromEnv().withLogSpans(true);
        Configuration config = new Configuration("jaeger-tracer-service").withSampler(samplerConfig).withReporter(
                        reporterConfig);
        this.jtracer = config.getTracer();
        logger.info("________________________Tracer is initialized ________________________");
    }

    public Scope buildSpan(String operationName, String tagName) {
        logger.info("----------------------------------- Build span has been called -----------------------------------");
        Scope scope = this.jtracer.buildSpan(operationName).startActive(true);
        scope.span().setTag(tagName, operationName);
        return scope;
    }

    public Tracer getTacer() {
        return this.jtracer;
    }

}
