package com.college.PlacementApl.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.college.PlacementApl.utilites.ApplicationStatus;
import com.college.PlacementApl.utilites.EmailException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendApplicationStatusEmail(
            String to,
            String studentName,
            String companyName,
            String jobTitle,
            ApplicationStatus status,
            String feedback) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Update on your Application: " + jobTitle);
            helper.setFrom("aryany1303@gmail.com");

            Context context = new Context();
            context.setVariable("studentName", studentName);
            context.setVariable("companyName", companyName);
            context.setVariable("jobTitle", jobTitle);
            context.setVariable("status", status);
            context.setVariable("feedback", feedback);

            String htmlContent = templateEngine.process("email/application-status", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailException("Failed to send application status email", e);
        }
    }

    public String generateUrl(HttpServletRequest request) {
        // If you have a configured base URL, use that
        if (baseUrl != null && !baseUrl.isEmpty()) {
            return baseUrl;
        }

        // Otherwise generate from request
        return request.getScheme() + "://" +
                request.getServerName() +
                (request.getServerPort() != 80 ? ":" + request.getServerPort() : "");
    }

    public boolean sendMail(String url, String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("aryany1303@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Password Reset Request");
            message.setText("To reset your password, click the link below:\n" + url +
                    "\n\nIf you didn't request this, please ignore this email.");

            mailSender.send(message);
            return true;
        } catch (MailException e) {
            e.printStackTrace();
            return false;
        }
    }
}
