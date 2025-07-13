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
import com.intacct.xtera.utils.XMLUtils;


public class ItemService {

	public static String getItem(String sessionId, String itemId) throws Exception {
		XmlRequestBuilder xmlBuilder = new XmlRequestBuilder();
		String xmlRequest = xmlBuilder.getItemRequest(sessionId, itemId);
		return IntacctApiClient.sendPostRequest(xmlRequest);
	}

	public static String getVendor(String sessionId, String vendorId) throws Exception {
		XmlRequestBuilder xmlBuilder = new XmlRequestBuilder();
		String xmlRequest = xmlBuilder.getVendorRequest(sessionId, vendorId);
		return IntacctApiClient.sendPostRequest(xmlRequest);
	}

	public static String createItem(String sessionId, String itemId, String name, String productLines) throws Exception {
		XmlRequestBuilder xmlBuilder = new XmlRequestBuilder();
		String xmlRequest = xmlBuilder.createItemRequest(sessionId, itemId, name, productLines);
		return IntacctApiClient.sendPostRequest(xmlRequest);
	}

	public static String updateItem(String sessionId, String recordNo, String itemId, String name) throws Exception {
		XmlRequestBuilder xmlBuilder = new XmlRequestBuilder();
		String xmlRequest = xmlBuilder.updateItemRequest(sessionId, recordNo, itemId, name);
		return IntacctApiClient.sendPostRequest(xmlRequest);
	}

	public static String createItemCrossReference(String sessionId, String itemId, String vendorId, String itemAliasId, String itemAliasDesc) throws Exception {
		XmlRequestBuilder xmlBuilder = new XmlRequestBuilder();
		String xmlRequest = xmlBuilder.createItemCrossreference(sessionId, itemId, vendorId, itemAliasId, itemAliasDesc);
		return IntacctApiClient.sendPostRequest(xmlRequest);
	}

	public static String updateItemCrossReference(String sessionId, String recordNo, String itemAliasDesc) throws Exception {
		XmlRequestBuilder xmlBuilder = new XmlRequestBuilder();
		String xmlRequest = xmlBuilder.updateItemCrossreference(sessionId, recordNo, itemAliasDesc);
		return IntacctApiClient.sendPostRequest(xmlRequest);
	}

	public static String getRecordNoFromResponse(String xmlResponse) throws Exception {
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
		return null;
	}

	public static Map<String, Boolean> parseVendorResponse(String vendorNames, String xmlResponse) throws Exception {
		Map<String, Boolean> resultMap = new HashMap<>();
		Set<String> requestedVendors = new HashSet<>(Arrays.asList(vendorNames.split(",")));
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
						resultMap.put(vendor, false);
					}
					return resultMap;
				}
			}
			NodeList vendorList = document.getElementsByTagName("VENDOR");
			for (int i = 0; i < vendorList.getLength(); i++) {
				Element vendor = (Element) vendorList.item(i);
				//String vendorName = getTagValue("NAME", vendor);
				String vendorId = XMLUtils.getTagValue("VENDORID", vendor);
				if (requestedVendors.contains(vendorId)) {
					resultMap.put(vendorId, true);
				}
			}
			for (String vendor : requestedVendors) {
				resultMap.putIfAbsent(vendor, false);
			}
		} catch (Exception e) {
			for (String vendor : requestedVendors) {
				resultMap.put(vendor, false);
			}
			throw new Exception(e);

		}
		return resultMap;
	}
	
    public static String getFirstFailureReason(String xmlResponse) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes("UTF-8")));
            doc.getDocumentElement().normalize();

            NodeList resultNodes = doc.getElementsByTagName("result");
            if (resultNodes.getLength() > 0) {
                Element result = (Element) resultNodes.item(0);
                String status = XMLUtils.getElementText(result, "status");
                if ("failure".equalsIgnoreCase(status)) {
                    NodeList errorNodes = result.getElementsByTagName("error");
                    for (int i = 0; i < errorNodes.getLength(); i++) {
                        Element error = (Element) errorNodes.item(i);
                        String description2 = XMLUtils.cleanDescription(XMLUtils.getElementText(error, "description2"));
                        if (!description2.isEmpty()) {
                            return description2;
                        }
                    }
                }
            }

            NodeList errorNodes = doc.getElementsByTagName("error");
            for (int i = 0; i < errorNodes.getLength(); i++) {
                Element error = (Element) errorNodes.item(i);
                String description2 = XMLUtils.cleanDescription(XMLUtils.getElementText(error, "description2"));
                if (!description2.isEmpty()) {
                    return description2;
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            return "XML parsing error: " + e.getMessage();
        }

        return "";
    }

	public static Map<String, String> getVendorRecordMap(String xmlResponse, List<ItemCrossReference> vendorItemList) throws Exception {
		Map<String, String> resultMap = new HashMap<>();
		for (ItemCrossReference item : vendorItemList) {
			if (!item.getVendorID().isEmpty())
				resultMap.put(item.getVendorID(), "");
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream inputStream = new ByteArrayInputStream(xmlResponse.getBytes("UTF-8"));
			Document document = builder.parse(inputStream);
			document.getDocumentElement().normalize();

			NodeList nodeList = document.getElementsByTagName("itemcrossref");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;

					String vendorID = element.getElementsByTagName("VENDORID").item(0).getTextContent();
					String itemAliasId = element.getElementsByTagName("ITEMALIASID").item(0).getTextContent();
					String recordId = element.getElementsByTagName("RECORDNO").item(0).getTextContent();

					for (ItemCrossReference item : vendorItemList) {
						if (!item.getVendorID().isEmpty() && item.getVendorID().equals(vendorID) && item.getId().equals(itemAliasId)) {
							resultMap.put(item.getVendorID(), recordId);
							break;
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
