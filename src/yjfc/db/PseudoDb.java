package yjfc.db;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PseudoDb {
    
    private Set<CheckoutItemPOJO> pseudoDb;
    
    private List<String> allType;
    private List<String> allMap;
    
    private List<String> epeeType;
    private List<String> epeeMap;
    
    private List<String> foilType;
    private List<String> foilMap;
    
    private List<String> sabreType;
    private List<String> sabreMap;
    

    /************************************
      Creating db
    ************************************/
    public PseudoDb() {
        pseudoDb = new HashSet<>();
        loadSymbolMappings();
    }
    
    public PseudoDb(List<CheckoutItemPOJO> insertList) {
        this();
        insertAll(insertList);
    }
    

    /************************************
      Inserting
    ************************************/
    private void loadSymbolMappings() {
        allType = Arrays.asList("CP", 				"PL", 		"GL", 		"KN", 		"JK");
        allMap 	= Arrays.asList("Chest Protector", 	"Plastron", "Glove", 	"Knickers", "Jacket");
        
        epeeType = Arrays.asList(	"EM", 	"", "EP", 		"EP", 		"EP");
        epeeMap = Arrays.asList(	"Mask", "", "Epee 1", 	"Epee 2", 	"Epee 3");
        
        foilType = Arrays.asList(	"FM", 	"FL", 	"FO", 		"FO", 		"FO");
        foilMap = Arrays.asList(	"Mask", "Lame", "Foil 1", 	"Foil 2", 	"Foil 3");
        
        sabreType = Arrays.asList(	"SM", 	"SL", 	"SB", 		"SB", 		"SB");
        sabreMap = Arrays.asList(	"Mask", "Lame", "Sabre 1", 	"Sabre 2", 	"Sabre 3");
    }

    public void insertAll(List<CheckoutItemPOJO> insertList) {
        pseudoDb.addAll(insertList);
//      DatabaseConnector.executeUpdateInDatabase(
//              "insert into checkout_item (type, num, size, hand, person, checkout_date) "
//              + "values ('"+item.getType()+"', "+item.getNum()+", '"+item.getSize()+"', null, null, null);");
    }
    

    /************************************
      Update
    ************************************/
    public void update(String person, LocalDate checkoutDate, CheckoutItemPOJO item) {
        item.setPerson(person);
        item.setCheckoutDate(checkoutDate);
        pseudoDb.add(item);
//      DatabaseConnector.executeUpdateInDatabase("update checkout_item"
//      + " set person='"+person+"',"
//      + " checkout_date="+date
//      + " where type='"+item.getType()+"' and num="+item.getNum()+";");
    }
    
    public void remove(CheckoutItemPOJO item) {
        item.setPerson(null);
        item.setCheckoutDate(null);
        pseudoDb.add(item);
    }
    

    /************************************
      Selecting
    ************************************/
    public List<CheckoutItemPOJO> selectByTypeAndPerson(String type, String person) {
        List<CheckoutItemPOJO> aList = new ArrayList<>();
        for(CheckoutItemPOJO item : pseudoDb) {
            if(item.getType().equals(type)
                    && (!item.isOwned() || item.getPerson().equals(person))) {
                aList.add(item);
            }
        }
        Collections.sort(aList);
        return aList;
//      myList = DatabaseConnector.getCheckoutItemInDatabase(
//      "select * from checkout_item where type='"+type.get(temp)+"' and person is null;");
    }
    
    public List<CheckoutItemPOJO> selectByPerson(String person) {
        List<CheckoutItemPOJO> aList = new ArrayList<>();
        for(CheckoutItemPOJO item : pseudoDb) {
            if(item.isOwned() && item.getPerson().equals(person)) {
                aList.add(item);
            }
        }
        Collections.sort(aList);
        return aList;
    }
    
    public List<CheckoutItemPOJO> selectByType(String type) {
        List<CheckoutItemPOJO> aList = new ArrayList<>();
        for(CheckoutItemPOJO item : pseudoDb) {
            if(item.getType().equals(type) && !item.isOwned()) {
                aList.add(item);
            }
        }
        Collections.sort(aList);
        return aList;
//      myList = DatabaseConnector.getCheckoutItemInDatabase(
//      "select * from checkout_item where type='"+type.get(temp)+"' and person is null;");
    }
    
    public List<String> selectPersons() {
        Set<String> aSet = new HashSet<>();
        for(CheckoutItemPOJO item : pseudoDb) {
            if(item.isOwned()) {
                aSet.add(item.getPerson());
            }
        }
        List<String> aList = new ArrayList<>(aSet);
        Collections.sort(aList);
        return aList;
    }
    
    public List<CheckoutItemPOJO> selectForExport(LocalDate date) {
        List<CheckoutItemPOJO> aList = new ArrayList<>();
        for(CheckoutItemPOJO item : pseudoDb) {
            if(item.getCheckoutDate() != null && item.getCheckoutDate().equals(date)) {
                aList.add(item);
            }
        }
        Collections.sort(aList, new Comparator<CheckoutItemPOJO>() {
            @Override
            public int compare(CheckoutItemPOJO one, CheckoutItemPOJO other) {
                if(!one.getPerson().equals(other.getPerson())) {
                    return one.getPerson().compareTo(other.getPerson());
                } else {
                    return one.compareTo(other);
                }
            }
        });
        return aList;
    }
    
    public List<CheckoutItemPOJO> selectAll() {
        List<CheckoutItemPOJO> aList = new ArrayList<>(pseudoDb);
        Collections.sort(aList);
        return aList;
    }
    
    public List<String> getAllSymbols() {
    	return allType;
    }
    
    public List<String> getAllLabels() {
    	return allMap;
    }
    
    public List<String> getEpeeSymbols() {
    	return epeeType;
    }
    
    public List<String> getEpeeLabels() {
    	return epeeMap;
    }
    
    public List<String> getFoilSymbols() {
    	return foilType;
    }
    
    public List<String> getFoilLabels() {
    	return foilMap;
    }
    
    public List<String> getSabreSymbols() {
    	return sabreType;
    }
    
    public List<String> getSabreLabels() {
    	return sabreMap;
    }

}
