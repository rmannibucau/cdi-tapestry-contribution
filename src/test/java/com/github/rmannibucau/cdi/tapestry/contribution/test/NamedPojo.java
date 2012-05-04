package com.github.rmannibucau.cdi.tapestry.contribution.test;

import javax.inject.Named;

@Named("named")
public class NamedPojo {
    public String name() {
        return "named";
    }
}
