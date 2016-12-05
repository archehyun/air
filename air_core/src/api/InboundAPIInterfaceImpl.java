package api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import msg.node.MsgForAPI;
import msg.node.QueueNode;
import msg.queue.MsgQueueForAPI;
import msg.queue.TagControlMsgQueue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import server.AIRThread;
import server.MonitorMessage;
import buffer.dao.TableBufferManager;
import buffer.info.AIRTable;
import buffer.info.ActionInfo;
import buffer.info.ConditionInfo;
import buffer.info.UserInfo;

/**
 * 서비스 플랫폼을 사용하는 클라이언트 연결을 위한 리스너 객체
 * 
 * 서비스 플랫폼 쪽 클라이어트 대응 객체
 * 
 * @author		손희목
 * @since       2014-02-08
 * @version     0.1       
 */

public class InboundAPIInterfaceImpl extends AIRThread implements Runnable, IFInboundAPI{

	private final int LOGIN_SUCCESS = 1; // 로그인 성공 ACK
	private final int LOGIN_FAILURE = 0; // 로그인 실패 ACK

	private final int LOGOUT_SUCCESS = 1; // 로그아웃 성공 ACK
	private final int LOGOUT_FAILURE = 0; // 로그아웃 실패 ACK

	private final int QUERY_DELETE_SUCCESS = 1; // 질의삭제 성공 ACK
	private final int QUERY_DELETE_FAILURE = 0; // 질의삭제 실패 ACK
	private final int LOOKUP_RESULT_FAILURE = 0; // 잘못된 Query ID

	private final byte LOGIN = 			(byte)0x01;
	private final byte REGISTER_QUERY = (byte)0x02;
	private final byte DELETE_QUERY = 	(byte)0x03;
	private final byte LOOKUP_RESULT = 	(byte)0x04;
	private final byte QUERYID_LIST = 	(byte)0x05;
	private final byte LOGOUT = 		(byte)0x06;

	private InboundAPIListener apiServerListener;
	
	private Socket socket; // 소켓
	
	private String socketInfo; // 클라이언트 소켓 정보(IP:Port)
	
	private InputStream commandInputStream; // 입력스트림
	
	private InputStream lookupResultInputStream; // 입력스트림
	
	private OutputStream clientOS; // 출력스트림

	LookupResultThread lookupResultThread;

	private String user_id; // 유저 ID 전역 변수
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	private String ip;

	private int port;
	
	private HashMap<Integer, RegisterQueryEntity> queryMap; // 쿼리 ID와 쿼리 Entity를 관리하기 위한 해시맵


	public InboundAPIInterfaceImpl(InboundAPIListener apiServerListener, Socket socket, String socketInfo) throws UnknownHostException, IOException {
		
		

		this.apiServerListener = apiServerListener;
		
		this.socket = socket;
		
		this.socketInfo = socketInfo;
		
		this.ip = socket.getInetAddress().getHostAddress();
		
		logger.info("ip:"+ip);		

		commandInputStream = socket.getInputStream();

		clientOS = socket.getOutputStream();		

		lookupResultThread = new LookupResultThread(ip);
		
		lookupResultThread.start();
		
		queryMap = new HashMap<Integer, RegisterQueryEntity>();
		
		serverStart();
		
		
	}

