package com.bridgelabz.rabbitmq_userregistration.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    //    Ability to sendMails
    @Autowired
    private JavaMailSender mailsender;

    public void sendEmail(String toEmail, String subject, String body ) {

        //    Ability to add or write Email(toEmail,subject, body of the mail)
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("syedmahammadSameer999@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        mailsender.send(message);
        System.out.println("Mail sent to the User...!");

    }
}
