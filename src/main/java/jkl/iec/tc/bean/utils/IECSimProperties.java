package jkl.iec.tc.bean.utils;


import java.util.Properties;

import jkl.iec.tc.bean.type.IECMap;
import jkl.iec.tc.bean.type.IECTCItem;
import jkl.iec.tc.bean.type.IECMap.IECTyp;

public class IECSimProperties  {
	
	protected enum SimtimeProperty {cycleTime,fixTime,intervalTime}
	
	public static final String[] Props = {"SIM.PROPERTY","SIM.VAL_INC"};
	
	public class TimeProperties {
		public SimtimeProperty timetyp=null;
		public String TimeString;
		private String newTimeString;
		public int TimeInc;
		public int TimeInc2=-1;
		public String TimeErrorStr= "unknown";
		
		public Boolean isValidTime(String txt) {
//			newTimeString =TimeString.replaceAll(" ","");
			newTimeString =txt.replaceAll(" ","");
			if (newTimeString.isEmpty()) {
				TimeErrorStr ="Empty";
				return false;
			}
			if (isCycleTime()) { return true;}
			if (isIntervalTime()) { return true;}
			if (isfixTime()) { return true;}
			return false;
		}
			
		private Boolean isCycleTime() {
				int tmp1;
				if (newTimeString.startsWith("+")) {
					  try {
						  tmp1 = Integer.parseInt(newTimeString.substring(1,newTimeString.length()));
						  timetyp = SimtimeProperty.cycleTime;
						  TimeString = newTimeString;
						  TimeInc = tmp1;
						  TimeInc2 = -1;
						  return true;
					  } catch (NumberFormatException e) {
						  TimeErrorStr ="invalid cycle";
						  return false;
					  }
				  }
				return false;
		  }
		
		private Boolean isIntervalTime() {
			// check for time interval e.g. "5-10"  
			int tmp1;
			int tmp2;
			if (newTimeString.contains("-")) {
				String[] s =newTimeString.split("-");
				System.out.println("newTimeString split length:"+s.length);	
				if (s.length == 2) {
				  try {
//					  System.out.println("TimerStr length 2");	
					  if (!s[0].isEmpty()) {
						  tmp1 = Integer.parseInt(s[0]);
					  } else {
						  TimeErrorStr ="invalid interval (missing from)";
						  return false;
					  }
					  if (!s[1].isEmpty()) {
						  tmp2 = Integer.parseInt(s[1]);
					  } else {
						  TimeErrorStr ="invalid interval (missing till)";
						  return false;
					  }
					  if (tmp2 <= tmp1) {
						  TimeErrorStr ="invalid interval (from is equal or bigger as till)";
						  return false;
					  }
					  TimeString = newTimeString;
					  TimeInc = tmp1;
					  TimeInc2 = tmp2;
					  timetyp = SimtimeProperty.intervalTime;
					  return true;
				  } catch (NumberFormatException e) {
					  TimeErrorStr ="invalid interval (format)";
        				return false;
				  }		
				} else {
					TimeErrorStr ="invalid interval (missing times)";
					return false;
				}
			}
			return false;
		}
		
		private Boolean isfixTime() {
			int tmp1;
			try {
				  tmp1 = Integer.parseInt(newTimeString);
				  if (tmp1>59) {
					  TimeErrorStr ="invalid fix second (second is to big)";
					  	return false;
				  }
				  TimeString = newTimeString;
				  TimeInc = tmp1*-1;
				  TimeInc2 = -1;
				  timetyp = SimtimeProperty.fixTime;
				  return true;
			  } catch (NumberFormatException e) {
				  TimeErrorStr ="invalid format";
				  return false;	
			  }				
		}
		
	}
	
	public class ItemProperties {

	  public String BackString;
      public IECTyp backType;
	  public int backASDU;
	  public int backIOB;
	  public String itemErrorStr= "unknown";
		
		public Boolean isValidItem(String txt) {
			int tmpasdu;
			int tmpiob;
			IECTyp tmptype;
			if (txt == null) {
				itemErrorStr ="Empty";
				return false;
			}
			System.out.println("txt"+txt);
			if (txt.isEmpty()) {
				itemErrorStr ="Empty";
				return false;
			}
			if (!txt.contains("/")) {
				itemErrorStr ="seperator '/' not found";
				return false;
			}
			
			String[] s =txt.split("/");
			if (s.length>3) {
				try {
					if (s[1].equals("")) {
						tmpasdu = item.getASDU();	
					} else {
						tmpasdu = Integer.parseInt(s[1]);
					}
					if (s[2].equals("")) {
						itemErrorStr ="missing Type (pos.2)";
						return false;
					}
					tmptype = IECMap.getType(Byte.parseByte(s[2]));
					if (tmptype== null) {
						itemErrorStr ="invalid Type (pos.2)";
						return false;
					}
					tmpiob = Integer.parseInt(s[3]);
					backType =tmptype;
					backASDU =tmpasdu;
					backIOB = tmpiob;
					System.out.println("set back ASDU:"+backASDU+"  Type:"+backType+"  IOB:"+backIOB);
					System.out.println("set backstring:"+ txt);
					BackString = txt;
					return true;

				} catch (NumberFormatException e) {

					itemErrorStr ="invalid Number for ASDU(pos.1) or IOB(pos.3)";
					System.out.println(e);			
				}
			} else {
				itemErrorStr ="to less parameters !";
				return false;			
			}			
			return false;
		}
	}


