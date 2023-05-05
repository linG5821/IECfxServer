package jkl.iec.tc.bean.type;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import jkl.iec.tc.bean.type.IECTCItem;
import jkl.iec.tc.bean.type.IECMap.IECTyp;
import jkl.iec.tc.bean.utils.IECFunctions;


public class IECTCObject implements Cloneable {
	
	public final static Logger log = Logger.getLogger(IECTCObject.class .getName()); 
	
//	public IECTCItem asdu;
	public IECTCItem item;
	
	public IECTyp iectyp;
	
	private SimpleIntegerProperty addr = new SimpleIntegerProperty();
	public SimpleIntegerProperty addrProperty() {return addr;}
	
	public Integer getaddr() { 
/*		int ob;
		if (IECTCItem.P_SHORT) {
			ob = (buf[0] & 0xFF) | ((buf[1] & 0xFF) << 8) ;
		} else {
			ob =  (buf[0] & 0xFF) | ((buf[1] & 0xFF) << 8) | ((buf[2] & 0xFF) << 16);
		}
		return ob;*/
		return addr.get();	}

	public void setaddr(Integer i) {
    	if (i > 0xffffff) {
    		i = 0xffffff;
		}
    	if (i < 0) {
    		i = 0;
		}
		this.addr.set(i);
		buf[0] =(byte) ((i & 0x00ff));
		buf[1] =(byte) ((i & 0x00ff00)>>8);
		buf[2] =(byte) ((i & 0xff0000)>>16);
		log.log(Level.FINEST,"{0} [{1}] ",new Object[]{addr.get(),IECFunctions.byteArrayToHexString(buf, 0, 3)});
	}
	
	private double MAX_VALUE;
	private double MIN_VALUE;
	
//	private double Val;
	private SimpleDoubleProperty Val = new SimpleDoubleProperty();
	public SimpleDoubleProperty ValProperty() {return Val;}
	
	public int ValueParam;
	double VALUE_TX;
	double VALUE_RX;
	byte[] buf = new byte[16];
	
	private SimpleIntegerProperty QU = new SimpleIntegerProperty();
	public SimpleIntegerProperty QUProperty() {return QU;}
//	byte QU;
	
	public byte getQU() {
		return (byte) QU.get();
	}
	public void setQU(byte qu){
		log.fine(String.valueOf(qu));
		QU.set(qu);
	}
	
	byte QU_TX;
	byte QU_RX;
	byte SEQ=0;
	private SimpleObjectProperty<Date> time = new SimpleObjectProperty<Date>();
	public SimpleObjectProperty<Date> timeProperty() {return time;};
//	private Date Time;
	public Date Time_RX;
	public Date Time_TX;

	int QOC;

	public IECTCObject(IECTCItem s) {
/*		buf[0]=  IOB1  buf[1]= IOB2   buf[2]= IOB3
		buf[3] = Val1	buf[4]= Val2   buf[5]= Val3	 buf[6]= Val4
		buf[7] = QU    
		buf[8]=  TIME1  buf[9]= TIME2   buf[10]= TIME3  buf[11]= TIME4  buf[12]= TIME5  buf[13]= TIME6   buf[14]= TIME7  */
		item = s;
		iectyp = item.getIectyp();
		setaddr(1);
		init();
		}
	
	public IECTCObject(IECTCItem s,int iob) {
		item = s;
		iectyp = item.getIectyp();
		setaddr(iob);
		init();
	}

	public IECTCObject(IECTyp t) {
		item = null;
		iectyp = t;
		setaddr(1);
		init();
	}
	
	private void init(){
		log.finest("");
		setDefLimits();
		setValues(0,(byte) 0,new Date());
		Time_TX =getTime();	
	}



	private void setValues(double v,byte qu,Date d) {
		setVal(v);
//		System.out.println("IOB getVal TIMEIDX. "+ getValue());
		setQU(qu);
		setTime(d);	
	}
	
	public IECTCObject clone() throws CloneNotSupportedException {
		return (IECTCObject) super.clone();
	}

