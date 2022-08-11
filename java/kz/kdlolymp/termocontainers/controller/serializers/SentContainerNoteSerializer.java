package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import kz.kdlolymp.termocontainers.entity.ContainerNote;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SentContainerNoteSerializer implements JsonSerializer<ContainerNote> {
    @Override
    public JsonElement serialize(ContainerNote containerNote, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jObject = new JsonObject();
        jObject.addProperty("id", containerNote.getId());
        LocalDateTime sendDateTime = containerNote.getSendTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime waitTime = sendDateTime.plusHours(containerNote.getTimeStandard());
        String sendDateString = sendDateTime.format(formatter);
        String waitTimeString = waitTime.format(formatter);
        String arriveDateString ="Не прибыл";
        String status = "В дороге";
        if(containerNote.getArriveTime()!=null){
            arriveDateString = containerNote.getArriveTime().format(formatter);
            if(containerNote.getDelayTime()>0) {
                status = "Опоздание: " + containerNote.getDelayTime() + " часов";
            } else {
                status = "Доставлен вовремя";
            }
        }
        jObject.addProperty("sendDate", sendDateString);
        jObject.addProperty("waitTime", waitTimeString);
        jObject.addProperty("arriveDate", arriveDateString);
        jObject.addProperty("status", status);
        jObject.addProperty("outDepartment", containerNote.getOutDepartment().getDepartmentName() + " " +
                containerNote.getOutDepartment().getBranch().getBranchName());
        jObject.addProperty("containerNumber", containerNote.getContainer().getContainerNumber());
        jObject.addProperty("toDepartment", containerNote.getToDepartment().getDepartmentName() + ", " +
                containerNote.getToDepartment().getBranch().getBranchName());
        if(containerNote.getSendNote()!=null) {
            jObject.addProperty("sendNote", containerNote.getSendNote());
        } else {
            jObject.addProperty("sendNote", "");
        }
        if(containerNote.getArriveNote()!=null) {
            jObject.addProperty("arriveNote", containerNote.getArriveNote());
        } else {
            jObject.addProperty("arriveNote", "");
        }
        if(containerNote.getSendPay()!=null) {
            jObject.addProperty("sendPay", containerNote.getSendPay());
        } else {
            jObject.addProperty("sendPay", "0");
        }
      return jObject;
    }
}
