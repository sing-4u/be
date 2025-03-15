
package com.sing4u.kr.hello.domain;

public record HelloUpdatedEvent(Long helloNo, String oldMessage, String newMessage) {
}