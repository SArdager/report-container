package kz.kdlolymp.termocontainers.controller;

import kz.kdlolymp.termocontainers.entity.User;
import kz.kdlolymp.termocontainers.service.DefaultEmailService;
import kz.kdlolymp.termocontainers.service.TemporaryPasswordGenerator;
import kz.kdlolymp.termocontainers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


@Controller
public class ChangePasswordController {

    @Autowired
    private UserService userService;
    @Autowired
    private DefaultEmailService emailService;

    private String message = "";
    @RequestMapping("/changePassword")
    public String viewChangePassword(Model model){
        return "/changePassword";
    }

    @PostMapping("/changePassword/change")
    public  String changePassword(HttpServletRequest req, HttpServletResponse resp, Model model){
        String password = req.getParameter("password");
        String username = "";
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user = userService.findByUsername(username);
        user.setPassword(password);
        user.setTemporary(false);
        if(userService.changePassword(user)){
            return "redirect:/work-starter";
        } else{
            model.addAttribute("errorChange", "Ошибка смены пароля пользователя");
            return "/changePassword";
        }

    }

    @PostMapping("/forget-password")
    public  void forgetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        User user = userService.findByUsername(username);
        if(user!=null){
            TemporaryPasswordGenerator generator = new TemporaryPasswordGenerator();
            String password = generator.generateTemporaryPassword();
//            user.setPassword(password);
//            user.setTemporary(true);
//            if(userService.changePassword(user)) {
            if(true) {
                String toAddress = user.getEmail();
                if (emailService.sendTemporaryPassword(toAddress, password)) {
                    message = "Временный пароль выслан на адрес корпоративной электронной почты.";
                } else {
                    message = "Ошибка сброса пароля. \nПовторите позднее или направьте заявку на сброс пароля в службу технической поддержки через сервис-платформу ELMA";
                }
            } else {
                message = "Ошибка сброса пароля. \nПовторите позднее или направьте заявку на сброс пароля в службу технической поддержки через сервис-платформу ELMA";
            }
//            if(userService.changePassword(user)){
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(message);
        resp.getWriter().flush();
        resp.getWriter().close();
    }

}
