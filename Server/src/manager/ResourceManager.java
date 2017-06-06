package manager;

import java.util.*;

import server.Message;
import server.PassiveQueue;
import server.Server;

public class ResourceManager extends PassiveQueue<Message> implements Runnable, Timable {
	private Timer timer;
	private boolean shouldStop;
	
	public ResourceManager() {
		
	}
	
	public void timeout(String type) {
		Message msg = new Message("ELECTION", "TIMEOUT", "", "OK");
		super.accept(msg);
	}
	
	public synchronized void update_nodes() {
		/* ConcurrentModification �ذ��� ���� ���� */
		HashMap<String, Integer> temp = (HashMap<String, Integer>)Server.getAliveServerMap().clone();
		
		for(Map.Entry<String, Integer> entry : temp.entrySet()) {
			entry.setValue(entry.getValue() + TIMER_TICK);
			if(entry.getValue() > HEARTBEAT_TIMEOUT) {
				temp.remove(entry.getKey());
				System.out.println(entry.getKey() + " Down");
			}
		}
		Server.setAliveServerMap(temp);
	}
	
	public synchronized void update_nodes(String ip) {
		Server.setAliveServerMap(ip);
	}
	
	public void startTimer(String type) {
		stopTimer();
		timer = new Timer(this, type);
		timer.start();
	}
	
	public void stopTimer() {
		if(timer != null)
		{
			timer.interrupt();
			timer = null;
		}
	}

	public void run() {	
		startTimer("HEARTBEAT");
		
		while(!shouldStop) {
			Message msg = super.release();
			switch(msg.getFlag()) {
				case "HEARTBEAT":
					update_nodes(msg.getAddr());
					break;
				case "TIMEOUT":
					update_nodes();
					startTimer("HEARTBEAT");
					break;
				case "EXIT":
					stopTimer();
					shouldStop = true;
					break;
			}
		}
		
		stopTimer();
		System.out.println("RESOURCE MANAGER IS DOWN");
	}	
}