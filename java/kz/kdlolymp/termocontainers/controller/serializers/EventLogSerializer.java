package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import kz.kdlolymp.termocontainers.entity.Department;
import kz.kdlolymp.termocontainers.entity.EventLog;
import kz.kdlolymp.termocontainers.entity.User;
import kz.kdlolymp.termocontainers.service.DepartmentService;
import kz.kdlolymp.termocontainers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EventLogSerializer implements JsonSerializer<EventLog> {
    private UserService userService;
    private DepartmentService departmentService;
    @Autowired
    public EventLogSerializer(UserService userService, DepartmentService departmentService){
        this.userService = userService;
        this.departmentService = departmentService;
    }

    @Override
    public JsonElement serialize(EventLog eventLog, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jObject = new JsonObject();
        if(eventLog!=null) {
            jObject.addProperty("id", eventLog.getId());
            LocalDateTime time = eventLog.getTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            jObject.addProperty("time", time.format(formatter));
            jObject.addProperty("editor", eventLog.getUser().getPosition() + " " +
                    eventLog.getUser().getUserSurname() + " " + eventLog.getUser().getUserFirstname());
            jObject.addProperty("branch", eventLog.getBranch().getBranchName());
            jObject.addProperty("department", eventLog.getDepartment().getDepartmentName() + ", " +
                    eventLog.getDepartment().getBranch().getBranchName());
            jObject.addProperty("oldValue", eventLog.getOldValue());
            jObject.addProperty("newValue", eventLog.getNewValue());
            jObject.addProperty("eventId", eventLog.getEventId());
            String target = "";
            if (eventLog.getEventId() == 1) {
                Department destination = departmentService.findDepartmentById(eventLog.getTarget().intValue());
                target = destination.getDepartmentName() + ", " + destination.getBranch().getBranchName();
            } else {
                User editor = userService.findUserById(eventLog.getTarget());
                target = editor.getPosition() + " " + editor.getUserSurname() + " " + editor.getUserFirstname();
            }
            jObject.addProperty("target", target);
        }
        return jObject;
    }


}
