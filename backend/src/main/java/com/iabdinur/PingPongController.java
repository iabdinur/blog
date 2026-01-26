package com.iabdinur;

import org.springframework.web.bind.annotation.GetMapping;

public class PingPongController {

    record PingPong(String result){}

    @GetMapping("/ping")
    public PingPong getPingPong() {
        return new PingPong("Pong");
    }
}
