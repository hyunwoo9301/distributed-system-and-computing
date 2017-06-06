package manager;

import server.*;

public class NodeManager implements Runnable, Timable {
	
	public NodeManager() {

	}
	
	public void send_heartbeat() {
		Message smsg = new Message("RESOURCEMANAGER", "HEARTBEAT", Server.getCoordinator(), "");
		Server.getMessageQueue().accept(smsg);
	}
	
	public void run() {
		System.out.println("NODEMANAGER UP");
		
		Thread.currentThread();
		while(!Thread.interrupted()) {
			try {
				Thread.sleep(HEARTBEAT_TICK);
				send_heartbeat();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		System.out.println("NODEMANAGER DOWN");
	}

	public void timeout(String type) {
		
	}
	
	public void startTimer(String type) {

	}
	
	public void stopTimer() {

	}
}
