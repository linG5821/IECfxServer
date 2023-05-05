package jkl.iec.tc.bean.type;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import jkl.iec.tc.bean.utils.IECFunctions;
import jkl.iec.tc.bean.type.IECMap.IECTyp;
import jkl.iec.tc.bean.type.IECTCObject;
import jkl.iec.tc.bean.utils.IECSimProperties;

/**
 * DOS not support Block packet TC Streams 
 * 
 * @author jaen
 *
 */

public class IECTCItem {
	
//	public static boolean P_SHORT = true; 
	public static boolean P_SHORT =false; 
	public static boolean Respone_Unknown = true;
	public static int globalID=1;
	
	public static final String[] Props = {"NAME","TYPE","ASDU","COT","IOB","VALUE","SIMULATE"};

//	private String name;
	private SimpleStringProperty name = new SimpleStringProperty();
	public SimpleStringProperty nameProperty() { return name;}
	public String getName() {
		return name.get();
	}
	public void setName(String s) {
		log.info(""+s);
		name.set(s);
	}

	public int itemID=1;
	private static int iob=1;
	
//	private IECTyp iectyp= IECTyp.IEC_NULL_TYPE;
	private SimpleObjectProperty<IECTyp> iectyp= new SimpleObjectProperty<IECTyp>(IECTyp.IEC_NULL_TYPE);
	public SimpleObjectProperty<IECTyp> iectypProperty() {return iectyp;}

	private SimpleBooleanProperty M_typ = new SimpleBooleanProperty(true);
	public SimpleBooleanProperty M_typProperty() {return M_typ;}
	
	public Boolean isM_typ() {
		return M_typ.get();
	}
	public void setM_typ(boolean b) {
		M_typ.set(b);
	}
	
	private SimpleStringProperty iecname = new SimpleStringProperty();
	public SimpleStringProperty iecnameProperty() { return iecname;}
	public String getIecname() {
		return iecname.get();
	}	
	public void setIecname(String s) {
		iecname.set(s);
		if (IECMap.getType(s)!= getIectyp()) {setIectyp(IECMap.getType(s));}
	}	
//	private Integer iASDU = new Integer(1);
	
	private SimpleIntegerProperty ASDU = new SimpleIntegerProperty();
	public SimpleIntegerProperty ASDUProperty() {return ASDU; }
	
	public int getASDU() {
		return ASDU.get();
	}

	private SimpleIntegerProperty COT = new SimpleIntegerProperty();
	public SimpleIntegerProperty COTProperty() {return COT; }
	
	public int getCOT() {
		return COT.get();
	}


	byte[] Stream = new byte[250];
	int StreamLength;
	public Object data = null;
//	public boolean flag1 =false;	
	
	private SimpleBooleanProperty flag1 = new SimpleBooleanProperty(false);	
	public  SimpleBooleanProperty flag1Property() {return flag1;}
	public boolean getFlag1() {
		return flag1.get();
	}
	public void setFlag1(boolean b) {
		flag1.set(b);
	}
	
//	ArrayList<IECTCObject> lIOB = new ArrayList<IECTCObject>();

//	private SimpleListProperty<IECTCObject> oList = new SimpleListProperty<IECTCObject>();
//	public SimpleListProperty<IECTCObject> oListeProperty() { return oList;}
	
    private ObservableList<IECTCObject> IOB = FXCollections.observableList(new ArrayList<IECTCObject>());
//    private ObservableList<IECTCObject> IOB = FXCollections.observableList(lIOB);
//	private SimpleListProperty<IECTCObject> IOB = new SimpleListProperty<IECTCObject>(FXCollections.observableArrayList(),"IOB");
	
    private SimpleObjectProperty<IECTCObject> object0 = new SimpleObjectProperty<IECTCObject>();
	public SimpleObjectProperty<IECTCObject> object0Property() {return object0; }
	
	public IECTCObject getObject0() {
//		return getIOB(0);
		return object0.get();
	}
	
	public void setObject0(IECTCObject o) {
		object0.set(o);
	}
	
	public final static Logger log = Logger.getLogger(IECTCItem.class .getName()); 

	private void init() {
		itemID=globalID++;
		setName("Item"+itemID);
		setCOT(3);
		IECTCObject o = new IECTCObject(this,iob++);
//		IECTCObject o = new IECTCObject(this,ID);
		addIOB(o);
		log.finer(getIectyp().name());
		System.out.println(IOB.get(0));
	}
	
