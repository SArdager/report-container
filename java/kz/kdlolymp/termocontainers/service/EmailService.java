package kz.kdlolymp.termocontainers.service;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;

public interface EmailService {

    void sendSimpleEmail(String toAddress, String subject, String message) throws MessagingException;

    void sendEmailWithAttachment(String toAddress, String subject, String message, String attachment) throws MessagingException, FileNotFoundException;
}
