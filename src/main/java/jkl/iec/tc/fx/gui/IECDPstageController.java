package jkl.iec.tc.fx.gui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyEvent;

public class IECDPstageController extends IECstageController {
	
	@FXML private RadioButton DiffRadio;
	@FXML private RadioButton OffRadio;
	@FXML private RadioButton OnRadio;
	@FXML private RadioButton FaultRadio;
	@FXML private CheckBox IVCheck;
	
	@FXML protected void handleOnKeyPressed(KeyEvent ke ) {
		 super.handleOnKeyPressed(ke);
    }
	
	@FXML
	private void limitButtonAction(){
		if (IVCheck.isSelected()){
		  iob.setMIN_VALUE(1);
		}
		saveStage();
		closeStage();
	}

	@FXML
	private void OnIVCheck(){
		if (IVCheck.isSelected()){
			if(DiffRadio.isSelected() ||
					FaultRadio.isSelected()) {
				OffRadio.setSelected(true);
			}
			DiffRadio.setDisable(true);
			FaultRadio.setDisable(true);
		} else {
			DiffRadio.setDisable(false);
			FaultRadio.setDisable(false);
		}
	}
	
	@FXML
	private void onAction(){
       saveStage();
	}
	
	@Override
	void loadStage() {
		if (iob.getVal() == 0 ) {
			DiffRadio.setSelected(true);
		} 
		if (iob.getVal() == 1 ) {
			OffRadio.setSelected(true);
		} 
		if (iob.getVal() == 2 ) {
			OnRadio.setSelected(true);
		} 
		if (iob.getVal() == 3 ) {
			FaultRadio.setSelected(true);
		}
		if (iob.getMIN_VALUE() > 0) {
			IVCheck.setSelected(true);
		}
		OnIVCheck();
	}

	@Override
	void saveStage() {
		// TODO Auto-generated method stub
		if(DiffRadio.isSelected()) {
			iob.setVal(0);
		} 
		if(OffRadio.isSelected()) {
			iob.setVal(1);
		} 
		if(OnRadio.isSelected()) {
			iob.setVal(2);
		} 
		if(FaultRadio.isSelected()) {
			iob.setVal(3);
		} 
		closeStage();
	}
}
