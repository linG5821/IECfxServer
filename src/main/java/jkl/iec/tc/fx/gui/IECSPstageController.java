package jkl.iec.tc.fx.gui;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyEvent;

public class IECSPstageController extends IECstageController {

	@FXML private RadioButton OffRadio;
	@FXML private RadioButton OnRadio;
	
	@FXML
	private void onAction(){
       saveStage();
	}
	
	@Override
	void loadStage() {
		if (iob.getVal() == 0 ) {
			OffRadio.setSelected(true);
		} else {
			OnRadio.setSelected(true);
		}
	}

	@Override
	void saveStage() {
		// TODO Auto-generated method stub
		if(OffRadio.isSelected()) {
			iob.setVal(0);
		} else {
			iob.setVal(1);
		}
		closeStage();
	}
}
