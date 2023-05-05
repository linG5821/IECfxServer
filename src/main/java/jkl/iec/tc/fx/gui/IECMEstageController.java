package jkl.iec.tc.fx.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class IECMEstageController extends IECstageController {
    
	@FXML private Button limitsButton;
	@FXML private Slider valueSlider;
	@FXML private TextField valueText;
	@FXML private TextField minText;
	@FXML private TextField maxText;
	
	@FXML
	private void limitsButtonAction(){
		try {
			iob.setMIN_VALUE(Double.parseDouble(minText.getText()));
			iob.setMAX_VALUE(Double.parseDouble(maxText.getText()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
       closeStage();
	}
	
	@Override
	void saveStage() {
		try {
			iob.setVal(Integer.parseInt(valueText.getText()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		closeStage();
	}

	@Override
	void loadStage() {
		System.out.println("init  ");
		
		valueSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
            	System.out.println("BOOL");
            	return "Expert";
            }

			@Override
			public Double fromString(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			});
		
//		int dif = (int) Math.round((iob.getMAX_VALUE()-iob.getMIN_VALUE()) / 2);
		double min = iob.getMIN_VALUE();
		if (min ==-32768) { min++;}
		
		double dif = ((iob.getMAX_VALUE()-min) / 2);
		valueSlider.setMajorTickUnit(dif);
		valueSlider.setMax(iob.getMAX_VALUE());
//		valueSlider.setMin(iob.getMIN_VALUE());
		valueSlider.setMin(min);
        
		valueSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
//				System.out.println("BOOL"+String.valueOf(arg2));
				if (arg2 == false) {
					valueText.setText(String.format("%d", Math.round(valueSlider.getValue())));
				}
			}
        });
		
		valueText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,	String arg1, String arg2) {
				try {
					Integer.parseInt(arg2);
					valueText.setStyle("-fx-background-color: -fx-focus-color, -fx-text-box-border, white;");
				} catch (Exception e) {
					valueText.setStyle("-fx-background-color: -fx-focus-color, -fx-text-box-border , yellow;");
				}
			}
        });
		maxText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,	String arg1, String arg2) {
				try {
					Integer.parseInt(arg2);
					maxText.setStyle("-fx-background-color: white;");
				} catch (Exception e) {
					maxText.setStyle("-fx-background-color: yellow;");
				}
			}
        });
		minText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,	String arg1, String arg2) {
				try {
					Integer.parseInt(arg2);
					minText.setStyle("-fx-background-color: white;");
				} catch (Exception e) {
					minText.setStyle("-fx-background-color: yellow;");
				}
			}
        });
		
		valueSlider.setValue((int)iob.getVal());
		valueText.setText(String.valueOf((int)iob.getVal()));
		maxText.setText(String.valueOf((int)iob.getMAX_VALUE()));
		minText.setText(String.valueOf((int)iob.getMIN_VALUE()));
	}
	
}
