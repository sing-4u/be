package com.sing4u.kr.hello.infra;

import com.sing4u.kr.hello.domain.HelloUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloEventListener {
    @EventListener
    public void handleHelloUpdatedEvent(HelloUpdatedEvent event) {
        log.info("Hello updated: ID={}, Old Message='{}', New Message='{}'",
                event.helloNo(), event.oldMessage(), event.newMessage());
    }
}