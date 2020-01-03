
package com.shopoholic.models.versionupdateresponse;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result implements Serializable
{

    @SerializedName("vid")
    @Expose
    private String vid;
    @SerializedName("version_name")
    @Expose
    private String versionName;
    @SerializedName("versiob_title")
    @Expose
    private String versiobTitle;
    @SerializedName("version_desc")
    @Expose
    private String versionDesc;
    @SerializedName("platform")
    @Expose
    private String platform;
    @SerializedName("app_type")
    @Expose
    private String appType;
    @SerializedName("update_type")
    @Expose
    private String updateType;
    @SerializedName("is_cur_version")
    @Expose
    private String isCurVersion;
    @SerializedName("create_date")
    @Expose
    private String createDate;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersiobTitle() {
        return versiobTitle;
    }

    public void setVersiobTitle(String versiobTitle) {
        this.versiobTitle = versiobTitle;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String getIsCurVersion() {
        return isCurVersion;
    }

    public void setIsCurVersion(String isCurVersion) {
        this.isCurVersion = isCurVersion;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

}
