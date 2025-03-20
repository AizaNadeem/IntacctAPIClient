package com.intacct.xtera.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.agile.api.APIException;
import com.agile.api.ChangeConstants;
import com.agile.api.IDataObject;
import com.agile.api.IItem;
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
			IDataObject docObj = row.getReferent();
			System.out.println("Item: "+ docObj.getName());
			if(docObj.getAgileClass() instanceof IItem) {
				itemList.add(docObj);
			}
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
				String mfr = row.getCell(ItemConstants.ATT_MANUFACTURERS_MFR_NAME).toString();
				String mfrPart = row.getCell(ItemConstants.ATT_MANUFACTURERS_MFR_PART_NUMBER).toString();
				String mfrDesc = row.getCell(ItemConstants.ATT_MANUFACTURERS_MFR_PART_DESCRIPTION).toString();
				manufacturers.add(new ItemCrossReference(mfr, mfrPart, mfrDesc));
			}
			itemsData.add(new Item(number, desc, manufacturers));
		}
		System.out.println(itemsData.toString());
		return itemsData;
	}
}
