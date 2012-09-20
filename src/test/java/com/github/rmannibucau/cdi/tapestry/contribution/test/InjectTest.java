package com.github.rmannibucau.cdi.tapestry.contribution.test;

import antlr.Grammar;
import com.github.rmannibucau.cdi.tapestry.contribution.BeanManagerHolder;
import com.github.rmannibucau.cdi.tapestry.contribution.CDIInjectModule;
import com.github.rmannibucau.cdi.tapestry.contribution.test.pages.Index;
import com.github.rmannibucau.cdi.tapestry.contribution.test.services.PojoModule;
import org.antlr.runtime.Lexer;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.codec.StringEncoder;
import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.func.Mapper;
import org.apache.tapestry5.ioc.IOCConstants;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.services.TapestryModule;
import org.apache.ziplock.IO;
import org.apache.ziplock.JarLocation;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.Extension;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

@RunWith(Arquillian.class)
public class InjectTest {
    @ArquillianResource
    private URL url;

    @Deployment(testable = false)
    public static WebArchive war() {
        return ShrinkWrap.create(WebArchive.class, "inject.war")
                // our module (src/main), as we are in the same project building the jar on the fly
                .addAsLibraries(ShrinkWrap.create(JavaArchive.class, "cdi-tapestry-contribution.jar")
                        .addPackage(CDIInjectModule.class.getPackage().getName())
                        .addAsManifestResource(new StringAsset(BeanManagerHolder.class.getName()), "services/" + Extension.class.getName()))

                // our test classes (src/test) = the webapp
                .addClasses(NamedPojo.class, Pojo.class, Index.class, PojoModule.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new StringAsset(
                        Descriptors.create(WebAppDescriptor.class)
                            .version("3.0")
                                .createContextParam()
                                .paramName("tapestry.app-package")
                                .paramValue(Pojo.class.getPackage().getName())
                            .up()
                            .createFilter()
                                .filterName("pojo")
                                .filterClass(TapestryFilter.class.getName())
                            .up()
                            .createFilterMapping()
                                .filterName("pojo")
                                .urlPattern("/*")
                            .up()
                            .exportAsString()),
                        "web.xml")

                // tapestry dependencies, for real project put it in a helper class: new TapestryArchive(name)...
                .addAsLibraries(JarLocation.jarLocation(Lexer.class))
                .addAsLibraries(JarLocation.jarLocation(Grammar.class))
                .addAsLibraries(JarLocation.jarLocation(StringTemplate.class))
                .addAsLibraries(JarLocation.jarLocation(StringEncoder.class))
                .addAsLibraries(JarLocation.jarLocation(IOCConstants.class))
                .addAsLibraries(JarLocation.jarLocation(PlasticClass.class))
                .addAsLibraries(JarLocation.jarLocation(JSONArray.class))
                .addAsLibraries(JarLocation.jarLocation(InjectService.class))
                .addAsLibraries(JarLocation.jarLocation(Mapper.class))
                .addAsLibraries(JarLocation.jarLocation(TapestryModule.class));
    }

    @Test
    public void checkInjectionsFromOutput() throws IOException {
        final String output = IO.slurp(url);

        assertNotNull(output);
        assertThat(output, containsString("injected"));
        assertThat(output, containsString("named"));
    }
}
