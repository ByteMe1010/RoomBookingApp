package hotel.model;

/**
 * Model class representing the ITEM table in HOTEL_DB.
 * Tracks drinks, food, snacks, their unit prices, stock levels, and measuring units.
 */

public class Item {
    private int itemId ;
    private String name ;
    private double pricePerUnit ;
    private double stockAmount ;
    private String measuringMethod ; // 'COUNT' or 'CONTINUOUS'
    private boolean isActive ;


    public Item() {
    }

    public Item(String name, double pricePerUnit , double stockAmount, String measuringMethod){
        this.name = name ;
        this.pricePerUnit = pricePerUnit;
        this.stockAmount = stockAmount;
        this.measuringMethod = measuringMethod;
        this.isActive = true;  // Default to active(1)
    }

    public Item(int itemId, String name, double pricePerUnit, double stockAmount, String measuringMethod, boolean isActive) {
        this.itemId = itemId;
        this.name = name;
        this.pricePerUnit = pricePerUnit;
        this.stockAmount = stockAmount;
        this.measuringMethod = measuringMethod;
        this.isActive = isActive;
    }

    // --- GETTERS AND SETTERS ---

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public double getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(double stockAmount) {
        this.stockAmount = stockAmount;
    }

    public String getMeasuringMethod() {
        return measuringMethod;
    }

    public void setMeasuringMethod(String measuringMethod) {
        this.measuringMethod = measuringMethod;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString(){
        return "Item{" +
                "itemId=" + itemId +
                ", name='" + name + '\'' +
                ", pricePerUnit=" + pricePerUnit +
                ", stockAmount=" + stockAmount +
                ", measuringMethod='" + measuringMethod + '\'' +
                ", active=" + isActive +
                '}';
        }

}




