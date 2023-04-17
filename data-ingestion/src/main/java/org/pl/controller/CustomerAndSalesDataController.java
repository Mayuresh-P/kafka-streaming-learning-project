package org.pl.controller;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.pl.entities.Customer;
import org.pl.entities.Sales;
import org.pl.producer.DataIngestionProducer;
import org.pl.serialization.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Path("api")
@Transactional(Transactional.TxType.SUPPORTS)
public class CustomerAndSalesDataController {

    public static final Logger logger = LoggerFactory.getLogger(CustomerAndSalesDataController.class.getSimpleName());

    @POST
    @Path("/addCustomerData")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerData(Customer customer) throws IOException {

        DataIngestionProducer producer = new DataIngestionProducer();

        logger.info("customerId: " + customer.getCustomerId() + " Name: " +
                customer.getName());

        producer.produce(customer);

        return Response.accepted(customer).build();

    }

    @POST
    @Path("/addSalesData")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSalesData(Sales sale) throws IOException {

        DataIngestionProducer producer = new DataIngestionProducer();

        logger.info("salesID: " + sale.getSalesId() + " productCategory: " + sale.getProductCategory());

        producer.produce(sale);

        return Response.accepted(sale).build();

    }

    @POST
    @Path("/addDataThroughFiles")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addData() throws IOException {
        DataIngestionProducer producer = new DataIngestionProducer();

        producer.produce(Object.class);

        return Response.accepted("Successfully added data").build();
    }

}
