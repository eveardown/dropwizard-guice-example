/**
 * Copyright Estafet Ltd. 2019. All rights reserved.
 */
package com.example.helloworld.features;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import com.example.helloworld.HellowWorldBinder;

/**
 * A {@link Feature} for Jersey HK2 injection.
 *
 *
 * @author Steve Brown, Estafet Ltd.
 */

@Provider
public class HK2Feature implements Feature {

   /**
     * Enable HK2 Injection.
     *
     * <p> This is a call-back method that is called when the feature is to be enabled in a given
     * runtime configuration scope.</p>
     *
     * <p>
     * The responsibility of the feature is to properly update the supplied runtime configuration context
     * and return {@code true} if the feature was successfully enabled or {@code false} otherwise.</p>
     *
     * <p>Note that under some circumstances a feature may decide not to enable itself, which is indicated by returning
     * {@code false}. In such a case, the configuration context does not add the feature to the collection of enabled
     * features and a subsequent call to either {@link javax.ws.rs.core.Configuration#isEnabled(Feature)} or
     * {@link javax.ws.rs.core.Configuration#isEnabled(Class)} method will return {@code false}.</p>
     *
     * @param context
     *          The configurable {@link FeatureContext} context in which to register this {@link Feature}.
     *
     * @return
     *          {@code true}. This feature always enables itself.
     *
     * @see javax.ws.rs.core.Feature#configure(javax.ws.rs.core.FeatureContext)
     */
    @Override
    public boolean configure(final FeatureContext context) {
        context.register(new HellowWorldBinder());
        return true;
    }
}