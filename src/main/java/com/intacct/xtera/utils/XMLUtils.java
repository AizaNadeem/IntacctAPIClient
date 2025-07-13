package com.intacct.xtera.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLUtils {
	
	private static final Map<String, String> agileToSageMap = createMap();

    private static Map<String, String> createMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Nu-Wave CXR", "CXR XLS");
        map.put("Nu-Wave NXT", "NXT");
        map.put("Nu-Wave Optima", "Optima");
        map.put("Common", "Cable and Accessories");
        map.put("Nu-Wave ES", "Channel Cards");
        map.put("Nu-Wave XLS", "Repeater");
        map.put("Submerged- SUB", "Branching Unit");
        return map;
    }
    
    public static String getSageProductLine(String agileProductLine) {
        if (agileProductLine == null || agileProductLine.trim().isEmpty()) {
            return null;
        }
        String[] parts = agileProductLine.split(";");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                return agileToSageMap.getOrDefault(trimmed, agileProductLine);
            }
        }
        return agileProductLine;
    }

    
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
        description = StringEscapeUtils.unescapeHtml4(description);
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