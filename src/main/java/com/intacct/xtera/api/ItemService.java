package com.intacct.xtera.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.intacct.xtera.model.ItemCrossReference;
import com.intacct.xtera.utils.XmlRequestBuilder;

public class ItemService {

    public static String getItem(String sessionId, String itemId) throws IOException {
        String xmlRequest = XmlRequestBuilder.getItemRequest(sessionId, itemId);
        return IntacctApiClient.sendPostRequest(xmlRequest);
    }
    
    public static String getVendor(String sessionId, String vendorId) throws IOException {
        String xmlRequest = XmlRequestBuilder.getVendorRequest(sessionId, vendorId);
        return IntacctApiClient.sendPostRequest(xmlRequest);
    }

    public static String createItem(String sessionId, String itemId, String name) throws IOException {
        String xmlRequest = XmlRequestBuilder.createItemRequest(sessionId, itemId, name);
        return IntacctApiClient.sendPostRequest(xmlRequest);
    }

    public static String updateItem(String sessionId, String recordNo, String name) throws IOException {
        String xmlRequest = XmlRequestBuilder.updateItemRequest(sessionId, recordNo, name);
        return IntacctApiClient.sendPostRequest(xmlRequest);
    }
    
    public static String createItemCrossReference(String sessionId, String itemId, String vendorId, String itemAliasId, String itemAliasDesc) throws IOException {
        String xmlRequest = XmlRequestBuilder.createItemCrossreference(sessionId, itemId, vendorId, itemAliasId, itemAliasDesc);
        return IntacctApiClient.sendPostRequest(xmlRequest);
    }

    public static String updateItemCrossReference(String sessionId, String recordNo, String itemAliasDesc) throws IOException {
        String xmlRequest = XmlRequestBuilder.updateItemCrossreference(sessionId, recordNo, itemAliasDesc);
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
    
    public static Map<String, String> parseVendorResponse(String vendorNames, String xmlResponse) {
        Map<String, String> resultMap = new HashMap<>();
        Set<String> requestedVendors = new HashSet<>(Arrays.asList(vendorNames.split(",")));
        Set<String> foundVendors = new HashSet<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputStream = new ByteArrayInputStream(xmlResponse.getBytes("UTF-8"));
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList statusList = document.getElementsByTagName("status");
            if (statusList.getLength() > 1) {
                String status = statusList.item(1).getTextContent();
                if (!"success".equalsIgnoreCase(status)) {
                    for (String vendor : requestedVendors) {
                        resultMap.put(vendor, "");
                    }
                    return resultMap;
                }
            }

            NodeList vendorList = document.getElementsByTagName("vendor");
            for (int i = 0; i < vendorList.getLength(); i++) {
                Element vendor = (Element) vendorList.item(i);
                String vendorName = getTagValue("NAME", vendor);
                String vendorId = getTagValue("VENDORID", vendor);
                if (requestedVendors.contains(vendorName)) {
                    resultMap.put(vendorName, vendorId);
                }
            }

            for (String vendor : requestedVendors) {
                resultMap.putIfAbsent(vendor, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            for (String vendor : requestedVendors) {
                resultMap.put(vendor, "");
            }
        }

        return resultMap;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
    
    public static Map<String, String> getVendorRecordMap(String xmlResponse, List<ItemCrossReference> vendorItemList) {
        Map<String, String> resultMap = new HashMap<>();
        for (ItemCrossReference item : vendorItemList) {
            resultMap.put(item.getVendor(), ""); // Default empty record ID
        }

        try {
            // Parse XML
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputStream = new ByteArrayInputStream(xmlResponse.getBytes("UTF-8"));
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Get all ITEMCROSSREFERENCE nodes
            NodeList nodeList = document.getElementsByTagName("itemcrossref");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // Extract vendor name, item alias ID, and record ID
                    String vendorName = element.getElementsByTagName("VENDORNAME").item(0).getTextContent();
                    String itemAliasId = element.getElementsByTagName("ITEMALIASID").item(0).getTextContent();
                    String recordId = element.getElementsByTagName("RECORDNO").item(0).getTextContent();

                    // Check if any item in vendorItemList matches vendorName and itemAliasId
                    for (ItemCrossReference item : vendorItemList) {
                        if (item.getVendor().equals(vendorName) && item.getId().equals(itemAliasId)) {
                            resultMap.put(item.getVendor(), recordId); // Update map with record ID
                            break; // Stop checking once we find a match
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return resultMap;
    }
}
