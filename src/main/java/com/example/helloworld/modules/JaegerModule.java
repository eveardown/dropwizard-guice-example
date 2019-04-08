package com.example.helloworld.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.helloworld.providers.JaegerTracerProvider;
import com.example.helloworld.providers.TracerProvider;
import com.google.inject.AbstractModule;


public class JaegerModule extends AbstractModule {
	
	private final static  Logger logger = LoggerFactory.getLogger(JaegerModule.class);
	
	@Override
	protected void configure() {
		logger.info("###########          Configured Jaeger Module    #############");
		bind(TracerProvider.class).to(JaegerTracerProvider.class);
	}

}
