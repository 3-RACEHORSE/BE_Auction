package com.skyhorsemanpower.auction;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/v1/auction")
public class TestController {

    @GetMapping("/test")
    public String getTest() {return "this is a test";}

    @GetMapping("/callOtherService")
    public ResponseEntity<?> getSubscribeInfo() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                "http://localhost:8000/subscribe-service/v1/subscribe/info",
                HttpMethod.GET, null, String.class);
    }
}