	/**
	 * 
	 * @param o1 = source
	 * @param o2 = destination
	 */
	public static void copy(IECTCObject o1,IECTCObject o2) {
		o2.item=o1.item;
		o2.iectyp=o1.iectyp;
		o2.setaddr(o1.getaddr());
		o2.setValues(o1.getVal(), o1.getQU(),  o1.getTime());
	}
	
//	public double getValue() {
//		readValue();
//		return Val;
//	}

	public double getVal() {
//		readValue();
//		return Val;
		return Val.get();
	}

	public String getValueasString() {
//		System.out.println("IOB getValasString "+ Val);
		switch(iectyp) {
		case M_SP_NA : case M_SP_TB : case C_SC_NA :{
			if (getVal() == 0) return "OFF (0)";
			if (getVal() == 1) return "ON  (1)";
		}
		case M_DP_NA : case M_DP_TB : {
			if (getVal() == 0) return "FAULT(00)";
			if (getVal() == 1) return "OFF  (01)";
			if (getVal() == 2) return "ON   (10)";
			if (getVal() == 3) return "INT  (11)";
		}
		case C_DC_NA : {
//			ValueParam =  buf[index] & 0x1c;
			if (getVal() == 1) return "OFF  (01)";
			if (getVal() == 2) return "ON   (10)";
		}
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :  {
			return String.valueOf((int)(getVal()));
		}
		case M_ME_NC: case M_ME_TF : {
			return String.valueOf(getVal());
		}
		case M_IT_NA: case M_IT_TB : {
			return String.valueOf((long)getVal());
		}
		case C_SE_NA: case C_SE_NB : {
			return String.valueOf((int)(getVal()));
		}
		case C_IC_NA : {
			return String.valueOf((int)(getVal()));
		}
		case C_CS_NA : {
//			Date d = new Date();
//			d.setTime((long) Value);
			SimpleDateFormat df = new SimpleDateFormat("yy.MM.dd  HH:mm:ss,S");
			return df.format((long) getVal());
//			return d.getTime();
		 }		
		}
		return "??";
	}
	
	public boolean isQUIV() {
		return ((getQU() & 0x80)==0x80);
	}
	public boolean isQUNT() {
		return ((getQU() & 0x40)==0x40);
	}
	public boolean isQUSB() {
		return ((getQU() & 0x20)==0x20);
	}
	public boolean isQUBL() {
		return ((getQU() & 0x10)==0x10);
	}
	public boolean isQUOV() {
		return ((getQU() & 0x01)==0x01);
	}

/*	
	public void setQUIV(boolean b) {
		if (b) {
			setQU((byte) (getQU() | (byte) 0x80));
			} 
		else {
			setQU((byte) (getQU() & (byte) 0x7F));
			}
	}
*/
	
	public String getQUasString() {
		String qus ="[";
//		IECTyp t = asdu.getIectyp();
		if (getQU() ==0x00) {qus =qus +".";}
		
		if (isQUIV()) {qus =qus +"IV,";}
		if (( iectyp == IECTyp.M_IT_NA) || ( iectyp == IECTyp.M_IT_TB) ) {
			if (isQUNT()) {qus =qus +"CA,";}
			if (isQUSB()) {qus =qus +"CY,";}
		} else {
			if (isQUNT()) {qus =qus +"NT,";}
			if (isQUSB()) {qus =qus +"SB,";}
			if (isQUBL()) {qus =qus +"BL,";}
			if (isQUOV()) {qus =qus +"OV,";}
		}
		if (qus.endsWith(",")) {
			qus = qus.substring(0,qus.lastIndexOf(","));
		}
		return qus+"]";	
	}
	
	public void readaddr() { 
		int ob;
		if (IECTCItem.P_SHORT) {
			ob = (buf[0] & 0xFF) | ((buf[1] & 0xFF) << 8) ;
		} else {
			ob =  (buf[0] & 0xFF) | ((buf[1] & 0xFF) << 8) | ((buf[2] & 0xFF) << 16);
		}
		setaddr(ob);
	}
		
