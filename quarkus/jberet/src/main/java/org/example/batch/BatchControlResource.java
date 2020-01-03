package org.example.batch;

import org.jberet.spi.BatchEnvironment;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Properties;

@Path("/")
public class BatchControlResource {

    @GET
    @Path("/execute/{name}")
    public void executeJob(@PathParam("name") String jobName) {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        System.out.println("app's jobOperator = " + jobOperator);
        final Properties jobparams = new Properties();
        jobparams.put("injected.property", "VALUE");
        long id = jobOperator.start(jobName, jobparams);
        System.out.println("Started execution id " + id);
    }

}