  public boolean enabled;
  public long NextSimTime;
  
  public TimeProperties timeproperties = new TimeProperties();
  public ItemProperties itemproperties = new ItemProperties();

  private double Valinc;
  private double Valinc2=-1;

  public IECTCItem item;
  IECTyp old_type =null;
  
  public Properties getProperties() {
		Properties p = new Properties();
		String pre = "ITEM"+item.itemID+"."+"SIM.";
		if (IECMap.IEC_M_Type.contains(item.getIectyp())) {
			  p.setProperty(pre+"PROPERTY",timeproperties.TimeString);
		  } else {
			  p.setProperty(pre+"PROPERTY",itemproperties.BackString);			  
		  }
		p.setProperty(pre+"VAL_INC",String.valueOf(Valinc));
		return p;
	}
  
  public String getPropertyString() {
		String s = "";
		if (IECMap.IEC_M_Type.contains(item.getIectyp())) {
			  s=s+timeproperties.TimeString;
		  } else {
			  s=s+ itemproperties.BackString;			  
		  }
		s=s+";"+String.valueOf(Valinc);
		return s;
	}
  
  public void setProperties(Properties p) {
	  p.list(System.out);
//	  String pre = "ITEM"+item.itemID+"."+"SIM.";
	  String pre = "SIM.";
	  setValinc(Double.parseDouble(p.getProperty(pre+"VAL_INC",String.valueOf(Valinc))));
	 
	  if (IECMap.IEC_M_Type.contains(item.getIectyp())) {
		  System.out.print("setSimProperties "+pre +"Value "+Valinc+"  TIMER__:"+timeproperties.TimeString);
		  if (timeproperties.isValidTime(p.getProperty(pre+"PROPERTY",timeproperties.TimeString))) {
			  System.out.println("Time property OK"); 
		  } else System.out.println(timeproperties.TimeErrorStr);
	  } else {
		  System.out.print("SET SIM_BACKSTRING: "+p.getProperty(pre+"PROPERTY","??")+" value "+Valinc);
		  if (itemproperties.isValidItem(p.getProperty(pre+"PROPERTY",itemproperties.BackString))) {
			  System.out.println(" OK"); 
		  } else System.out.println(itemproperties.itemErrorStr);
	  }
	  old_type= item.getIectyp();
	}	
  
  public IECSimProperties(IECTCItem o) {
	  item = o;
	  setDefProps();
	  }

  public void setDefProps() {
	System.out.println("set Default sim props");
	switch (item.getIectyp()) {
	case M_SP_NA : case M_SP_TB :
	case M_DP_NA : case M_DP_TB :{
		  setValinc(1);
		  timeproperties.isValidTime("+10");
		  break;
	  }
	  case M_ME_NA : case M_ME_TB : case M_ME_NB : case M_ME_TD :{
		  setValinc(10);
		  timeproperties.isValidTime("+4");
		  break;
	  }
	  case M_ME_NC : case M_ME_TF : {
		  setValinc(11.1);
		  timeproperties.isValidTime("4-10");
		  break;
	  }
	  case M_IT_NA : case M_IT_TB : {
		  setValinc(11);
		  timeproperties.isValidTime("4");
		  break;
	  }
	  default :
		  setValinc(1);
		  timeproperties.isValidTime("+4");
	  }
	  old_type= item.getIectyp();
  }
  
  public String getBackString() {
	return itemproperties.BackString;
  	}  
  
  public String getTimerString() {
	return timeproperties.TimeString;
}

  public double getValinc() {
	return Valinc;
}

  public void setValinc(double v) {
	if (IECMap.IEC_M_Type.contains(item.getIectyp())) {
		setMValinc(v);
	} else {
		setCValinc(v);
	}

  }
  
  private void setMValinc(double v) {
/*
  	if (v> item.getIOB(0).getMAX_VALUE()) {
		v = item.getIOB(0).getMAX_VALUE();
	}

	if (v < item.getIOB(0).getMIN_VALUE()) {
		switch (item.getIectyp()) {
		case M_SP_NA: case M_SP_TB : 
		case M_DP_NA: case M_DP_TB : 
		case C_SC_NA : break; 
		default : v = -1;System.out.print("INC now: "+v);
		}
	}*/
	Valinc = v;
  }

  private void setCValinc(double v) {
		Valinc = v;
  }
}
