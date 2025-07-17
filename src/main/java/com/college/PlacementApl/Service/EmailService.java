package com.college.PlacementApl.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

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

    public void sendApplicationStatusEmail(
        String to,
        String studentName,
        String companyName,
        String jobTitle,
        ApplicationStatus status,
        String feedback
    ) {
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
}
