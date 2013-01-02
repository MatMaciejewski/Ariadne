package ariadne.core.network;

import ariadne.core.network.SocketManager.Conversation;

public abstract class AbstractServerWorker extends java.lang.Thread {
	private SocketManager sm;

	public void run() {
		Conversation c;
		System.out.println("Thread " + this.getId() + " started.");
		try {
			while (sm.isRunning()) {
				System.out.println("Thread " + getId() + " asks for work.");
				c = sm.askForWork();
				if (c != null){
					System.out.println("Thread " + this.getId() + " handles conversation " + c.id());
					work(c);
				}else{
					System.out.println("NULL Conversation received.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Thread " + this.getId() + " stopped.");
	}

	public void setSocketManager(SocketManager s) {
		sm = s;
	}

	protected void await(Conversation c) {
		try {
			sm.awaitResponse(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public abstract void work(Conversation c);
}