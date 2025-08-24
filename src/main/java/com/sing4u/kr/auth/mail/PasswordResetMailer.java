package com.sing4u.kr.auth.mail;

import com.sing4u.kr.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResetMailer {
    private final MailService mailService;

    @Value("${mail.brand:Sing4U}")
    private String brandName;

    @Value("${mail.support-email:sing4uofficial@gmail.com}")
    private String supportEmail;

    /** 비밀번호 재설정 인증번호 메일 전송 (HTML) */
    public void send(String to, String code) {
        String subject = brandName + " 비밀번호 재설정 인증번호";
        String html = buildPasswordResetHtml(code);
        mailService.sendHtml(to, subject, html);
    }

    /** HTML 템플릿 (인라인 CSS) */
    private String buildPasswordResetHtml(String code) {
        return """
<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8">
  <meta name="x-apple-disable-message-reformatting">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>%s Password Reset</title>
</head>
<body style="margin:0;padding:0;background:#f5f6f8;">
  <!-- preheader -->
  <div style="display:none;max-height:0;overflow:hidden;opacity:0;">비밀번호 재설정 인증번호: %s</div>

  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="background:#f5f6f8;">
    <tr>
      <td align="center" style="padding:24px;">
        <table role="presentation" width="600" cellspacing="0" cellpadding="0" border="0"
               style="width:100%%;max-width:600px;background:#ffffff;border-radius:12px;overflow:hidden;
                      box-shadow:0 2px 12px rgba(0,0,0,0.06);">
          <!-- 헤더 -->
          <tr>
            <td align="center" style="padding:28px 32px 8px 32px;
                font:700 22px/1.2 -apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,AppleSDGothicNeo,'Noto Sans KR',Helvetica,Arial,sans-serif;color:#111;">
              %s
            </td>
          </tr>
          <br>

          <!-- 본문 타이틀/설명 -->
          <tr>
            <td style="padding:40px 32px 0 32px;
                font:600 18px/1.6 -apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,AppleSDGothicNeo,'Noto Sans KR',Helvetica,Arial,sans-serif;color:#111;">
              이메일을 확인하기 위해 아래 인증 번호를 입력해 주세요.
            </td>
          </tr>

          <!-- 인증번호 라벨 -->
          <tr>
            <td style="padding:16px 32px 6px 32px;
                font:700 16px/1.4 -apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,AppleSDGothicNeo,'Noto Sans KR',Helvetica,Arial,sans-serif;color:#111;">
              인증 번호 :
            </td>
          </tr>

          <!-- 인증번호 박스 -->
          <tr>
            <td align="center" style="padding:0 32px 16px 32px;">
              <div style="display:inline-block;background:#111;color:#fff;border-radius:10px;
                          padding:14px 20px;letter-spacing:4px;
                          font:700 26px/1 'SFMono-Regular',Menlo,Consolas,monospace;">
                %s
              </div>
            </td>
          </tr>
          <br>

          <!-- 안내문 -->
          <tr>
            <td style="padding:30px 32px 24px 32px;
                font:400 14px/1.8 -apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,AppleSDGothicNeo,'Noto Sans KR',Helvetica,Arial,sans-serif;color:#444;">
              비밀번호를 변경하고 싶지 않거나 본인이 요청한 것이 아닌 경우, 본 메일은 무시해 주세요.
            </td>
          </tr>
          <br>

          <!-- 구분선 -->
          <tr>
            <td style="padding:30px 32px 24px 32px;">
              <hr style="border:none;border-top:1px solid #eceff3;margin:0;">
            </td>
          </tr>

          <!-- 풋터 -->
          <tr>
            <td style="padding:8px 32px 28px 32px;
                font:400 12px/1.7 -apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,AppleSDGothicNeo,'Noto Sans KR',Helvetica,Arial,sans-serif;color:#8a8f98;">
              자동 생성된 이메일입니다. 이 이메일 주소에 회신하는 경우 답변을 드릴 수 없습니다.<br><br>
              문의가 있는 경우 아래 이메일 주소로 문의 부탁드립니다.<br>
              고객 문의 이메일 : <a href="mailto:%s" style="color:#8a8f98;text-decoration:underline;">%s</a>
            </td>
          </tr>
        </table>

        <div style="padding:12px 0;color:#98a0aa;
            font:400 12px/1.4 -apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,AppleSDGothicNeo,'Noto Sans KR',Helvetica,Arial,sans-serif;">
          © 2024 %s. All rights reserved.
        </div>
      </td>
    </tr>
  </table>
</body>
</html>
        """.formatted(brandName, code, brandName, code, supportEmail, supportEmail, brandName);
    }
}
