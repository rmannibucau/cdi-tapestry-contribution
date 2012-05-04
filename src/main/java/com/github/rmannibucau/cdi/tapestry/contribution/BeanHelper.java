package com.github.rmannibucau.cdi.tapestry.contribution;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class BeanHelper {
    private BeanHelper() {
        // no-op
    }

    public static BeanInstance getInstance(final Class<?> clazz, final Annotation[] qualifiers) {
        final BeanManager beanManager = getBeanManager();
        final Set<Bean<?>> beans = beanManager.getBeans(clazz, qualifiers);
        if (beans == null || beans.isEmpty()) {
            return null;
        }

        final Bean<?> bean = beanManager.resolve(beans);
        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        final Object result = beanManager.getReference(bean, clazz, creationalContext);
        return new BeanInstance(result, creationalContext, Dependent.class.equals(bean.getScope()));
    }

    public static Annotation[] getQualifiers(final Class<?> clazz, final String fieldName) {
        final BeanManager bm = getBeanManager();
        final List<Annotation> qualifiers = new ArrayList<Annotation>();
        try {
            final Field field = clazz.getField(fieldName);
            for (Annotation annotation : field.getAnnotations()) {
                if (bm.isQualifier(annotation.annotationType())) {
                    qualifiers.add(annotation);
                }
            }
        } catch (NoSuchFieldException e) {
            // ignored, no qualifiers will be used
        }
        return qualifiers.toArray(new Annotation[qualifiers.size()]);
    }

    private static BeanManager getBeanManager() {
        return BeanManagerHolder.get();
    }
}
