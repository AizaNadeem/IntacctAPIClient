package com.intacct.xtera.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.intacct.xtera.utils.XmlRequestBuilder;

public class ItemService {

    public static String getItem(String sessionId, String itemId) throws IOException {
        String xmlRequest = XmlRequestBuilder.getItemRequest(sessionId, itemId);
        return IntacctApiClient.sendPostRequest(xmlRequest);
    }

    public static String createItem(String sessionId, String itemId, String name, String itemType) throws IOException {
        String xmlRequest = XmlRequestBuilder.createItemRequest(sessionId, itemId, name, itemType);
        return IntacctApiClient.sendPostRequest(xmlRequest);
    }

    public static String updateItem(String sessionId, String recordNo, String name) throws IOException {
        String xmlRequest = XmlRequestBuilder.updateItemRequest(sessionId, recordNo, name);
        return IntacctApiClient.sendPostRequest(xmlRequest);
    }
    
    public static String getRecordNoFromResponse(String xmlResponse) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputStream = new ByteArrayInputStream(xmlResponse.getBytes("UTF-8"));
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("RECORDNO");
            if (nodeList.getLength() > 0) {
                Element itemElement = (Element) nodeList.item(0);
                return itemElement.getTextContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
