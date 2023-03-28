package org.pl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.pl.entities.Customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class InteractiveQueries {

//    @Inject
    KafkaStreams streams;

    @Inject
    ObjectMapper mapper;

    private final String STORE_NAME = "customer-sales-store";

    public void setStream(KafkaStreams ks) {
        this.streams = ks;
    }

    public String getCustomers() {
        ReadOnlyKeyValueStore<Integer, JsonNode> store = getStore();
        List<Customer> customers = new ArrayList<>();
        String mesg = "";
        for (KeyValueIterator<Integer, JsonNode> it = store.all(); it.hasNext();) {
            KeyValue<Integer, JsonNode> kv = it.next();
//            System.out.println(kv.key.toString()+" "+kv.value);
//            customers.add(mapper.convertValue(kv.value, Customer.class));
            mesg = mesg.concat("\n").concat("Not Processed Records -- key: "+kv.key +" and value: "+kv.value);
        }
        return mesg;
    }

    ReadOnlyKeyValueStore<Integer, JsonNode> getStore() {
        while (true) {
            try {
                StoreQueryParameters<ReadOnlyKeyValueStore<Integer, JsonNode>> parameters = StoreQueryParameters.fromNameAndType(STORE_NAME, QueryableStoreTypes.keyValueStore());
                return  streams.store(parameters);
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }


}
