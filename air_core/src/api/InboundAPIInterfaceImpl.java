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
 * ���� �÷����� ����ϴ� Ŭ���̾�Ʈ ������ ���� ������ ��ü
 * 
 * ���� �÷��� �� Ŭ���̾�Ʈ ���� ��ü
 * 
 * @author		�����
 * @since       2014-02-08
 * @version     0.1       
 */

public class InboundAPIInterfaceImpl extends AIRThread implements Runnable, IFInboundAPI{

	private final int LOGIN_SUCCESS = 1; // �α��� ���� ACK
	private final int LOGIN_FAILURE = 0; // �α��� ���� ACK

	private final int LOGOUT_SUCCESS = 1; // �α׾ƿ� ���� ACK
	private final int LOGOUT_FAILURE = 0; // �α׾ƿ� ���� ACK

	private final int QUERY_DELETE_SUCCESS = 1; // ���ǻ��� ���� ACK
	private final int QUERY_DELETE_FAILURE = 0; // ���ǻ��� ���� ACK
	private final int LOOKUP_RESULT_FAILURE = 0; // �߸��� Query ID

	private final byte LOGIN = 			(byte)0x01;
	private final byte REGISTER_QUERY = (byte)0x02;
	private final byte DELETE_QUERY = 	(byte)0x03;
	private final byte LOOKUP_RESULT = 	(byte)0x04;
	private final byte QUERYID_LIST = 	(byte)0x05;
	private final byte LOGOUT = 		(byte)0x06;

	private InboundAPIListener apiServerListener;
	
	private Socket socket; // ����
	
	private String socketInfo; // Ŭ���̾�Ʈ ���� ����(IP:Port)
	
	private InputStream commandInputStream; // �Է½�Ʈ��
	
	private InputStream lookupResultInputStream; // �Է½�Ʈ��
	
	private OutputStream clientOS; // ��½�Ʈ��

	LookupResultThread lookupResultThread;

	private String user_id; // ���� ID ���� ����
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	private String ip;

	private int port;
	
	private HashMap<Integer, RegisterQueryEntity> queryMap; // ���� ID�� ���� Entity�� �����ϱ� ���� �ؽø�


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

				initPacket = commandInputStream.read();	// �ʱ� 1byte Ȯ��
				
				logger.debug("[initPacket]: " + initPacket);

				int packetLength = commandInputStream.read(packet); // initByte ������ ������ ��Ŷ ����

				byte[] rawPacket = new byte[packetLength]; // ��Ŷ ������ �����Ǹ� ���� byte[] ����
				
