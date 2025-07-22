package semigg.semi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    @Value("${email.auth.expire-seconds}")
    private long expireSeconds;

    public void sendAuthCode(String toEmail) {
        String authCode = createRandomCode();

        // 이메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[SEMI] 이메일 인증 코드");
        message.setText("인증 코드는 다음과 같습니다: " + authCode);
        mailSender.send(message);

        // Redis에 저장 (key: email, value: code)
        redisTemplate.opsForValue().set(toEmail, authCode, expireSeconds, TimeUnit.SECONDS);
    }

    public boolean verifyCode(String email, String inputCode) {
        String storedCode = redisTemplate.opsForValue().get(email);
        return storedCode != null && storedCode.equals(inputCode);
    }

    private String createRandomCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}

