package jkl.iec.tc.application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javafx.stage.FileChooser;

import jkl.iec.tc.bean.type.IECTCItem;
import jkl.iec.tc.bean.utils.IECSimProperties;

public class IECServerFileWorker {
	
	BufferedWriter bw ;
	
	public Properties getVersionProperties(){
		Properties result= new Properties();
	    URL url = Server.class.getResource(Server.VersionFile);
		System.out.println("verfile:"+Server.VersionFile+"  URL.Info "+url);	
		if (url != null) {
			File f=null;
			try {
				f= URLFileCopier(url,"version.ser");
			} catch (Exception e) {
				e.printStackTrace();
			}
			InputStream fis = null;
			try
				{
		    	  fis = new FileInputStream( f );
			      ObjectInputStream o = new ObjectInputStream( fis );
				  result =(Properties) o.readObject();
				}
				catch ( Exception e ) { System.err.println( e ); }
				finally { try { fis.close(); }
							catch ( Exception e ) { } 
						}		
			result.list(System.out);
			f.delete();
		}
		return result;
	}
	
	private File URLFileCopier(URL url, String filePath) throws Exception {
		byte[] buffer = new byte[1024];
		int bytesRead;
		 
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		URLConnection connection = url.openConnection();
	    inputStream = new BufferedInputStream(connection.getInputStream());
	    File f = new File(filePath);
	    outputStream = new BufferedOutputStream(new FileOutputStream(f));
	    while ((bytesRead = inputStream.read(buffer)) != -1) {
		      outputStream.write(buffer, 0, bytesRead);
		    }
	    inputStream.close();
	    outputStream.close();
	    System.out.println("URL:File "+url+" copyed to Local:File "+ f);	
	    return f;
		}
	
	public void write(String tx) {
		System.out.println("write to file "+tx);
        try { bw.write(tx); bw.newLine();}
        catch (Exception e) {System.out.println(e);}
	}

//	private void saveItems(IECTCItem[] l) {
	private void saveItems() {
		String txt;
		IECTCItem item =null;
		for (int it=0 ;it < Server.iecPane.itemlist.size();it++ ) {
//		for (IECTCItem item :l) {
			item = (IECTCItem) Server.iecPane.itemlist.get(it);
			txt = IECTCItem.getPropertyString(item);
			write(txt);
		}		
	}
	
	public boolean save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Itemlist");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"/Documents/"));
        File file = fileChooser.showSaveDialog(Server.stage);
        System.out.println("file:"+file);
        if (file != null) {
			String txt = "";
			try {bw = new BufferedWriter(new FileWriter(file, false));}
 			 catch (Exception e) {System.out.println(e);}
			txt="FILE.VERSION=2";
			write(txt);
			txt ="ITEMS.COUNT="+Server.iecPane.itemlist.size();
			write(txt);	 		
			int props = IECTCItem.Props.length +IECSimProperties.Props.length;
			String[] result = new String[props];
			System.arraycopy(IECTCItem.Props,0,result,0,IECTCItem.Props.length);
			System.arraycopy(IECSimProperties.Props,0,result,IECTCItem.Props.length,IECSimProperties.Props.length);
			txt ="";
//			System.out.println("Properties:"+result);
	        for (String it : result) {
				txt = txt+it+";";
	        }
	        txt = txt.substring(0, txt.length()-1);
	        txt ="ITEM.PROPERTIES="+txt;
	 		write(txt);
	 		
//	 		IECTCItem[] l= (IECTCItem[]) Server.iecPane.itemlist.toArray();
	 		saveItems();

	 		try { bw.close(); }
			catch (Exception e) { System.out.println(e); }
			return true;
        }
       return false;
	}
	
	private void loadItem(List<String> keyList,String[] values) {
		System.out.println("LOAD ITEM");
		Properties props = new Properties();
		int index = 0;
		for (String key:keyList) {
			props.setProperty(key, values[index]);
			index++;
		}
    	IECTCItem item =new IECTCItem();
    	item.data =new IECSimProperties(item);
    	item.setProperties(props);
		Server.iecPane.add(item);
	}
	
	private void loadItems(Properties p) {
		System.out.println("LOAD Items");
		int c= Integer.parseInt(p.getProperty("ITEMS.COUNT","0"));
		List<String> KeyList = Arrays.asList(p.getProperty("ITEM.PROPERTIES").split(";")); 
		System.out.println(c+"ITEM PROBETIES IN FILE "+KeyList);
		String pre = "ITEM";
		for (int it=1; it <= c; it++) {
			String[] values= p.getProperty(pre+it).split(";");
			loadItem(KeyList,values);
		}
	}
	
	public boolean load() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Itemlist");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"/Documents/"));
        File file = fileChooser.showOpenDialog(Server.stage);
        System.out.println("LOAD file:"+file);
        if (file != null) {        
          Properties p = new Properties();
          BufferedInputStream stream;
          try {
			stream = new BufferedInputStream(new FileInputStream(file));
			p.load(stream);
			stream.close();}
          catch (Exception e) {
			e.printStackTrace();
			return false;
          }
          if (p.getProperty("FILE.VERSION").equals("2")) {
        	  loadItems(p);
        	  return true;
          }
        return false;  
        }
	return true;
	}

}
