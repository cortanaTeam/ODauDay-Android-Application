package com.odauday.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kunsubin on 4/4/2018.
 */

public class Favorite {
    
    @SerializedName("user_id")
    @Expose
    private String userId;
    
    @SerializedName("property_id")
    @Expose
    private String propertyId;
    
    public Favorite() {
    }
    
    
    public Favorite(String userId, String propertyId) {
        this.userId = userId;
        this.propertyId = propertyId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getPropertyId() {
        return propertyId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
}



