package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import kz.kdlolymp.termocontainers.entity.ContainerNote;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SentContainerNoteSerializer implements JsonSerializer<ContainerNote> {
    @Override
    public JsonElement serialize(ContainerNote containerNote, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jObject = new JsonObject();
        if(containerNote!=null) {
            jObject.addProperty("id", containerNote.getId());
            LocalDateTime sendDateTime = containerNote.getSendTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime waitTime = sendDateTime.plusHours(containerNote.getTimeStandard());
            String sendDateString = sendDateTime.format(formatter);
            String waitTimeString = waitTime.format(formatter);
            String arriveDateString = "Не прибыл";
            String status = "В дороге";
            if (containerNote.getArriveTime() != null) {
                arriveDateString = containerNote.getArriveTime().format(formatter);
                if (containerNote.getDelayTime()!=null && containerNote.getDelayTime() > 0) {
                    status = "Опоздание: " + containerNote.getDelayTime() + " часов";
                } else {
                    status = "Доставлен вовремя";
                }
            }
            jObject.addProperty("sendDate", sendDateString);
            jObject.addProperty("waitTime", waitTimeString);
            jObject.addProperty("arriveDate", arriveDateString);
            jObject.addProperty("status", status);
            jObject.addProperty("outBranch", containerNote.getOutDepartment().getBranch().getBranchName());
            jObject.addProperty("toBranch", containerNote.getToDepartment().getBranch().getBranchName());
            jObject.addProperty("outDepartment", containerNote.getOutDepartment().getDepartmentName() + " " +
                    containerNote.getOutDepartment().getBranch().getBranchName());
            jObject.addProperty("containerNumber", containerNote.getContainer().getContainerNumber());
            jObject.addProperty("toDepartment", containerNote.getToDepartment().getDepartmentName() + ", " +
                    containerNote.getToDepartment().getBranch().getBranchName());
            jObject.addProperty("outDepartment", containerNote.getOutDepartment().getDepartmentName() + ", " +
                    containerNote.getOutDepartment().getBranch().getBranchName());
            if (containerNote.getSendNote() != null) {
                jObject.addProperty("sendNote", containerNote.getSendNote());
            } else {
                jObject.addProperty("sendNote", "");
            }
            if (containerNote.getThermometer() != null) {
                jObject.addProperty("thermometer", containerNote.getThermometer());
            } else {
                jObject.addProperty("thermometer", "");
            }
            if (containerNote.getAmount() > 0) {
                jObject.addProperty("amount", containerNote.getAmount());
            } else {
                jObject.addProperty("amount", "1");
            }
            if (containerNote.isPaidEnd()) {
                jObject.addProperty("paidEnd", "при получении");
            } else {
                jObject.addProperty("paidEnd", "при отгрузке");
            }
            if (containerNote.getArriveNote() != null) {
                jObject.addProperty("arriveNote", containerNote.getArriveNote());
            } else {
                jObject.addProperty("arriveNote", "");
            }
            if (containerNote.getSendPay() != null) {
                jObject.addProperty("sendPay", containerNote.getSendPay());
            } else {
                jObject.addProperty("sendPay", "0");
            }
        }
      return jObject;
    }
}
