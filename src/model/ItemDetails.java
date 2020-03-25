package model;

public class ItemDetails {
	private String itemName;
	private int quantity;
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "ItemDetails - Item Name=" + itemName + ", quantity=" + quantity ;
	}
	public ItemDetails(String itemName, int quantity) {
		super();
		this.itemName = itemName;
		this.quantity = quantity;
	}
	
}