	/**
	 * Create default IECStream with ONE IECObject<br><br>
	 * <b>Default Values:</b><br>
	 * IECType = M_SP_NA<br>
	 * ASDU = 1<br>
	 */
	public IECTCItem() {
		setIectyp(IECTyp.M_SP_NA);
		setASDU(1);
		init();
	}

	public IECTCItem(IECTyp iectyp) {
		setIectyp(iectyp);
		setASDU(1);
		init();
		}

	/**
	 * Create new IECStream with ONE IECObject<br>
	 */
	public IECTCItem(IECTyp iectype,int ASDU) {
		setIectyp(iectype);
		setASDU(ASDU);
		init();
		}
	
	/**
	 * Create new IECStream from an byte stream<br>
	 */  	
	public IECTCItem(byte[] b,int length) {
//		System.out.println("create item from Stream len: "+length);
		log.finer("create item from Stream len: "+length);
 	//  DECODE an recieved stream 
		Stream[0] = b[0];
		Stream[1] = 0;
//		readType();
		setIectyp(IECMap.getType(Stream[0])); 
//		IECTCObject o = new IECTCObject(this);
//		addIOB(o);	
		setIOBCount(b[1]);		

//		byte[] b_long = new byte[length+20]; 
		
		int index;		
		if (P_SHORT) {
			log.finer("Short profil (P_SHORT) aktiv Delete bytes");
			System.arraycopy(b,0,Stream,0,3);
			Stream[3] =0;
			System.arraycopy(b,3,Stream,4,length-3);
			length++;
			index =5;   
		} else {
			System.arraycopy(b,0,Stream,0,length);
			index=6;  
		}
		
//		Stream = b_long;
		StreamLength= length;
		readASDU();
		readCOT();
		
		IECTCObject o = getIOB(0);
		o.buf = Arrays.copyOfRange(b,index, b.length);  // copy rest of stream 
		if (getIectyp() != IECTyp.IEC_NULL_TYPE) {
			o.readaddr();
			o.readValue();
			o.readQU();
			o.readTime();
	        crcLength(length);
	    }
		log.log(Level.INFO,"create item [TYPE:{0} count:{1} ASDU:{2} COT:{3}  Addr:{4}] ",
				new Object[]{iectyp.get(),getIOBCount(),ASDU.get(),getCOT(),o.getaddr()});
	}	

   public int MaxObjects() {
//	   IECTCObject item = new IECTCObject(getIectyp());
	   return 240/ new IECTCObject(getIectyp()).getBufLength();
//	   return 240/ getIOB(0).getBufLength();
	  }
   
	/**
  	 * 
  	 * @param length
  	 * @return
  	 */
	 public boolean crcLength(int length) {
		 int index=6;
		 if (P_SHORT) {
			 length--;
			 index =5; 
		 }
		 IECTCObject o = getIOB(0);
		 int l =  o.getBufLength();
		 if (length == l * getIOBCount() +index) {
			 log.log(Level.FINE,"Stream Length:{2} (Head.length[{0}] + IOB.count[{3}]*IOB.length[{1}])",new Object[]{index,l,l+index,getIOBCount()});
			 return true;
		 } 
		 log.log(Level.SEVERE,"Stream Length:{3} should {2} (Head.length[{0}] + IOB.count[{4}]*IOB.length[{1}])",new Object[]{index,l,l * getIOBCount()+index,length,getIOBCount()});
		 return false;
	}

	public void addIOB() {
		 IECTCObject item = new IECTCObject(this,0);
		 addIOB(item);
	 }

	public void addIOB(IECTCObject item) {
		log.finest(String.valueOf(item.getaddr()));
		IOB.add(item);
        if (IOB.size()==1) {
        	setObject0(item);
        }
 /// save number of items in stream[1],
		Stream[1]= (byte) IOB.size();
	 }
	 
	 public IECTCObject[] getIOB() {
			return IOB.toArray(new IECTCObject[IOB.size()]);
//			return IOBa;
	 }	 

	 public IECTCObject getIOB(int index) {
			if (index <= IOB.size()) {return IOB.get(index);}
			return null;
//			return IOBa[index];
		 }
	
