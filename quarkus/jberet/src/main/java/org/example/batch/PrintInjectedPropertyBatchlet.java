package org.example.batch;

import javax.batch.api.BatchProperty;
import javax.batch.api.Batchlet;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Jan Martiska
 */
@Named(value = "printInjectedPropertyBatchlet")
@Dependent
public class PrintInjectedPropertyBatchlet implements Batchlet {

    @Inject
    @BatchProperty(name = "injected.property")
    String property;

    @Override
    public String process() throws Exception {
        System.out.println("Injected property value = " + property + ", into object " + this.toString());
        System.out.println("processing in thread " + Thread.currentThread().getName());
        return "ok";
    }

    @Override
    public void stop() throws Exception {

    }
}
