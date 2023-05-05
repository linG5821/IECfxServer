package jkl.iec.tc.fx.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jkl.iec.tc.bean.type.IECMap;
import jkl.iec.tc.bean.type.IECTCObject;
import jkl.iec.tc.fx.gui.IECObjectCellFactory.IECvalueProperty;

public class IECstage extends Stage {
	IECTCObject iob;
	IECvalueProperty iecp;
    
	Parent root = null;
    final FXMLLoader loader = new FXMLLoader();
    final String fxmlResPath = "/jkl/iec/tc/fxml/";
    String fxml = fxmlResPath;
//    FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemClassLoader().getResource("fxml"));
    
	public IECstage(IECTCObject o, IECvalueProperty ival) {
		this.iob =o;
		initModality(Modality.APPLICATION_MODAL);
//      initStyle(StageStyle.UTILITY); 
        initStyle(StageStyle.UNDECORATED); 
        setResizable(false);
        
        if (ival == IECvalueProperty.value) {
        	getValueStageFile();			
		}
        if (ival == IECvalueProperty.qu) {
        	getQuStageFile();			
		}
        
        if (ival == IECvalueProperty.sim) {
        	getSimStageFile();			
		}
//        System.out.println("val:" + ival+"  File:"+fxml);
//        URL url = TableDemo.class.getResource(fxml);
        URL url = IECstage.class.getResource(fxml);
        IECListTablePane.log.info("val:" + ival+"  File:"+fxml+"   URL: "+ url );
        
        try {
//  		root = (Parent) loader.load(IECstage.class.getResource(fxml).openStream());
 			root = (Parent) loader.load(url.openStream());
  		} catch (IOException e) {
  			e.printStackTrace();
  		}
        
        Scene scene = new Scene(root);
 //       URL urlstyle = IECstage.class.getResource(fxml+"IECscene.css");
        scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/jkl/iec/tc/fxml/IECscene.css")).toExternalForm());
//        scene.getStylesheets().add(urlstyle);
    	
        IECstageController ic = loader.getController();
        ic.init(iob);
        setScene(scene);
	}
	
	private void getValueStageFile() {
//      IECTCItem i = iob.asdu;
      
      switch (iob.iectyp) {
      case M_ME_NA : case M_ME_NB : case M_ME_TB : case M_ME_TD : {
      	fxml = fxml +"ME-VAL.fxml";
      	break;   
      	}
      case M_ME_NC : case M_ME_TF : {
        	fxml = fxml +"IEEE-VAL.fxml";
        	break;   
        	}
      case M_SP_NA : case M_SP_TB : {
        	fxml = fxml +"SP-VAL.fxml";
        	break;   
        	}
      case M_DP_NA : case M_DP_TB : {
      	fxml = fxml +"DP-VAL.fxml";
      	break;   
      	}
      case M_IT_NA : case M_IT_TB : {
        	fxml = fxml +"IT-VAL.fxml";
        	break;   
        	}
		default:
			fxml = fxml +"DEF-VAL.fxml";
      }
	}
	
	private void getQuStageFile() {
      fxml = fxml +"QU.fxml";
	}
	
	private void getSimStageFile() {
	      if (IECMap.IEC_M_Type.contains(iob.iectyp)) {
	    	  fxml = fxml +"M-SIM.fxml";
	      }
	      if (IECMap.IEC_C_Type.contains(iob.iectyp)) {
	    	  fxml = fxml +"C-SIM.fxml";
	      }
		}	
}
