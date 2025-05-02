package com.intacct.xtera.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.agile.api.APIException;
import com.agile.api.ChangeConstants;
import com.agile.api.IChange;
import com.agile.api.IDataObject;
import com.agile.api.IItem;
import com.agile.api.IAgileClass;
import com.agile.api.IManufacturer;
import com.agile.api.IRow;
import com.agile.api.ITable;
import com.agile.api.ItemConstants;
import com.intacct.xtera.model.Item;
import com.intacct.xtera.model.ItemCrossReference;

public class AppHandler {
	public List<IDataObject> getItemList (IDataObject change) throws APIException {
		System.out.println("Entering getDocumentList..");
    	ITable affItems = change.getTable(ChangeConstants.TABLE_AFFECTEDITEMS);
		List<IDataObject> itemList=new ArrayList<>();
		System.out.println("Traversing affected items for change:" + change.getName());
		Iterator iter = affItems.iterator();
		while (iter.hasNext()){
			IRow row = (IRow) iter.next();
			String itemName = row.getCell(ChangeConstants.ATT_AFFECTED_ITEMS_ITEM_NUMBER).toString();
			IDataObject item = (IDataObject) change.getSession().getObject(IItem.OBJECT_TYPE, itemName);
			System.out.println("Item: "+ item.getName());
			
			IAgileClass agileClass = item.getAgileClass();
			IAgileClass parentClass = agileClass.getSuperClass();

			if (parentClass != null && parentClass.getId().equals(ItemConstants.CLASS_PARTS_CLASS)) {
				itemList.add(item);
			}
//			if(ItemConstants.CLASS_PARTS_CLASS.equals(item.getAgileClass().getId())) {
//				itemList.add(item);
//			}
		}
		System.out.println("Exiting getDocumentList..");
		return itemList;
    } 
	
	public List<Item> getItemData (List<IDataObject> items) throws APIException {
		List<Item> itemsData = new ArrayList<>();
		for(IDataObject item: items) {
			String number = item.getValue(ItemConstants.ATT_TITLE_BLOCK_NUMBER).toString();
			String desc = item.getValue(ItemConstants.ATT_TITLE_BLOCK_DESCRIPTION).toString();
			ITable manufacturerTable = item.getTable(ItemConstants.TABLE_MANUFACTURERS);
			Iterator iter = manufacturerTable.iterator();
			List<ItemCrossReference> manufacturers = new ArrayList<>();
			while (iter.hasNext()){
				IRow row = (IRow) iter.next();		
				String mfrName = row.getCell(ItemConstants.ATT_MANUFACTURERS_MFR_NAME).toString();
				IDataObject manufacturer = (IDataObject) item.getSession().getObject(IManufacturer.OBJECT_TYPE, mfrName);
				Object vendorID = manufacturer.getCell(1301).getValue();
				String mfrID = "";
				if(vendorID != null) {
					mfrID = vendorID.toString();
				}
				String mfrPart = row.getCell(ItemConstants.ATT_MANUFACTURERS_MFR_PART_NUMBER).toString();
				String mfrDesc = row.getCell(ItemConstants.ATT_MANUFACTURERS_MFR_PART_DESCRIPTION).toString();
				manufacturers.add(new ItemCrossReference(mfrID, mfrName, mfrPart, mfrDesc));
			}
			itemsData.add(new Item(number, desc, manufacturers));
		}
		System.out.println(itemsData.toString());
		return itemsData;
	}
	
	public void sendNotification(IChange change, String comment) throws APIException {
		//List<IUser> notifyList = new ArrayList<>();
		boolean urgent = true;
		String template = "SageIntegrationNotitification";
		//change.getSession().getCurrentUser();
		//notifyList.add(change.getSession().getCurrentUser());
		change.getSession().sendNotification(change, template, null, urgent, comment);
	}
}
