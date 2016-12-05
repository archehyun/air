package api;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * AIR 서비스 플랫폼에서 클라이언트에 제공하는 CLI (Call Level Interface)
 * @author ssima
 *
 */
public abstract class APIClient implements IFAIRClinet, Runnable{
	
	
	public final int LOGIN_SUCCESS = 1; // 로그인 성공 ACK
	public final int LOGIN_FAILURE = 0; // 로그인 실패 ACK

	
	protected Logger 			logger = Logger.getLogger(getClass());
	
	private InputStream clientInput = null;
	
	private static OutputStream os = null;	
	
	private Socket client;
	
	public static final byte LOGIN = 			(byte)0x01;
	public static final byte REGISTER_QUERY = 	(byte)0x02;
	public static final byte DELETE_QUERY = 	(byte)0x03;
	public static final byte LOOKUP_RESULT = 	(byte)0x04;
	public static final byte QUERYID_LIST = 	(byte)0x05;
	public static final byte LOGOUT = 			(byte)0x06;
	
	private String message;
	
	private QueryBundle queryResult;
	
	public QueryBundle getQueryResult() {
		return queryResult;
	}
	public void setQueryResult(QueryBundle queryResult) {
		this.queryResult = queryResult;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	

	/**
	 * @param IP
	 * @param port
	 * @param ID
	 * @return
	 * @throws IOException
	 */
	public int logout(String IP,int port, String ID) throws IOException
	{
		int iID_Length = ID.length();
		byte[] bID = ID.getBytes();
		byte[] logoutPacket = new byte[3 + iID_Length]; // type(1) + idLength(1)  + 아이디 + 비밀번호
		
		logoutPacket[0] = LOGOUT; // Packet Type			
		logoutPacket[1] = (byte)iID_Length; // ID length	

		for (int i = 0; i < ID.length(); i++) 
		{
			logoutPacket[i + 2] = bID[i];  // ID
		}
		os.write(logoutPacket);
		os.flush();

		int iReturn = clientInput.read();
		

		logger.debug("logout return:"+iReturn);
		return iReturn;
	}
	/**
	 * @param ip
	 * @param port
	 * @param userID
	 * @param password
	 * @return
	 * @throws IOException
	 */
	public int login(String ip, int port, String userID, String password) throws IOException {
		
		logger.info("ip:"+ip+",port:"+port+",userID:"+userID+",password:"+password);
		
		int iID_Length = userID.length();
		byte[] bID = userID.getBytes();
		int iPassword_Length = password.length();
		byte[] bPassword = password.getBytes();

		int iReturn = 0;

		if(client==null)	
			client = new Socket(ip, port);

		
		
		clientInput = client.getInputStream();
		
		os = client.getOutputStream();


		byte[] loginPacket = new byte[3 + iID_Length + iPassword_Length]; // type(1) + idLength(1) + pwdLength(1) + 아이디 + 비밀번호 

		loginPacket[0] = LOGIN; // Packet Type		
		
		loginPacket[1] = (byte)iID_Length; // ID length	

		for (int i = 0; i < userID.length(); i++) 
		{
			loginPacket[i + 2] = bID[i];  // ID
		}

		loginPacket[2 + iID_Length] = (byte)password.length(); // Password Length

		for (int i = 0; i < password.length(); i++) 
		{
			loginPacket[3 + userID.length() + i] = bPassword[i]; // Password
		}
		os.write(loginPacket);
		os.flush();

		iReturn = clientInput.read();
		
		
		
		logger.info("[1] 로그인 리턴 값: " + iReturn + "     ===================> "+(iReturn==LOGIN_SUCCESS?"인증성공":"인증실패"));
		if (iReturn == LOGIN_FAILURE) 
		{	
			client.close();
			client=null;
		}				

		return iReturn;
	}

	/**
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public int registerQuery(String query) throws IOException {

		byte[] bQuery = query.getBytes();
		int iReturn = 0;

		byte[] registerPacket = new byte[1 + query.length()]; // // type(1) + query 

		registerPacket[0] = 0x02; // Packet Type
		for (int i = 0; i < query.length(); i++) {
			registerPacket[i + 1] = bQuery[i]; // query
		}


		os.write(registerPacket);
		os.flush();

		iReturn = clientInput.read();


		System.out.println("[2] 질의등록 리턴 값: " + iReturn);

		return iReturn;
	}

	/**
	 * @param queryID
	 * @return
	 * @throws IOException
	 */
	public int deleteQuery(int queryID) throws IOException {
		int iReturn = 0;

		byte[] deletePacket = new byte[1 + 4];

		deletePacket[0] = 0x03; // Packet Type

		deletePacket[1] = (byte)((queryID & 0xff000000) >> 24);
		deletePacket[2] = (byte)((queryID & 0x00ff0000) >> 16);
		deletePacket[3] = (byte)((queryID & 0x0000ff00) >> 8);
		deletePacket[4] = (byte)(queryID & 0x000000ff);		

		logger.debug("[3] queryID(int): " + queryID);
		/// 변환된 Query ID
		int temp_queryID = 0;
		temp_queryID |= ((deletePacket[1]) << 24) & 0xff000000;
		temp_queryID |= ((deletePacket[2]) << 16) & 0xff0000;
		temp_queryID |= ((deletePacket[3]) << 8) & 0xff00;
		temp_queryID |= (deletePacket[4]) & 0xff;
		////////////////////////////////////////////////////
		logger.debug("[3] queryID(byte[]): " + temp_queryID);

		os.write(deletePacket);
		os.flush();

		iReturn = clientInput.read();



		if (iReturn == 1) {								
			logger.debug("[3] 질의삭제 리턴 값: " + iReturn + "     ===================> 질의삭제 성공");		
		} else if (iReturn == 0) {
			logger.debug("[3] 질의삭제 리턴 값: " + iReturn + "     ===================> 질의삭제 실패");
		}

		return iReturn;
	}

	/**
	 * @deprecated
	 * @param queryID
	 * @return
	 * @throws IOException
	 */
	public String lookupResult(int queryID) throws IOException {
		int iReturn = 0;
		String lookupResultQuery = "";
		byte[] bTemp = new byte[1500];

		byte[] lookupResultPacket = new byte[1 + 4];

		lookupResultPacket[0] = 0x04; // Packet Type

		lookupResultPacket[1] = (byte)((queryID & 0xff000000) >> 24);
		lookupResultPacket[2] = (byte)((queryID & 0x00ff0000) >> 16);
		lookupResultPacket[3] = (byte)((queryID & 0x0000ff00) >> 8);
		lookupResultPacket[4] = (byte)(queryID & 0x000000ff);		

		os.write(lookupResultPacket);
		os.flush();

		// 소스 수정 필요

		iReturn = clientInput.read(bTemp);
		byte[] bQuery = new byte[iReturn];
		System.arraycopy(bTemp, 0, bQuery, 0, iReturn);
		lookupResultQuery = new String(bQuery, 0, bQuery.length); 
		System.out.println("[4] 질의결과 리턴 값: " + lookupResultQuery);
		System.out.println("[test] bTemp's Length: " + bTemp.length );
		System.out.println("[test] bQuery's Length: " + bQuery.length );
		System.out.println("[test] lookupResultQuery's Length(): " + lookupResultQuery.length() );


		return lookupResultQuery;
	}


	/**
	 * @throws IOException
	 */
	public Document udpateQueryID() throws IOException
	{
		int initPacket = 0;
		byte[] packet = new byte[2048];
		initPacket = clientInput.read();	// 초기 1byte 확인

		int packetLength = clientInput.read(packet); // initByte 제외한 나머지 패킷 길이
		logger.debug("[initPacket]: " + initPacket);
		byte[] rawPacket = new byte[packetLength]; // 패킷 사이즈 재정의를 위한 byte[] 생성
		System.arraycopy(packet, 0, rawPacket, 0, packetLength); // 수신된 패킷 사이즈에 맞게 복사
		String rawQuery = new String(rawPacket , 0, rawPacket .length); // 질의 추출

		String rawQueryNew  =rawQuery.trim();
		System.out.println(rawQuery);
		Document newDoc = this.newDocumentFromString(rawQueryNew);

		return newDoc;

	}
	/**
	 * @throws IOException
	 */
	public Document udpateQueryID2() throws IOException
	{
		int initPacket = 0;
		byte[] packet = new byte[2048];
		byte[] lookupResultPacket = new byte[1];

		lookupResultPacket[0] = 0x05; // Packet Type
		os.write(lookupResultPacket);
		os.flush();
		
		initPacket = clientInput.read();	// 초기 1byte 확인
		
		

		int packetLength = clientInput.read(packet); // initByte 제외한 나머지 패킷 길이
		logger.debug("[initPacket]: " + initPacket);
		byte[] rawPacket = new byte[packetLength]; // 패킷 사이즈 재정의를 위한 byte[] 생성
		System.arraycopy(packet, 0, rawPacket, 0, packetLength); // 수신된 패킷 사이즈에 맞게 복사
		String rawQuery = new String(rawPacket , 0, rawPacket .length); // 질의 추출

		String rawQueryNew  =rawQuery.trim();
		System.out.println(rawQuery);
		Document newDoc = this.newDocumentFromString(rawQueryNew);

		return newDoc;

	}	
	public Document newDocumentFromString(String xmlString) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document ret = null;

		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		try {
			ret = builder.parse(new InputSource(new StringReader(xmlString)));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * (자원)연결해제 메소드
	 * I/O 및 Socket 연결을 해제함
	 * 
	 */
	public void disconnect() {
		try {
			if (os != null) {
				os.close();
			}
			if (clientInput != null) {
				clientInput.close();
			}
			if (!client.isClosed()) {
				client.close();
			}
		} catch (IOException e) {
			logger.debug("해제 오류: " + e.getMessage());
		}
	}
	public abstract void lookupResult(String result);
	public abstract void start();
}
