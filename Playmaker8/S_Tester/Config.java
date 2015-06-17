package S_Tester;
import java.io.InputStream;
import java.util.Properties;
public class Config {
	public static String AUTHOR = "";
	public Config(){
		this.getPropValues();
	}
	public void getPropValues(){		
		try{			
			Properties prop = new Properties();
			String pfilename = "config.properties";
			InputStream istream = getClass().getClassLoader().getResourceAsStream(pfilename);
			prop.load(istream);
			
			AUTHOR = prop.getProperty("author");
			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

}
