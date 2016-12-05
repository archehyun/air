package api;

import java.io.IOException;

import org.w3c.dom.Document;

public interface IFAIRClinet {

	
	public int logout(String IP,int port, String ID) throws IOException;
	public int login(String IP, int Port, String ID, String Password) throws IOException;
	public int registerQuery(String query) throws IOException;
	public int deleteQuery(int queryID) throws IOException;
	public Document udpateQueryID() throws IOException;
}
