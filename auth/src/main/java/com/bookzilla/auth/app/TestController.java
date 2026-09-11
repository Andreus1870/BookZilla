package com.bookzilla.auth.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String testOutput() {
        return "test. swagger r u here?";
    }
}
