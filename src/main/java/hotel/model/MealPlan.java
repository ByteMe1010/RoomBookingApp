package hotel.model;

/**
 * Model class representing the MEAL_PLAN table in HOTEL_DB.
 * Tracks meal packages (RO, BB, HB, FB) for incoming booking requests.
 */

/*
    RO - Room Only	(No meals included)
    BB - Breakfast Included	(Daily morning breakfast)
    HB - Half Board	(Breakfast and Dinner included)
    FB - Full Board	(Breakfast, Lunch, and Dinner included)

 */

public class MealPlan {
    private int mealPlanId ;
    private String code ;
    private String name ;
    private String description ;
    private boolean isActive ;


    public MealPlan(){

    }

    public  MealPlan(String code,String name, String description ){
        this.code = code ;
        this.name = name ;
        this.description = description ;
        this.isActive = true ;//  -- 1(Chosen) ,0(Hidden)
    }

    public MealPlan(int mealPlanId, String code,String name, String description ,boolean isActive){
        this.mealPlanId = mealPlanId ;
        this.code = code ;
        this.name = name ;
        this.description = description ;
        this.isActive = isActive ;
    }

    // --- GETTERS AND SETTERS ---


    public int getMealPlanId() {
        return mealPlanId;
    }

    public void setMealPlanId(int mealPlanId) {
        this.mealPlanId = mealPlanId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString(){
        return "MealPlan{" +
                "mealPlanId=" + mealPlanId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                '}' ;
    }

}
