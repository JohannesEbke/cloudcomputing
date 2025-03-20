package edu.qaware.cc.webshop.sqs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
public class SqsService {

    private final static String QUEUE_URL = "";

    private final SqsClient sqsClient;

    @Autowired
    public SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public String publishMessage(String message) {

        var sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody(message)
                .build();

        SendMessageResponse sendMessageResponse = sqsClient.sendMessage(sendMessageRequest);

        return sendMessageResponse.messageId();
    }
}