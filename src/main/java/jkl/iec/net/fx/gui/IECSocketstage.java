package jkl.iec.net.fx.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jkl.iec.net.sockets.IECSocketParameter;

public class IECSocketstage extends Stage {
	
	IECSocketParameter p;
    
	Parent root = null;
    final FXMLLoader loader = new FXMLLoader();
    final String fxml = "/jkl/iec/net/fxml/IECSocketDlg.fxml";
    
	public IECSocketstage(String n, IECSocketParameter p) {
//        scene.getStylesheets().add(urlstyle);
        URL url = IECSocketstage.class.getResource(fxml);
        
        try {
//  		root = (Parent) loader.load(IECstage.class.getResource(fxml).openStream());
 			root = (Parent) loader.load(url.openStream());
  		} catch (IOException e) {
  			e.printStackTrace();
  		}
		
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED); 
        setResizable(false);
        
        IECSocketStageController ic = loader.getController();
        ic.init(n,p);
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/jkl/iec/tc/fxml/IECscene.css")).toExternalForm());
        setScene(scene);
	}

}
