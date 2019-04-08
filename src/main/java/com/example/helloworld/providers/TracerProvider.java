/**
 * 
 */
package com.example.helloworld.providers;

import io.opentracing.Scope;
import io.opentracing.Tracer;

/**
 * @author jennya
 *
 */
public interface TracerProvider {
	
	public Scope buildSpan(String operationName, String tagName);
	
	public Tracer getTacer() ;
}
