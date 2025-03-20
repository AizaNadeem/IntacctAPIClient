package com.intacct.xtera.px;

import java.io.IOException;
import java.util.List;

import com.agile.api.APIException;
import com.agile.api.IAgileSession;
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
		try {
			iAgileSession.disableAllWarnings();
			IDataObject change = ((IWFChangeStatusEventInfo) iEventInfo).getDataObject();
			List<IDataObject> itemList = appHandler.getItemList(change);
			if(!itemList.isEmpty()) {
				List<Item> items = appHandler.getItemData(itemList);
				app.exportFromAgileToIntacct(items);
			} else {
				rs = new ActionResult(ActionResult.STRING, "No items found");
			}
		} catch (APIException | RuntimeException | IOException e) {
			System.out.println(e);
			rs = new ActionResult(ActionResult.EXCEPTION, e);
		}
		return new EventActionResult(iEventInfo, rs);
	}
}
