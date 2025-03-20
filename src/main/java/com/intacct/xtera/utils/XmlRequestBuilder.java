package com.intacct.xtera.utils;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import com.intacct.xtera.config.ApiConstants;

public class XmlRequestBuilder {

    public static String getSessionRequest() {
    	//String controlId = UUID.randomUUID().toString();
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
    	String controlId = UUID.randomUUID().toString();
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
                + "<function controlid=\"" + controlId + "\">"
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

    public static String createItemRequest(String sessionId, String itemId, String name) {
    	String controlId = UUID.randomUUID().toString();
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
                + "<function controlid=\"" + controlId + "\">"
                + "<create>"
                + "<ITEM>"
                + "<ITEMID>" + itemId + "</ITEMID>"
                + "<NAME>" + name + "</NAME>"
                + "<ITEMTYPE>Inventory</ITEMTYPE>"
                + "<TAXABLE>true</TAXABLE>"
                + "<TAXGROUP>"
                + "<NAME>Goods Standard Rate</NAME>"
                + "<TAXSOLUTION>"
                + "<SOLUTIONID>United Kingdom - VAT</SOLUTIONID>"
                + "</TAXSOLUTION>"
                + "<TAXSOLUTIONKEY>4</TAXSOLUTIONKEY>"
                + "</TAXGROUP>"
                + "<ENABLE_BINS>true</ENABLE_BINS>"
                + "<INV_PRECISION>6</INV_PRECISION>"
                + "<PO_PRECISION>6</PO_PRECISION>"
                + "<SO_PRECISION>6</SO_PRECISION>"
                + "</ITEM>"
                + "</create>"
                + "</function>"
                + "</content>"
                + "</operation>"
                + "</request>";
    }
    
    public static String createItemCrossreference(String sessionId, String itemId, String vendorId, String itemAliasId, String itemAliasDesc) {
        String controlId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<request>"
                + "<control>"
                + "<senderid>" + ApiConstants.SENDER_ID + "</senderid>"
                + "<password>" + ApiConstants.SENDER_PASSWORD + "</password>"
                + "<controlid>" + timestamp + "</controlid>"
                + "<uniqueid>false</uniqueid>"
                + "<dtdversion>3.0</dtdversion>"
                + "<includewhitespace>false</includewhitespace>"
                + "</control>"
                + "<operation>"
                + "<authentication>"
                + "<sessionid>" + sessionId + "</sessionid>"
                + "</authentication>"
                + "<content>"
                + "<function controlid=\"" + controlId + "\">"
                + "<create>"
                + "<ITEMCROSSREF>"
                + "<REFTYPE>Vendor</REFTYPE>"
                + "<ITEMID>" + itemId + "</ITEMID>"
                + "<VENDORID>" + vendorId + "</VENDORID>"
                + "<ITEMALIASID>" + itemAliasId + "</ITEMALIASID>"
                + "<ITEMALIASDESC>" + itemAliasDesc + "</ITEMALIASDESC>"
                + "</ITEMCROSSREF>"
                + "</create>"
                + "</function>"
                + "</content>"
                + "</operation>"
                + "</request>";
    }
    
    public static String updateItemCrossreference(String sessionId, String recordNo, String itemAliasDesc) {
        String controlId = UUID.randomUUID().toString(); // Generate unique control ID
        long timestamp = System.currentTimeMillis(); // Get current timestamp
        
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<request>"
                + "<control>"
                + "<senderid>" + ApiConstants.SENDER_ID + "</senderid>"
                + "<password>" + ApiConstants.SENDER_PASSWORD + "</password>"
                + "<controlid>" + timestamp + "</controlid>"
                + "<uniqueid>false</uniqueid>"
                + "<dtdversion>3.0</dtdversion>"
                + "<includewhitespace>false</includewhitespace>"
                + "</control>"
                + "<operation>"
                + "<authentication>"
                + "<sessionid>" + sessionId + "</sessionid>"
                + "</authentication>"
                + "<content>"
                + "<function controlid=\"" + controlId + "\">"
                + "<update>"
                + "<ITEMCROSSREF>"
                + "<RECORDNO>" + recordNo + "</RECORDNO>"
                + "<ITEMALIASDESC>" + itemAliasDesc + "</ITEMALIASDESC>"
                + "</ITEMCROSSREF>"
                + "</update>"
                + "</function>"
                + "</content>"
                + "</operation>"
                + "</request>";
    }

    public static String updateItemRequest(String sessionId, String recordNo, String name) {
        String controlId = UUID.randomUUID().toString();
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
    
    public static String getVendorRequest(String sessionId, String vendorNames) {
        String controlId = UUID.randomUUID().toString();
        String query = Arrays.stream(vendorNames.split(","))
                .map(name -> "NAME LIKE '%" + name.trim() + "%'")
                .collect(Collectors.joining(" OR "));

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<request>"
                + "<control>"
                + "<senderid>" + ApiConstants.SENDER_ID + "</senderid>"
                + "<password>" + ApiConstants.SENDER_PASSWORD + "</password>"
                + "<controlid>" + System.currentTimeMillis() + "</controlid>"
                + "<uniqueid>false</uniqueid>"
                + "<dtdversion>3.0</dtdversion>"
                + "<includewhitespace>false</includewhitespace>"
                + "</control>"
                + "<operation>"
                + "<authentication>"
                + "<sessionid>" + sessionId + "</sessionid>"
                + "</authentication>"
                + "<content>"
                + "<function controlid=\"" + controlId + "\">"
                + "<readByQuery>"
                + "<object>VENDOR</object>"
                + "<query>" + query + "</query>"
                + "<fields>*</fields>"
                + "</readByQuery>"
                + "</function>"
                + "</content>"
                + "</operation>"
                + "</request>";
    }

}
