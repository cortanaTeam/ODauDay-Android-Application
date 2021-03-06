package com.odauday.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.odauday.utils.ObjectUtils;
import java.util.Date;

/**
 * Created by infamouSs on 4/18/18.
 */
public class Image implements Parcelable {
    
    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }
        
        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
    
    @SerializedName("id")
    @Expose
    private String id;
    
    @SerializedName("url")
    @Expose
    private String url;
    
    @SerializedName("date_created")
    @Expose
    private Date dateCreated;
    
    public Image() {
    
    }
    
    public Image(String id, String url) {
        this.id = id;
        this.url = url;
    }
    
    public Image(String id, String url, Date dateCreated) {
        this.id = id;
        this.url = url;
        this.dateCreated = dateCreated;
    }
    
    public Image(String url) {
        this.url = url;
    }
    
    protected Image(Parcel in) {
        id = in.readString();
        url = in.readString();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Date getDateCreated() {
        return dateCreated;
    }
    
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Image image = (Image) o;
        return ObjectUtils.equals(url, image.url);
    }
    
    @Override
    public int hashCode() {
        
        return ObjectUtils.hash(url);
    }
    
    @Override
    public String toString() {
        return "Image{" +
               "id='" + id + '\'' +
               ", url='" + url + '\'' +
               ", dateCreated=" + dateCreated +
               '}';
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        
        parcel.writeString(id);
        parcel.writeString(url);
    }
}