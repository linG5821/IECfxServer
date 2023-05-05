package jkl.iec.tc.application;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class IECServerTraceHandler extends ConsoleHandler {
	
	public TextArea textArea =null;

	public IECServerTraceHandler() {
		setFormatter(new IECLoggFormatter());
		setLevel(Level.FINE);
	}

	public void publish(final LogRecord record) {
		Runnable  runnable = new Runnable() {
            public void run(){
               	String formattedMessage ;
        		if (!isLoggable(record)) {
        			return;
        		}
        		formattedMessage = getFormatter().format(record);
        		textArea.appendText(formattedMessage);
  //      		textArea.setCaretPosition(textArea.getDocument().getLength());           	
               }};
//        SwingUtilities.invokeLater(runnable);
          if (textArea != null) {
        	  Platform.runLater(runnable);
          }
	}

}
