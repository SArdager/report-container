package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import kz.kdlolymp.termocontainers.entity.Branch;

import java.lang.reflect.Type;

public class BranchSerializer implements JsonSerializer<Branch> {
    @Override
    public JsonElement serialize(Branch branch, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jObject = new JsonObject();
        if(branch!=null) {
            jObject.addProperty("id", branch.getId());
            jObject.addProperty("branchName", branch.getBranchName());
            jObject.addProperty("companyId", branch.getCompany().getId());
            jObject.addProperty("hasLabor", branch.getCompany().isLabor());
        }
        return jObject;
    }


}
