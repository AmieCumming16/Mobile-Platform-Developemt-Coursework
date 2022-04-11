package com.example.cumming_amie_s1824920;

import android.os.Parcel;
import android.os.Parcelable;

public class itemData implements Parcelable {
    // Amie Cumming S1824920
    // acummi205@caledonian.ac.uk
    private String channel;
    private String item;
    private String title;
    private String description;
    private String mapPosition;
    private String xPosition;
    private String yPosition;

    public itemData(){
        channel = "";
        item = "";
        title= "";
        description = "";
        mapPosition = "";
    }


    protected itemData(Parcel in) {
        title = in.readString();
        description = in.readString();
        mapPosition = in.readString();
        xPosition = in.readString();
        yPosition = in.readString();
    }
    public itemData(String achannel, String aitem, String atitle, String adescription, String amapPosition){
        channel = achannel;
        item = aitem;
        title = atitle;
        description = adescription;
        mapPosition= amapPosition;
    }

    public static final Creator<itemData> CREATOR = new Creator<itemData>() {
        @Override
        public itemData createFromParcel(Parcel in) {
            return new itemData(in);
        }

        @Override
        public itemData[] newArray(int size) {
            return new itemData[size];
        }
    };

    public String getChannel() {
        return channel;
    }

    public void setChannel(String achannel) {
        this.channel = achannel;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String aitem) {
        this.item = aitem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String atitle) {
        this.title = atitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String adescription) {
        this.description = adescription;
    }




    public String getMapPosition() {
        return mapPosition;
    }
    public String getxPosition() {
        return this.xPosition;
    }
    public String getyPosition() {
        return this.yPosition;
    }

    public void setMapPosition(String amapPosition) {
        this.mapPosition = amapPosition;
        String[] mapcoords = this.mapPosition.split(" ");
        this.xPosition = (mapcoords[0]);
        this.yPosition =(mapcoords[1]);
    }


    @Override
    public String toString()
    {
        String temp;

        temp = channel + " " + item + " " + title + " " + description;

        return temp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(mapPosition);
        parcel.writeString(String.valueOf(xPosition));
        parcel.writeString(String.valueOf(yPosition));


    }
}
