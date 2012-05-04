package com.github.rmannibucau.cdi.tapestry.contribution.test.services;

import com.github.rmannibucau.cdi.tapestry.contribution.CDIInjectModule;
import org.apache.tapestry5.ioc.annotations.SubModule;

@SubModule({
    CDIInjectModule.class
})
public final class PojoModule {
}
