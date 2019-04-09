package com.example.helloworld.health;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

/**
 * Dropwizard health check.
 *
 * <p>Checks the template is correct.</p>
 *
 *
 * @author Steve Brown, Estafet Ltd.
 */
@Singleton
public class TemplateHealthCheck extends NamedHealthCheck {

    /**
     * The template to check.
     */
    private final String template;

    /**
     * Constructor.
     *
     * @param theTemplate
     *          The template to check.
     */
    @Inject
    public TemplateHealthCheck(@Named("template") final String theTemplate) {
        template = theTemplate;
    }

    /**
     * Perform a check of the template.
     *
     * @return
     *          If the template is healthy, a healthy {@link com.codahale.metrics.health.HealthCheck.Result}.
     *          Otherwise, an unhealthy {@link com.codahale.metrics.health.HealthCheck.Result} with a
     *          descriptive error message or exception.
     * @throws Exception
     *          If there is an error during the health check. This will result in a failed health check.
     * @see com.codahale.metrics.health.HealthCheck#check()
     */
    @Override
    protected Result check() throws Exception {
        final String saying = String.format(template, "TEST");
        if (!saying.contains("TEST")) {
            return Result.unhealthy("The template doesn't include a name.");
        }
        return Result.healthy();
    }

    /**
     * Get the name of the health check.
     * @return
     *          The name of health check.
     * @see ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck#getName()
     */
    @Override
    public String getName() {
        return "template";
    }
}