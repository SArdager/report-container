package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.*;
import kz.kdlolymp.termocontainers.entity.User;
import kz.kdlolymp.termocontainers.entity.UserRights;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.List;

public class UserSerializer implements JsonSerializer<User>{
    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jObject = new JsonObject();
        if(user!=null) {
            jObject.addProperty("id", user.getId());
            jObject.addProperty("userSurname", user.getUserSurname());
            jObject.addProperty("userFirstname", user.getUserFirstname());
            jObject.addProperty("username", user.getUsername());
            jObject.addProperty("position", user.getPosition());
            jObject.addProperty("email", user.getEmail());
            jObject.addProperty("departmentId", user.getDepartmentId());
            jObject.addProperty("isEnabled", user.isEnabled());
            jObject.addProperty("isTemporary", user.isTemporary());
            jObject.addProperty("role", user.getRole());
            jObject.addProperty("branchId", user.getBranchId());
        }
        return jObject;
    }
}
