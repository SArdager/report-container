package kz.kdlolymp.termocontainers.service;

import kz.kdlolymp.termocontainers.entity.ContainerNote;
import kz.kdlolymp.termocontainers.entity.Department;
import kz.kdlolymp.termocontainers.entity.User;
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
import java.util.List;

@Service
public class DefaultEmailService implements EmailService {
    @Autowired
    public JavaMailSender emailSender;
    @Autowired
    public UserService userService;
    @Autowired
    public DepartmentService departmentService;

    @Override
    public void sendSimpleEmail(String toAddress, String subject, String message) throws MessagingException{
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom("record.termocontainer@kdlolymp.kz");
        emailSender.send(simpleMailMessage);
    }

    @Override
    public void sendEmailWithAttachment(String toAddress, String subject, String message, String attachment) throws MessagingException, FileNotFoundException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(toAddress);
        messageHelper.setSubject(subject);
        messageHelper.setText(message);
        messageHelper.setFrom("record.termocontainer@kdlolymp.kz");
        FileSystemResource file = new FileSystemResource(ResourceUtils.getFile(attachment));
        messageHelper.addAttachment("Purchase Order", file);
        emailSender.send(mimeMessage);
    }

    public boolean sendMessageToAdmin(String message) {
        List<User> admins = userService.getAdmins();
        try {
            for (User user: admins) {
                sendSimpleEmail(user.getEmail(), "ВАЖНО: Системное предупреждение", message);
            }
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean sendDelayNote(List<User> users, Long delay, ContainerNote note) {
        String delayMessage = "Прибытие термоконтейнера № " + note.getContainer().getContainerNumber() +
                " зарегистировано с опозданием на " + delay + " часов.\nОбъект отправки: " +
                note.getOutDepartment().getDepartmentName() + ", " + note.getOutDepartment().getBranch().getBranchName() +
                ", "+ "\nОбъект приема: " + note.getToDepartment().getDepartmentName() + ", " +
                note.getToDepartment().getBranch().getBranchName() + "\nВремя отправки: " + note.getSendTime() +
                "\nВремя получения: " + note.getArriveTime();
        try {

            for (User user: users){
                sendSimpleEmail(user.getEmail(), "Оповещение о задержке прибытия", delayMessage);
            }
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean sendTimeStandardNote(List<User> users, int toDepartmentId, int outDepartmentId) {
        Department toDepartment = departmentService.findDepartmentById(toDepartmentId);
        Department outDepartment = departmentService.findDepartmentById(outDepartmentId);
        String delayMessage = "В системе учета термоконтейнеров отсутствует время доставки между объектом: \n" +
                outDepartment.getDepartmentName() + ", " + outDepartment.getBranch().getBranchName() +
                "\nи объектом: \n" + toDepartment.getDepartmentName() + ", " + toDepartment.getBranch().getBranchName() +
                ".\nЧтобы система учета не зарегистрировала опоздание доставки следует сообщить администратору для внесения " +
                "срока доставки для внесения в базу до прибытия термоконтейнера в конечный пункт маршрута.";
        try {
            for (User user: users){
                sendSimpleEmail(user.getEmail(), "Оповещение об отсутствии времени доставки между объектами", delayMessage);
            }
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean sendTemporaryPassword(String toAddress, String password) {
        String subject = "Временный пароль";
        String message = "Мы получили запрос на отправку разового пароля для вашей учетной записи.\nВаш разовый пароль:   " +
                password + "\nПосле входа по разовому паролю вам необходимо будет установить новый пароль.\n" +
                "Если вы не запрашивали разовый пароль, игнорируйте это сообщение.\n\n" +
                "Не следует отвечать на это сообщение. \n\nС уважением,\nСлужба поддержки системы учета термоконтейнеров";
        try {
            sendSimpleEmail(toAddress, subject, message);
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean sendNewUserMessage(String userName, String login, String toAddress, String password) {
        String subject = "Регистрация в системе учета термоконтейнеров";
        String message = "Уважамый(ая) " + userName + "!\nВы зарегистрированы в системе учета использования термоконтейнеров.\n" +
//                "Для входа в систему зайдите в браузере на адрес ТЕСТОВОГО СЕРВЕРА: http://192.168.10.42:8080/record-container/ и авторизуйтесь \n           по логину: " +
                "Для входа в систему зайдите в браузере на адрес: http://route.kdlolymp.kz:8080/record-container/ и авторизуйтесь \n           по логину: " +
                login + "\n            и паролю: " + password + "\nПосле входа по разовому паролю вам необходимо будет установить новый пароль." +
                "\nНе следует отвечать на это сообщение. \n\nС уважением,\nСлужба поддержки системы учета термоконтейнеров";
        try {
            sendSimpleEmail(toAddress, subject, message);
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean sendNewRightsMessage(String userName, String toAddress, String department, String rights) {
        String subject = "Добавление прав в системе учета термоконтейнеров";
        String message = "Уважамый(ая) " + userName + "!\nВам изменены права доступа в системе учета использования термоконтейнеров. \n" +
                "На объекте: " + department +  "\n установлены права: " + rights +
                "\n\nС уважением,\nСлужба поддержки системы учета термоконтейнеров";
        try {
            sendSimpleEmail(toAddress, subject, message);
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
