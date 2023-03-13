package org.pl.resources;
import org.pl.entities.Customer;

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
        customers.add(customer);
        return Response.ok(customer).build();
    }
}
