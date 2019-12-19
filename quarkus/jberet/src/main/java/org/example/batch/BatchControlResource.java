package org.example.batch;

import javax.batch.operations.JobOperator;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Properties;

@Path("/")
public class BatchControlResource {

    @Inject
    JobOperator jobOperator;

    @GET
    @Path("/execute/{name}")
    public void executeJob(@PathParam("name") String jobName) {
        final Properties jobparams = new Properties();
        jobparams.put("injected.property", "VALUE");
        long id = jobOperator.start(jobName, jobparams);
        System.out.println("Started job id " + id);
    }

}
