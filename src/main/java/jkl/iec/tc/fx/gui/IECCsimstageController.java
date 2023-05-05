package jkl.iec.tc.fx.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import jkl.iec.tc.bean.utils.IECSimProperties;

public class IECCsimstageController extends IECstageController {
    
	private IECSimProperties sim;
	
	@FXML private TextField itemText;
	@FXML private TextField changeValueText;
	@FXML private Label itemabel;	
	
	@Override
	void saveStage() {
		sim.itemproperties.isValidItem(itemText.getText());
		System.out.println("set Valinc " +Integer.parseInt(changeValueText.getText()));
		sim.setValinc(Integer.parseInt(changeValueText.getText()));
		closeStage();
	}

	@Override
	void loadStage() {
		sim = (IECSimProperties) iob.item.data;
		System.out.println("load backstring:"+ sim.itemproperties.BackString);
		itemText.setText(sim.getBackString());
		changeValueText.setText( String.valueOf((int) sim.getValinc()) );

		itemText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,	String arg1, String newtxt) {
//				System.out.println("sim.timeproperties"+sim.timeproperties.isValidTime());
				if (sim.itemproperties.isValidItem(newtxt)) {
					itemText.setStyle("-fx-background-color: white;");
					itemabel.setText("");
				} else {
					itemText.setStyle("-fx-background-color: yellow;");
					itemabel.setText(sim.itemproperties.itemErrorStr);
				}
			}
        });
		
		changeValueText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,	String arg1, String arg2) {
				try {
					Integer.parseInt(arg2);
					changeValueText.setStyle("-fx-background-color: white;");
				} catch (Exception e) {
					changeValueText.setStyle("-fx-background-color: yellow;");
				}
			}
        });
	}
	
}
