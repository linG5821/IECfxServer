package jkl.iec.tc.fx.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class IECITstageController extends IECstageController {
    
	@FXML private TextField valueText;

	
	@Override
	void saveStage() {
		try {
			iob.setVal(Long.parseLong(valueText.getText()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		closeStage();
	}

	@Override
	void loadStage() {
        
		valueText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,	String arg1, String arg2) {
				try {
					Long.parseLong(arg2);
					valueText.setStyle("-fx-background-color: white;");
				} catch (Exception e) {
					valueText.setStyle("-fx-background-color: yellow;");
				}
			}
        });

		valueText.setText(String.valueOf((long)iob.getVal()));

	}
	
}
