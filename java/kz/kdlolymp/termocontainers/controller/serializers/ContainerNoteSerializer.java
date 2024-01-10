package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.*;
import kz.kdlolymp.termocontainers.entity.ContainerNote;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ContainerNoteSerializer implements JsonSerializer<ContainerNote> {
    @Override
    public JsonElement serialize(ContainerNote note, Type type, JsonSerializationContext context) {
        JsonObject jObject = new JsonObject();
        if(note!=null && note.getContainer()!=null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            jObject.addProperty("id", note.getId());
            jObject.addProperty("containerNumber", note.getContainer().getContainerNumber());
            jObject.addProperty("outDepartmentId", note.getOutDepartment().getId());
            jObject.addProperty("outDepartment", note.getOutDepartment().getDepartmentName() + ", " + note.getOutDepartment().getBranch().getBranchName());
            if(note.getToDepartment()!=null) {
                jObject.addProperty("toDepartmentId", note.getToDepartment().getId());
                jObject.addProperty("toBranchId", note.getToDepartment().getBranch().getId());
                jObject.addProperty("toDepartment", note.getToDepartment().getDepartmentName() + ", " + note.getToDepartment().getBranch().getBranchName());
                String outName = note.getOutUser().getUserFirstname();
                String outIni = "";
                if(outName.length()>0) {
                    outIni = outName.substring(0, 1) + ".";
                    int posOut = outName.indexOf(" ");
                    if (posOut > 0 && outName.length()>posOut + 2) {
                        outIni += outName.substring(posOut + 1, posOut + 2) + ".";
                    }
                }
                jObject.addProperty("outUser", note.getOutUser().getUserSurname() + " " + outIni);
                jObject.addProperty("outUserId", note.getOutUser().getId());
                LocalDateTime sendDateTime = note.getSendTime();
                LocalDateTime arriveDateTime = note.getArriveTime();
                String sendTimeString = sendDateTime.format(formatter);
                jObject.addProperty("sendTime", sendTimeString);
            } else {
                jObject.addProperty("toDepartmentId", "");
                jObject.addProperty("toBranchId", "");
                jObject.addProperty("toDepartment", "");
                jObject.addProperty("outUser", "");
                jObject.addProperty("outUserId", "");
                jObject.addProperty("sendTime", "");
            }
            if(note.getToUser()!=null) {
                String name = note.getToUser().getUserFirstname();
                String ini = "";
                if(name.length()>0) {
                    ini = name.substring(0, 1) + ".";
                    int pos = name.indexOf(" ");
                    if (pos > 0 && name.length()>pos + 2) {
                        ini += name.substring(pos + 1, pos + 2) + ".";
                    }
                }
                jObject.addProperty("toUser",  note.getToUser().getUserSurname() + " " + ini);
                jObject.addProperty("toUserId", note.getToUser().getId());
            } else {
                jObject.addProperty("toUser", "");
                jObject.addProperty("toUserId", "");
            }
            if(note.getArriveTime()!=null){
                String arriveTimeString = note.getArriveTime().format(formatter);
                jObject.addProperty("arriveTime", arriveTimeString);
                if(note.getDelayTime()!=null && note.getDelayTime()>0) {
                    jObject.addProperty("delayTime", note.getDelayTime() + " часов");
                    jObject.addProperty("status", "Опоздание: " + note.getDelayTime() + " часов");
                } else {
                    jObject.addProperty("status", "Доставлен вовремя");
                }
            } else {
                jObject.addProperty("arriveTime", "");
                jObject.addProperty("status", "В дороге");
            }
            if (note.getThermometer() != null) {
                jObject.addProperty("thermometer", note.getThermometer());
            } else {
                jObject.addProperty("thermometer", "");
            }
            if (note.getAmount() > 0) {
                jObject.addProperty("amount", note.getAmount());
            } else {
                jObject.addProperty("amount", "1");
            }
            if (note.isPaidEnd()) {
                jObject.addProperty("paidEnd", "при получении");
            } else {
                jObject.addProperty("paidEnd", "при отгрузке");
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
        }
        return jObject;
    }
}
