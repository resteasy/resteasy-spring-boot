package com.sample.app;

import org.springframework.stereotype.Component;

@Component
public class EchoBean {
    public String echo(String val) {
        return val;
    }
}
