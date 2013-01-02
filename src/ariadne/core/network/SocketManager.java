package ariadne.core.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Semaphore;

class SocketManager {
	public class Conversation {
		private SocketChannel channel;
		private ConversationState state;

		private Conversation(SocketChannel c, ConversationState s) {
			channel = c;
			state = s;
		}
		
		public int id(){
			return channel.socket().hashCode();
		}

		public Socket socket() {
			return channel.socket();
		}

		public ConversationState getState() {
			return state;
		}

		public void setState(ConversationState s) {
			state = s;
		}
		
		public InputStream input() throws IOException{
			return channel.socket().getInputStream();
		}
		
		public OutputStream output() throws IOException{
			return channel.socket().getOutputStream();
		}

		public void close() {
			try {
				socket().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private int port;
	private ServerSocketChannel ssc;
	private Selector selector;
	private LinkedList<Conversation> queue;
	private LinkedList<Conversation> awaiting;
	private HashMap<SelectionKey, ConversationState> conversations;
	private SelectionKey sscKey;
	private boolean running = false;
	private Semaphore sem_awaiting;
	private boolean hangs_on_select;

	public SocketManager(int port) {
		this.port = port;
		ssc = null;
		selector = null;
		queue = new LinkedList<Conversation>();
		awaiting = new LinkedList<Conversation>();
		conversations = new HashMap<SelectionKey, ConversationState>();
		sem_awaiting = new Semaphore(1);
		running = false;
		hangs_on_select = false;
	}

	public int getPort() {
		return port;
	}

	public boolean isRunning() {
		return running;
	}

	public void start() throws Exception {
		InetAddress lh;
		ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		lh = InetAddress.getLocalHost();
		InetSocketAddress isa = new InetSocketAddress(lh, port);
		ssc.socket().bind(isa);
		selector = SelectorProvider.provider().openSelector();
		sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);

		running = true;
	}

	public void stop() throws Exception {
		running = false;
		sscKey.cancel();
		selector.close();
		ssc.close();
		conversations.clear();
		queue.clear();
	}

	public void awaitResponse(Conversation c){
		if (!running)
			return;
		try {
			sem_awaiting.acquire();
			
			awaiting.push(c);
			if(hangs_on_select) selector.wakeup();
			
			sem_awaiting.release();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
	}

	public synchronized Conversation askForWork() {
		if (!running)
			return null;
		SocketChannel socket;
		boolean timeout = true;
		
		while (queue.size() == 0) {
			//perform the SELECT operation
			hangs_on_select = true;
			try {
				if(timeout){
					//do not wait for result - selector must acknowledge the SelectionKey.cancel() so we can add it again.
					selector.selectNow();
				}else{
					selector.select();
				}
			} catch (IOException e) {
				e.printStackTrace();
				hangs_on_select = false;
				return null;
			}
			hangs_on_select = false;
			timeout = false;
			try {
				//add awaiting conversations to the selector
				sem_awaiting.acquire();
				while (awaiting.size() > 0) {
					try {
						Conversation c = awaiting.removeFirst();
						c.channel.configureBlocking(false);
						SelectionKey k = c.channel.register(selector, SelectionKey.OP_READ);
						conversations.put(k, c.state);
					} catch (ClosedChannelException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				sem_awaiting.release();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				timeout = true;
			}
			
			//get the selected keys
			Set<SelectionKey> selected;
			try {
				selected = selector.selectedKeys();
			} catch (ClosedSelectorException e) {
				return null;
			}

			//perform appropriate action for each key
			for (SelectionKey k : selected) {
				try {
					switch (k.interestOps()) {
					case SelectionKey.OP_READ:
						socket = (SocketChannel) k.channel();
						queue.push(new Conversation(socket, conversations.remove(k)));
						k.cancel();
						socket.configureBlocking(true);
						System.out.println("Data received.");

						break;

					case SelectionKey.OP_ACCEPT:

						SelectionKey sk;
						socket = ssc.accept();
						socket.configureBlocking(false);
						sk = socket.register(selector, SelectionKey.OP_READ);
						conversations.put(sk, null);
						System.out.println("New connection accepted (" + socket.socket().getInetAddress().getHostAddress() + ":" + socket.socket().getPort() + ").");

						break;
					default:
						System.out.println("Unsupported selection key!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			selected.clear();
		}
		//if work queue is not empty, return the pending conversation.
		return queue.pop();
	}
}
