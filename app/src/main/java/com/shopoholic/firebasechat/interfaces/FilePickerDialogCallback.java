package com.shopoholic.firebasechat.interfaces;

/**
 * this interface is used to get the callback from the dialog
 */
public interface FilePickerDialogCallback {
    void onFilesSelection();
    void onPhotosSelection();
    void onVideosSelection();
    void onLocationSelection();
}
