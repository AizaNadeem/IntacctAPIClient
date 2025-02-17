package com.intacct.xtera;

import java.io.IOException;

import com.intacct.xtera.api.ItemService;
import com.intacct.xtera.api.SessionService;

public class App {
    public static void main(String[] args) throws IOException {
    	String sessionId = SessionService.getSessionId();
        if (sessionId != null) {
            System.out.println("Session ID: " + sessionId);
            String itemResponse = ItemService.getItem(sessionId, "170-4150-004");
            System.out.println(itemResponse);
            String recordNo = ItemService.getRecordNoFromResponse(itemResponse);
            System.out.println("Updating Item: " + ItemService.updateItem(sessionId, recordNo, "RMM OADM 26 CH BAND 2AB (OF26-2AB) /"));

            //System.out.println("Creating Item: " + ItemService.createItem(sessionId, "I1", "hello world", "Inventory"));
            
        }
    }
}

