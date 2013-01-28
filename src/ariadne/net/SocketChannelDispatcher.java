package ariadne.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import ariadne.utils.Log;

public class SocketChannelDispatcher implements Dispatcher {

	private int port;
	private ServerSocketChannel ssc;
	private SelectionKey sscKey;
	private Selector selector;
	private Queue<Conversation> ready;
	private Queue<Conversation> awaiting;
	private Map<SelectionKey, Conversation.State> states;

	public SocketChannelDispatcher(int port) {
		this.port = port;
		ssc = null;
		selector = null;
	}

	@Override
	public boolean enable() {
		InetAddress lh;
		ready = new LinkedList<Conversation>();
		awaiting = new ConcurrentLinkedQueue<Conversation>();
		states = new HashMap<SelectionKey, Conversation.State>();
		try {
			ssc = ServerSocketChannel.open();
		} catch (IOException e) {
			Log.error("Could not open the server socket");
			return false;
		}
		try {
			ssc.configureBlocking(false);
		} catch (IOException e) {
			Log.error("Could not configure server socket blocking");
			return false;
		}
		try {
			lh = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			Log.error("Could not retreive the local machine address");
			return false;
		}
		InetSocketAddress isa = new InetSocketAddress(lh, port);
		try {
			ssc.socket().bind(isa);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			Log.error("Could not bind the server socket to local address");
			return false;
		}
		try {
			selector = SelectorProvider.provider().openSelector();
		} catch (IOException e) {
			Log.error("Could not create a socket multiplexer (selector)");
		}
		try {
			sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
		} catch (ClosedChannelException e) {
			Log.error("Could not register server socket in a selector");
		}

		return true;
	}

	@Override
	public void disable() {
		sscKey.cancel();
		try {
			selector.close();
		} catch (IOException e) {
			Log.warning("Could not close the selector - ignoring.");
		}
		try {
			ssc.close();
		} catch (IOException e) {
			Log.warning("Could not close the server socket - ignoring.");
		}
	}

	@Override
	public synchronized Conversation accept() {
		Set<SelectionKey> selected;
		SocketChannel socket;
		Conversation c;
		SelectionKey sk;
		
		//if no conversations currently await for workers
		while(ready.size() == 0){
			//if selector is opened and works
			if (selector.isOpen()) {
				try {
					//perform select()
					selector.select();
				} catch (IOException e) {
					//something went wrong - return null (failure)
					Log.warning("Selector.select() had thrown an exception.");
					return null;
				}
				//now add the awaiting conversations to the selector
				while(awaiting.size() > 0){
					c = awaiting.poll();
					try {
						sk = c.getSocket().register(selector, SelectionKey.OP_READ);
						states.put(sk, c.getState());
					} catch (ClosedChannelException e) {
						Log.notice("Tried to ad a closed conversation to the selector.");
					}
				}
				
				//get the selected keys
				try{
					selected = selector.selectedKeys();
				}catch(ClosedSelectorException e){
					continue;
				}
				
				for(SelectionKey k: selected){
					switch(k.interestOps()){
					
					case SelectionKey.OP_READ:
						socket = (SocketChannel) k.channel();
						ready.add(new Conversation(socket, states.remove(k)));
						k.cancel();
						break;
					
					case SelectionKey.OP_ACCEPT:
						try {
							socket = ssc.accept();
							socket.configureBlocking(false);
							socket.register(selector, SelectionKey.OP_READ);
						} catch (IOException e) {
							Log.warning("SSC.accept() failed - ignoring.");
						}
						break;
						
					default:
						Log.warning("Unsupported selectionKey intercepted.");
					}
				}
				selected.clear();
				
			} else {
				//selector closed - return null (failure).
				return null;
			}
		}
		return ready.poll();
	}

	@Override
	public void await(Conversation c) {
		awaiting.add(c);
		selector.wakeup();
	}

	@Override
	public void dispose(Conversation c) {
		try {
			c.getSocket().close();
		} catch (IOException e) {
			Log.notice("Conversation disposing generated an exception.");
		}
	}

	@Override
	public int getPort() {
		return port;
	}
}
