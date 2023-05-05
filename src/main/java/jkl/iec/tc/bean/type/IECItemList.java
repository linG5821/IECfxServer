package jkl.iec.tc.bean.type;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import jkl.iec.tc.bean.type.IECMap.IECTyp;
import jkl.iec.tc.bean.utils.IECFunctions;

//public class IECOList extends SimpleListProperty {
@SuppressWarnings("rawtypes")
public class IECItemList extends SimpleListProperty {

	public IIECTCStreamListener iectcactionlistener = null;
	public void setIECTCStreamListener(IIECTCStreamListener l) {
		 iectcactionlistener = l;
	 }
	
	private Timer timer ;
	byte[] buf =new byte[32];
	public final static Logger log = Logger.getLogger(IECItemList.class .getName()); 
    
	public class ThreadAction extends Thread {
		byte[] buf;
		int count;
		public ThreadAction(byte[] b, int c){
        	this.buf=b;
        	this.count = c;
        	start();
        }
		@Override
		public void run() {
			if (iectcactionlistener!=null) {
	    		log.info("send" );
	    		log.finer(IECFunctions.byteArrayToHexString(buf, 0, count));
	    		iectcactionlistener.doStream(buf, count);
	    	} else {
		    	log.info("null");	    		
	    	}
		}
	}
    
  class irq extends TimerTask {
	  boolean send;
	  IECTCItem item;
  @SuppressWarnings("deprecation")
	public void run() {
	    Date now =new Date();
//	    System.out.println("IRQ size"+size());
	    for (int it=0;it<size();it++) {
//	    for (IECTCItem it : items) {
	    	send=false;
	    	item =(IECTCItem) get(it);
	    	if (IECMap.IEC_M_Type.contains(item.getIectyp())) {
	    		if( (item.getIectyp()!= IECTyp.M_IT_NA) && (item.getIectyp()!= IECTyp.M_IT_TB) ) {
			    	boolean Valuechange = (item.getIOB(0).getVal() != item.getIOB(0).VALUE_TX);
		    	    boolean QUchange = (item.getIOB(0).getQU() != item.getIOB(0).QU_TX);
	    			if (Valuechange | QUchange) {  // send an M_Type on change
	    				send =true;
	    			}
	    		} else {  // send an M_Type Counter only once a minute
	    	    	log.finest("Counter to send ? last send was at min:"+ item.getIOB(0).Time_TX.getMinutes());
	    	    	if ( now.getMinutes()!= item.getIOB(0).Time_TX.getMinutes()) {
		    	    	send = true;
		    	    	}
	    			}
	    		}
//	    	System.out.println("Check "+((IECTCItem) get(it)).getIECtyp()+" send "+send);
			if (send) {
//	    		log.info("send "+((IECTCItem) get(it)).printStream());
    			buf = ((IECTCItem) get(it)).getStream();
    			new ThreadAction(buf ,buf.length);
  		
    			item.getIOB(0).VALUE_TX = item.getIOB(0).getVal();
    			item.getIOB(0).QU_TX = item.getIOB(0).getQU();
    			item.getIOB(0).Time_TX = now;
				}
			}
	  }
	}
	
	@SuppressWarnings("unchecked")
	public IECItemList() {
        super(FXCollections.observableArrayList());
	}
	
    public IECTCItem getIECItem(IECTyp t,int asdu,int addr) {
   	 log.fine("Search Item "+t+"_"+asdu+"_"+addr);
      	 for (int it=0;it<size();it++) {
      		IECTCItem i = (IECTCItem) get(it);
      		if ((i.getIectyp() == t) &&
       				 (i.getASDU()== asdu) &&
       				 (i.getObject0().getaddr() == addr)) {
       			 return i;
       		 }
   	 }
   	 return null;
    }
    
    public void start(){
		if (timer == null) {
			timer = new Timer();
		}
		timer.scheduleAtFixedRate(new irq(), 1000,500);		
	}

	public void stop(){
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
