/**
 * 
 */
package com.example.helloworld.providers;

import io.opentracing.Scope;
import io.opentracing.Tracer;

/**
 * 
 * Guice injections and bindings are based on interfaces. 
 * The interface isinjected in com.example.helloworld.resources.HelloWorldResource.
 * 
 * @see com.example.helloworld.modules.JaegerTracerProvider {@link com.example.helloworld.modules.JaegerTracerProvider} 
 * @see com.example.helloworld.modules.JaegerTracerProvider {@link com.example.helloworld.modules.JaegerModule}
 * 
 * @author Jennya Dobreva, Estafet Ltd.
 *
 */
public interface TracerProvider {

    public Scope buildSpan(String operationName, String tagName);

    public Tracer getTacer();
}