	public void readValue() {
		double result = 0;
		int index = 3;
		if (IECTCItem.P_SHORT) {
			index=2;
		} 
		switch(iectyp) {
		case M_SP_NA : case M_SP_TB : {
			result = buf[index] & 0x01;
			break;
		}
		case C_SC_NA : {
			result = buf[index] & 0x03;
			ValueParam =  buf[index] & 0x1c;
			break;
		}	
		case M_DP_NA : case M_DP_TB : {
			result = buf[index] & 0x03;
			break;
		}
		case C_DC_NA : {
			result = buf[index] & 0x03;
			ValueParam =  buf[index] & 0x1c;
			break;
		}
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :  {
			result = buf[index] + buf[index+1]*256;
			break;
		}
		case M_ME_NC: case M_ME_TF : {
			result =buf[index] +
					buf[index+1]<<8+
					buf[index+2]<<16+
					buf[index+3]<<24 ;
			break;
		}
		case M_IT_NA: case M_IT_TB : {
			result = buf[index] +
					 buf[index+1]<<8+
					 buf[index+2]<<16+
					 buf[index+3]<<24 ;
			break;
		}
		case C_SE_NA: case C_SE_NB : {
			result = buf[index] + buf[4]*256;
			break;
		}
		case C_IC_NA : {
			result = buf[index];
			break;
		}
		case C_CS_NA : {
			result = readTime(index).getTime();
			break;
		}
    	}
		log.finer("buf["+index+"]["+Integer.toHexString(buf[index])+"..]  Value := "+result);
		setVal(result);
	}
	 
/**
 *  Sets new Value
 * @param v = Value to set
 * @return true if value has changed ; false if Value has NOT changed
 */
	public boolean setVal(double v) {
    	boolean result=true;
    	if (v > MAX_VALUE) {
			v = MAX_VALUE;
		}
		if (v < MIN_VALUE) {
//			System.out.println(v+"< MinValue : "+MIN_VALUE);
			v = MIN_VALUE;
		}
		if (v == getVal()) {
			result=false;
		}
		Val.set(v);
		log.fine(String.valueOf(v));
		setTime( new Date());

		return result;
	}
	
	private int getTimeLength(){
		if (isTimeType()) {
			switch (this.iectyp) {
				case M_SP_TB:
				case M_DP_TB:
				case M_ME_TD:
				case M_ME_TF:
				case M_IT_TB:
					return 7;
			}
			return 3;
		} 
		return 0;
	}

	public int getBufLength(){
		return getTimeIndex()+getTimeLength();
	}
	
	private int getIOBLength() {
		int result =0;
		switch(iectyp) {
		case M_SP_NA : case M_SP_TB : 
		case M_DP_NA : case M_DP_TB : 
		case C_SC_NA : case C_DC_NA : 
		case C_IC_NA : case C_CI_NA :{
			result= 1;
			break;
		}		
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :
		case C_SE_NA: case C_SE_NB :  {
			result= 3;
			break;
		}
		case M_ME_NC: case M_ME_TF :
		case C_SE_NC :{
			result= 5;
			break;
		}
		case M_IT_NA: case M_IT_TB : {
			result= 5;
			break;
		}
		case C_CS_NA: {
			result= 7;
			break;
		}
    	}
        log.finest(String.valueOf(result));
		return result;		
	}
	
	private int getTimeIndex(){
		if (IECTCItem.P_SHORT) {
			return getIOBLength()+2;
		}
		return getIOBLength()+3;
	}
	
	public boolean isShortPuls() {
		return (QOC == 1);
	}
	public boolean isLongPuls() {
		return (QOC == 2);
	}
	public boolean isPersistent() {
		return (QOC == 3);
	}
	
	private boolean isTimeType(){
		switch(iectyp) {
		case M_SP_TB : case M_DP_TB :
		case M_ME_TB : case M_ME_TD : case M_ME_TF : case M_IT_TB : {
			return true;
		}
    	}
		return false;
	}	
	
