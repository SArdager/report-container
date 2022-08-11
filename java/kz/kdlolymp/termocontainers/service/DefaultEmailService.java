package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.ContainerNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.ResourceUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.FileNotFoundException;

@Service
public class DefaultEmailService implements EmailService {
    @Autowired
    public JavaMailSender emailSender;

    @Override
    public void sendSimpleEmail(String toAddress, String subject, String message) throws MessagingException{
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom("a.saduakasov@kdlolymp.kz");
        emailSender.send(simpleMailMessage);
    }

    @Override
    public void sendEmailWithAttachment(String toAddress, String subject, String message, String attachment) throws MessagingException, FileNotFoundException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(toAddress);
        messageHelper.setSubject(subject);
        messageHelper.setText(message);
        messageHelper.setFrom("a.saduakasov@kdlolymp.kz");
        FileSystemResource file = new FileSystemResource(ResourceUtils.getFile(attachment));
        messageHelper.addAttachment("Purchase Order", file);
        emailSender.send(mimeMessage);
    }

    public boolean sendDelayNote(Long delay, ContainerNote note) {
        String delayMessage = "Прибытие термоконтейнера № " + note.getContainer().getContainerNumber() +
                " зарегистировано с опозданием на " + delay + " часов.\nОбъект отправки: " +
                note.getOutDepartment().getDepartmentName() + ", " + note.getOutDepartment().getBranch().getBranchName() +
                ", "+ "\nОбъект приема: " + note.getToDepartment().getDepartmentName() + ", " +
                note.getToDepartment().getBranch().getBranchName() + "\nВремя отправки: " + note.getSendTime() +
                "\nВремя получения: " + note.getArriveTime();
        try {
            sendSimpleEmail("ardagers@mail.ru", "Оповещение о задержке прибытия", delayMessage);
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean sendTemporaryPassword(String toAddress, String password) {
        String subject = "Временный пароль";
        String message = "Мы получили запрос на отправку разового пароля для вашей учетной записи.\nВаш разовый пароль:   " +
                password + "\nПосле входа по разовому паролю вам необходимо будет установить новый пароль.\n" +
                "Если вы не запрашивали разовый парольб игнорируйте это сообщение.\n\n" +
                "Не следует отвечать на это сообщение. \n\nС уважением,\nСлужба поддержки системы учета термоконтейнеров";
        try {
            sendSimpleEmail(toAddress, subject, message);
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
