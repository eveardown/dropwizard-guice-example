/**
 *
 */
package com.example.helloworld.installers;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Tracer;

import com.google.inject.Injector;

import ru.vyarus.dropwizard.guice.module.installer.FeatureInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.provider.ProviderReporter;
import ru.vyarus.dropwizard.guice.module.installer.install.JerseyInstaller;
import ru.vyarus.dropwizard.guice.module.installer.order.Order;
import ru.vyarus.dropwizard.guice.module.installer.util.FeatureUtils;
import ru.vyarus.dropwizard.guice.module.installer.util.JerseyBinding;

/**
 * @author jennya
 *
 */
@Order(30)
public class JaegerInstaller implements JerseyInstaller<Tracer>, FeatureInstaller<Tracer> {

    private final Logger logger = LoggerFactory.getLogger(JaegerInstaller.class);
    private final ProviderReporter reporter = new ProviderReporter();
    private JaegerTracer tracer ;

    /**
     *
     */
    public JaegerInstaller() {
        logger.info("Called Jaeger installler ..............................................................");
        initTracer();
    }


    private void initTracer() {
        SamplerConfiguration samplerConfig = new SamplerConfiguration().fromEnv().withType("const").withParam(1);
        ReporterConfiguration reporterConfig = new ReporterConfiguration().fromEnv().withLogSpans(true);
        Configuration config = new Configuration("jaeger-tracer-service").withSampler(samplerConfig).withReporter(reporterConfig);
        this.tracer = config.getTracer();
    }

    @Override
    public boolean matches(Class<?> type) {
         return FeatureUtils.hasAnnotation(type, Service.class);
    }

    @Override
    public void report() {
        logger.info("Called REPORT method");
        reporter.report();

    }

    @Override
    public void install(AbstractBinder binder, Injector injector,
            Class<Tracer> type) {
         JerseyBinding.bindComponent(binder, injector, type, true, true);

    }
}
