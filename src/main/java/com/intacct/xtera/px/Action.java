package com.intacct.xtera.px;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.agile.api.APIException;
import com.agile.api.IAgileSession;
import com.agile.api.IChange;
import com.agile.api.IDataObject;
import com.agile.api.INode;
import com.agile.px.ActionResult;
import com.agile.px.EventActionResult;
import com.agile.px.IEventAction;
import com.agile.px.IEventInfo;
import com.agile.px.IWFChangeStatusEventInfo;
import com.intacct.xtera.App;
import com.intacct.xtera.handler.AppHandler;
import com.intacct.xtera.model.Item;

public class Action implements IEventAction {
	@Override
	public EventActionResult doAction(IAgileSession iAgileSession, INode iNode, IEventInfo iEventInfo) {
	    ActionResult rs = null;
	    AppHandler appHandler = new AppHandler();
	    App app = new App();
	    IDataObject change = null;
	    try {
	        iAgileSession.disableAllWarnings();
	        change = ((IWFChangeStatusEventInfo) iEventInfo).getDataObject();
	        List<IDataObject> itemList = appHandler.getItemList(change);
	        if (!itemList.isEmpty()) {
	            List<Item> items = appHandler.getItemData(itemList);
	            Map<String, String> failedItemMap = new HashMap<>();
	            Map<String, Map<String, String>> itemVendorMap = app.exportFromAgileToIntacct(items, failedItemMap);
	            String comment = buildIntegrationComment(change.getName(), itemVendorMap, failedItemMap);
	            appHandler.sendNotification((IChange) change, comment);
	            rs = new ActionResult(ActionResult.STRING, "Integration Completed");
	        } else {
	            rs = new ActionResult(ActionResult.STRING, "No items found");
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	        rs = new ActionResult(ActionResult.EXCEPTION, e);
	        try {
	            String comment = "Hi,\n\nThe integration from Agile PLM to Sage has failed due to an unexpected error.\n\nError Details:\n- "
	                    + e.getMessage();
	            appHandler.sendNotification((IChange) change, comment);
	        } catch (APIException e1) {
	            e1.printStackTrace();
	        }
	    }
	    return new EventActionResult(iEventInfo, rs);
	}

	private String buildIntegrationComment(
		    String changeName,
		    Map<String, Map<String, String>> itemVendorMap,
		    Map<String, String> failedItemMap) {
		    StringBuilder commentBuilder = new StringBuilder();
		    commentBuilder.append("Hi,\n\n");
		    commentBuilder.append("The integration from Agile PLM to Sage has been successfully completed for ")
		                  .append(changeName).append(".\n");
		    if (!failedItemMap.isEmpty()) {
		        commentBuilder.append("\nHowever, the following item(s) could not be created in Sage due to the respective reason(s):\n\n");
		        for (Map.Entry<String, String> entry : failedItemMap.entrySet()) {
		            commentBuilder.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		        }
		        commentBuilder.append("\n");
		    }
		    
		    boolean hasVendorIssues = itemVendorMap.values().stream()
		        .flatMap(vendorMap -> vendorMap.values().stream())
		        .anyMatch(issue -> issue != null && !issue.trim().isEmpty());
		    if (hasVendorIssues) {
		        commentBuilder.append("The cross reference(s) for the following vendors could not be created in Sage due to the respective reason(s):\n\n");
		        for (Map.Entry<String, Map<String, String>> itemEntry : itemVendorMap.entrySet()) {
		            String item = itemEntry.getKey();
		            Map<String, String> vendorIssues = itemEntry.getValue();
		            Map<String, String> filteredVendors = vendorIssues.entrySet().stream()
		                .filter(entry -> entry.getValue() != null && !entry.getValue().trim().isEmpty())
		                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		            if (!filteredVendors.isEmpty()) {
		                commentBuilder.append("- ").append(item).append(":\n");
		                for (Map.Entry<String, String> vendorEntry : filteredVendors.entrySet()) {
		                    commentBuilder.append("     - ").append(vendorEntry.getKey()).append(": ")
		                                  .append(vendorEntry.getValue()).append("\n");
		                }
		                commentBuilder.append("\n");
		            }
		        }
		        commentBuilder.append("Please review and take the necessary action.\n");
		    }
		    return commentBuilder.toString();
		}
}