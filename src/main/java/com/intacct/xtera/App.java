package com.intacct.xtera;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.intacct.xtera.api.ItemService;
import com.intacct.xtera.api.SessionService;
import com.intacct.xtera.model.Item;
import com.intacct.xtera.model.ItemCrossReference;

public class App {
	public Map<String, List<String>> exportFromAgileToIntacct(List<Item> items, Map<String, String> failedItemMap) throws Exception {
	    String sessionId = SessionService.getSessionId();
        System.out.println("Session ID: " + sessionId);
	    Map<String, List<String>> itemVendorMap = new HashMap<>();
	    for (Item item : items) {
	        if (sessionId != null) {
	            List<String> vendors;
	            List<ItemCrossReference> itemCrossReferences = item.getManufacturers();
	            String itemResponse = ItemService.getItem(sessionId, item.getId());
	            System.out.println(itemResponse);
	            String recordNo = ItemService.getRecordNoFromResponse(itemResponse);
	            if (recordNo != null) {
	                System.out.println("Updating Item: " + ItemService.updateItem(sessionId, recordNo, item.getDesc()));
	                Map<String, String> isVendorRecordExists = ItemService.getVendorRecordMap(itemResponse, itemCrossReferences);

	                for (ItemCrossReference crossReference : itemCrossReferences) {
	                    if (!crossReference.getVendorID().isEmpty()
	                            && isVendorRecordExists.containsKey(crossReference.getVendorID())
	                            && !isVendorRecordExists.get(crossReference.getVendorID()).isEmpty()) {
	                        ItemService.updateItemCrossReference(sessionId, isVendorRecordExists.get(crossReference.getVendorID()), crossReference.getDesc());
	                    }
	                }
	                String vendorsWithEmptyRecords = isVendorRecordExists.entrySet().stream()
	                        .filter(entry -> entry.getValue().isEmpty())
	                        .map(Map.Entry::getKey)
	                        .collect(Collectors.joining(","));
	                if (!vendorsWithEmptyRecords.isEmpty()) {
	                    String vendorResponse = ItemService.getVendor(sessionId, vendorsWithEmptyRecords);
	                    Map<String, Boolean> vendorsExists = ItemService.parseVendorResponse(vendorsWithEmptyRecords, vendorResponse);

	                    for (ItemCrossReference crossReference : itemCrossReferences) {
	                        if (!crossReference.getVendorID().isEmpty()
	                                && vendorsExists.containsKey(crossReference.getVendorID())
	                                && vendorsExists.get(crossReference.getVendorID())) {
	                            System.out.println(ItemService.createItemCrossReference(sessionId, item.getId(), crossReference.getVendorID(), crossReference.getId(), crossReference.getDesc()));
	                        }
	                    }
	                    vendors = getInvalidVendorNames(itemCrossReferences, vendorsExists);
	                } else {
	                    vendors = getInvalidVendorNames(itemCrossReferences, Collections.emptyMap());
	                }
	                itemVendorMap.put(item.getId(), vendors);
	            } else {
	            	String itemCreationResponse = ItemService.createItem(sessionId, item.getId(), item.getDesc());
	            	System.out.println("Creating Item: " + itemCreationResponse);
	            	String reason = ItemService.getFirstFailureReason(itemCreationResponse);
	                if (!reason.isEmpty()) {
	                    System.out.println("Failure: " + reason);
	                    failedItemMap.put(item.getId(), reason);
	                    continue;
	                } else {
	                    System.out.println("Success.");
	                }
	                String vendorNames = itemCrossReferences.stream()
	                        .map(ItemCrossReference::getVendorID)
	                        .filter(v -> v != null && !v.isEmpty())
	                        .collect(Collectors.joining(","));

	                String vendorResponse = ItemService.getVendor(sessionId, vendorNames);
	                System.out.println(vendorResponse);
	                Map<String, Boolean> vendorsExists = ItemService.parseVendorResponse(vendorNames, vendorResponse);

	                for (ItemCrossReference crossReference : itemCrossReferences) {
	                    if (!crossReference.getVendorID().isEmpty()
	                            && vendorsExists.containsKey(crossReference.getVendorID())
	                            && vendorsExists.get(crossReference.getVendorID())) {
	                    	System.out.println(ItemService.createItemCrossReference(sessionId, item.getId(), crossReference.getVendorID(), crossReference.getId(), crossReference.getDesc()));
	                    }
	                }
	                vendors = getInvalidVendorNames(itemCrossReferences, vendorsExists);
	                itemVendorMap.put(item.getId(), vendors);
	            }
	        }
	    }
	    return itemVendorMap;
	}
	
	public static List<String> getInvalidVendorNames(List<ItemCrossReference> itemCrossReferences, Map<String, Boolean> vendorsExists) {
		return itemCrossReferences.stream()
				.filter(crossReference ->
				crossReference.getVendorID().isEmpty() ||
				(vendorsExists.containsKey(crossReference.getVendorID()) &&
						!vendorsExists.get(crossReference.getVendorID()))
						)
				.map(ItemCrossReference::getVendorName)
				.collect(Collectors.toList());
	}

}

