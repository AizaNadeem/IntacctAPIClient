package com.intacct.xtera.utils;

import com.intacct.xtera.config.ApiConstants;

public class XmlRequestBuilder {

    public static String getSessionRequest() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<request>"
                + "<control>"
                + "<senderid>" + ApiConstants.SENDER_ID + "</senderid>"
                + "<password>" + ApiConstants.SENDER_PASSWORD + "</password>"
                + "<controlid>test123</controlid>"
                + "<uniqueid>false</uniqueid>"
                + "<dtdversion>3.0</dtdversion>"
                + "<includewhitespace>false</includewhitespace>"
                + "</control>"
                + "<operation>"
                + "<authentication>"
                + "<login>"
                + "<userid>" + ApiConstants.USER_ID + "</userid>"
                + "<companyid>" + ApiConstants.COMPANY_ID + "</companyid>"
                + "<password>" + ApiConstants.USER_PASSWORD + "</password>"
                + "</login>"
                + "</authentication>"
                + "<content>"
                + "<function controlid=\"test123\">"
                + "<getAPISession />"
                + "</function>"
                + "</content>"
                + "</operation>"
                + "</request>";
    }
    
    public static String getItemRequest(String sessionId, String itemId) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<request>"
                + "<control>"
                + "<senderid>" + ApiConstants.SENDER_ID + "</senderid>"
                + "<password>" + ApiConstants.SENDER_PASSWORD + "</password>"
                + "<controlid>getItemRequest</controlid>"
                + "<uniqueid>false</uniqueid>"
                + "<dtdversion>3.0</dtdversion>"
                + "<includewhitespace>false</includewhitespace>"
                + "</control>"
                + "<operation>"
                + "<authentication>"
                + "<sessionid>" + sessionId + "</sessionid>"
                + "</authentication>"
                + "<content>"
                + "<function controlid=\"getItem\">"
                + "<readByName>"
                + "<object>ITEM</object>"
                + "<keys>" + itemId + "</keys>"
                + "<fields>*</fields>"
                + "</readByName>"
                + "</function>"
                + "</content>"
                + "</operation>"
                + "</request>";
    }

    public static String createItemRequest(String sessionId, String itemId, String name, String itemType) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<request>"
                + "<control>"
                + "<senderid>" + ApiConstants.SENDER_ID + "</senderid>"
                + "<password>" + ApiConstants.SENDER_PASSWORD + "</password>"
                + "<controlid>createItemRequest</controlid>"
                + "</control>"
                + "<operation>"
                + "<authentication><sessionid>" + sessionId + "</sessionid></authentication>"
                + "<content>"
                + "<function controlid=\"createItem\">"
                + "<create>"
                + "<ITEM>"
                + "<ITEMID>" + itemId + "</ITEMID>"
                + "<NAME>" + name + "</NAME>"
                + "<ITEMTYPE>" + itemType + "</ITEMTYPE>"
                + "</ITEM>"
                + "</create>"
                + "</function>"
                + "</content>"
                + "</operation>"
                + "</request>";
    }

    public static String updateItemRequest(String sessionId, String recordNo, String name) {
        String controlId = "updateItemRequest_" + System.currentTimeMillis();
        
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<request>"
                + "<control>"
                + "<senderid>" + ApiConstants.SENDER_ID + "</senderid>"
                + "<password>" + ApiConstants.SENDER_PASSWORD + "</password>"
                + "<controlid>" + controlId + "</controlid>"
                + "<uniqueid>false</uniqueid>"
                + "<dtdversion>3.0</dtdversion>"
                + "<includewhitespace>false</includewhitespace>"
                + "</control>"
                + "<operation>"
                + "<authentication>"
                + "<sessionid>" + sessionId + "</sessionid>"
                + "</authentication>"
                + "<content>"
                + "<function controlid=\"updateItem\">"
                + "<update>"
                + "<ITEM>"
                + "<RECORDNO>" + recordNo + "</RECORDNO>"
                + "<NAME>" + name + "</NAME>"
                + "</ITEM>"
                + "</update>"
                + "</function>"
                + "</content>"
                + "</operation>"
                + "</request>";
    }

}
