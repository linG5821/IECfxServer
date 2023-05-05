package jkl.iec.tc.bean.type;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

import javafx.application.Platform;

import jkl.iec.tc.bean.type.IECMap.IECTyp;

public class IECTCItemWorker {

	public final static Logger log = Logger.getLogger(IECTCItemWorker.class .getName());
	
	private IECItemList itemlist = null;
	private IIECTCStreamListener listener;
	
	class updateItem implements Runnable{
		private IECTCItem updateitem;
		private int method;
		private IECTCItem sourceitem;
		
		updateItem(IECTCItem i,int m,IECTCItem s) {
			this.updateitem = i;
			this.method =m;
			this.sourceitem =s;
		}
		
		@Override
		public void run() {
			log.info(String.valueOf(method));
			if (method == 0 ){
				updateitem.getObject0().setVal(sourceitem.getObject0().getVal());
			}
			if (method == 1 ){
				updateitem.setCOT(sourceitem.getCOT());	
			}
			if (method == 2 ){
				updateitem.getObject0().setVal(sourceitem.getObject0().getVal());
				updateitem.setCOT(sourceitem.getCOT());	
			}
		}
		
	}
	
	public IECTCItemWorker (IECItemList l) {
		this.itemlist = l;
	}
	
	public void doStream (IECTCItem i) {
		if (listener != null) {
			byte[] buf = i.getStream();
			listener.doStream(buf, buf.length);
		}
	}
	
	public void process(IECTCItem item) {
		if (itemlist == null) {
			return;
			}
		
    	IECTCItem listitem = itemlist.getIECItem(item.getIectyp(),item.getASDU(),item.getObject0().getaddr());

    	if (item.getIectyp() == IECTyp.C_IC_NA) {
    		if (listitem != null) {
    			Platform.runLater(new updateItem(listitem ,0, item));
    		}
			doIC(item);
			return;
		}
    	
    	if (item.getIectyp() == IECTyp.C_SC_NA) {
    		if (listitem == null) {
    			log.warning("ITEM NOT IN LIST --> nactcon");
    			item.setCOT(0x47);
    			doStream(item);
    			return;
     			}
    		processCommand(item ,listitem );
		}		
		
	}
	
	public void processCommand(IECTCItem item, IECTCItem listitem ) {
        Platform.runLater(new updateItem(listitem ,0, item));
		item.setCOT(0x07);
		doStream(item);
		if (item.getObject0().isPersistent()) {return; }
		
 		log.finer("_QOC: "+String.valueOf(item.getObject0().QOC));
		long t=100000;
		if (item.getObject0().isShortPuls()) {
		 t=5000;	
		} 
 		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		item.setCOT(0x0A);
		doStream(item);		
	}
    
    private void doICType(IECTCItem i,Set<IECTyp> set) {
   	int hit =0;
   	log.fine("IECTypes "+set);
   	for (int it=0 ; it < itemlist.size(); it++) {
   		IECTCItem li = (IECTCItem) itemlist.get(it);
   		if ((set.contains(li.getIectyp())) &&
				( li.getASDU() == i.getASDU())) {
//				System.out.println("GS ITEM Found!");
				hit++;
				if (hit > 1) {
					i.addIOB();
				} 
//				IECTCObject.copy(li.getObject0(), i.getIOB(hit-1));
				log.finest("Item found add as Obj. No:"+String.valueOf(hit));
//				doStream(li);
			}
		}
		log.fine("Items in GS answer "+String.valueOf(hit)); 
		//send only if items in stream (hit >0)
		if (hit >0 ) {
			doStream(i);
		}
    }
    
	private void doIC(IECTCItem gs) {
//System.out.println("General Scan started!"); 
    	gs.setCOT(0x07);
		doStream(gs);	    	
    	Set<IECTyp> set;
		IECTCItem i= new IECTCItem();
	    i.setCOT((int) gs.getIOB(0).getVal());
	    i.setASDU(gs.getASDU());
    	log.info("General Scan regular started!  COT/ASDU "+i.getCOT()+"/"+i.getASDU());

	    i.setName("GS_SP_DUMMY");
	    i.setIectyp(IECTyp.M_SP_NA);
		set = EnumSet.of(IECTyp.M_SP_NA,IECTyp.M_SP_TB);
		doICType(i,set);

		i.setName("GS_DP_DUMMY");
	    i.setIectyp(IECTyp.M_DP_NA);
		set = EnumSet.of(IECTyp.M_DP_NA,IECTyp.M_DP_TB);
		doICType(i,set);

		i.setName ("GS_MEn_DUMMY");
	    i.setIectyp(IECTyp.M_ME_NA);
	    i.setIOBCount(1);
	    set = EnumSet.of(IECTyp.M_ME_NA,IECTyp.M_ME_TB);
		doICType(i,set);

		i.setName("GS_MEs_DUMMY");
	    i.setIectyp(IECTyp.M_ME_NB);
	    i.setIOBCount(1);
	    set = EnumSet.of(IECTyp.M_ME_NB,IECTyp.M_ME_TD);
		doICType(i,set);

		i.setName("GS_MEiee_DUMMY");
		i.setIectyp(IECTyp.M_ME_NC);
	    i.setIOBCount(1);
	    set = EnumSet.of(IECTyp.M_ME_NC,IECTyp.M_ME_TF);
		doICType(i,set);
		
		gs.setCOT(10);
    	log.fine("General Scan regular end!");
    	
    	doStream(gs);	
		
    }

	public IIECTCStreamListener getListener() {
		return listener;
	}

	public void setListener(IIECTCStreamListener listener) {
		this.listener = listener;
	}
    
}
