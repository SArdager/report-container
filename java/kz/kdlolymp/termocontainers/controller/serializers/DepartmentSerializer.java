package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import kz.kdlolymp.termocontainers.entity.Department;

import java.lang.reflect.Type;

public class DepartmentSerializer implements JsonSerializer<Department> {
    @Override
    public JsonElement serialize(Department department, Type type, JsonSerializationContext context) {
        JsonObject jObject = new JsonObject();
        if(department!=null) {
            jObject.addProperty("id", department.getId());
            jObject.addProperty("departmentName", department.getDepartmentName());
            jObject.addProperty("branchId", department.getBranch().getId());
            jObject.addProperty("branchName", department.getBranch().getBranchName());
        }
        return jObject;
    }
}

