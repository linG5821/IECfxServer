package jkl.iec.tc.application;

import java.awt.event.ActionEvent;

import javafx.application.Platform;

import jkl.iec.net.sockets.IECSocket;
import jkl.iec.net.sockets.IIECnetActionListener;
import jkl.iec.net.sockets.IECServer.IECServerAction;
import jkl.iec.tc.bean.type.IECMap.IECTyp;
import jkl.iec.tc.bean.type.IECTCItem;
import jkl.iec.tc.bean.type.IECTCItemWorker;
import jkl.iec.tc.bean.type.IIECTCStreamListener;

public class IECTCEventListener implements IIECTCStreamListener,IIECnetActionListener {

	private boolean awnserUnsupported = true;

	public void doStream (IECTCItem i) {
		 byte[] buf = i.getStream();
		 doStream(buf, buf.length);
	}
	
	@Override
	public void doStream(byte[] b, int c) {
		Server.log.fine("IECTCStreamListener:doStream");
		if (Server.iecserver != null) {
			Server.iecserver.send(b, c);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(IECServerAction.IECServerClientConnect.toString())) {
//			Server.Clientpanel.addClient(iecSock);
			}		
	}

	@Override
	public void onReceive(IECSocket sender, byte[] b, int len) {
		Server.log.fine("IECTCStreamListener:onReceive");
		IECTCItem itemreceived = new IECTCItem(b, len);
		itemreceived.setName("DUMMY");
		if (itemreceived.getIectyp()==IECTyp.IEC_NULL_TYPE) {
			Server.log.warning("received unsupported Type!: ");
		  // setCOT(0x45);
		  if (awnserUnsupported ) {
			  b[2] = 0x44;
			  doStream(b, len);
		  }
   		return;
   	 }
		if (itemreceived.getCOT()==6) {
			Server.itemworker.process(itemreceived);
			}
		else {  
			System.out.println("COT NOT 6 nothing to do! "+itemreceived.getCOT());
		    }
	}

}
