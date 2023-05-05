package jkl.iec.tc.fx.gui;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jkl.iec.tc.bean.type.IECTCObject;


public abstract  class IECstageController {
	
	@FXML protected Pane rootPane;
	
	protected IECTCObject iob;
	protected Stage stage;
	
	@FXML protected void handleOnKeyPressed(KeyEvent ke ) {
        System.out.println("Key Pressed: " + ke.getCode());
        if (ke.getCode() == KeyCode.ESCAPE)  {
        	closeStage();
        }
        if (ke.getCode() == KeyCode.ENTER)  {
            	saveStage();
            }
    }
		
	abstract void loadStage();
	
	abstract void saveStage();

	protected void closeStage() {
		this.stage = (Stage) rootPane.getScene().getWindow();
		stage.close();	
	}

	public void init(IECTCObject o) {
		this.iob=o;
		loadStage();
		}
	
}
