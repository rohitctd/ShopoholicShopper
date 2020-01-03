package com.dnitinverma.mylibrary.interfaces;

import com.dnitinverma.mylibrary.models.ContactResult;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vivek on 27/6/17.
 */
public interface ContactUpdationListener {
    void showLocalContacts(ArrayList<ContactResult> allContacts);
    void allContacts(ArrayList<ContactResult> allContacts);
    void editedContacts(ArrayList<ContactResult> updatedContacts);
    void deletedContacts(ArrayList<ContactResult> deletedContacts);
    void newlyAddedContacts(ArrayList<ContactResult> newlyAddedContacts);
    void nonSynchedContacts(ArrayList<ContactResult> nonSynchedContacts);
}
