package com.github.rmannibucau.cdi.tapestry.contribution;

import org.apache.tapestry5.internal.services.ComponentClassCache;
import org.apache.tapestry5.internal.transform.InjectNamedProvider;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.UsesOrderedConfiguration;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.util.UnknownValueException;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.PlasticField;
import org.apache.tapestry5.services.transform.InjectionProvider2;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.github.rmannibucau.cdi.tapestry.contribution.BeanHelper.getInstance;
import static com.github.rmannibucau.cdi.tapestry.contribution.BeanHelper.getQualifiers;

// replace InjectNamedProvider
@UsesOrderedConfiguration(InjectionProvider2.class)
public final class CDIInjectionProvider extends InjectNamedProvider {
    private final ComponentClassCache cache;
    private final Map<PlasticField, Annotation[]> annotationsCache = new HashMap<PlasticField, Annotation[]>();
    private final Collection<BeanInstance> instancesToRelease = new ArrayList<BeanInstance>();

    public CDIInjectionProvider(final ObjectLocator locator, final ComponentClassCache cache) {
        super(locator, cache);
        this.cache = cache;
    }

    @Override
    public boolean provideInjection(final PlasticField field, final ObjectLocator locator, final MutableComponentModel componentModel) {
        try {
            if (super.provideInjection(field, locator, componentModel)) {
                return true;
            }
        } catch (UnknownValueException ignored) {
            // let's try with CDI
        }

        final Class<?> fieldClass = load(field.getTypeName());
        final Class<?> injectedClass = load(field.getPlasticClass().getClassName());
        final Annotation[] qualifiers;
        if (!annotationsCache.containsKey(field)) {
            synchronized (annotationsCache) {
                if (!annotationsCache.containsKey(field)) {
                    annotationsCache.put(field, getQualifiers(injectedClass, field.getName()));
                }
            }
        }
        qualifiers = annotationsCache.get(field);

        try {
            final BeanInstance instance = getInstance(fieldClass, qualifiers);
            final boolean resolved = instance != null && instance.isResolved();
            if (resolved) {
                field.inject(instance.getBean());
            }

            if (instance != null && instance.isReleasable()) {
                synchronized (instancesToRelease) {
                    instancesToRelease.add(instance);
                }
            }

            return resolved;
        } catch (IllegalStateException isa) {
            return false;
        }
    }

    private Class<?> load(String typeName) {
        try {
            return cache.forName(typeName);
        } catch (RuntimeException re) {
            try {
                return Thread.currentThread().getContextClassLoader().loadClass(typeName);
            } catch (ClassNotFoundException e) {
                throw re;
            }
        }
    }

    @PostInjection
    public void startupService(final RegistryShutdownHub shutdownHub) {
        shutdownHub.addRegistryShutdownListener(new ShutdownCleanUpListener(instancesToRelease));
    }

    private static class ShutdownCleanUpListener implements Runnable {
        private final Collection<BeanInstance> releasables;

        public ShutdownCleanUpListener(final Collection<BeanInstance> instancesToRelease) {
            releasables = instancesToRelease;
        }

        @Override
        public void run() {
            synchronized (releasables) { // should be useless but just to be sure
                for (BeanInstance instance : releasables) {
                    instance.release();
                }
                releasables.clear();
            }
        }
    }
}
