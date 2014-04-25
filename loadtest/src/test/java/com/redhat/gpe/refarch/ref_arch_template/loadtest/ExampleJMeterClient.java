package com.redhat.gpe.refarch.ref_arch_template.loadtest;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * Example optional JMeter Sampler Client
 */
public final class ExampleJMeterClient extends AbstractJavaSamplerClient {

    public SampleResult runTest(JavaSamplerContext context){
        SampleResult results = new SampleResult();
        results.sampleStart();
        try {

            // Do work of a single client thread execution

            results.setResponseMessage("success");
            results.setSuccessful(true);
            results.setResponseCodeOK();
        }catch(Throwable x){
            StringWriter sw = new StringWriter();
            x.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();
            log.error("runTest() stackTrace = "+stackTrace);
            results.setResponseMessage(stackTrace);
            results.setSuccessful(false);
        }
        return results;
    }
}
