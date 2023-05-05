package jkl.iec.tc.bean.utils;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import jkl.iec.tc.bean.type.IECMap;
import jkl.iec.tc.bean.type.IECItemList;
import jkl.iec.tc.bean.type.IECTCItem;
import jkl.iec.tc.bean.type.IECMap.IECTyp;


public class IECSimPlayer {
	
	private Timer timer = null;
	private BufferedReader br;
	private FileReader fr ;
	
	private IECItemList ieclist =null; 
	
	class player extends TimerTask {
//		Properties p;
		private int newTime =2000; 
		private Boolean PlayFileEnd = false;
		
		private int ASDU = 0;
		private int IOB = 0;
		private IECTyp iectype = null;
		private int qu = 0;
		private Float Value = null;
		
		public void run() {
			System.out.println("Player Create ");
//			p = new Properties();
			doline();
			while(!PlayFileEnd) {
//				System.out.println("Player SLEEP("+newTime+")");
				try {
					Thread.sleep(newTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				doline();
			} 
//			System.out.println("LAST LINE -> EXIT");
			try {
				fr.close();
				System.out.println("Player destroyed ");	
			} catch (IOException e) {
				System.out.println("Player destroy ERROR: "+e);	
			} 
	    	timer.cancel();
			}
	    
// EXAMPLE of an IECSimFile Line
//
//" item=/9/1/4097;value=1 "
//		
// only created items will be Simulated
		
		private boolean isValidLine(String txt) {
			String[] param;
			String[] keyval;
			String Stream = null;
			
			newTime =2000;
			
			ASDU = 0;
			IOB = 0;
			iectype = null;
			qu = 0;
			Value = (float) 0;
			
//			System.out.println("LINE_"+lineNo+ ": "+txt);
			if (txt == null) {
/**				try {
					br.reset();
				} catch (IOException e) {
					e.printStackTrace();
				}
**/
				PlayFileEnd = true;
			}
				
			if(txt != null) {
				param =txt.split(";");
//				System.out.println("LINE_parms "+param.length);
				for(int i =0; i < param.length ; i++){
					keyval = param[i].split("=");
				    if (keyval[0].equals("break")) {
				    	newTime =Integer.parseInt(keyval[1]);
				    }
				    if (keyval[0].equals("value")) {
				    	Value =Float.parseFloat(keyval[1]);
				    }
				    if (keyval[0].equals("QU")) {
				    	qu =Integer.parseInt(keyval[1]);
				    }
				    if (keyval[0].equals("item")) {
				    	Stream = keyval[1];
				    	keyval = Stream.split("/");
				    	iectype = IECMap.getType((Byte.parseByte(keyval[1])));
				    	ASDU = Integer.parseInt(keyval[2]);
				    	IOB = Integer.parseInt(keyval[3]);
				    }
				}
//			System.out.println("Validate : Type "+iectype+" ASDU "+ASDU+" IOB "+IOB+ "  Val "+Value+"  QU "+qu+" Player SLEEP("+newTime+")"); 	
			return ((iectype != null)&&(ASDU != 0)&&(IOB !=0));
			}
		return false;	
		}

		
	    private void doline() {
			String txt = "";
			IECTCItem item =null;
      try {
				while (!isValidLine(txt)&&(PlayFileEnd == false)) {
					txt = br.readLine();
//					System.out.println("LINE_"+lineNo+ ": "+txt);
				}
				if (PlayFileEnd == false) {
					System.out.println("Player: Type "+iectype+" ASDU "+ASDU+" IOB "+IOB+ "  Val "+Value+"  QU "+qu+" Player SLEEP("+newTime+")"); 	
					if (ieclist !=null) {
//						item = ieclist.getIECStream(iectype, ASDU, IOB);
						if (item !=null) {
//							System.out.println("Type in LIST --> SIMULATE");
							item.getIOB(0).setVal(item.getIOB(0).getVal()+Value);
							item.getIOB(0).setQU((byte) qu);
//							item.iob(0).s
						}
					}
					} 

			} catch (IOException e) {
				System.out.println(e);
			}
	    }
	}
	
	public boolean play(String filename){
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
//			System.out.println("Player Create ");
			timer = new Timer();
			timer.schedule(new player(), 10);
			return  true;
		} catch (FileNotFoundException e) {
			System.out.println("Player Not Create : ERROR "+e);
			return  false;
		}
  }

	public IECItemList getIeclist() {
		return ieclist;
	}

	public void setIeclist(IECItemList ieclist) {
		this.ieclist = ieclist;
	}
}