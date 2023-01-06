package org.example.dynamodb;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.example.domain.DuplicateOrderException;
import org.example.domain.Order;
import org.example.domain.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DynamoDbClient {
    @Value("${dynamo_table_name}")
    private String tableName;

    @Value("${aws_region}")
    private String awsRegion;

    @Value("${access_key}")
    private String awsAccessKey;

    @Value("${secret_access_key}")
    private String awsSecretKey;

    public void createOrder(Order order) throws DuplicateOrderException {
        try {
            getOrder(order.getOrderId());
            throw new DuplicateOrderException() ;
        } catch (OrderNotFoundException e) {
            createOrUpdateOrder(order);
        }
    }

    public void createOrUpdateOrder(Order order) {
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(getClient());
        dynamoDBMapper.save(order);
    }

    public List<Order> getCustomerOrders(String customerId) {
        AmazonDynamoDB client = getClient();
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(customerId));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("customer_id = :v1").withExpressionAttributeValues(eav);
        return dynamoDBMapper.scan(Order.class, scanExpression);
    }

    public Order getOrder(String orderId) throws OrderNotFoundException {
        AmazonDynamoDB client = getClient();
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(orderId));
        DynamoDBQueryExpression<Order> queryExpression = new DynamoDBQueryExpression<Order>()
                .withKeyConditionExpression("order_id = :v1")
                .withExpressionAttributeValues(eav);
        List<Order> orders = dynamoDBMapper.query(Order.class, queryExpression);
        if (orders.size() == 0) {
            throw new OrderNotFoundException();
        } else {
            return orders.get(0);
        }
    }

    public void deleteOrder(String id) throws OrderNotFoundException {
        AmazonDynamoDB client = getClient();
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);
        Order order = dynamoDBMapper.load(Order.class,id);
        if (order == null) {
            throw new OrderNotFoundException();
        }
        dynamoDBMapper.delete(order);
    }

    private AmazonDynamoDB getClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(awsRegion)
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                        awsAccessKey,
                                        awsSecretKey
                                )
                        )
                ).build();
        return client;
    }

}
