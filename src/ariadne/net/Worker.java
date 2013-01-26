package ariadne.net;

public abstract class Worker implements Runnable {
	private boolean working;
	private Dispatcher dispatcher;
	

	public void setDispatcher(Dispatcher d){
		dispatcher = d;
	}
	
	public void stopWorking(){
		working = false;
	}

	@Override
	public void run() {
		Conversation c;
		working = true;
		while(working){
			c = dispatcher.accept();
			if(c != null){
				if(handle(c)){
					dispatcher.await(c);
				}else{
					dispatcher.dispose(c);
				}
			}
		}
	}

	public abstract boolean handle(Conversation c);
}
