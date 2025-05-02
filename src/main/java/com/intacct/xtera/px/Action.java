package com.intacct.xtera.px;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	            Map<String, List<String>> itemVendorMap = app.exportFromAgileToIntacct(items, failedItemMap);
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

	private String buildIntegrationComment(String changeName, Map<String, List<String>> itemVendorMap, Map<String, String> failedItemMap) {
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
	    boolean hasMissingVendors = itemVendorMap.values().stream().anyMatch(list -> !list.isEmpty());
	    if (hasMissingVendors) {
	        commentBuilder.append("\nThe following vendor(s) do not exist in Sage:\n\n");
	        for (Map.Entry<String, List<String>> entry : itemVendorMap.entrySet()) {
	            if (!entry.getValue().isEmpty()) {
	                commentBuilder.append("- ").append(entry.getKey()).append(":\n");
	                for (String vendor : entry.getValue()) {
	                    commentBuilder.append("     - ").append(vendor).append("\n");
	                }
	                commentBuilder.append("\n");
	            }
	        }
	        commentBuilder.append("Please review and take the necessary action.\n");
	    }
	    return commentBuilder.toString();
	}
}