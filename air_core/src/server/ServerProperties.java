package server;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ServerProperties extends Properties{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ServerProperties() {
		
		
		try{
		String air_home = System.getenv("AIR_HOME");
		this.load(new FileInputStream(new File(air_home+"\\conf\\db.properties")));
		this.load(new FileInputStream(new File(air_home+"\\conf\\server.properties")));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
