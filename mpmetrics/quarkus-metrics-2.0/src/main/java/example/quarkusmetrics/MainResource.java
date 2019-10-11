package example.quarkusmetrics;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metric;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Path("/")
public class MainResource {

    @Inject
    @ForCurrentTransaction
            Counter counter;

    @Inject
    @Metric(name = "global-request-counter", absolute = true)
    Counter globalRequestCounter;


    AtomicLong transactionIdGenerator = new AtomicLong();

    @GET
    @Path("/")
    public void incrementAndGetCounter() {
        long txId = transactionIdGenerator.incrementAndGet();
        System.out.println("Start transaction " + txId);
        TransactionScopedMetricProducer.activeTransaction.set(txId);
        try {
            int n = ThreadLocalRandom.current().nextInt(500);
            System.out.println("Increasing " + counter + " by " + n);
            counter.inc(n);

            System.out.println("Increasing global request counter " + globalRequestCounter);
            globalRequestCounter.inc();

        }
        finally {
            TransactionScopedMetricProducer.activeTransaction.set(null);
        }
    }


}