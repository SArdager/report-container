package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import kz.kdlolymp.termocontainers.entity.BetweenPoint;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BetweenPointSerializer implements JsonSerializer<BetweenPoint> {
    @Override
    public JsonElement serialize(BetweenPoint point, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jObject = new JsonObject();
        jObject.addProperty("departmentId", point.getDepartment().getId());
        jObject.addProperty("departmentName", point.getDepartment().getDepartmentName() + ", " + point.getDepartment().getBranch().getBranchName());
        LocalDateTime passDateTime = point.getPassTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String passTimeString = passDateTime.format(formatter);
        jObject.addProperty("passTime", passTimeString);
        jObject.addProperty("passUser", point.getPassUser().getUserFirstname() + " " + point.getPassUser().getUserSurname());
        jObject.addProperty("passUserId", point.getPassUser().getId());
        if(point.getPointNote()!=null) {
            jObject.addProperty("passNote", point.getPointNote());
        } else {
            jObject.addProperty("passNote", "");
        }
        return jObject;
    }
}
