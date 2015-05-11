package yjfc.db;

import java.time.LocalDate;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CheckoutItemPOJO implements Comparable<CheckoutItemPOJO> {
    
    private StringProperty type;
    private int num;
    private String size;
    private String handed;
    private StringProperty person;
    private LocalDate checkoutDate;
    
    private SimpleBooleanProperty checked = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty checkedProperty() {
        return this.checked;
    }
    public java.lang.Boolean getChecked() {
        return this.checkedProperty().get();
    }
    public void setChecked(final java.lang.Boolean checked) {
        this.checkedProperty().set(checked);
    }
    
    public CheckoutItemPOJO() {
    	setType("");
    	setPerson("");
    }
    
    public boolean isOwned() {
    	return getPerson() != null && getPerson().length() > 0;
    }
    
    public String getType() {
        return typeProperty() == null ? null : typeProperty().get();
    }
    public void setType(String type) {
    	if(typeProperty() == null) {
    		this.type = new SimpleStringProperty(type);
    	} else {
    		if(type == null) {
    			type = "";
    		}
    		typeProperty().set(type);
    	}
    }
    public StringProperty typeProperty() {
        return type; 
    }
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getHanded() {
        return handed;
    }
    public void setHanded(String handed) {
        this.handed = handed;
    }

    public String getPerson() {
        return personProperty() == null ? null : personProperty().get();
    }
    public void setPerson(String person) {
    	if(personProperty() == null) {
    		this.person = new SimpleStringProperty(person);
    	} else {
    		if(person == null) {
    			person = "";
    		}
    		personProperty().set(person);
    	}
    }
    public StringProperty personProperty() {
        return person; 
    }
    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }
    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }
    public String toString() {
    	String aString = "";
    	if(isOwned()) {
    		aString += "*";
    	}
    	aString += this.getType() + num;
        return aString;
    }
    @Override
    public boolean equals(Object other) {
        if(other == null) return false;
        if(other == this) return true;
        if(!(other instanceof CheckoutItemPOJO)) return false;
        CheckoutItemPOJO that = (CheckoutItemPOJO) other;
        return that.getType().equals(this.getType()) && this.getNum() == that.getNum();
    }
    @Override
    public int hashCode() {
        return this.getType().hashCode() + this.getNum();
    }
    @Override
    public int compareTo(CheckoutItemPOJO another) {
    	if(this.getType().equals("")) {
    		return -1;
    	} else if(another.getType().equals("")) {
    		return 1;
    	}
        if(!this.getType().equals(another.getType())) {
            return this.getType().compareTo(another.getType());
        } else {
            return this.getNum() - another.getNum();
        }
    }

}
