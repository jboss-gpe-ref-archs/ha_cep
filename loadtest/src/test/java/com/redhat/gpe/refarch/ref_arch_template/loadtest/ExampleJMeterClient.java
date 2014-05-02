package com.redhat.gpe.refarch.ref_arch_template.loadtest;

import java.io.StringWriter;
import java.io.PrintWriter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;

import com.redhat.gpe.refarch.ref_arch_template.domain.MyDomain;

/**
 * Example JMeter Sampler Client.  This class is optional; maybe your reference architecture can use one of the existing JMeter sampler classes ?
 */
public final class ExampleJMeterClient extends AbstractJavaSamplerClient {

    private static final String PATH_TO_LOG4J_CONFIG = "path.to.log4j.xml";
    private static Logger log = Logger.getLogger("ExampleJMeterClient");
    private static final String SUCCESS_MESSAGE = "**success**";

    private String name;

    // obviously gets invoked a single time per JVM
    static{
        String pathToLog4jConfig = System.getProperty(PATH_TO_LOG4J_CONFIG);
        if(pathToLog4jConfig != null && !pathToLog4jConfig.equals("")) {
            DOMConfigurator.configure(pathToLog4jConfig);
        }
    }

    // gets invoked a single time for each concurrent client
    public void setupTest(JavaSamplerContext context){
        name = context.getParameter(TestElement.NAME);
        log.info("setupTest() name = "+name);
    }

    public SampleResult runTest(JavaSamplerContext context){
        SampleResult result = new SampleResult();
        result.setSampleLabel(name);
        try {
            // Record sample start time
            result.sampleStart();

            // If interested, do something with domain model class ... maybe use as the payload to some remote call ?
            MyDomain mdObj = new MyDomain();

            // Do work of a single client thread execution

            log.info("runTest() mdObj = "+mdObj);

            result.setResponseMessage(SUCCESS_MESSAGE);
            result.setSuccessful(true);
            result.setResponseCodeOK();
        }catch(Throwable x){
            StringWriter sw = new StringWriter();
            x.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();
            log.error("runTest() stackTrace = "+stackTrace);
            result.setResponseMessage(stackTrace);
            result.setSuccessful(false);
        } finally{
            result.sampleEnd();
        }
        return result;
    }
}
