package com.sing4u.kr.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmtpMailService implements MailService {
    private final JavaMailSender sender;

    // Gmail은 보통 username과 동일한 주소를 From으로 쓰는 게 안전
    @Value("${mail.from:}")
    private String from; // 비워두면 username이 자동 사용됨(메일서버 설정에 따름)

    @Override
    public void send(String to, String subject, String body) {
        sendInternal(to, subject, body, false);
    }

    @Override
    public void sendHtml(String to, String subject, String html) {
        sendInternal(to, subject, html, true);
    }

    private void sendInternal(String to, String subject, String content, boolean html) {
        try {
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, "UTF-8");
            if (!from.isBlank()) h.setFrom(from);
            h.setTo(to);
            h.setSubject(subject);
            h.setText(content, html);
            sender.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 발송 실패", e);
        }
    }
}
