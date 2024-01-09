package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import kz.kdlolymp.termocontainers.entity.ParcelPoint;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParcelPointSerializer implements JsonSerializer<ParcelPoint> {


    @Override
    public JsonElement serialize(ParcelPoint point, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jObject = new JsonObject();
        if(point!=null){
            jObject.addProperty("parcelNumber", point.getParcelNumber());
            jObject.addProperty("departmentId", point.getDepartment().getId());
            jObject.addProperty("departmentName", point.getDepartment().getDepartmentName() + ", " + point.getDepartment().getBranch().getBranchName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            if(point.getArriveTime()!=null) {
                LocalDateTime arriveDateTime = point.getArriveTime();
                String arriveTimeString = arriveDateTime.format(formatter);
                jObject.addProperty("arriveTime", arriveTimeString);
            } else {
                jObject.addProperty("arriveTime", "не принято");
            }
            if(point.getSendTime()!=null) {
                LocalDateTime sendDateTime = point.getSendTime();
                String sendTimeString = sendDateTime.format(formatter);
                jObject.addProperty("sendTime", sendTimeString);;
            } else {
                jObject.addProperty("sendTime", "");
            }
            if(point.getToDepartment()!=null){
                jObject.addProperty("toDepartmentName", point.getToDepartment().getDepartmentName() + ", " + point.getToDepartment().getBranch().getBranchName());
            } else {
                jObject.addProperty("toDepartmentName", "");
            }
            if(point.getIntoUser()!=null) {
                String name = point.getIntoUser().getUserFirstname();
                String ini = "";
                if(name.length()>0) {
                    ini = name.substring(0, 1) + ".";
                    int pos = name.indexOf(" ");
                    if (pos > 0 && name.length()>pos + 2) {
                        ini += name.substring(pos + 1, pos + 2) + ".";
                    }
                }
                jObject.addProperty("intoUser", point.getIntoUser().getUserSurname() + " " + ini);
            } else {
                jObject.addProperty("intoUser", "");
            }
            if(point.getOutUser()!=null) {
                String outName = point.getOutUser().getUserFirstname();
                String outIni = "";
                if(outName.length()>0) {
                    outIni = outName.substring(0, 1) + ".";
                    int posOut = outName.indexOf(" ");
                    if (posOut > 0 && outName.length()>posOut + 2) {
                        outIni += outName.substring(posOut + 1, posOut + 2) + ".";
                    }
                }
                jObject.addProperty("outUser", point.getOutUser().getUserSurname() + " " + outIni);
            } else {
                jObject.addProperty("outUser", "");
            }
            String parent = point.getParent();
            if(parent!=null && parent.length()==8){
                if(Character.isDigit(parent.charAt(0))){
                    jObject.addProperty("parent", "т/к " + parent);
                } else {
                    jObject.addProperty("parent", "пос. " + parent);
                }
            } else {
                jObject.addProperty("parent", "");
            }
            if(point.getPayment()!=null) {
                jObject.addProperty("payment", point.getPayment());
            } else {
                jObject.addProperty("payment", "0");
            }
            if(point.getText()!=null) {
                jObject.addProperty("text", point.getText());
            } else {
                jObject.addProperty("text", "");
            }
        }
        return jObject;
    }
}
