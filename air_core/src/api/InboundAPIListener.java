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
 * ���� �÷����� ����ϴ� Ŭ���̾�Ʈ ������ ���� ������ ��ü
 * 
 * @author		�����
 * @since       2014-02-08
 * @version     0.1       
 */
public class InboundAPIListener extends InboundListener implements Runnable{
	
	ServerProcess serverProcess;
	
	private static InboundAPIListener apiServerListener; // ApiServerListener ��ü �ν��Ͻ�

	static {
		apiServerListener = new InboundAPIListener();		
	}

	private HashMap<String, InboundAPIInterfaceImpl> clientList;// Ŭ���̾�Ʈ ���� ���

	private ServerSocket serverSocket; // ���� ����

	private Socket socket; // Ŭ���̾�Ʈ ����


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
	 * ���� ��� �� ��ȯ�Ǵ� ������ ������Ű�� �޼ҵ�
	 *  ���� ����: ���� ���̵� ���� ��� �輱
	 * @return	1�� ������ queryID		
	 */
	private int queryID = 0; // ���� ��� �� ������ ���� ���� ��ȣ
	
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
					logger.info(socketInfo + " -----> ���� ����");

					InboundAPIInterfaceImpl client=new InboundAPIInterfaceImpl(this, socket, socketInfo);					

					clientList.put(socket.getInetAddress().getHostAddress(), client);
					
					notifyMonitor("login:"+socketInfo);
				}
				else
				{
					logger.info(socketInfo + " -----> ���� ���� ����");
				}

			} catch (IOException e) {

				logger.error("���� ����:"+e.getMessage());
				e.printStackTrace();
			}			
		}
		} catch (IOException e) {

			logger.error("����:"+e.getMessage());
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
