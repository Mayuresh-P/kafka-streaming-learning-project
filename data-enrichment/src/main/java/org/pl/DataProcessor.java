//package org.acme;
//
//import java.time.Duration;
//import java.time.Instant;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import io.vertx.core.json.Json;
//import org.apache.kafka.streams.KeyValue;
//import org.apache.kafka.streams.processor.PunctuationType;
//import org.apache.kafka.streams.processor.Punctuator;
//import org.apache.kafka.streams.processor.api.Processor;
//import org.apache.kafka.streams.processor.api.ProcessorContext;
//import org.apache.kafka.streams.processor.api.Record;
//import org.apache.kafka.streams.state.KeyValueIterator;
//import org.apache.kafka.streams.state.KeyValueStore;
//import org.pl.InteractiveQueries;
//
//
//public class DataProcessor implements Processor<Integer, JsonNode, Integer, JsonNode> {
//
//    private ProcessorContext<Integer, JsonNode> context;
//    private KeyValueStore<Integer, JsonNode> kvStore;
//    private final String STORE_NAME = "customer-sales-store";
//
////    @Override
////    public void init(ProcessorContext context) {
////        this.context = context;
////        kvStore = (KeyValueStore) context.getStateStore(STORE_NAME);
////
////        // schedule a punctuate() method every 10 seconds based on stream-time
////        this.context.schedule(Duration.ofSeconds(60), PunctuationType.WALL_CLOCK_TIME,
////                new Punctuator(){
////
////                    @Override
////                    public void punctuate(long timestamp) {
////                        System.out.println("Scheduled punctuator called at "+timestamp);
////                        KeyValueIterator<Integer, JsonNode> iter = kvStore.all();
////                        while (iter.hasNext()) {
////                            KeyValue<Integer, JsonNode> entry = iter.next();
////                            System.out.println("  Processed key: "+entry.key+" and value: "+entry.value+" and sending to processed-topic topic");
////                            context.forward(entry.key, entry.value.toString());
////                            kvStore.put(entry.key, null);
////                        }
////                        iter.close();
////
////                        // commit the current processing progress
////                        context.commit();
////                    }
////                }
////        );
////
////    }
//
////    @Override
////    public void process(Integer key, JsonNode value) {
////        if(value != null) {
////            if (kvStore.get(key) != null) {
////                // this means that the other value arrived first
////                // you have both the values now and can process the record
//////                String newvalue = value.concat(" ").concat(kvStore.get(key));
////                process(key, value);
////
////                // remove the entry from the statestore (if any left or right record came first as an event)
////                kvStore.delete(key);
////
////                context.forward(key, value);
////            } else {
////                // add to state store as either left or right data is missing
////                System.out.println("Incomplete value: "+value+" detected. Putting into statestore for later processing");
////                kvStore.put(key, value);
////            }
////        } else {
////            processRecord(key, value);
////
////            // remove the entry from the statestore (if any left or right record came first as an event)
////            kvStore.delete(key);
////
////            //forward the processed data to processed-topic topic
////            context.forward(key, value);
////        }
////        context.commit();
////    }
//
//    @Override
//    public void init(ProcessorContext<Integer, JsonNode> context) {
//        Processor.super.init(context);
//        this.context = context;
//        kvStore = (KeyValueStore) context.getStateStore(STORE_NAME);
//
//        // schedule a punctuate() method every 10 seconds based on stream-time
//        this.context.schedule(Duration.ofSeconds(60), PunctuationType.WALL_CLOCK_TIME,
//                timestamp -> {
//                    System.out.println("Scheduled punctuator called at "+timestamp);
//                    KeyValueIterator<Integer, JsonNode> iter = kvStore.all();
//                    while (iter.hasNext()) {
//                        KeyValue<Integer, JsonNode> entry = iter.next();
////                        Record<Integer, JsonNode> modifiedRecord = new Record().withKey(entry.key).withValue(entry.value);
//                        System.out.println("  Processed key: "+entry.key+" and value: "+entry.value+" and sending to processed-topic topic");
//                        context.forward(new Record<>(entry.key, entry.value, timestamp));
//                        kvStore.put(entry.key, null);
//                    }
//                    iter.close();
//
//                    // commit the current processing progress
//                    context.commit();
//                }
//        );
//    }
//
//    @Override
//    public void process(Record<Integer, JsonNode> record) {
//        Integer key = record.key();
//        JsonNode value = record.value();
//        if(value != null) {
//            if (kvStore.get(key) != null) {
//                // this means that the other value arrived first
//                // you have both the values now and can process the record
////                String newvalue = value.concat(" ").concat(kvStore.get(key));
//                process(new Record<>(key, value));
//
//                // remove the entry from the statestore (if any left or right record came first as an event)
//                kvStore.delete(key);
//
//                context.forward(key, value);
//            } else {
//                // add to state store as either left or right data is missing
//                System.out.println("Incomplete value: "+value+" detected. Putting into statestore for later processing");
//                kvStore.put(key, value);
//            }
//        } else {
//            processRecord(key, value);
//
//            // remove the entry from the statestore (if any left or right record came first as an event)
//            kvStore.delete(key);
//
//            //forward the processed data to processed-topic topic
//            context.forward(key, value);
//        }
//    }
//
//    @Override
//    public void close() {
//        // TODO Auto-generated method stub
//
//    }
//
//    private void processRecord (Integer key, JsonNode value) {
//        System.out.println("==== Record Processed ==== key: "+key.toString()+" and value: "+value.toString());
//    }
//
//}