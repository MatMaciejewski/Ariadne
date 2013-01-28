package ariadne.net;

import java.util.ArrayList;

public class Server {
	private Dispatcher dispatcher;
	private ArrayList<Worker> workers;
	private boolean running;

	public Server(int port) {
		dispatcher = new SocketChannelDispatcher(port);
		workers = new ArrayList<Worker>();
		running = false;
	}

	public void start(int threadCount) {
		stop();

		if (dispatcher.enable()) {
			for (int i = 0; i < threadCount; ++i) {
				Worker w = new WorkHandler();
				w.setDispatcher(dispatcher);
				Thread t = new Thread(w);
				t.start();
				workers.add(w);
			}
			running = true;
		}
	}

	public void stop() {
		if (running) {
			for (int i = 0; i < workers.size(); ++i) {
				workers.get(i).stopWorking();
			}
			dispatcher.disable();
			workers = new ArrayList<Worker>();
			running = false;
		}
	}
}