	private void writeValue() {
//		System.out.println("IOB writeVal :"+ Value);
		int index = 3;
		switch(iectyp) {
		case M_SP_NA : case M_SP_TB : {
			buf[index]= (byte) (((byte) getVal())& 0x01) ;
			break;
		}
		case C_SC_NA : {
			buf[index]= (byte) (((byte) getVal())& 0x01) ;
			buf[index]= (byte) (buf[index] |(byte) ValueParam);
			break;
		}
		case M_DP_NA : case M_DP_TB : {
			buf[index]= (byte) (((byte) getVal())& 0x03) ;
			break;
		}
		case C_DC_NA : {
//			System.out.println("C_DC_ValueParam "+ ValueParam);
			buf[index]= (byte) (((byte) getVal()) & 0x03);
			buf[index]= (byte) (buf[index] |(byte) ValueParam);
			break;
		}
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :  {
			int value=(int)getVal();
			buf[index]=(byte) (value % 256);
			buf[index+1]=(byte) (value / 256);
			break;
		}
		case C_SE_NA: case C_SE_NB :  {
			int value=(int)getVal();
			buf[index]=(byte) (value % 256);
			buf[index+1]=(byte) (value / 256);
			break;
		}
		case M_ME_NC: case M_ME_TF : {
			int value=Float.floatToRawIntBits((float) getVal());
			buf[index+3] =(byte) ((value & 0xff000000)>>24);
			buf[index+2] =(byte) ((value & 0x00ff0000)>>16);
			buf[index+1] =(byte) ((value & 0x0000ff00)>>8);
			buf[index] =(byte) ((value & 0x000000ff));
			break;
		}
		case C_SE_NC : {
			int value=Float.floatToRawIntBits((float) getVal());
			buf[index+3] =(byte) ((value & 0xff000000)>>24);
			buf[index+2] =(byte) ((value & 0x00ff0000)>>16);
			buf[index+1] =(byte) ((value & 0x0000ff00)>>8);
			buf[index] =(byte) ((value & 0x000000ff));
			break;
		}
		case M_IT_NA: case M_IT_TB : {
			int value=(int)getVal();
			buf[index+3] =(byte) ((value & 0xff000000)>>24);
			buf[index+2] =(byte) ((value & 0x00ff0000)>>16);
			buf[index+1] =(byte) ((value & 0x0000ff00)>>8);
			buf[index] =(byte) ((value & 0x000000ff));
			break;
		}
		case C_IC_NA : {
			buf[index]= (byte) getVal();
			break;
		}	
		case C_CS_NA : {
			writeTime(index);
			break;
		}
    	}
	}
	

	public void readQU() {
		int index = 3;
		switch(iectyp) {
		case M_SP_NA : case M_SP_TB : {
			setQU((byte) (buf[index]& ((byte) 0xfe))) ;
			break;
		}
		case C_SC_NA : {
			setQU((byte) (buf[index]& ((byte) 0xfe))) ;
			QOC = getQU() >>> 2;
//			log.severe("QUC: "+String.valueOf(QOC));
			break;
		}
		case M_DP_NA : case M_DP_TB : {
			setQU((byte) (buf[index]& ((byte) 0xfc))) ;
			break;
		}	
		case C_DC_NA : {
			setQU((byte) (buf[index]& ((byte) 0xfc))) ;
			QOC = getQU() >>> 2;
			break;
		}	
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :  {
			setQU(buf[index+2]);
			break;
		}
		case C_SE_NA : case C_SE_NB :  {
			setQU(buf[index+2]);
			break;
		}
		case M_ME_NC: case M_ME_TF : {
			setQU(buf[index+4]);
			break;
		}
		case C_SE_NC : {
			setQU(buf[index+4]);
			break;
		}
		case M_IT_NA: case M_IT_TB : {
			setQU((byte) (buf[index+4] & (byte)0xe0));
			SEQ = (byte) (buf[index+4] & (byte)0x1f);
			break;
		}
    	}
	}
	
