package com.skyhorsemanpower.auction;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auction")
public class TestController {

    @GetMapping("/test")
    public String getTest() {return "this is a test";}
}