				System.arraycopy(packet, 0, rawPacket, 0, packetLength); // ���ŵ� ��Ŷ ����� �°� ����

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
			logger.debug(socketInfo + " -----> ���� ����");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		/*finally
		{
			
		}*/
	}

	/**
	 * �α��� �޼ҵ�
	 * Payload�� ID�� PWD�� �Ľ��� �� ����
	 * 
	 * @param raw
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public void login(byte[] raw) throws IOException, SQLException {

		user_id = new String(raw, 1, raw[0]); // Ŭ���̾�Ʈ �α��� ID ����
		String pwd = new String(raw, raw[0] + 2, raw.length - raw[0] - 2); // Ŭ���̾�Ʈ �α��� PWD ����
		logger.info("1. �α��� ID: " + user_id + "\t Password: " + pwd);		

		UserInfo userInfo = new UserInfo();
		userInfo.setUser_id(user_id); // UserInfo�� Ŭ���̾�Ʈ �α��� ID ����


		UserInfo tb_user_pw = (UserInfo) TableBufferManager.getInstance().selectUserInfo(userInfo); // Ŭ���̾�Ʈ �α��� ID�� ���ؼ� DB�� ����� PWD ����
		if(tb_user_pw==null)
		{
			clientOS.write(LOGIN_FAILURE);
			clientOS.flush();
			logger.info("��������(1)");
			disconnect(); // ���� ���� (I/O ��Ʈ�� �� ���� ����)
		}else
		{
			if (pwd.equals(tb_user_pw.getUser_pw())) 
			{ // Ŭ���̾�Ʈ �α��� PWD�� DB PWD ��
				user_id = userInfo.getUser_id(); // ��ġ�ϸ� �������� userID�� Ŭ���̾�Ʈ �α��� ID ����
				MonitorMessage message = new MonitorMessage();
				message.setUser_id(user_id);
				message.setIp(ip);
				apiServerListener.notifyMonitors(message);
				clientOS.write(LOGIN_SUCCESS);
				clientOS.flush();
				// ���� ���̵� ��ȸ
				this.queryIDList();
				logger.info("��������");

			} else 
			{
				clientOS.write(LOGIN_FAILURE);
				clientOS.flush();
				logger.debug("��������(2)");
				disconnect(); // ���� ���� (I/O ��Ʈ�� �� ���� ����)
			}	
		}


		clientOS.write(LOGIN_SUCCESS);
		
		clientOS.flush();

	}
	/**
	 * �α׾ƿ� �޼ҵ�
	 * Payload�� ID�� PWD�� �Ľ��� �� ����
	 * 
	 * @param raw
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public void logout(byte[] raw) throws IOException, SQLException {

		String id = new String(raw, 1, raw[0]); // Ŭ���̾�Ʈ �α��� ID ����
		//String pwd = new String(raw, raw[0] + 2, raw.length - raw[0] - 2); // Ŭ���̾�Ʈ �α��� PWD ����

		if(apiServerListener.removeClient(socket.getInetAddress().getHostAddress())){
			logger.info("�α׾ƿ� ID: " + id);
			apiServerListener.notifyMonitor("�α׾ƿ� ID: " + id);
			clientOS.write(LOGOUT_SUCCESS);
			clientOS.flush();
			lookupResultThread.stop();
		}
		else
		{
			logger.info("�α׾ƿ� ���� ID: " + id);
		}
	}	

	/**
	 * ���ǵ�� �޼ҵ�
	 * Payload�� Query �����͸� XML �Ľ���
	 * 
	 * @param raw
	 * @param queryConditionChange 
	 * @throws IOException 
	 */
	public void registerQuery(byte[] raw, QueueNode queryConditionChange) throws IOException {
		int registerQueryID=1;
		String rawQuery = new String(raw, 0, raw.length); // ���� ����
		try{

			logger.debug("2. ���ǵ�� �Ľ� ���: " + rawQuery);
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

			logger.debug(user_id+" 3. ���� �÷������� �߱��� Query ID: " + registerQueryID);
			TagControlMsgQueue.getInstance().append(queryConditionChange);
			clientOS.write(registerQueryID);
			clientOS.flush();
			logger.debug("���ǵ�� ����");

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
				logger.debug("���:"+registerQueryID);
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
	 * ���ǻ��� �޼ҵ�
	 * Payload�� Query ID�� �Ľ��� �� ��ϵ� Query�� ������
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

		// deleteQueryID�� ���� Action TB ��ȸ ------ userID ��
		ActionInfo actionInfo = new ActionInfo();
		actionInfo.setQuery_number(Integer.toString(deleteQueryID));

		ActionInfo tb_action_qid = (ActionInfo) TableBufferManager.getInstance().selectActionInfo(actionInfo);
		logger.debug("delete query> user_id:"+user_id+","+deleteQueryID);
		if (user_id.equals(tb_action_qid.getUser_id())) { // Ŭ���̾�Ʈ User ID == Action TB's User ID

			deleteTB(deleteQueryID, AIRTable.CONTAINER);
			deleteTB(deleteQueryID, AIRTable.TEMPERATURE);
			deleteTB(deleteQueryID, AIRTable.HUMIDITY);
			deleteTB(deleteQueryID, AIRTable.HIT);
			//deleteTB(deleteQueryID, AIRTable.DATE);
			deleteTB(deleteQueryID, AIRTable.ACTION); // Action TB �����͸� ���������� ����

			QueueNode queryConditionChange=null;

			TagControlMsgQueue.getInstance().append(queryConditionChange);


			clientOS.write(QUERY_DELETE_SUCCESS);
			clientOS.flush();
			logger.debug("���ǻ��� ����");

			queryIDList();


		} else { // ��ġ���� ������,,,, ���� ���� �� ����!
			clientOS.write(QUERY_DELETE_FAILURE);
			clientOS.flush();
			logger.debug("���ǻ��� ����");
		}

	}

	/**
	 * ���ǰ��Ȯ�� �޼ҵ�
	 * Payload�� Query ID�� �Ľ��� �� ���ǰ���� Ȯ����
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

		// lookupResultQueryID�� ���� Action TB ��ȸ ------ userID ��
		ActionInfo actionInfo = new ActionInfo();
		actionInfo.setQuery_number(Integer.toString(lookupResultQueryID));

		ActionInfo tb_action_qid = (ActionInfo) TableBufferManager.getInstance().selectActionInfo(actionInfo);

		if(tb_action_qid!=null)
		{
			if (user_id.equals(tb_action_qid.getUser_id())) { // Ŭ���̾�Ʈ User ID == Action TB's User ID

				// API M/Q���� �˻�
				// TODO NullPointException ó�� ���� ���� Ȯ��!
				MsgForAPI apiMsg = MsgQueueForAPI.getInstance().poll(user_id, lookupResultQueryID);
				try{
					lookupResultQuery = apiMsg.getResultXML();
				}catch(NullPointerException ee)
				{
					logger.error(user_id+":���� ��� ����");
					clientOS.write(LOOKUP_RESULT_FAILURE);
					clientOS.flush();
					return;

				}

				// ACK ���� ���� ����
				logger.info("���� ���: " + lookupResultQuery);
				logger.info("���� ��� byte[]: " + lookupResultQuery.getBytes());
				clientOS.write(lookupResultQuery.getBytes());
				clientOS.flush();

				logger.info("���ǰ�� ����");
			} else {
				clientOS.write(LOOKUP_RESULT_FAILURE);
				clientOS.flush();
				logger.info("���� ����:"+user_id+","+tb_action_qid.getUser_id());
				return;
			}
		}else
		{
			clientOS.write(LOOKUP_RESULT_FAILURE);
			clientOS.flush();
			logger.info("���ǰ�� Ȯ�� Query ID ����2");	
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
	 * (�ڿ�)�������� �޼ҵ�
	 * I/O �� Socket ������ ������
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
			logger.debug("���� ����: " + e.getMessage());
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
	 * @���� api ť���� ����� �޾Ƽ� Ŭ���̾�Ʈ�� �����ϴ� ���
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

					// ACK ���� ���� ����

					lookupResultQuery = apiMsg.getResultXML();

					lookupResultOutputStream.write(lookupResultQuery.getBytes());
					lookupResultOutputStream.flush();
					logger.info("send message:"+this.lookUpSocket.getInetAddress().getHostName()+":"+lookUpSocket.getPort());
					logger.info("���� ���: " + lookupResultQuery);
					logger.info("���� ��� byte[]: " + lookupResultQuery.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch(NullPointerException ee)
				{
					try {
						lookupResultOutputStream.write(LOOKUP_RESULT_FAILURE);
						lookupResultOutputStream.flush();
						logger.error(user_id+":���� ��� ����");
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
