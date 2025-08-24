package com.sing4u.kr.mail.service;

public interface MailService {
    void send(String to, String subject, String body);               // 텍스트
    default void sendHtml(String to, String subject, String html) {  // 필요하면 HTML
        send(to, subject, html);
    }
}