	public void setSEQ(byte seq) {
		SEQ =seq;
	}
	public byte getSEQ() {
		return SEQ;
	}	
	private void writeQU() {
		int index = 3;
		switch(iectyp) {
		case M_SP_NA : case M_SP_TB : {
			buf[index]= (byte) (((byte) getQU())|((byte) getVal())& 0x01) ;
			break;
		}
		case C_SC_NA : {
			buf[index]= (byte) (((byte) getQU())|  buf[index]) ;
			break;
		}		
		case M_DP_NA : case M_DP_TB : {
			buf[index]= (byte) ((byte) getQU() | ((byte) getVal())& 0x03) ;
			break;
		}	
		case C_DC_NA : {
			buf[index]= (byte) ((byte) getQU() | buf[index]);
			break;
		}	
		case M_ME_NA: case M_ME_TB : case M_ME_NB : case M_ME_TD :  {
			buf[index+2]= getQU();
			break;
		}
		case C_SE_NA: case C_SE_NB :  {
			buf[index+2]= getQU();
			break;
		}
		case M_ME_NC: case M_ME_TF : {
			buf[index+4] = getQU();
			break;
		}
		case C_SE_NC : {
			buf[index+4] = getQU();
			break;
		}
		case M_IT_NA: case M_IT_TB : {
//			buf[7] = (byte) (buf[7]| qu);
			buf[index+4] = (byte) (SEQ| getQU());
			log.finest("SEQ: "+SEQ);
			SEQ++;
			if (SEQ>31) {
				SEQ=0;
			}
			break;
		}
    	}
	}
	
	public void setTime(Date dt) {
	  time.set(dt);
	}
	public Date getTime() {
		  return time.get();
		}
			
	private void writeTime() {
		writeTime(getTimeIndex());
	}
	
	private void writeTime(int TimeIndex) {
//		System.out.println("IOB TIMEIDX. "+ TimeIndex);
		Calendar d = Calendar.getInstance();
		d.setTime(getTime());
		buf[TimeIndex]= (byte) (d.get(Calendar.MILLISECOND)%256);
		buf[TimeIndex+1]= (byte) (d.get(Calendar.MILLISECOND)/256);
		buf[TimeIndex+2]= (byte) (d.get(Calendar.MINUTE));
		switch (this.iectyp) {
			case M_SP_TB:
			case M_DP_TB:
			case M_ME_TD:
			case M_ME_TF:
			case M_IT_TB:
				buf[TimeIndex+3]= (byte) (d.get(Calendar.HOUR_OF_DAY));
				if (TimeZone.getDefault().inDaylightTime(d.getTime())) {
					buf[TimeIndex+3]=(byte) (buf[TimeIndex+3]+0x80) ;
				}
				buf[TimeIndex+4]= (byte) (d.get(Calendar.DAY_OF_MONTH));
				buf[TimeIndex+5]= (byte) (d.get(Calendar.MONTH)+1);
				buf[TimeIndex+6]= (byte) (d.get(Calendar.YEAR)-2000);
				break;
		}
	}
	
	public Date readTime() {
		int TimeIndex=getTimeIndex();
		return readTime(TimeIndex);
	}

	private Date readTime(int index) {
		Calendar d = Calendar.getInstance();
		String txt =" Sytem TIME ";
		if (isTimeType()) {
			txt = " IOB TIME ";
			d.set(Calendar.MILLISECOND,((buf[index+1] & 0xFF) << 8) | (buf[index] & 0xFF));
			d.set(Calendar.MINUTE,(buf[index+2] & 0xFF));
			d.set(Calendar.HOUR_OF_DAY,(buf[index+3] & 0x7F));
			d.set(Calendar.DAY_OF_MONTH,(buf[index+4] & 0xFF));
			d.set(Calendar.MONTH,(buf[index+5] & 0xFF)-1);
			d.set(Calendar.YEAR,(buf[index+6] & 0xFF)+2000);
		} 
		log.finer(txt+ d.getTime());
		return d.getTime();
	}
	
	private double getDef_MAX() {
		if (iectyp == null) {
		   return MAX_VALUE;
		}
		switch(iectyp) {
    	case M_SP_NA : case M_SP_TB :{	
    		return 1;
    	}
    	case C_SC_NA :{	
    		return 1;
    	}
    	case M_DP_NA : case M_DP_TB :{	
    		return 3;
    	}
    	case C_DC_NA :{	
    		return 3;
    	}
    	case M_ME_NA : case M_ME_TB : case M_ME_NB : case M_ME_TD :{	
    		return Short.MAX_VALUE;
    	}
    	case C_SE_NA : case C_SE_NB :{	
    		return Short.MAX_VALUE;
    	}
    	case M_ME_NC : case M_ME_TF :{	
//    		return Float.MAX_VALUE;
    		return +1000000000000.0;
    	}   
    	case C_SE_NC :{	
    		return Float.MAX_VALUE;
    	}   
    	case M_IT_NA : case  M_IT_TB :{	
    		return Integer.MAX_VALUE;
    	}   
    	case C_IC_NA : case  C_CI_NA :{	
    		return 255;
    	} 
    	}
		return MAX_VALUE;
	}
	
