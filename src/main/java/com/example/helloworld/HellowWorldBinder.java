/**
 * Copyright Estafet Ltd. 2019. All rights reserved.
 */
package com.example.helloworld;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.example.helloworld.context.OpenTracingContextInitializer;

/**
 * Bind implementations to contracts (interfaces).
 *
 *
 * @author Steve Brown, Estafet Ltd.
 */
public class HellowWorldBinder extends AbstractBinder {

    /**
     * Constructor.
     */
    public HellowWorldBinder () {
       super();
    }

    /**
     * Bind the {@link OpenTracingContextInitializer} class to inject tracing into Jersey.
     * @see org.glassfish.hk2.utilities.binding.AbstractBinder#configure()
     */
    @Override
    protected void configure() {
        bind(OpenTracingContextInitializer.class).to(OpenTracingContextInitializer.class).in(Singleton.class);
    }
}
