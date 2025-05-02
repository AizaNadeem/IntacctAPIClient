package com.intacct.xtera.utils;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLUtils {
    
    public static String escapeXML(String input) {
        if (input == null) {
            return null;
        }
        
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
    }
    
    public static String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0 && nodes.item(0).getFirstChild() != null) {
            return nodes.item(0).getFirstChild().getNodeValue().trim();
        }
        return "";
    }

    public static String cleanDescription(String description) {
        if (description == null || description.isEmpty()) {
            return "";
        }
        int idx = description.indexOf(" [");
        return idx != -1 ? description.substring(0, idx).trim() : description;
    }

    public static String getTagValue(String tag, Element element) {
		NodeList nodeList = element.getElementsByTagName(tag);
		if (nodeList.getLength() > 0) {
			return nodeList.item(0).getTextContent();
		}
		return "";
	}

}