package edu.qaware.cc.webshop.sns;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.qaware.cc.webshop.sns.model.Address;
import edu.qaware.cc.webshop.sns.model.Item;
import edu.qaware.cc.webshop.sns.model.WareHouseRequest;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.List;
import java.util.UUID;

@Service
public class SnsService {

    private final static String TOPIC_ARN = "arn:aws:sns:eu-central-1:941377120628:foobar-gmbh-lagerhaltung-anfragen";

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    public SnsService(SnsClient snsClient, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
    }

    public String sendWarehouseRequest() throws JsonProcessingException {
        var item = new Item("Foo-Widget", 3);
        var address = new Address(
                "DE",
                "BY",
                "Munich",
                "80333",
                "Katrin Kunde",
                "Lothstr. 34",
                "");

        var wareHouseRequest = new WareHouseRequest(
                UUID.randomUUID().toString(),
                List.of(item),
                address);


        PublishRequest publishRequest = PublishRequest.builder()
                .message(objectMapper.writeValueAsString(wareHouseRequest))
                .topicArn(TOPIC_ARN)
                .build();

        PublishResponse publish = snsClient.publish(publishRequest);

        return publish.messageId();
    }
}
