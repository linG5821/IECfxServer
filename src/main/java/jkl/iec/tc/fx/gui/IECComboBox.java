package jkl.iec.tc.fx.gui;


import javafx.scene.control.ComboBox;

import jkl.iec.tc.bean.type.IECMap;
import jkl.iec.tc.bean.type.IECMap.IECTyp;

public class IECComboBox extends ComboBox<String> {
	 public IECComboBox() {
		for (IECTyp it : IECTyp.values()) {
			if (it != IECTyp.IEC_NULL_TYPE) {
				getItems().add(IECMap.getTypeDescription(it));
			}
		}
	setValue(IECMap.getTypeDescription(IECTyp.M_SP_NA));
	}


	
}
