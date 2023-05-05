package jkl.iec.tc.fx.gui;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import jkl.iec.tc.bean.type.IECItemList;
import jkl.iec.tc.bean.type.IECTCItem;
import jkl.iec.tc.bean.type.IECTCObject;
import jkl.iec.tc.bean.utils.IECSimulatorThread;
import jkl.iec.tc.fx.gui.IECObjectCellFactory.IECvalueProperty;

//public class IECListTablePane extends StackPane{
public class IECListTablePane extends TableView<IECTCItem> {
	 
	public final static Logger log = Logger.getLogger(IECListTablePane.class .getName()); 
	
	public final IECItemList itemlist = new IECItemList();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private IECSimulatorThread iecsimulator; 
	
	private class irq implements Runnable {
		@Override
		public void run() {
            Platform.runLater(iecsimulator);
		}
	}
    
    private class SendButtonCell extends TableCell<IECTCItem, Boolean> {
      	final Button cellButton = new Button("Send");
           // Init the Button
        SendButtonCell(){
              cellButton.setOnAction(new EventHandler<ActionEvent>(){
                   @Override
                  public void handle(ActionEvent t) {
                  	 IECTCItem i = getItems().get(getTableRow().getIndex());
                  	 byte[] buf = i.getStream();
                     itemlist.new ThreadAction(buf, buf.length);
                  }
              });
          }

          //Display button if the row is not empty
          @Override
          protected void updateItem(Boolean o, boolean empty) {
          	super.updateItem(o, empty);
              if( !empty) {
                  if ( o ) {
              		setGraphic(cellButton);
        			} else {
              		setGraphic(null);
        			}
              	} else {
                  		setGraphic(null);           			
              		}
              	}
    }
    