    public void setIOB(IECTCObject[] iob) {IOB.addAll(iob);}
//	public void setIOB(IECTCObject[] iob) {lIOB = (ArrayList<IECTCObject>) Arrays.asList(iob);}
	public void setIOB(IECTCObject iob,int index) {
		IOB.add(index,iob);
		Stream[1]= (byte) IOB.size();
	}
	 
	public IECTyp getIectyp(){
			return iectyp.get();
	}
		
	public void setIectyp(IECTyp t) {
		log.fine(t.toString());
		Stream[0] = t.tk();	
		for (IECTCObject ob : IOB) {
				ob.iectyp=t;
				ob.setDefLimits();   //Reset The Limits
				ob.setQU((byte) 0);  //Reset The Quality
		}
//		System.out.println("IOBCount :"+IOB.size());		
		iectyp.set(t);
		setIecname(IECMap.getTypeDescription(t));
		if (IECMap.getTypeDescription(t) != iecname.get()) {setIecname(IECMap.getTypeDescription(t));}
		
		if (IECMap.IEC_M_Type.contains(t)) {
			setM_typ(true);
		} else {
			setM_typ(false);
		}
	}
	
	public void readType() {
		setIectyp(IECMap.getType(Stream[0]) );
		if (getIectyp() == IECTyp.IEC_NULL_TYPE) {
			log.warning("Stream-Type: "+iectyp);
		} else {
			log.finest("Stream-Type: "+iectyp);
		}
	}
	
	/**
	 * Ceate 'c' IECTCObject s for this stream
	 * @param c = Number of Objects to create
	 * @throws Exception 
	 */
	
	public void setIOBCount(int c) {
		if (c > MaxObjects()) {
			throw new IllegalArgumentException("Number of Elements exides Max Element numbers: ");
		}
		byte count = (byte) c;
//		System.out.println("setIOBCount "+c);
		if (count!=Stream[1]) {
			while (IOB.size()< count) {
//				IECTCObject item = new IECTCObject(this,iob++);
				addIOB();
			}
			while (IOB.size()> count) {
// ??				IOB.get(IOB.size()-1) =null;
				IOB.remove(IOB.size()-1);
			}
		}
		
		if (count!=Stream[1]) {
			Stream[1]= count;
		}
	}
	
	public int getIOBCount() {
		log.finest(String.valueOf(Stream[1]));
		return Stream[1];
	}
	
	public void setCOT(int cot) {
    	if (cot > 65535) {
			cot = 65535;
		}
    	if (cot < 1) {
			cot = 1;
		}
    	log.finer(String.valueOf(cot));
    	COT.set(cot);
    	Stream[2] =	(byte) (cot % 256);
		Stream[3] = (byte) (cot / 256);	
	}

	public void setASDU(int asdu) {
    	if (asdu > 65535) {
			asdu = 65535;
		}
    	if (asdu < 1) {
			asdu = 1;
		}
		
    	this.ASDU.set(asdu);
    	
		int index;
		if (P_SHORT) {
			index =3;    	
		} else {
			index=4;    	
		}
//		Stream[index] =	(byte) (ASDU % 256);
//		Stream[index +1] = (byte) (ASDU / 256);					
		Stream[index] =	(byte) (asdu % 256);
		Stream[index +1] = (byte) (asdu / 256);					
	}
	
		
	/**
	 *  set IECStream-ASDU by reading out of the Stream
	 */
	private void readASDU() {
//		System.out.println(c+"[4]"+ (int) Stream[4] +"[5]"+ (int) Stream[5] +"getASDU "+re);
		int index=4;    	
//		ASDU = ((Stream[index+1] & 0xFF) << 8) | (Stream[index] & 0xFF);
		ASDU.set( ((Stream[index+1] & 0xFF) << 8) | (Stream[index] & 0xFF) );
//		log.finest("Stream-ASDU: "+ASDU);
		log.finest("Stream-ASDU: "+getASDU());
	}
	
