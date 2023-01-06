package org.example.restservice;

import com.google.gson.Gson;
import org.example.domain.DuplicateOrderException;
import org.example.domain.Order;
import org.example.domain.OrderConfirmation;
import org.example.domain.OrderNotFoundException;
import org.example.dynamodb.DynamoDbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@Component
public class RestController {
    @Autowired
    DynamoDbClient dynamoDbClient;

    @PostMapping(
            value = "/order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            dynamoDbClient.createOrder(order);
            OrderConfirmation orderConfirmation = new OrderConfirmation(order.getOrderId());
            return new ResponseEntity<>(new Gson().toJson(orderConfirmation), HttpStatus.OK);
        } catch (DuplicateOrderException ex) {
            return new ResponseEntity<>("order " + order.getOrderId() + " is a duplicate", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(
            value = "/order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrUpdateOrder(@RequestBody Order order) {
        dynamoDbClient.createOrUpdateOrder(order);
        OrderConfirmation orderConfirmation = new OrderConfirmation(order.getOrderId());
        return new ResponseEntity<>(new Gson().toJson(orderConfirmation), HttpStatus.OK);
    }

    @GetMapping(
            value = "/orders/customer/{customerId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCustomerOrders(@PathVariable String customerId) {
        List<Order> orders = dynamoDbClient.getCustomerOrders(customerId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping(
            value = "/order/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOrder(@PathVariable String id) {
        try {
            Order order = dynamoDbClient.getOrder(id);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (OrderNotFoundException ex) {
            return new ResponseEntity<>("order " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(
            value = "/order/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteOrder(@PathVariable String id) {
        try {
            dynamoDbClient.deleteOrder(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (OrderNotFoundException ex) {
            return new ResponseEntity<>("order " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

}
