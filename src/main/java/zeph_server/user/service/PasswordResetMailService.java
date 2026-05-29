package zeph_server.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PasswordResetMailService {

    private final JavaMailSender mailSender;
    private final String resetUrl;
    private final String from;

    public PasswordResetMailService(
            JavaMailSender mailSender,
            @Value("${app.password-reset-url:https://www.kmuzeph.site/reset-password}") String resetUrl,
            @Value("${spring.mail.username:}") String from
    ) {
        this.mailSender = mailSender;
        this.resetUrl = resetUrl;
        this.from = from;
    }

    public void sendPasswordResetMail(String to, String token) {
        String link = UriComponentsBuilder.fromUriString(resetUrl)
                .queryParam("token", token)
                .build()
                .toUriString();

        SimpleMailMessage message = new SimpleMailMessage();
        if (!from.isBlank()) {
            message.setFrom(from);
        }
        message.setTo(to);
        message.setSubject("[ZEPH] 비밀번호 재설정 안내");
        message.setText("""
                비밀번호 재설정을 요청하셨습니다.

                아래 링크에서 새 비밀번호를 설정해 주세요.
                %s

                요청하지 않았다면 이 메일을 무시해 주세요.
                """.formatted(link));

        mailSender.send(message);
    }
}
