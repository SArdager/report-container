package kz.kdlolymp.termocontainers.controller.serializers;

import com.google.gson.*;
import kz.kdlolymp.termocontainers.entity.Parcel;
import kz.kdlolymp.termocontainers.entity.ParcelPoint;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ParcelSerializer implements JsonSerializer<Parcel> {
    @Override
    public JsonElement serialize(Parcel parcel, Type type, JsonSerializationContext context) {
        JsonObject jObject = new JsonObject();
        if(parcel!=null && parcel.getOutDepartment()!=null) {
            jObject.addProperty("id", parcel.getId());
            jObject.addProperty("parcelNumber", parcel.getParcelNumber());
            jObject.addProperty("outDepartment", parcel.getOutDepartment().getDepartmentName() + ", " +
                    parcel.getOutDepartment().getBranch().getBranchName());
            jObject.addProperty("destinationName", parcel.getDestination().getDepartmentName() + ", " +
                    parcel.getDestination().getBranch().getBranchName());
            if (parcel.getParentNumber() != null && parcel.getParentNumber().length() > 0) {
                jObject.addProperty("parentNumber", parcel.getParentNumber());
            } else {
                jObject.addProperty("parentNumber", "");
            }
            String parcelLetter = parcel.getParcelNumber().substring(0, 1);
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
            if(parcel.getDimensions()!=null){
                jObject.addProperty("dimensions", parcel.getDimensions());
            } else {
                jObject.addProperty("dimensions", "");
            }
            if(parcel.getPayment()!=null){
                jObject.addProperty("payment", parcel.getPayment());
            } else {
                jObject.addProperty("payment", "0");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            if(parcel.getSendDate()!=null){
                LocalDateTime sendDateTime = parcel.getSendDate();
                String sendTimeString = sendDateTime.format(formatter);
                jObject.addProperty("sendDateTime", sendTimeString);
            } else {
                jObject.addProperty("sendDateTime", "");
            }
            if(parcel.getDeliveryDate()!=null){
                LocalDateTime deliveryDateTime = parcel.getDeliveryDate();
                String deliveryTimeString = deliveryDateTime.format(formatter);
                jObject.addProperty("deliveryDateTime", deliveryTimeString);
                jObject.addProperty("status", "Доставлено");
            } else {
                jObject.addProperty("deliveryDateTime", "");
                jObject.addProperty("status", "В пути");
            }
            if(parcel.getNote()!=null){
                jObject.addProperty("note", parcel.getNote());
            } else {
                jObject.addProperty("note", "");
            }
            if(parcel.getParcelPoints()!=null && parcel.getParcelPoints().size()>0) {
                List<ParcelPoint> points = parcel.getParcelPoints();
                Collections.sort(points, new Comparator<ParcelPoint>(){
                    public int compare(ParcelPoint pp1, ParcelPoint pp2){
                        return pp1.getId().compareTo(pp2.getId());
                    }
                });
                JsonArray pointList = new JsonArray();
                for (int i = 0; i < points.size(); i++) {
                    JsonElement point = context.serialize(points.get(i));
                    pointList.add(point);
                }
                jObject.add("parcelPoints", pointList);
            }
        }
        return  jObject;
    }
}
