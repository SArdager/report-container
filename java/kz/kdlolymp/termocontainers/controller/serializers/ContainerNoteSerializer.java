package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.*;
import kz.kdlolymp.termocontainers.entity.BetweenPoint;
import kz.kdlolymp.termocontainers.entity.ContainerNote;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ContainerNoteSerializer implements JsonSerializer<ContainerNote> {
    @Override
    public JsonElement serialize(ContainerNote note, Type type, JsonSerializationContext context) {
        JsonObject jObject = new JsonObject();
        jObject.addProperty("id", note.getId());
        jObject.addProperty("containerNumber", note.getContainer().getContainerNumber());
        jObject.addProperty("outDepartmentId", note.getOutDepartment().getId());
        jObject.addProperty("toDepartmentId", note.getToDepartment().getId());
        jObject.addProperty("outDepartment", note.getOutDepartment().getDepartmentName() + ", " + note.getOutDepartment().getBranch().getBranchName());
        jObject.addProperty("toDepartment", note.getToDepartment().getDepartmentName() + ", " + note.getToDepartment().getBranch().getBranchName());
        if(note.getToUser()!=null) {
            jObject.addProperty("toUser", note.getToUser().getUserFirstname() + " " + note.getToUser().getUserSurname());
            jObject.addProperty("toUserId", note.getToUser().getId());
        } else {
            jObject.addProperty("toUser", "");
            jObject.addProperty("toUserId", "");
        }
        jObject.addProperty("outUser", note.getOutUser().getUserFirstname() + " " + note.getOutUser().getUserSurname());
        jObject.addProperty("outUserId", note.getOutUser().getId());
        LocalDateTime sendDateTime = note.getSendTime();
        LocalDateTime arriveDateTime = note.getArriveTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String sendTimeString = sendDateTime.format(formatter);
        jObject.addProperty("sendTime", sendTimeString);
        if(arriveDateTime!=null){
            String arriveTimeString = arriveDateTime.format(formatter);
            jObject.addProperty("arriveTime", arriveTimeString);
            if(note.getDelayTime()>0) {
                jObject.addProperty("delayTime", note.getDelayTime() + " часов");
                jObject.addProperty("status", "Опоздание: " + note.getDelayTime() + " часов");
            } else {
                jObject.addProperty("status", "Доставлен вовремя");
            }
        } else {
            jObject.addProperty("arriveTime", "");
            jObject.addProperty("status", "В дороге");
        }
        if(note.getSendNote()!=null) {
            jObject.addProperty("sendNote", note.getSendNote());
        } else {
            jObject.addProperty("sendNote", "");
        }
        if(note.getSendPay()!=null) {
            jObject.addProperty("sendPay", note.getSendPay());
        } else {
            jObject.addProperty("sendPay", "");
        }
        if(note.getArriveNote()!=null) {
            jObject.addProperty("arriveNote", note.getArriveNote());
        } else {
            jObject.addProperty("arriveNote", "");
        }
        if(note.getBetweenPoints()!=null && note.getBetweenPoints().size()>0) {
            JsonArray pointList = new JsonArray();
            for (int i = 0; i < note.getBetweenPoints().size(); i++) {
                JsonElement point = context.serialize(note.getBetweenPoints().get(i));
                pointList.add(point);
            }
            jObject.add("betweenPoints", pointList);

        }
        return jObject;
    }
}
