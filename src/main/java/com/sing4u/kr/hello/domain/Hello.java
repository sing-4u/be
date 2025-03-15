package com.sing4u.kr.hello.domain;

import com.sing4u.kr.common.event.Events;
import jakarta.persistence.*;

@Entity
@Access(AccessType.FIELD)
@Table(name = "hello")
public class Hello {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hello_no")
    private Long helloNo;


    private String message;

    protected Hello() {
    }

    public Hello(String message) {
        this.message = message;
    }

    public void updateMessage(String newMessage) {
        String oldMessage = this.message;
        this.message = newMessage;
        Events.raise(new HelloUpdatedEvent(helloNo, oldMessage, newMessage));
    }

    public Long getHelloNo() {
        return helloNo;
    }

}
