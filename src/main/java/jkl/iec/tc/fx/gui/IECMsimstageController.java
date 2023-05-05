package jkl.iec.tc.fx.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import jkl.iec.tc.bean.utils.IECSimProperties;
import jkl.iec.tc.bean.utils.IECSimProperties.TimeProperties;

public class IECMsimstageController extends IECstageController {
    
	private IECSimProperties sim;
	
	@FXML private TextField timeText;
	@FXML private TextField ValueText;
	@FXML private Label timeLabel;	
	
	@Override
	void saveStage() {
		sim.timeproperties.isValidTime(timeText.getText());
		sim.setValinc(Integer.parseInt(ValueText.getText()));
		closeStage();
	}

	@Override
	void loadStage() {
		sim = (IECSimProperties) iob.item.data;
		timeText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,	String arg1, String newtxt) {
//				System.out.println("sim.timeproperties"+sim.timeproperties.isValidTime());
				if (sim.timeproperties.isValidTime(newtxt)) {
					timeText.setStyle("-fx-background-color: white;");
			    	timeLabel.setText("");
//			    	timeLabel.setVisible(true);
				} else {
					timeText.setStyle("-fx-background-color: yellow;");
			    	timeLabel.setText(sim.timeproperties.TimeErrorStr);
//			    	timeLabel.setVisible(true);
				}
			}
        });
		
		ValueText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,	String arg1, String arg2) {
				try {
					Integer.parseInt(arg2);
					ValueText.setStyle("-fx-background-color: white;");
				} catch (Exception e) {
					ValueText.setStyle("-fx-background-color: yellow;");
				}
			}
        });
		
		timeText.setText(sim.getTimerString());
		ValueText.setText( String.valueOf((int) sim.getValinc()) );
	}
	
}
