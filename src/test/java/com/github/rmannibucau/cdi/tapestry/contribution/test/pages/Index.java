package com.github.rmannibucau.cdi.tapestry.contribution.test.pages;

import com.github.rmannibucau.cdi.tapestry.contribution.test.NamedPojo;
import com.github.rmannibucau.cdi.tapestry.contribution.test.Pojo;

import javax.inject.Inject;
import javax.inject.Named;

public class Index {
    @Inject
    private Pojo pojo;

    @Inject
    @Named("named")
    private NamedPojo namedPojo;

    public String getPojo() {
        return pojo.name();
    }
    public String getNamedPojo() {
        return namedPojo.name();
    }

}
