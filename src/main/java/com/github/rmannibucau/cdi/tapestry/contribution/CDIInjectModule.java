package com.github.rmannibucau.cdi.tapestry.contribution;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.transform.InjectionProvider2;

public final class CDIInjectModule {
    @Contribute(InjectionProvider2.class)
    public static void provideStandardInjectionProviders(final OrderedConfiguration<InjectionProvider2> configuration) {
        configuration.addInstance("CDI", CDIInjectionProvider.class, "before:Named");
    }
}
