package com.shopoholic.models.productservicedetailsresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SlotSelectedDate implements Serializable {

    @SerializedName("slot_id")
    @Expose
    private String slotId;
    @SerializedName("selected_date")
    @Expose
    private String selectedDate;
    @SerializedName("is_available")
    @Expose
    private String isAvailable;

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }
}
