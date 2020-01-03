package events.com.socketlibrary.interfaces;


import com.dnitinverma.mylibrary.models.ContactResult;

import org.json.JSONObject;

import java.util.ArrayList;



/**
 * Created by Vivek on 27/6/17.
 */
public interface ResponseListener {
    void contactsSyncResponse(JSONObject jsonObject, ArrayList<ContactResult> contactResults);
    //void contactsEditResponse(JSONObject jsonObject,List<ContactResult> contactResults);
    void error(String message);
}