	@Override
	public void run() {

		int initPacket = 0;
		byte[] packet = new byte[2048];

		try {

			while (isStarted) {

				initPacket = commandInputStream.read();	// 초기 1byte 확인
				
				logger.debug("[initPacket]: " + initPacket);

				int packetLength = commandInputStream.read(packet); // initByte 제외한 나머지 패킷 길이

				byte[] rawPacket = new byte[packetLength]; // 패킷 사이즈 재정의를 위한 byte[] 생성
				
				System.arraycopy(packet, 0, rawPacket, 0, packetLength); // 수신된 패킷 사이즈에 맞게 복사

				switch (initPacket) {
				case LOGIN:
					login(rawPacket);
					break;
				case LOGOUT:
					logout(rawPacket);
					break;					

				case REGISTER_QUERY:
					registerQuery(rawPacket, null);
					break;

				case DELETE_QUERY:
					deleteQuery(rawPacket);
					break;

				case LOOKUP_RESULT:					
					lookupResult(rawPacket);
					break;

				}

			}
		} catch (IOException e) {
			
			e.printStackTrace();
			disconnect();
			logger.debug(socketInfo + " -----> 연결 종료");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		/*finally
		{
			
		}*/
	}

	/**
	 * 로그인 메소드
	 * Payload의 ID와 PWD를 파싱한 후 비교함
	 * 
	 * @param raw
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public void login(byte[] raw) throws IOException, SQLException {

		user_id = new String(raw, 1, raw[0]); // 클라이언트 로그인 ID 추출
		String pwd = new String(raw, raw[0] + 2, raw.length - raw[0] - 2); // 클라이언트 로그인 PWD 추출
		logger.info("1. 로그인 ID: " + user_id + "\t Password: " + pwd);		

		UserInfo userInfo = new UserInfo();
		userInfo.setUser_id(user_id); // UserInfo에 클라이언트 로그인 ID 저장


		UserInfo tb_user_pw = (UserInfo) TableBufferManager.getInstance().selectUserInfo(userInfo); // 클라이언트 로그인 ID를 통해서 DB에 저장된 PWD 추출
		if(tb_user_pw==null)
		{
			clientOS.write(LOGIN_FAILURE);
			clientOS.flush();
			logger.info("인증실패(1)");
			disconnect(); // 연결 종료 (I/O 스트림 및 소켓 해제)
		}else
		{
			if (pwd.equals(tb_user_pw.getUser_pw())) 
			{ // 클라이언트 로그인 PWD와 DB PWD 비교
				user_id = userInfo.getUser_id(); // 일치하면 전역변수 userID에 클라이언트 로그인 ID 저장
				MonitorMessage message = new MonitorMessage();
				message.setUser_id(user_id);
				message.setIp(ip);
				apiServerListener.notifyMonitors(message);
				clientOS.write(LOGIN_SUCCESS);
				clientOS.flush();
				// 질의 아이디 조회
				this.queryIDList();
				logger.info("인증성공");

			} else 
			{
				clientOS.write(LOGIN_FAILURE);
				clientOS.flush();
				logger.debug("인증실패(2)");
				disconnect(); // 연결 종료 (I/O 스트림 및 소켓 해제)
			}	
		}


		clientOS.write(LOGIN_SUCCESS);
		
		clientOS.flush();

	}
	/**
	 * 로그아웃 메소드
	 * Payload의 ID와 PWD를 파싱한 후 비교함
	 * 
	 * @param raw
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public void logout(byte[] raw) throws IOException, SQLException {

		String id = new String(raw, 1, raw[0]); // 클라이언트 로그인 ID 추출
		//String pwd = new String(raw, raw[0] + 2, raw.length - raw[0] - 2); // 클라이언트 로그인 PWD 추출

		if(apiServerListener.removeClient(socket.getInetAddress().getHostAddress())){
			logger.info("로그아웃 ID: " + id);
			apiServerListener.notifyMonitor("로그아웃 ID: " + id);
			clientOS.write(LOGOUT_SUCCESS);
			clientOS.flush();
			lookupResultThread.stop();
		}
		else
		{
			logger.info("로그아웃 실패 ID: " + id);
		}
	}	

	/**
	 * 질의등록 메소드
	 * Payload의 Query 데이터를 XML 파싱함
	 * 
	 * @param raw
	 * @param queryConditionChange 
	 * @throws IOException 
	 */
	public void registerQuery(byte[] raw, QueueNode queryConditionChange) throws IOException {
		int registerQueryID=1;
		String rawQuery = new String(raw, 0, raw.length); // 질의 추출
		try{

			logger.debug("2. 질의등록 파싱 결과: " + rawQuery);
			registerQueryID = TableBufferManager.getInstance().getMaxQueryID()+1;
			DocumentBuilder domParser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document xmlDoc = domParser.parse(new InputSource(new StringReader(rawQuery)));
			NodeList conditionElements = xmlDoc.getElementsByTagName("registerQuery");

			ActionInfo actionInfo = new ActionInfo();
			//actionInfo.setCid(findElementOrContainer(xmlDoc, (Element) conditionElements.item(0), "cid").getTextContent());
			//actionInfo.setCid(findElementOrContainer(xmlDoc, (Element) conditionElements.item(0), "tid").getTextContent());
			actionInfo.setQuery_number(String.valueOf(registerQueryID));
			actionInfo.setUser_id(user_id);
			TableBufferManager.getInstance().insertActionInfo(actionInfo);

			logger.debug(user_id+" 3. 서비스 플랫폼에서 발급한 Query ID: " + registerQueryID);
			TagControlMsgQueue.getInstance().append(queryConditionChange);
			clientOS.write(registerQueryID);
			clientOS.flush();
			logger.debug("질의등록 성공");

		}
		catch(Exception e)
		{
			registerQueryID =-1;
			e.printStackTrace();
		}	
		finally
		{
			try {
				clientOS.write(registerQueryID);
				clientOS.flush();
				logger.debug("결과:"+registerQueryID);
			} catch (IOException ee) {
				ee.printStackTrace();
			}
		}

	}


	/**
	 * @param document
	 * @param parent
	 * @param element
	 * @return
	 */
	public static Element findElementOrContainer(Document document,
			Element parent, String element) {
		NodeList nl = parent.getElementsByTagName(element);
		if (nl.getLength() == 0) {
			return null;
		}
		return (Element) nl.item(0);
	}
	/**
	 * 질의삭제 메소드
	 * Payload의 Query ID를 파싱한 후 등록된 Query를 삭제함
	 * 
	 * @param raw
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public void deleteQuery(byte[] raw) throws SQLException, IOException {
		int deleteQueryID = 0;
		deleteQueryID |= ((raw[0]) << 24) & 0xFF000000;
		deleteQueryID |= ((raw[1]) << 16) & 0x00FF0000;
		deleteQueryID |= ((raw[2]) << 8) & 0x0000FF00;
		deleteQueryID |= (raw[3]) & 0x000000FF;

		// deleteQueryID를 통해 Action TB 조회 ------ userID 비교
		ActionInfo actionInfo = new ActionInfo();
		actionInfo.setQuery_number(Integer.toString(deleteQueryID));

		ActionInfo tb_action_qid = (ActionInfo) TableBufferManager.getInstance().selectActionInfo(actionInfo);
		logger.debug("delete query> user_id:"+user_id+","+deleteQueryID);
		if (user_id.equals(tb_action_qid.getUser_id())) { // 클라이언트 User ID == Action TB's User ID

			deleteTB(deleteQueryID, AIRTable.CONTAINER);
			deleteTB(deleteQueryID, AIRTable.TEMPERATURE);
			deleteTB(deleteQueryID, AIRTable.HUMIDITY);
			deleteTB(deleteQueryID, AIRTable.HIT);
			//deleteTB(deleteQueryID, AIRTable.DATE);
			deleteTB(deleteQueryID, AIRTable.ACTION); // Action TB 데이터를 마지막으로 삭제

			QueueNode queryConditionChange=null;

			TagControlMsgQueue.getInstance().append(queryConditionChange);


			clientOS.write(QUERY_DELETE_SUCCESS);
			clientOS.flush();
			logger.debug("질의삭제 성공");

			queryIDList();


		} else { // 일치하지 않으면,,,, 삭제 실패 값 전송!
			clientOS.write(QUERY_DELETE_FAILURE);
			clientOS.flush();
			logger.debug("질의삭제 실패");
		}

	}

	/**
	 * 질의결과확인 메소드
	 * Payload의 Query ID를 파싱한 후 질의결과를 확인함
	 * 
	 * @param queryID
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private void lookupResult(byte[] queryID) throws SQLException, IOException {
		int lookupResultQueryID = 0;	
		String lookupResultQuery = "";
		lookupResultQueryID |= ((queryID[0]) << 24) & 0xFF000000;
		lookupResultQueryID |= ((queryID[1]) << 16) & 0xFF0000;
		lookupResultQueryID |= ((queryID[2]) << 8) & 0xFF00;
		lookupResultQueryID |= (queryID[3]) & 0xFF;
		logger.info("lookupResultQueryID: " + lookupResultQueryID);

		// lookupResultQueryID를 통해 Action TB 조회 ------ userID 비교
		ActionInfo actionInfo = new ActionInfo();
		actionInfo.setQuery_number(Integer.toString(lookupResultQueryID));

		ActionInfo tb_action_qid = (ActionInfo) TableBufferManager.getInstance().selectActionInfo(actionInfo);

		if(tb_action_qid!=null)
		{
			if (user_id.equals(tb_action_qid.getUser_id())) { // 클라이언트 User ID == Action TB's User ID

				// API M/Q에서 검색
				// TODO NullPointException 처리 여부 추후 확인!
				MsgForAPI apiMsg = MsgQueueForAPI.getInstance().poll(user_id, lookupResultQueryID);
				try{
					lookupResultQuery = apiMsg.getResultXML();
				}catch(NullPointerException ee)
				{
					logger.error(user_id+":질의 결과 없음");
					clientOS.write(LOOKUP_RESULT_FAILURE);
					clientOS.flush();
					return;

				}

				// ACK 전송 로직 구현
				logger.info("질의 결과: " + lookupResultQuery);
				logger.info("질의 결과 byte[]: " + lookupResultQuery.getBytes());
				clientOS.write(lookupResultQuery.getBytes());
				clientOS.flush();

				logger.info("질의결과 보냄");
			} else {
				clientOS.write(LOOKUP_RESULT_FAILURE);
				clientOS.flush();
				logger.info("유저 오류:"+user_id+","+tb_action_qid.getUser_id());
				return;
			}
		}else
		{
			clientOS.write(LOOKUP_RESULT_FAILURE);
			clientOS.flush();
			logger.info("질의결과 확인 Query ID 없음2");	
		}
	}

	/**@deprecated
	 * @param message
	 * @throws IOException
	 */
	public void lookupResult(MsgForAPI message) throws IOException
	{
		logger.info("lookupResult:"+message.getResultXML());
		clientOS.write(message.getResultXML().getBytes());
		clientOS.flush();
	}
	/**
	 * @param queryID
	 * @param tableType
	 */
	private void deleteTB(int queryID, int tableType) {
		ConditionInfo conditionInfo = new ConditionInfo(tableType);
		conditionInfo.setQuery_number(String.valueOf(queryID));
		try {
			TableBufferManager.getInstance().deleteConditionInfo(conditionInfo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	 * @throws SQLException
	 */
	private void queryIDList() throws SQLException
	{
		ActionInfo op=new ActionInfo();
		op.setUser_id(user_id);
		List li=TableBufferManager.getInstance().selectQueryIDList(op);

		AIR_XMLManager xmlManager = new AIR_XMLManager();
		String query=xmlManager.queryIDList(li);

		byte[] bQuery = query.getBytes();

		byte[] registerPacket = new byte[1 + query.length()]; // // type(1) + query 

		registerPacket[0] = 0x05; // Packet Type
		for (int i = 0; i < query.length(); i++) {
			registerPacket[i + 1] = bQuery[i]; // query
		}

		try {
			clientOS.write(registerPacket);
			clientOS.flush();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}catch(NullPointerException e)
		{
			JOptionPane.showMessageDialog(null, "Not login");
		}
	}

	/**
	 * (자원)연결해제 메소드
	 * I/O 및 Socket 연결을 해제함
	 * 
	 */
	public void disconnect() {
		try {
			if (clientOS != null) {
				clientOS.close();
			}
			if (commandInputStream != null) {
				commandInputStream.close();
			}
			if (!socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			logger.debug("해제 오류: " + e.getMessage());
		}
	}
	@Override
	public void serverStart() {
		if(thread== null)
		{
			isStarted=true;
			thread = new Thread(this);  
			thread.start();
		}

	}

	@Override
	public void serverStop() {

		isStarted=false;
		thread = null;

	}
	/**
	 * @설명 api 큐에서 결과를 받아서 클라이언트로 전송하는 모듈
	 * @author archehyun
	 *
	 */
	class LookupResultThread implements Runnable
	{
		private static final int LOOKUP_PORT = 10010;

		String lookupResultQuery;

		OutputStream lookupResultOutputStream;
		
		private Socket lookUpSocket;
		

		
		public LookupResultThread(String clientIP) throws IOException {
			
			lookUpSocket = new Socket(clientIP,LOOKUP_PORT);
			
			lookupResultOutputStream = lookUpSocket.getOutputStream();
			
			logger.info(socket.getPort());

		}
		Thread thread;
		
		public void start()		
		{
			isStarted = true;
			thread = new Thread(this);
			thread.start();
		}
		
		public void stop()
		{
			isStarted = false;
			thread = null;
			
			
		}

		public void run()		
		{
			logger.info("lookupResult Start("+lookUpSocket.getPort()+")...");
			while(isStarted)
			{
				try{
					MsgForAPI apiMsg = (MsgForAPI) MsgQueueForAPI.getInstance().poll();

					// ACK 전송 로직 구현

					lookupResultQuery = apiMsg.getResultXML();

					lookupResultOutputStream.write(lookupResultQuery.getBytes());
					lookupResultOutputStream.flush();
					logger.info("send message:"+this.lookUpSocket.getInetAddress().getHostName()+":"+lookUpSocket.getPort());
					logger.info("질의 결과: " + lookupResultQuery);
					logger.info("질의 결과 byte[]: " + lookupResultQuery.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch(NullPointerException ee)
				{
					try {
						lookupResultOutputStream.write(LOOKUP_RESULT_FAILURE);
						lookupResultOutputStream.flush();
						logger.error(user_id+":질의 결과 없음");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return;

				}
			}
		}

	}
	@Override
	public void yu(byte[] raw) throws SQLException, IOException {
		// TODO Auto-generated method stub
		
	}

}
