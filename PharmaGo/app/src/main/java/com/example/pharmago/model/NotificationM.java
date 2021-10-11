package com.example.pharmago.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationM {
    private Integer notificationId;
    private String notificationTitle;
    private String notificationContent;
    private Long timeStamp;
    private Boolean isSeen;
    private Integer typeId;
    private String additionalData;


    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }


    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }


    public boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }


    public static ArrayList<NotificationM> getArrayListFromJsonArray(JSONArray jsonArray)
    {
        ArrayList<NotificationM> notificationMS = new ArrayList<>();

        try{

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject object=jsonArray.optJSONObject(i);
                NotificationM notificationM = new NotificationM();
                notificationM.setNotificationId(object.getInt("id"));
                notificationM.setIsSeen(object.getBoolean("seen"));
                notificationM.setNotificationContent(object.getString("content"));
                notificationM.setNotificationTitle(object.getString("title"));
                notificationM.setTimeStamp(object.getLong("time"));
                notificationM.setTypeId(object.getInt("type"));
                try {
                    notificationM.setAdditionalData(object.getString("additional"));
                }catch (Exception e)
                {
                    notificationM.setAdditionalData(null);
                }
                notificationMS.add(notificationM);
            }


        }catch (Exception e)
        {
            Log.e("ERROR",e.toString());
        }

        return notificationMS;
    }


    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }
}
