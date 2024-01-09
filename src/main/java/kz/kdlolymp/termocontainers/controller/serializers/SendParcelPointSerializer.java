package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import kz.kdlolymp.termocontainers.entity.ParcelPoint;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SendParcelPointSerializer implements JsonSerializer<ParcelPoint> {
    @Override
    public JsonElement serialize(ParcelPoint point, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jObject = new JsonObject();
        if(point!=null){
            jObject.addProperty("parcelNumber", point.getParcelNumber());
            jObject.addProperty("departmentId", point.getDepartment().getId());
            jObject.addProperty("departmentName", point.getDepartment().getBranch().getBranchName());
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
                jObject.addProperty("toDepartmentName", point.getToDepartment().getBranch().getBranchName());
            } else {
                jObject.addProperty("toDepartmentName", "");
            }
            if(point.getIntoUser()!=null) {
                String name = point.getIntoUser().getUserFirstname();
                String ini1 = "";
                if(name.length()>0) {
                    int pos1 = name.indexOf(" ");
                    ini1 = name.substring(0, 1) + ".";
                    if (pos1 > 0 && name.length()>pos1 + 2) {
                        ini1 += name.substring(pos1 + 1, pos1 + 2) + ".";
                    }
                }
                jObject.addProperty("intoUser", point.getIntoUser().getUserSurname() + " " + ini1);
            } else {
                jObject.addProperty("intoUser", "");
            }
            if(point.getOutUser()!=null) {
                String outName = point.getOutUser().getUserFirstname();
                String ini2 = "";
                if(outName.length()>0) {
                    int pos2 = outName.indexOf(" ");
                    ini2 = outName.substring(0, 1) + ".";
                    if (pos2 > 0 && outName.length()>pos2 + 2) {
                        ini2 += outName.substring(pos2 + 1, pos2 + 2) + ".";
                    }
                }
                jObject.addProperty("outUser",  point.getOutUser().getUserSurname() + " " + ini2);
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
            String parcelLetter = point.getParcelNumber().substring(0, 1);
            String parcelType = "";
            switch (parcelLetter) {
                case ("K"):
                    parcelType = "Документы, корреспонденция";
                    break;
                case ("P"):
                    parcelType = "Реагенты";
                    break;
                case ("M"):
                    parcelType = "Материалы";
                    break;
                case ("O"):
                    parcelType = "Запасные части";
                    break;
                case ("C"):
                    parcelType = "Компьютерное оборудование";
                    break;
                default:
                    parcelType = "";
                    break;
            }
            jObject.addProperty("parcelType", parcelType);
        }
        return jObject;
    }
}
