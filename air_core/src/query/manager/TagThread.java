package query.manager;

import msg.node.InboundMsgForData;
import msg.queue.CQPTagQueue;

public class TagThread implements Runnable{
	
	private String tid;// 태그 아이디
	private Thread thread;
	private CQPTagQueue queue;
	private boolean flag;
	
	public TagThread(String tid) {
		
		
		queue = new CQPTagQueue();
		this.tid= tid;
		thread = new Thread(this);
		thread.start();
	}
	@Override
	public void run() {
		
		
		while (flag)
		{
			InboundMsgForData message = (InboundMsgForData) queue.poll();
			process(message);
			updateState();
		}
		
	}
	private void process(InboundMsgForData message) {
		// TODO Auto-generated method stub
		
	}		
	private void updateState() {
		// TODO Auto-generated method stub
		
	}

}