	private void readCOT() {
		int cot;
		if (P_SHORT) {cot =  (Stream[2] & 0xFF);       	
		} else {cot =  ((Stream[3] & 0xFF) << 8) | (Stream[2] & 0xFF);   	
		}
		setCOT(cot);
	}
	/**
	 * Return an Byte buffer that contains an ready to send Stream of this object
	 */
	public byte[] getStream() {
//		System.out.println("getIOB_Buffer");
		if (getIectyp()==null) {  // Type NOT supported
		   if (Respone_Unknown) {
			   setCOT(0x44);
				if (P_SHORT) {
					byte[] Stream_s = Arrays.copyOf(Stream,StreamLength);
					System.arraycopy(Stream,4,Stream_s,3,StreamLength-4);    	
					return Arrays.copyOf(Stream_s,StreamLength-1);
				} else {
					return Arrays.copyOf(Stream,StreamLength);
				}
		   } else {
			   return null;
		   }
		}  		
//  Known Type 
		byte[] Stream_s = Arrays.copyOf(Stream,StreamLength+200);
		int asdu = getASDU();
		Stream[5] =	(byte) (asdu % 256);
		Stream[5] = (byte) (asdu / 256);			
		Stream_s[3] =	(byte) (asdu % 256);
		Stream_s[4] = (byte) (asdu / 256);			
		int index =6;
		if (P_SHORT) {
			index =5;    	
		}
		int indexIOB = index;
		byte[] b = null;
		int bl=0;
		int i=getIOBCount();
		for (int it=0;it<i;it++) {
			b = getIOB(it).getStream();
//			System.out.println("IOB_index "+indexIOB+"   IOB_Stream "+IECFunctions.byteArrayToHexString(b,0,b.length));
			if (P_SHORT) {
				System.arraycopy(b,0,Stream_s,indexIOB,b.length);    	
			} else {
				System.arraycopy(b,0,Stream,indexIOB,b.length);    	
			}
			indexIOB =indexIOB +b.length;
			bl = b.length;
		}
//		System.out.println("Stream "+Functionsn.byteArrayToHexString(Stream,0, 6+ getIOBCount()*(bl)));
//		System.out.println("_STREAM: "+StreamLength+printStream());
		int l = index+ i *(bl);
		byte[] result;
		String s;
		if (P_SHORT) {
			s="s";
			result=Arrays.copyOf(Stream_s,l);
//			return Arrays.copyOf(Stream_s,l);
		} else {
			s="S";
			result=Arrays.copyOf(Stream,l);
//			System.out.println("b.length "+b.length+"  LENGTH "+l);
//			return Arrays.copyOf(Stream,l);
		}
		log.finer(s+":"+l+"[ "+IECFunctions.byteArrayToHexString(result,0,result.length)+"]");
		return result;
	}
	
	/**
	 * !! If the Requested TCType is an counter this will increase the Sequence !!
	 * @return HEX String of the IECTCItem
	 */
	public String printStream() {
//		System.out.println("IEC mesage "+Functionsn.byteArrayToHexString(Stream,0,StreamLength));
		byte [] buf = getStream();
		return IECFunctions.byteArrayToHexString(buf,0,buf.length);
	}
	
	public void setProperties(Properties p) {
//		p.list(System.out);
		Enumeration<?> it = p.propertyNames();
		String key;
		while (it.hasMoreElements()) {
			key = it.nextElement().toString();
			setProperty(key,p.getProperty(key));
		}
		if (data.getClass()== IECSimProperties.class) {
			IECSimProperties s = (IECSimProperties) data;
			s.setProperties(p);
		}
	}

	public String getProperty(int index) {
		if (index < Props.length) {
			return getProperty(Props[index]);
			}
		return null;
		}
	
	private String getProperty(String p) {
		switch (p) {
		case "NAME" : return getName();
		case "TYPE" : return String.valueOf(getIectyp().tk());
		case "ASDU" : return String.valueOf(getASDU());
		case "COT" : return String.valueOf(getCOT());
		case "IOB" : return String.valueOf(getIOB(0).getaddr());
		case "VALUE" : return String.valueOf(getIOB(0).getVal());
		case "SIMULATE" : return String.valueOf(getFlag1());
		}
		return "??";
	}
	
	private void setProperty(String key,String value) {
		log.finest(key+":"+value);
		switch (key) {
		case "NAME" : {
			setName(value);
			break;
			}
		case "TYPE" : {
			setIectyp(IECMap.getType(Byte.parseByte(value)));
			break;
			}
		case "ASDU" : {
			setASDU(Integer.parseInt(value));
			break;
			}
		case "COT" : {
			setCOT(Integer.parseInt(value));
			break;
			}
		case "IOB" : {
			getObject0().setaddr(Integer.parseInt(value));
			break;
			}
		case "VALUE" : {
			getObject0().setVal(Double.parseDouble(value));
			break;
			}
		case "SIMULATE" : {
			setFlag1(Boolean.parseBoolean(value));
			break;
			}
		}
	}
	
