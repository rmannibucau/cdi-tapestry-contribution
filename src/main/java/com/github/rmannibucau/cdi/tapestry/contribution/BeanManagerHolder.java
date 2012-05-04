package com.github.rmannibucau.cdi.tapestry.contribution;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

public class BeanManagerHolder implements Extension {
    private static BeanManagerHolder HOLDER = new BeanManagerHolder();
    private BeanManager beanManager;

    public static BeanManager get() {
        return HOLDER.beanManager;
    }

    protected void saveBeanManager(@Observes final AfterBeanDiscovery afterBeanDiscovery, final BeanManager bm) {
        HOLDER.beanManager = bm;
    }
}
