package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import kz.kdlolymp.termocontainers.entity.Container;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ContainerSerializer implements JsonSerializer<Container> {
    @Override
    public JsonElement serialize(Container container, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jObject = new JsonObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime registrationDate = container.getRegistrationDate();
        jObject.addProperty("containerNumber", container.getContainerNumber());
        jObject.addProperty("value", container.getValue().getValueName());
        jObject.addProperty("registrationDate", registrationDate.format(formatter));
        jObject.addProperty("departmentId", container.getDepartment().getId());
        jObject.addProperty("departmentName", container.getDepartment().getDepartmentName());
        jObject.addProperty("branchId", container.getDepartment().getBranch().getId());
        jObject.addProperty("branchName", container.getDepartment().getBranch().getBranchName());
        return jObject;
    }
}