    private class SimButtonCell extends TableCell<IECTCItem, Boolean> {
        final Button cellButton = new Button("SimProp.");
        // Init the Button
        SimButtonCell(){
            cellButton.setOnAction(new EventHandler<ActionEvent>(){
                 @Override
                public void handle(ActionEvent t) {
                	 int selectdIndex = getTableRow().getIndex();
  
                	 IECTCItem Item = (IECTCItem) getItems().get(selectdIndex);
                     IECstage iecDialog = new IECstage(Item.getObject0(),IECvalueProperty.sim);
                     iecDialog.show();  
                }
            });
        }
        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
        	super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellButton);
            }
        }
    }
    
    final IECComboBox ieccombo = new IECComboBox();
	
	private final TableColumn typeCol = new TableColumn<IECTCItem, String> ("type");
	private final TableColumn nameCol = new TableColumn<IECTCItem, String> ("Name");
	private final TableColumn ASDUCol = new TableColumn<IECTCItem, Integer> ("ASDU");
	private final TableColumn COTCol = new TableColumn<IECTCItem, Integer> ("COT");
	private final TableColumn IOBCol = new TableColumn<IECTCItem, Integer> ("IOB");
	private final TableColumn<IECTCItem, IECTCObject> valCol = new TableColumn<IECTCItem, IECTCObject> ("value");
	private final TableColumn<IECTCItem, IECTCObject> quCol = new TableColumn<IECTCItem, IECTCObject> ("QU");
	private final TableColumn<IECTCItem, Date> timeCol = new TableColumn<IECTCItem, Date> ("TIME");
	private final TableColumn<IECTCItem, Boolean>  actionCol = new TableColumn<>("Action");
	private final TableColumn<IECTCItem, Boolean>  simcheckcol = new TableColumn<>("Sim");
	private final TableColumn<IECTCItem, Boolean>  simvalcol = new TableColumn<>("SimParam");
	
	private IECObjectCellFactory iecCellFactory = new IECObjectCellFactory();
	
	public IECListTablePane() {
		setItems(itemlist);
		setEditable(true);
		
		setColValueFactory();
		setColwidth();
		setColFactory();
		
	    getColumns().addAll(typeCol,nameCol,ASDUCol,COTCol,IOBCol,valCol,quCol,timeCol,actionCol,simcheckcol,simvalcol);
	}
	
	private void setColwidth() {
		typeCol.setPrefWidth(200);		
		ASDUCol.setPrefWidth(50);
		COTCol.setPrefWidth(50);
		timeCol.setPrefWidth(120);
		actionCol.setPrefWidth(60);
		simcheckcol.setPrefWidth(30);
		}
	
	private void setColValueFactory() {
//		typeCol.setPrefWidth(150);
		
		typeCol.setCellValueFactory( new PropertyValueFactory<IECTCItem,String>("Iecname"));
		nameCol.setCellValueFactory( new PropertyValueFactory<IECTCItem,String>("Name")	);
	    ASDUCol.setCellValueFactory( new PropertyValueFactory<IECTCItem,Integer>("ASDU") );
	    COTCol.setCellValueFactory( new PropertyValueFactory<IECTCItem,Integer>("COT") );
		
	    IOBCol.setCellValueFactory(new Callback<CellDataFeatures<IECTCItem, Integer>, ObservableValue<Integer>>() {
			@Override
			public ObservableValue call(
					CellDataFeatures<IECTCItem, Integer> p) {
				return p.getValue().getIOB(0).addrProperty();
			}
		  });
		
		valCol.setCellValueFactory(new Callback<CellDataFeatures<IECTCItem, IECTCObject>, ObservableValue<IECTCObject>>() {
			@Override
			public ObservableValue<IECTCObject> call(
					CellDataFeatures<IECTCItem, IECTCObject> p) {
				return p.getValue().object0Property();
			}
		  });	
	   	
		quCol.setCellValueFactory(new Callback<CellDataFeatures<IECTCItem, IECTCObject>, ObservableValue<IECTCObject>>() {
			@Override
			public ObservableValue<IECTCObject> call(
					CellDataFeatures<IECTCItem, IECTCObject> p) {
				return p.getValue().object0Property();
			}
		  });
	   	
		timeCol.setCellValueFactory(new Callback<CellDataFeatures<IECTCItem, Date>, ObservableValue<Date>>() {
			@Override
			public ObservableValue<Date> call(
					CellDataFeatures<IECTCItem, Date> p) {
				return p.getValue().getIOB(0).timeProperty();
			}
		  });
		
		actionCol.setCellValueFactory( new PropertyValueFactory<IECTCItem,Boolean>("M_typ"));
		
	    simcheckcol.setCellValueFactory(new Callback<CellDataFeatures<IECTCItem, Boolean>, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(
					CellDataFeatures<IECTCItem, Boolean> p) {
				return p.getValue().flag1Property();
			}
		  });      
	    
		simvalcol.setCellValueFactory(new Callback<CellDataFeatures<IECTCItem, Boolean>, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(
					CellDataFeatures<IECTCItem, Boolean> p) {
				return new SimpleBooleanProperty(p.getValue() != null);
			}
		  });
	}
	
	private void setColFactory() {
		typeCol.setCellFactory(ComboBoxTableCell.forTableColumn(ieccombo.getItems()));
	    typeCol.setOnEditCommit(new EventHandler<CellEditEvent<IECTCItem, String>>() {
	        @Override
	        public void handle(CellEditEvent<IECTCItem, String> t) {
	        	IECTCItem i= ((IECTCItem) getItems().get(t.getTablePosition().getRow()));
	        	String s = t.getNewValue();
	        	i.setIecname(s);
	        }
	    });		

	    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
	    nameCol.setOnEditCommit(new EventHandler<CellEditEvent<IECTCItem, String>>() {
        @Override
        public void handle(CellEditEvent<IECTCItem, String> t) {
        	IECTCItem i= ((IECTCItem) getItems().get(t.getTablePosition().getRow()));
            i.setName(t.getNewValue());
        	}
	    });
      
		ASDUCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
	    ASDUCol.setOnEditCommit(new EventHandler<CellEditEvent<IECTCItem, Integer>>() {
	        @Override
	        public void handle(CellEditEvent<IECTCItem, Integer> t) {
	        	IECTCItem i= ((IECTCItem) getItems().get(t.getTablePosition().getRow()));
	        	System.out.println("CellEditEvent:"+t+" "+i);
	            i.setASDU(t.getNewValue());
	        }
	    });
	    
		COTCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
	    COTCol.setOnEditCommit(new EventHandler<CellEditEvent<IECTCItem, Integer>>() {
	        @Override
	        public void handle(CellEditEvent<IECTCItem, Integer> t) {
	        	IECTCItem i= ((IECTCItem) getItems().get(t.getTablePosition().getRow()));
	        	System.out.println("CellEditEvent:"+t+" "+i);
	            i.setCOT(t.getNewValue());
	        }
	    });
	    
		IOBCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		IOBCol.setOnEditCommit(new EventHandler<CellEditEvent<IECTCItem, Integer>>() {
	        @Override
	        public void handle(CellEditEvent<IECTCItem, Integer> t) {
	        	IECTCItem i= ((IECTCItem) getItems().get(t.getTablePosition().getRow()));
	            i.getIOB(0).setaddr(t.getNewValue());
	        }
	    });
		
		valCol.setUserData(IECvalueProperty.value);
	   	valCol.setCellFactory(iecCellFactory);	
	   	quCol.setUserData(IECvalueProperty.qu);
		quCol.setCellFactory(iecCellFactory);
		timeCol.setUserData(IECvalueProperty.time);
		timeCol.setCellFactory(new Callback<TableColumn<IECTCItem, Date>, TableCell<IECTCItem, Date>>() {
			@Override
			public TableCell<IECTCItem, Date> call(final TableColumn<IECTCItem, Date> col) {
				final TableCell<IECTCItem, Date> cell = new IECTcell();
				return cell;
			}});
		
	   	actionCol.setCellFactory(new Callback<TableColumn<IECTCItem, Boolean>, TableCell<IECTCItem, Boolean>>() {
            @Override
            public TableCell<IECTCItem, Boolean> call(TableColumn<IECTCItem, Boolean> p) {
                return new SendButtonCell();
           }         
	   	});
	   	
	   	simcheckcol.setCellFactory(CheckBoxTableCell.forTableColumn(simcheckcol));
		simvalcol.setCellFactory(new Callback<TableColumn<IECTCItem, Boolean>, TableCell<IECTCItem, Boolean>>() {
            @Override
           public TableCell<IECTCItem, Boolean> call(TableColumn<IECTCItem, Boolean> p) {
               return new SimButtonCell();
           }         
       });
	}
	
	public void add(IECTCItem i) {
		itemlist.add(i);
	}

	public void start() {
		iecsimulator  =new IECSimulatorThread(itemlist);
		scheduler.scheduleAtFixedRate(new irq(),2,1,TimeUnit.SECONDS);
		itemlist.start();
	}

	public void stop() {
		scheduler.shutdown();
		itemlist.stop();
	}
}
