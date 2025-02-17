package com.intacct.xtera.api;

import com.intacct.xtera.utils.XmlRequestBuilder;
import java.io.IOException;

public class SessionService {

    public static String getSessionId() throws IOException {
        String xmlRequest = XmlRequestBuilder.getSessionRequest();
        String response = IntacctApiClient.sendPostRequest(xmlRequest);

        if (response.contains("<sessionid>")) {
            return response.split("<sessionid>")[1].split("</sessionid>")[0];
        }
        return null;
    }
}
