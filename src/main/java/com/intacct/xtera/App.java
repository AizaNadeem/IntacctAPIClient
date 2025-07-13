package com.intacct.xtera;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.intacct.xtera.api.ItemService;
import com.intacct.xtera.api.SessionService;
import com.intacct.xtera.config.ApiConstants;
import com.intacct.xtera.model.Item;
import com.intacct.xtera.model.ItemCrossReference;

public class App {
	public Map<String, Map<String, String>> exportFromAgileToIntacct(List<Item> items, Map<String, String> failedItemMap) throws Exception {
		String sessionId = SessionService.getSessionId();
		System.out.println("Session ID: " + sessionId);
		Map<String, Map<String, String>> itemVendorMap = new HashMap<>();
		for (Item item : items) {
			if (sessionId != null) {
				Map<String, String> failedVendorMap = new HashMap<>();
				List<ItemCrossReference> itemCrossReferences = item.getManufacturers();
				String itemResponse = ItemService.getItem(sessionId, item.getId());
				System.out.println(itemResponse);
				String recordNo = ItemService.getRecordNoFromResponse(itemResponse);
				if (recordNo != null) {
					System.out.println("Updating Item: " + ItemService.updateItem(sessionId, recordNo, item.getId(), item.getDesc()));
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
					Map<String, Boolean> vendorsExists = Collections.emptyMap();
					if (!vendorsWithEmptyRecords.isEmpty()) {
						String vendorResponse = ItemService.getVendor(sessionId, vendorsWithEmptyRecords);
						vendorsExists = ItemService.parseVendorResponse(vendorsWithEmptyRecords, vendorResponse);
					}
					for (ItemCrossReference crossReference : itemCrossReferences) {
						String vendorId = crossReference.getVendorID();
						if(vendorId.isEmpty() || vendorsExists.containsKey(vendorId)) {
							boolean hasValidVendor = !vendorId.isEmpty() && vendorsExists.get(vendorId);
							String resolvedVendorId = hasValidVendor ? vendorId : ApiConstants.GENERIC_VENDOR_ID;
							String itemCrossRefCreateRes = ItemService.createItemCrossReference(sessionId, item.getId(), resolvedVendorId, crossReference.getId(), crossReference.getDesc());
							System.out.println("Creating Item Cross Reference: " + itemCrossRefCreateRes);
							String reason = ItemService.getFirstFailureReason(itemCrossRefCreateRes);
							if (!reason.isEmpty()) {
								System.out.println("Failure: " + reason);
								failedVendorMap.put(crossReference.getVendorName(), reason);
								continue;
							}
							System.out.println("Success.");
						}
					}
					//vendors = getInvalidVendorNames(itemCrossReferences, vendorsExists);
					itemVendorMap.put(item.getId(), failedVendorMap);
				} else {
					String itemCreationResponse = ItemService.createItem(sessionId, item.getId(), item.getDesc(), item.getProductLines());
					System.out.println("Creating Item: " + itemCreationResponse);
					String reason = ItemService.getFirstFailureReason(itemCreationResponse);
					if (!reason.isEmpty()) {
						System.out.println("Failure: " + reason);
						failedItemMap.put(item.getId(), reason);
						continue;
					}
					System.out.println("Success.");
					String vendorNames = itemCrossReferences.stream()
							.map(ItemCrossReference::getVendorID)
							.filter(v -> v != null && !v.isEmpty())
							.collect(Collectors.joining(","));

					String vendorResponse = ItemService.getVendor(sessionId, vendorNames);
					System.out.println(vendorResponse);
					Map<String, Boolean> vendorsExists = ItemService.parseVendorResponse(vendorNames, vendorResponse);

					for (ItemCrossReference crossReference : itemCrossReferences) {
						String vendorId = crossReference.getVendorID();
						boolean vendorExists = !vendorId.isEmpty() && vendorsExists.getOrDefault(vendorId, false);
						String finalVendorId = vendorExists ? vendorId : ApiConstants.GENERIC_VENDOR_ID;
						String itemCrossRefCreateRes = ItemService.createItemCrossReference(
								sessionId, item.getId(), finalVendorId, crossReference.getId(), crossReference.getDesc());
						System.out.println("Creating Item Cross Reference: " + itemCrossRefCreateRes);
						String cause = ItemService.getFirstFailureReason(itemCrossRefCreateRes);
						if (!cause.isEmpty()) {
							System.out.println("Failure: " + cause);
							failedVendorMap.put(crossReference.getVendorName(), cause);
							continue;
						}
						System.out.println("Success.");
					}
					//vendors = getInvalidVendorNames(itemCrossReferences, vendorsExists);
					itemVendorMap.put(item.getId(), failedVendorMap);
				}
			}
		}
		return itemVendorMap;
	}

	//	public static List<String> getInvalidVendorNames(List<ItemCrossReference> itemCrossReferences, Map<String, Boolean> vendorsExists) {
	//		return itemCrossReferences.stream()
	//				.filter(crossReference ->
	//				vendorsExists.containsKey(crossReference.getVendorID()) &&
	//				!vendorsExists.get(crossReference.getVendorID())
	//						)
	//				.map(ItemCrossReference::getVendorName)
	//				.collect(Collectors.toList());
	//	}
}

