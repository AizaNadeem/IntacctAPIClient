package com.intacct.xtera;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.intacct.xtera.api.ItemService;
import com.intacct.xtera.api.SessionService;
import com.intacct.xtera.model.Item;
import com.intacct.xtera.model.ItemCrossReference;

public class App {
	public void exportFromAgileToIntacct (List<Item> items) throws IOException {
		String sessionId = SessionService.getSessionId();
		for(Item item: items) {
			if (sessionId != null) {
				System.out.println("Session ID: " + sessionId);
				List<ItemCrossReference> itemCrossReferences = item.getManufacturers();
				String itemResponse = ItemService.getItem(sessionId, item.getId());
				System.out.println(itemResponse);
				String recordNo = ItemService.getRecordNoFromResponse(itemResponse);
				if(recordNo != null) {
					System.out.println("Updating Item: " + ItemService.updateItem(sessionId, recordNo, item.getDesc()));
					Map<String, String> isVendorRecordExists =  ItemService.getVendorRecordMap(itemResponse, itemCrossReferences);
					for(ItemCrossReference crossReference: itemCrossReferences) {
						if (isVendorRecordExists.containsKey(crossReference.getVendor()) && !isVendorRecordExists.get(crossReference.getVendor()).isEmpty()) {
							ItemService.updateItemCrossReference(sessionId, isVendorRecordExists.get(crossReference.getVendor()), crossReference.getDesc());

						}
					}
					String vendorsWithEmptyRecords = isVendorRecordExists.entrySet().stream()
							.filter(entry -> entry.getValue().isEmpty())
							.map(Map.Entry::getKey)
							.collect(Collectors.joining(","));
					String vendorResponse = ItemService.getVendor(sessionId, vendorsWithEmptyRecords);
					Map<String, String> vendorsExists = ItemService.parseVendorResponse(vendorsWithEmptyRecords, vendorResponse);
					for(ItemCrossReference crossReference: itemCrossReferences) {
						if(vendorsExists.containsKey(crossReference.getVendor()) && !vendorsExists.get(crossReference.getVendor()).isEmpty()) {
							System.out.println(ItemService.createItemCrossReference(sessionId, item.getId(), vendorsExists.get(crossReference.getVendor()), crossReference.getId(), crossReference.getDesc()));
						}
					}
				} else {
					System.out.println("Creating Item: " + ItemService.createItem(sessionId, item.getId(), item.getDesc()));
					String vendorNames = itemCrossReferences.stream()
					        .map(ItemCrossReference::getVendor)
					        .collect(Collectors.joining(","));
					String vendorResponse = ItemService.getVendor(sessionId, vendorNames);
					Map<String, String> vendorsExists = ItemService.parseVendorResponse(vendorNames, vendorResponse);
					for(ItemCrossReference crossReference: itemCrossReferences) {
						if(vendorsExists.containsKey(crossReference.getVendor()) && !vendorsExists.get(crossReference.getVendor()).isEmpty()) {
							ItemService.createItemCrossReference(sessionId, item.getId(), vendorsExists.get(crossReference.getVendor()), crossReference.getId(), crossReference.getDesc());
						}
					}
				}
			}
		}
	}
}

