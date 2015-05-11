package yjfc.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import yjfc.db.CheckoutItemPOJO;
import yjfc.db.ExcelToSqliteReader;
import yjfc.db.PseudoDb;
 
public class ArmoryApp extends Application {
    
    public final static String APP_VERSION = "v0.0.1";


    /************************************
      Launch
    ************************************/
    public static void main(String[] args) {
        launch(args);
    }
    
    /************************************
      UI assembly
    ************************************/
    private GridPane allGrid;
    private GridPane epeeGrid;
    private GridPane foilGrid;
    private GridPane sabreGrid;
    
    private DatePicker datePick;
    private ComboBox<String> fencerCombo;

    private ObservableList<CheckoutItemPOJO> tableList;
    
    private PseudoDb db;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("YJFC Armory App - "+APP_VERSION);
        primaryStage.setScene(this.createCheckoutScene());
        primaryStage.show();
    }
    
    private Scene createCheckoutScene() {
    	db = new PseudoDb();
    	
        BorderPane borderpane = new BorderPane();
        borderpane.setTop(createAdminPane());
        borderpane.setCenter(createSelectPane());
        borderpane.setBottom(createExportButton());
        borderpane.setRight(createTablePane());
        
        StackPane root = new StackPane();
        root.getChildren().add(borderpane);
        Scene checkoutScene = new Scene(root, 850, 500);

    	return checkoutScene;
    }
    
    private Node createAdminPane() {
    	VBox adminBox = new VBox();
    	adminBox.getChildren().addAll(createImportPane(), createDatePane());
    	return adminBox;
    }
    
    private Node createExportButton() {
        final Button exportButton = new Button("Export");
        exportButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                exportButton.setDisable(true);
                exportData();
                exportButton.setDisable(false);
            }
        });
        
        
        VBox box = new VBox();
        box.getChildren().addAll(exportButton);

        return box;
    }
    
    private Node createDatePane() {
    	HBox box = new HBox();
        datePick = new DatePicker(LocalDate.now());
        box.getChildren().addAll(new Text("Checkout date"), datePick);
    	return box;
    }
    
    private Node createImportPane() {
    	HBox box = new HBox();
    	
        final ProgressBar bar = new ProgressBar();
        bar.setVisible(false);
        
    	final TextField importField = new TextField("Armory.xlsx");

        final Button importButton = new Button();
        importButton.setText("Upload Excel");
        importButton.setOnAction(new EventHandler<ActionEvent>() {
            @SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
            public void handle(ActionEvent event) {
                importButton.setDisable(true);
                bar.setVisible(true);

                // load the excel data into main memory
                final Task<Void> task = new Task<Void>() {
                    @Override protected Void call() throws Exception {
                        List<CheckoutItemPOJO> aList = uploadExcel(importField.getText());
                        uploadToDatabase(aList);
						return null;
                    }
                };

                // update ui
                task.setOnSucceeded(new EventHandler() {
					@Override
					public void handle(Event arg0) {
		                Platform.runLater(new Runnable() {
		                	@Override
		                	public void run() {
		                        bar.setVisible(false);
		                        importButton.setDisable(false);   
		                  	}
		                });
					}
                	
                });

                new Thread(task).start();
            }
        });
        
        box.getChildren().addAll(importField, importButton, bar);
        
        return box;
    }
    
    @SuppressWarnings("unchecked")
	private Node createTablePane() {
    	VBox box = new VBox();
    	
        tableList = FXCollections.observableArrayList();

        final TableView<CheckoutItemPOJO> tableView = new TableView<>(tableList);
        tableView.setEditable(true);

        TableColumn<CheckoutItemPOJO, String> personCol = new TableColumn<CheckoutItemPOJO, String>("Person");
        personCol.setCellValueFactory(new PropertyValueFactory<CheckoutItemPOJO, String>("person"));

        TableColumn<CheckoutItemPOJO, String> typeCol = new TableColumn<CheckoutItemPOJO, String>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<CheckoutItemPOJO, String>("type"));

        TableColumn<CheckoutItemPOJO, Integer> numCol = new TableColumn<CheckoutItemPOJO, Integer>("Num");
        numCol.setCellValueFactory(new PropertyValueFactory<CheckoutItemPOJO, Integer>("num"));

        TableColumn<CheckoutItemPOJO, Boolean> checkCol = new TableColumn<CheckoutItemPOJO, Boolean>("Remove?");
        checkCol.setCellValueFactory(new PropertyValueFactory<CheckoutItemPOJO, Boolean>("checked"));
        checkCol.setCellFactory(
    	    new Callback<TableColumn<CheckoutItemPOJO,Boolean>, TableCell<CheckoutItemPOJO,Boolean>>(){
    	    	@Override
    	        public TableCell<CheckoutItemPOJO,Boolean> call(TableColumn<CheckoutItemPOJO,Boolean> p) {
    	    		CheckBoxTableCell<CheckoutItemPOJO, Boolean> check = new CheckBoxTableCell<>();
    	    		check.setEditable(true);
    	    		return check;
    	        }
	        }
	     );

        tableView.getColumns().setAll(personCol, typeCol, numCol, checkCol);

        final Button removeButton = new Button("Remove");
        removeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	removeButton.setDisable(true);

            	for(CheckoutItemPOJO item : tableList) {
        			System.out.println(item);
        			System.out.println(item.getChecked());
            		if(item.getChecked()) {
            			item.setChecked(false);
            			db.remove(item);
            		}
            	}

                tableList.clear();
                tableList.addAll(db.selectForExport(datePick.getValue()));

                fencerCombo.getSelectionModel().clearSelection();
                populateComboAll();
            	
                removeButton.setDisable(false);
            }
        });
        
        box.getChildren().addAll(tableView, removeButton);

        return box;
    }
    
    private Node createFencerPane() {
    	HBox addBox = new HBox();
        
        Text fencerTitleText = new Text("Fencer info");
    	
        final TextField fencerField = new TextField();

        fencerCombo = new ComboBox<>();
        fencerCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> value,
					String before, String after) {
                String fencerName = fencerCombo.getSelectionModel().getSelectedItem();
                
                if(fencerName != null && fencerName.length() > 0) {
                	fencerField.setText(fencerName);
                	
                	getFencerSelectionAll(fencerName);
                }
			}
        });


        final Button fencerButton = new Button("Add");
        fencerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fencerButton.setDisable(true);
                String fencerName = fencerField.getText();

                if(fencerName != null && fencerName.length() > 0) {
                	updateAll(fencerName);
	                
	                fencerCombo.setItems(FXCollections.observableArrayList(db.selectPersons()));
	                
	                tableList.clear();
	                tableList.addAll(db.selectForExport(datePick.getValue()));
	                
	                populateComboAll();
	                
	                fencerField.clear();
                }

                fencerButton.setDisable(false);
            }
        });

        addBox.getChildren().addAll(fencerTitleText, fencerField, fencerButton, fencerCombo);

        return addBox;
    }
    
    private Node createSelectPane() {
    	Text selectTitleText = new Text("Equipment Checkout");
    	
        allGrid = new GridPane();
        epeeGrid = new GridPane();
        foilGrid = new GridPane();
        sabreGrid = new GridPane();
        
        Text allText = new Text("All Fencers");
        populateGrid(db.getAllSymbols(), db.getAllSymbols(), allGrid);

        Text epeeText = new Text("Epee Fencers");
        populateGrid(db.getEpeeSymbols(), db.getEpeeLabels(), epeeGrid);
        
        Text foilText = new Text("Foil Fencers");
        populateGrid(db.getFoilSymbols(), db.getFoilLabels(), foilGrid);

        Text sabreText = new Text("Sabre Fencers");
        populateGrid(db.getSabreSymbols(), db.getSabreLabels(), sabreGrid);
        
        VBox selectPane = new VBox();
        selectPane.getChildren().addAll(
        		selectTitleText,
                allText, allGrid,
                epeeText, epeeGrid,
                foilText, foilGrid,
                sabreText, sabreGrid,
                createFencerPane()
        );
        
        return selectPane;
    }
    
    /************************************
      Upload to database
    ************************************/
    public List<CheckoutItemPOJO> uploadExcel(String filename) {
        try {
            List<CheckoutItemPOJO> aList = ExcelToSqliteReader.parseExcel(filename);
            return aList;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void uploadToDatabase(List<CheckoutItemPOJO> aList) {
        db.insertAll(aList);
        populateComboAll();
    }

    
    /************************************
      Populate interface with data
    ************************************/    
    private void populateGrid(List<String> type, List<String> map, GridPane grid) {
        for(int i=0; i<type.size(); i++) {
            ColumnConstraints column = new ColumnConstraints(75);
            grid.getColumnConstraints().add(column);
            grid.add(new Text(map.get(i)), i, 0);
            grid.add(new ComboBox<CheckoutItemPOJO>(), i, 1);
        }
        grid.setPadding(new Insets(5, 5, 25, 5));
    }
    
    private void populateComboAll() {
        populateCombo(db.getAllSymbols(), allGrid);
        populateCombo(db.getEpeeSymbols(), epeeGrid);
        populateCombo(db.getFoilSymbols(), foilGrid);
        populateCombo(db.getSabreSymbols(), sabreGrid);
    }

    @SuppressWarnings("unchecked")
    private void populateCombo(List<String> type, GridPane grid) {
        List<CheckoutItemPOJO> myList = null;
        ComboBox<CheckoutItemPOJO> box = null;
        int temp = 0;
        for(int i=0; i<grid.getChildren().size(); i++) {
            if(grid.getChildren().get(i) instanceof ComboBox) {
                box = (ComboBox<CheckoutItemPOJO>) grid.getChildren().get(i);
                box.setPromptText("");

                myList = db.selectByType(type.get(temp));
                ObservableList<CheckoutItemPOJO> items = FXCollections.observableArrayList(myList);
                box.setItems(items);

                temp++;
            }
        }
    }

    private void getFencerSelectionAll(String fencerName) {
        getFencerSelection(fencerName, db.getAllSymbols(), allGrid);
        getFencerSelection(fencerName, db.getEpeeSymbols(), epeeGrid);
        getFencerSelection(fencerName, db.getFoilSymbols(), foilGrid);
        getFencerSelection(fencerName, db.getSabreSymbols(), sabreGrid);
    }
    
    @SuppressWarnings("unchecked")
    private void getFencerSelection(String person, List<String> type, GridPane grid) {
        List<CheckoutItemPOJO> myList = null;
        List<CheckoutItemPOJO> selectList = db.selectByPerson(person);
        List<CheckoutItemPOJO> removedList = new ArrayList<>();
        ComboBox<CheckoutItemPOJO> box = null;
        int temp = 0;

        for(int i=0; i<grid.getChildren().size(); i++) {
            if(grid.getChildren().get(i) instanceof ComboBox) {
            	boolean alreadyThere = false;
                box = (ComboBox<CheckoutItemPOJO>) grid.getChildren().get(i);
                box.getItems().clear();
                box.setPromptText("");

                myList = db.selectByTypeAndPerson(type.get(temp), person);
                ObservableList<CheckoutItemPOJO> items = FXCollections.observableArrayList(myList);
                box.setItems(items);

                for(CheckoutItemPOJO item : selectList) {
                	if(myList.contains(item) && !removedList.contains(item) && !alreadyThere) {
                		box.getSelectionModel().select(item);
                		removedList.add(item);
                		alreadyThere = true;
                	}
                }

                temp++;
            }
        }
    }
    

    /************************************
      Update database
    ************************************/
    private void updateAll(String person) {
        updateDatabase(person, db.getAllSymbols(), allGrid);
        updateDatabase(person, db.getEpeeSymbols(), epeeGrid);
        updateDatabase(person, db.getFoilSymbols(), foilGrid);
        updateDatabase(person, db.getSabreSymbols(), sabreGrid);
    }
    
    @SuppressWarnings("unchecked")
    private void updateDatabase(String person, List<String> type, GridPane grid) {
    	LocalDate date = datePick.getValue();
        ComboBox<CheckoutItemPOJO> box = null;
        for(int i=0; i<grid.getChildren().size(); i++) {
            if(grid.getChildren().get(i) instanceof ComboBox) {
                box = (ComboBox<CheckoutItemPOJO>) grid.getChildren().get(i);
                CheckoutItemPOJO item = box.getSelectionModel().getSelectedItem();
                if(item != null && !item.isOwned()) {
                    db.update(person, date, item);
                }
            }
        }
    }


    /************************************
      Export data
    ************************************/
    public void exportData() {
    	LocalDate date = datePick.getValue();
        List<CheckoutItemPOJO> aList = db.selectForExport(date);
        if(aList.size() > 0) {
            PdfExporter.export(date, aList);
        }
    }
}