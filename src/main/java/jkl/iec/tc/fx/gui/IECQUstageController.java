package jkl.iec.tc.fx.gui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import jkl.iec.tc.bean.type.IECMap.IECTyp;

public class IECQUstageController extends IECstageController {
	
	@FXML private CheckBox IVBox;
	@FXML private CheckBox NTBox;
	@FXML private CheckBox SBBox;
	@FXML private CheckBox BLBox;
	@FXML private CheckBox OVBox;
	
	
	@Override
	void loadStage() {
		if (iob.isQUIV()) {IVBox.setSelected(true);}
		if (iob.isQUNT()) {NTBox.setSelected(true);}
		if (iob.isQUSB()) {SBBox.setSelected(true);}
		if (iob.isQUBL()) {BLBox.setSelected(true);}
		if (iob.isQUOV()) {OVBox.setSelected(true);}
		
//		IECTyp t = iob.asdu.getIectyp();
		if (( iob.iectyp == IECTyp.M_SP_NA) || ( iob.iectyp == IECTyp.M_SP_TB) ||
			( iob.iectyp == IECTyp.M_DP_NA) || ( iob.iectyp == IECTyp.M_DP_TB) ) {
			OVBox.setVisible(false);
		}
		
		if (( iob.iectyp == IECTyp.M_IT_NA) || ( iob.iectyp == IECTyp.M_IT_TB) ) {
			
			NTBox.setText("CA [0x40]");
			SBBox.setText("CY [0x40]");
     		BLBox.setVisible(false);
			OVBox.setVisible(false);
			}
		}

	@Override
	void saveStage() {
		int qu = 0;
		if (IVBox.isSelected()) { qu = (qu | (byte) 0x80);}
		if (NTBox.isSelected()) { qu = (qu | (byte) 0x40);}
		if (SBBox.isSelected()) { qu = (qu | (byte) 0x20);}
		if (BLBox.isSelected()) { qu = (qu | (byte) 0x10);}
		if (OVBox.isSelected()) { qu = (qu | (byte) 0x01);}
		
		iob.setQU((byte) qu);
	}
	
	@Override
	protected void closeStage() {
		saveStage();
		super.closeStage();
	}
	
}
