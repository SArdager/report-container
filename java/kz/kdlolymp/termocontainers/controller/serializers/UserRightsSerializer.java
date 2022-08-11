package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.*;
import kz.kdlolymp.termocontainers.entity.User;
import kz.kdlolymp.termocontainers.entity.UserRights;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class UserRightsSerializer implements JsonSerializer<UserRights> {
    @Override
    public JsonElement serialize(UserRights userRights, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jObject = new JsonObject();
        jObject.addProperty("id", userRights.getId());
        jObject.addProperty("departmentId", userRights.getDepartment().getId());
        jObject.addProperty("departmentName", userRights.getDepartment().getDepartmentName());
        jObject.addProperty("branchName", userRights.getDepartment().getBranch().getBranchName());
        jObject.addProperty("rights", userRights.getRights());
        return jObject;
    }
}
