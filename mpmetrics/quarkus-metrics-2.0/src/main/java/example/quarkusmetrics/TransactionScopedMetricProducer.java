package example.quarkusmetrics;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.Metric;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

@Alternative
//@Priority(999)
@Priority(1001)
public class TransactionScopedMetricProducer {

    public static final ThreadLocal<Long> activeTransaction = new ThreadLocal<>();

    @Inject
    MetricRegistry metricRegistry;

    @Produces
    @RequestScoped
//    @Alternative
    @ForCurrentTransaction
//    @Metric(name = "SHOULD-NOT-REGISTER", absolute = true)
    public Counter counterForTransaction() {
        Long id = activeTransaction.get();
        System.out.println("Producing for transaction id: " + id);
        return metricRegistry.counter("metric", new Tag("transaction", String.valueOf(id)));
    }

}
