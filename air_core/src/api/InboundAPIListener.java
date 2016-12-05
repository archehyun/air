package api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import io.InboundListener;
import msg.node.MsgForAPI;
import msg.queue.MsgQueueForAPI;

/**
 * 서비스 플랫폼을 사용하는 클라이언트 연결을 위한 리스너 객체
 * 
 * @author		손희목
 * @since       2014-02-08
 * @version     0.1       
 */
public class InboundAPIListener extends InboundListener implements Runnable{
	
	ServerProcess serverProcess;
	
	private static InboundAPIListener apiServerListener; // ApiServerListener 객체 인스턴스

	static {
		apiServerListener = new InboundAPIListener();		
	}

	private HashMap<String, InboundAPIInterfaceImpl> clientList;// 클라이언트 연결 목록

	private ServerSocket serverSocket; // 서버 소켓

	private Socket socket; // 클라이언트 소켓


	private InboundAPIListener() {
		port=10001; // IP port:default:10001
			clientList = new HashMap<String, InboundAPIInterfaceImpl>();

			serverProcess = new ServerProcess();

			
			new Thread(serverProcess).start();
		
	}

	public static InboundAPIListener getInstance() {
		return apiServerListener;
	}

	/**
	 * 질의 등록 시 반환되는 쿼리를 증가시키는 메소드
	 *  수정 예정: 질의 아이디 생성 방법 계선
	 * @return	1씩 증가된 queryID		
	 */
	private int queryID = 0; // 질의 등록 및 관리를 위한 고유 번호
	
	public int addQueryID() {				
		return ++queryID;
	}


	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			logger.info("AIR Service Platform("+InetAddress.getLocalHost().getHostAddress()+") Starting..");
		

		logger.info("API Server("+ serverSocket.getLocalPort() + ") Starting...\n");

		while (isStarted) {
			try{
				socket = serverSocket.accept();			

				String socketInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
				
				logger.info("access:"+socketInfo);

				if(!clientList.containsKey(socket.getInetAddress().getHostAddress()))
				{
					logger.info(socketInfo + " -----> 연결 성공");

					InboundAPIInterfaceImpl client=new InboundAPIInterfaceImpl(this, socket, socketInfo);					

					clientList.put(socket.getInetAddress().getHostAddress(), client);
					
					notifyMonitor("login:"+socketInfo);
				}
				else
				{
					logger.info(socketInfo + " -----> 기존 연결 존재");
				}

			} catch (IOException e) {

				logger.error("소켓 에러:"+e.getMessage());
				e.printStackTrace();
			}			
		}
		} catch (IOException e) {

			logger.error("에러:"+e.getMessage());
		}
	}
	public boolean removeClient(String host)
	{
		if(clientList.containsKey(host))
		{
			clientList.remove(host);
			
			return true;
		}
		else
		{
			return false;
		}
	}
	class ServerProcess implements Runnable
	{
		MsgQueueForAPI queue = MsgQueueForAPI.getInstance();
		
		@Override
		public void run() {

			while(isStarted)
			{
				MsgForAPI api = (MsgForAPI) queue.poll();
				
				String userID=api.getUserID();
				
				Set keys= clientList.keySet();
				
				Iterator iter  = keys.iterator();
				
				while(iter.hasNext())
				{
					InboundAPIInterfaceImpl client=clientList.get(iter.next());
					
					if(client.getUser_id().equals(userID))
					{
						try {

							client.lookupResult(api);
						} catch (IOException e) {

							e.printStackTrace();
							
							clientList.remove(userID);
						}
					}
				}
			}			
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
		/*try {
			if(serverSocket !=null)
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


		isStarted=false;
		thread = null;



	}
}
