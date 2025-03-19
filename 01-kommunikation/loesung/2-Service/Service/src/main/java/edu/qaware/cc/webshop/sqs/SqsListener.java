package edu.qaware.cc.webshop.sqs;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Component
public class SqsListener {

    private final static String QUEUE_URL = "";

    private final SqsClient sqsClient;

    @Autowired
    public SqsListener(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @PostConstruct
    public void subscribeToQueue() {
        System.out.println("Subscribing to to queue");
        new Thread(() -> {
            while (true) {
                System.out.println("Trying to receive message");
                ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                        .queueUrl(QUEUE_URL)
                        .maxNumberOfMessages(5)
                        .waitTimeSeconds(10)
                        .build();

                List<Message> messages = sqsClient.receiveMessage(request).messages();
                for (Message message : messages) {
                    System.out.println("Received message: " + message.body());

                    DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                            .queueUrl(QUEUE_URL)
                            .receiptHandle(message.receiptHandle())
                            .build();

                    System.out.println("Cleaning up message: " + message.receiptHandle());
                    sqsClient.deleteMessage(deleteMessageRequest);
                }
            }
        }).start();
    }
}
