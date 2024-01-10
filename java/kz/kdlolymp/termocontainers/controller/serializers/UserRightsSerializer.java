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
        if(userRights!=null) {
            jObject.addProperty("id", userRights.getId());
            jObject.addProperty("departmentId", userRights.getDepartment().getId());
            jObject.addProperty("departmentName", userRights.getDepartment().getDepartmentName());
            jObject.addProperty("branchName", userRights.getDepartment().getBranch().getBranchName());
            jObject.addProperty("branchId", userRights.getDepartment().getBranch().getId());
            if(userRights.getRights().equals("editor")) {
                jObject.addProperty("rights", "внесение и редактирование записей");
            } else if(userRights.getRights().equals("reader")) {
                jObject.addProperty("rights", "просмотр записей и получение отчетов");
            } else if(userRights.getRights().equals("chef")) {
                jObject.addProperty("rights", "полные права по лаборатории");
            } else if(userRights.getRights().equals("changer")) {
                jObject.addProperty("rights", "просмотр записей и изменение срока доставки");
            } else if(userRights.getRights().equals("righter")) {
                jObject.addProperty("rights", "просмотр записей и редактирование прав");
            } else if(userRights.getRights().equals("creator")) {
                jObject.addProperty("rights", "по созданию и отправке почтовых корреспонденций");
            } else if(userRights.getRights().equals("courier")) {
                jObject.addProperty("rights", "курьер - внесение записей");
            } else {
                jObject.addProperty("rights", "учет термоконтейнеров");
            }
            jObject.addProperty("rightsShort", userRights.getRights());
        }
        return jObject;
    }
}


