package edu.qaware.cc.webshop.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.qaware.cc.webshop.sns.SnsService;
import edu.qaware.cc.webshop.sqs.SqsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webshop")
public class ShopController {

    private final SqsService sqsService;
    private final SnsService snsService;

    @Autowired
    public ShopController(SqsService sqsService, SnsService snsService) {
        this.sqsService = sqsService;
        this.snsService = snsService;
    }

    @PostMapping("/publish")
    public String sendMessage(@RequestParam String message)  {
        return sqsService.publishMessage(message);
    }

    @PostMapping("/lageranfrage")
    public String sendWarehouseRequest() throws JsonProcessingException {
        return snsService.sendWarehouseRequest();
    }
}