	private double getDef_MIN() {
		if (iectyp == null) {
			   return MIN_VALUE;
			}
		switch(iectyp) {
    	case M_SP_NA : case M_SP_TB :{	
    		return 0;
    	}
    	case C_SC_NA : case C_DC_NA :{	
    		return 0;
    	}
    	case M_DP_NA : case M_DP_TB :{	
    		return 0;
    	}
    	case M_ME_NA : case M_ME_TB : case M_ME_NB : case M_ME_TD :{	
    		return Short.MIN_VALUE;
    	}
    	case C_SE_NA : case C_SE_NB :{	
    		return Short.MIN_VALUE;
    	}
    	case M_ME_NC : case M_ME_TF :{	
//    		return Float.MIN_VALUE ;
    		return -1000000000000.0;
    	}   
    	case C_SE_NC :{	
    		return Float.MIN_VALUE;
    	}   
    	case M_IT_NA : case  M_IT_TB :{	
    		return Integer.MIN_VALUE;
    	}   
    	case C_IC_NA : case  C_CI_NA :{	
    		return 0;
    	} 
    	}
		return MIN_VALUE;
	}
	
	public void setDefLimits() {
   		MAX_VALUE =getDef_MAX();
   		MIN_VALUE =getDef_MIN();
//    	System.out.println("Limits  "+MIN_VALUE+":"+MAX_VALUE);
    	
	}
	
	public double getMAX_VALUE() {
		return MAX_VALUE;
	}

	public void setMAX_VALUE(double mAX_VALUE) {
    	if (mAX_VALUE > getDef_MAX()) {
    		mAX_VALUE = getDef_MAX();
		}
    	if (mAX_VALUE > MIN_VALUE) {
    		MAX_VALUE = mAX_VALUE;
    	}
	}

	public double getMIN_VALUE() {
		return MIN_VALUE;
	}

	public void setMIN_VALUE(double mIN_VALUE) {
    	if (mIN_VALUE < getDef_MIN()) {
    		mIN_VALUE = getDef_MIN();
		}
    	if (mIN_VALUE < MAX_VALUE) {
    		MIN_VALUE = mIN_VALUE;
    	}
	}
	
//	public String printStream() {
	public ObservableValue<String> printStream() {
//		System.out.println("IEC mesage "+Functionsn.byteArrayToHexString(Stream,0,StreamLength));
		ObservableValue<String> o = new SimpleStringProperty(IECFunctions.byteArrayToHexString(buf,0,getBufLength()));
//		return IECFunctions.byteArrayToHexString(buf,0,getBufLength());
		return o;
	}	
	
	public byte[] getStream() {
		writeValue();
		writeQU();
		if (isTimeType()) {
			writeTime();
		}
		int l= getBufLength();
		byte[] result = Arrays.copyOf(buf, l);
		if (IECTCItem.P_SHORT) {
			//creates "short array"
			byte[] result_s =  Arrays.copyOf(buf, l); 
			System.arraycopy(buf, 3, result_s, 2, l-3);
			log.finer("s:"+l+"[ "+IECFunctions.byteArrayToHexString(result_s,0,result_s.length)+"]");
			return result_s;
		} else {
			log.finer("S:"+l+"[ "+IECFunctions.byteArrayToHexString(result,0,result.length)+"]");
			return result;
		}
//			System.out.println("IOB_buffer"+Functionsn.byteArrayToHexString(buf,0,15));
//		System.out.print("IOB: get_Stream "+getTimeIndex()+" ");
//		System.out.println(Functionsn.byteArrayToHexString(buf,0, getTimeIndex()));
	}

}
