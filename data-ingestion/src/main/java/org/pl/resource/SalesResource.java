package org.pl.resource;

import org.pl.producer.DataIngestionProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/api/sales")
@Transactional(Transactional.TxType.SUPPORTS)
public class SalesResource {

    public static final Logger log = LoggerFactory.getLogger(SalesResource.class.getSimpleName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postSales() throws IOException {

        DataIngestionProducer dataIngestionProducer = new DataIngestionProducer();

//        dataIngestionProducer.produce();

        return Response.accepted().build();
    }

}
