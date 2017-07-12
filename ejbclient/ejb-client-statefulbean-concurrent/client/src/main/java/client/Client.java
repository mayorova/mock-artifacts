package client;

import java.security.PrivilegedActionException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import ejb.HelloBeanRemote;

/**
 * @author jmartisk
 */
public class Client {

    private static final int NUMBER_INVOCATIONS = 18;

    private static final AtomicInteger FINISHED_INVOCATIONS = new AtomicInteger(0);

    public static void main(String[] args)
            throws NamingException, PrivilegedActionException, InterruptedException, TimeoutException,
            ExecutionException {
        InitialContext ctx = new InitialContext(getCtxProperties());
        ExecutorService threadPool = null;
        try {

            // ----- switch between the following two lines to switch to EJB client API
//            String lookupName = "ejb:/server/HelloBean!ejb.HelloBeanRemote?stateful";
            String lookupName = "server/HelloBean!ejb.HelloBeanRemote";

            HelloBeanRemote bean = (HelloBeanRemote)ctx.lookup(lookupName);

            @SuppressWarnings("unchecked") final CompletableFuture<Void>[] futures
                    = new CompletableFuture[NUMBER_INVOCATIONS];

            threadPool = Executors.newFixedThreadPool(80);
            for (int i = 0; i < NUMBER_INVOCATIONS; i++) {
                futures[i] = CompletableFuture.runAsync(
                        () -> {
                            bean.hello();
                            System.out.println(Thread.currentThread().getName() + " --- " + FINISHED_INVOCATIONS.incrementAndGet());
                        },
                        threadPool);

            }
            CompletableFuture.allOf(futures).get(30, TimeUnit.SECONDS);
        } finally {
            if (threadPool != null) {
                threadPool.shutdown();
            }
            ctx.close();
        }
    }

    public static Properties getCtxProperties() {
        Properties props = new Properties();

        // ----- switch between the following two lines to switch to EJB client API
//        props.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");

        props.put(Context.PROVIDER_URL, "remote+http://127.0.0.1:8080");
        return props;
    }

}
