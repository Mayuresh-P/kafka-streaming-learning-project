package org.pl.resources;
import org.pl.entities.Customer;
import org.pl.producer.Producer;
import org.pl.streams.ConsumerStreams;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/api/customers")
public class CustomerResource {

    List<Customer> customers = new ArrayList<>();


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response appendCustomers (@Valid Customer customer){
//        customers.add(customer);
//        Producer.produceToCustomer(customer);
        return Response.ok(customer).build();
    }

    @GET
    public void consumeCustomers(){
        ConsumerStreams.consume();
    }
}
