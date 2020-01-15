package org.example.batch;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
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

    @GET
    @Path("/execute/{name}/{inject}")
    public void executeJob(@PathParam("name") String jobName, @PathParam("inject") String injectedValue) {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        System.out.println("app's jobOperator = " + jobOperator);
        final Properties jobparams = new Properties();
        jobparams.put("injected.property", injectedValue);
        long id = jobOperator.start(jobName, jobparams);
        System.out.println("Started execution id " + id);
    }

}