    /**
     * return the Properties of IEC steam including properties of first IEC Object 
     */
	public Properties getProperties() {
		Properties p = new Properties();
		String pre = "ITEM"+String.valueOf(itemID)+".";
//		p.setProperty(pre+"NAME",name);
		for (String s: Props) {
			p.setProperty(pre+s,getProperty(s));
		}
		
		if (data.getClass()== IECSimProperties.class) {
   				IECSimProperties sim = (IECSimProperties) data;
   				p.putAll(sim.getProperties());   				
   			}
//		p.list(System.out);
		return p;
	}

	public static String getPropertyString(IECTCItem i) {
		String result = "ITEM"+String.valueOf(i.itemID)+"=";
		for (String s: Props) {
			result = result + i.getProperty(s)+";";
		}
		result = result.substring(0, result.length()-1);
		
		if (i.data.getClass()== IECSimProperties.class) {
   				IECSimProperties sim = (IECSimProperties) i.data;
   				result =result +";"+sim.getPropertyString();   				
   			}
		return result;
	}

/**
**  Separates an Item with more than one Objects to an item list where each Item contains only ONE Object  
*	 @param i = IEC Item with more than one objects
* 	 @return IECItem list where each Item contains only ONE Object
 **/
 	public static ArrayList<IECTCItem> Seperate(IECTCItem i) {
		log.finest("");
		IECTyp type = i.getIectyp();
		int asdu =i.getASDU();
		int cot =i.getCOT();
		ArrayList<IECTCItem> result = new ArrayList<IECTCItem>();
		IECTCItem t;
		IECTCObject o;
		int c = i.getIOBCount();
		log.finest("Object count"+c);
		for (int x=0; x<c ;x++) {
			t =new IECTCItem(type,asdu);
			t.setCOT(cot);
			o = i.getIOB(x);
			t.getIOB(0).setaddr(o.getaddr());
			t.getIOB(0).setQU(o.getQU());
			t.getIOB(0).setVal(o.getVal());
			t.getIOB(0).setTime(o.getTime());
//			Seperate.add(t);
			result.add(t);
		}
		return result;
	}

 	/**
 	**  Packs a list of IECItems contains one ore a little objects to an list of IECItems that hold an maximum possible number of objects  
 	*	 @param itemarray = IECItem list 
 	*    @return IECItem list of IECItems that hold an maximum possible number of objects
 	**/
 	public static ArrayList<IECTCItem> Pack(ArrayList<IECTCItem> itemarray) {
		int TCItemcount = itemarray.size();
		log.info(TCItemcount+" TCItems in Input array");
		if (TCItemcount ==0) return null;
		ArrayList<IECTCItem> result = new ArrayList<IECTCItem>();
		IECTCItem i = itemarray.get(0);
		IECTyp iectype = IECTyp.IEC_NULL_TYPE;
		int asdu = 0 ;
		int cot = 0 ;
		int MAX = 0 ;
		IECTCItem i2 = null;
		boolean clone;
		for (int x=0 ; x < TCItemcount; x++) {
			clone= true;
			i = itemarray.get(x);
			log.finest("TCItem "+x);
			if ((i.getIectyp()!= iectype)||(i.getASDU()!=asdu)||(i.getCOT()!=cot)) {
				if (i2 != null) result.add(i2);
				iectype = i.getIectyp();
				asdu =i.getASDU();
				cot =i.getCOT();
//				MAX = 8;
				MAX = i.MaxObjects();

				i2 = new IECTCItem(iectype,asdu);
				i2.setCOT(cot);
				IECTCObject.copy(i.getIOB(0),i2.getIOB(0));
				log.info("Paramter changed create new TCItem "+iectype.name());	
				clone= false;
			}
			if (i2.getIOBCount()>=MAX) {
				if (i2 != null) result.add(i2);
				i2 = new IECTCItem(iectype,asdu);
				i2.setCOT(cot);
				IECTCObject.copy(i.getIOB(0),i2.getIOB(0));
				log.info("MAX Objects >"+MAX+" create new TCItem "+iectype.name());
				clone= false;
			}
			if (clone) {
				try {
					i2.addIOB(i.getIOB(0).clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		result.add(i2);
		log.fine("TCItems in result "+result.size());
		return result;
	}
}


