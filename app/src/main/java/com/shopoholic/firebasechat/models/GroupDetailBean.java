package com.shopoholic.firebasechat.models;

import java.util.List;

/**
 * Class contain group details
 */

public class GroupDetailBean {
    private String groupRoomId;
    private String groupImage;
    private String groupName;
    private List<UserBean> groupMembers;

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<UserBean> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<UserBean> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getGroupRoomId() {
        return groupRoomId;
    }

    public void setGroupRoomId(String groupRoomId) {
        this.groupRoomId = groupRoomId;
    }
}
