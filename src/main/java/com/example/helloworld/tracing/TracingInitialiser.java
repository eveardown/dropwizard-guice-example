/**
 * Copyright Estafet Ltd. 2019. All rights reserved.
 */
package com.example.helloworld.tracing;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import io.opentracing.contrib.jaxrs2.server.OperationNameProvider.ClassNameOperationName;
import io.opentracing.contrib.jaxrs2.server.ServerTracingDynamicFeature;
import io.opentracing.util.GlobalTracer;

/**
 *
 *
 *
 * @author Steve Brown, Estafet Ltd.
 */
@Provider
public class TracingInitialiser implements DynamicFeature {

    /**
     * The {@link ServerTracingDynamicFeature} that starts the tracing spans.
     */
    private final ServerTracingDynamicFeature serverTracingDynamicFeature;

    /**
     * Constructor.
     */
    public TracingInitialiser () {
        super();
        final ServerTracingDynamicFeature.Builder builder = new ServerTracingDynamicFeature.Builder(GlobalTracer.get());

         serverTracingDynamicFeature = builder.withOperationNameProvider(ClassNameOperationName.newBuilder())
                                             .withJoinExistingActiveSpan(true)
                                             .withTraceSerialization(true)
                                             .build();    }

    /**
     * Register the {@link io.opentracing.contrib.jaxrs2.server.ServerTracingFilter}.
     *
     * <p>The {@code ServerTracingFilter} starts the tracing span.</p>
     *
     * @param resourceInfo
     *          The resource class and method information.
     * @param context
     *          The configurable context passed to {@link Feature} and {@link javax.ws.rs.container.DynamicFeature}
     *          instances by JAX-RS runtime during their configuration phase.
     *
     * @see javax.ws.rs.container.DynamicFeature#configure(javax.ws.rs.container.ResourceInfo, javax.ws.rs.core.FeatureContext)
     */
    @Override
    public void configure(final ResourceInfo resourceInfo, final FeatureContext context) {
      serverTracingDynamicFeature.configure(resourceInfo, context);
    }

}
