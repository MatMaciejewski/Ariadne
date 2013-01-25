package ariadne.net;

public interface Dispatcher {
	public boolean enable();
	public void disable();
	public Conversation accept();
	public void await(Conversation c);
	public void dispose(Conversation c);
	public int getPort();
}
