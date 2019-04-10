package com.example.helloworld;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 * This is the {@link io.dropwizard.Configuration} object for this application.
 *
 * <p> It is created from the {@code server.yml} file.</p>
 * <p>For example, given a YAML file, e.g. "{@code server.yaml}", containing this:
 * <pre>
 * name: "Random Person"
 * age: 43
 * # ... etc ...
 * </pre>
 * And a configuration class, {@code ExampleConfiguration}, such as this:
 * <pre>
 * public class ExampleConfiguration extends Configuration {
 *     \@NotNull
 *     private String name;
 *
 *     \@Min(1)
 *     \@Max(120)
 *     private int age;
 *
 *     \@JsonProperty
 *     public String getName() {
 *         return name;
 *     }
 *
 *     \@JsonProperty
 *     public void setName(String name) {
 *         this.name = name;
 *     }
 *
 *     \@JsonProperty
 *     public int getAge() {
 *         return age;
 *     }
 *
 *     \@JsonProperty
 *     public void setAge(int age) {
 *         this.age = age;
 *     }
 * }
 * </pre>
 * <p>Dropwizard will parse the "{@code server.yaml}" YAML file and create an {@code ExampleConfiguration} instance
 * for the application. The {@code ExampleConfiguration.getName()} method will return {@code "Random Person"} and the
 * {@code ExampleConfiguration.getAge()} method will return {@code 43}.</p>
 *
 * <p>Dropwizard will also enforce these conditions:</p>
 *
 *  <ul>
 *  <li>{@code name} is not {@code null}.
 *  <li> {@code age} is an integer between {@code 1} and {@code 120}.
 *  </ul>
 * @see <a href="http://www.yaml.org/YAML_for_ruby.html">YAML Cookbook</a>
 * @see com.fasterxml.jackson.annotation.JsonProperty
 * @see org.hibernate.validator.constraints.NotEmpty
 *
 * @author Steve Brown, Estafet Ltd.
 */
public class HelloWorldConfiguration extends Configuration {

    /**
     * The name of the service.
     */
    @NotEmpty
    @JsonProperty(required = true)
    private String serviceName;

    /**
     * The template string to use to create the message.
     */
    @NotEmpty
    @JsonProperty(required = true)
    private String template;

    /**
     * The name to use when no name is provided with the request.
     */
    @NotEmpty
    @JsonProperty
    private final String defaultName = "Stranger";

    /**
     * @return
     *          The service name.
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return
     *          The template string.
     */
    public String getTemplate() {
        return template;
    }

    /**
     * @return
     *          The default name to use.
     */
    public String getDefaultName() {
        return defaultName;
    }
}
