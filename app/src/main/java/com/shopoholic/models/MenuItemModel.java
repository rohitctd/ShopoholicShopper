package com.shopoholic.models;

/**
 * Created by Sachin on 19-Sep-17.
 */

public class MenuItemModel {
    private boolean isSelected;
    private int selectedResourceId;
    private int deselectedResourceId;
    private String itemName = "";

    public int getSelectedResourceId() {
        return selectedResourceId;
    }

    public void setSelectedResourceId(int selectedResourceId) {
        this.selectedResourceId = selectedResourceId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getDeselectedResourceId() {
        return deselectedResourceId;
    }

    public void setDeselectedResourceId(int deselectedResourceId) {
        this.deselectedResourceId = deselectedResourceId;
    }
}
