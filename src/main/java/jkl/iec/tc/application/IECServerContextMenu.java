package jkl.iec.tc.application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import jkl.iec.net.fx.gui.IECSocketstage;

public class IECServerContextMenu extends ContextMenu {
	final MenuItem settings = new MenuItem("settings");
    final MenuItem s2 = new MenuItem("s2");
    
    IECServerContextMenu() {
    	getItems().addAll(settings,s2);
    	
    	settings.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
              System.out.println("Resize requested");
              IECSocketstage sockDlg = new IECSocketstage("??",Server.SocketParameter);
              sockDlg.show();
            }
          });
    }

}
