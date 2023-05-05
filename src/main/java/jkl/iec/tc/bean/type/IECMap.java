package jkl.iec.tc.bean.type;

import java.io.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@SuppressWarnings("serial")
public class IECMap extends HashMap<Object, Object> {
	
	public static enum IECTyp {IEC_NULL_TYPE ((byte)0x00),
		M_SP_NA ((byte)0x01),M_DP_NA ((byte)0x03),M_ME_NA((byte)0x09),M_ME_NB((byte)0x0b),M_ME_NC((byte)0x0d),M_IT_NA((byte)0x0f),
		M_SP_TB((byte)0x1e,(byte)0x01),M_DP_TB((byte)0x1f,(byte)0x03), M_ME_TB((byte)0x0c,(byte)0x09),M_ME_TD((byte)0x22,(byte)0x0a),M_ME_TF((byte)0x24,(byte)0x0d),
		M_IT_TB((byte)0x25,(byte)0x0f),
		C_IC_NA ((byte)0x64), C_CI_NA ((byte)0x65),
		C_SE_NA ((byte)48), C_SE_NB ((byte)49), C_SE_NC ((byte)50) ,
		C_SC_NA ((byte)0x2d), C_DC_NA ((byte)0x2e),C_CS_NA((byte)0x67);
		
		// additional Type-Byte property of each enum 
		private byte TK;
		// additional Base-Type property of each enum 
		private byte BT;
		// set TK for each enum
		IECTyp (byte tk) {
			this.TK = tk;
			this.BT = tk;
			}	
		IECTyp (byte tk,byte bt) {
			this.TK = tk;
			this.BT = bt;
			}	
		
		public byte tk() {return TK;}
		public byte bt() {return BT;}
	}
	
    public static Set<IECTyp> IEC_M_Type = EnumSet.of(IECTyp.M_SP_NA,IECTyp.M_DP_NA,IECTyp.M_ME_NA,IECTyp.M_ME_NB,IECTyp.M_ME_NC,IECTyp.M_IT_NA,
			IECTyp.M_SP_TB,IECTyp.M_DP_TB,IECTyp.M_ME_TB,IECTyp.M_ME_TD,IECTyp.M_ME_TF,IECTyp.M_IT_TB);
    public static Set<IECTyp> IEC_C_Type = EnumSet.of(IECTyp.C_IC_NA,IECTyp.C_CI_NA,
    		IECTyp.C_SC_NA,IECTyp.C_DC_NA,IECTyp.C_SE_NA,IECTyp.C_SE_NB,IECTyp.C_SE_NC);

    public static Set<String> IEC_M_BaseType =new HashSet<String>();
    public static Set<String> IEC_C_BaseType =new HashSet<String>();
    
	public static IECMap map = new IECMap();

	private static String Type2BaseType(String s) {
		String tmp = s.substring(0, s.length()-3);
		String ext = s.substring(s.length()-3, s.length());
		return tmp+"_"+ext.charAt(ext.length()-1);
	}
	

 	private void setBaseType (Set<IECTyp> s) {
		String tmp,ext;
		Iterator<IECTyp> iter = s.iterator();
		while (iter.hasNext()) {
			tmp = iter.next().toString();
			ext = tmp.substring(tmp.length()-3, tmp.length()-1); 
			if (ext.equals("_N")) {
				if (s==IEC_M_Type) {
					IEC_M_BaseType.add(Type2BaseType(tmp));
				}
				if (s==IEC_C_Type) {
					IEC_C_BaseType.add(Type2BaseType(tmp));
				}
				}
		}	
	}

	
	public IECMap() {
		for (IECTyp it:IECTyp.values()) {
			put(it,it.tk());
			put("tk_"+it.tk(),it);
		}
		setBaseType(IEC_M_Type);
		setBaseType(IEC_C_Type);
		setTypeDescription();
	}
	
	public static IECTyp getType(String description) {
		return (IECTyp) map.get("Description."+description);
	}
	
	public static IECTyp getType(byte b) {
		if ((IECTyp) map.get("tk_"+b)==null) {
			return IECTyp.IEC_NULL_TYPE;
		}
		return (IECTyp) map.get("tk_"+b);
	}
		
	public static String getBaseType(byte b) {
		IECTyp t = (IECTyp) map.get("tk_"+b);
		byte bt= t.BT;
		t= (IECTyp) map.get("tk_"+bt);
		String tmp = t.toString();
		return Type2BaseType(tmp);
	}
	public static String getBaseTypeDescription(String s) {
		return (String) map.get(s+".BaseDescription");
	}
	
	public static String getTypeDescription(IECTyp t) {
		return (String) map.get(t.toString()+".Description");
	}
	
	private void setBaseDescription(Set<String> s,Properties p) {
		String tmp;
		Iterator<String> iter = s.iterator();
		while (iter.hasNext()) {
			tmp = iter.next();
			put(tmp+".BaseDescription",p.getProperty(tmp,tmp));
			put("BaseDescription."+p.getProperty(tmp,tmp),tmp);
			}
	}
	
	private void setTypeDescription() {
		Properties description = new Properties();
		String PropertiesFile = "IECTypeDescription.properties";

		BufferedInputStream stream;
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(PropertiesFile);
//			InputStream in = new FileInputStream(PropertiesFile);
			stream = new BufferedInputStream(in);
			description.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		for (IECTyp it:IECTyp.values()) {
			put(it.toString()+".Description",this.typeDes(it, description.getProperty(it.toString(),it.toString())));
			put("Description."+ this.typeDes(it, description.getProperty(it.toString(),it.toString())),it);
		}
		setBaseDescription(IEC_M_BaseType,description);
//		System.out.println("M_BASE "+IEC_M_BaseType);
		setBaseDescription(IEC_C_BaseType,description);
	}
	
	public void list() {
		System.out.println("IEC_MAP:");
		for (Entry<Object,Object> entry : entrySet()) System.out.println(entry.getKey() +"="+ entry.getValue());
	}

	private String typeDes(IECTyp it, String desc) {
		return it + " (" + desc + ")";
	}

}
