package ariadne;

import java.io.IOException;
import java.nio.ByteBuffer;

import ariadne.net.Conversation;
import ariadne.net.Dispatcher;
import ariadne.net.SocketChannelDispatcher;
import ariadne.ui.UI;
import ariadne.ui.DelegableUI.HashAddedEvent;
import ariadne.ui.UI.Event;
import ariadne.ui.UI.Listener;
import ariadne.ui.graphic.GraphicUI;


public class Ariadne {
	public static void main(String[] args){
		
		Conversation c;
		Dispatcher d = new SocketChannelDispatcher(25566);
		
		d.enable();
		c = d.accept();
		ByteBuffer b = ByteBuffer.allocate(1024);
		try {
			c.getSocket().read(b);
			for(int i=0;i<b.position();++i){
				System.out.println((char) b.get(i));
			}
			
			System.out.println(b);
		} catch (IOException e) {
			System.out.println("IOExc");
		}
		d.await(c);
		c = d.accept();
		try {
			c.getSocket().read(b);
			for(int i=0;i<b.position();++i){
				System.out.println((char) b.get(i));
			}
			
			System.out.println(b);
		} catch (IOException e) {
			System.out.println("IOExc");
		}
		
		d.dispose(c);
		d.disable();
		
		/*
		final UI ui = new GraphicUI();
		ui.open();
		ui.onClosing(new Listener(){
			public void trigger(Event e) {
				ui.close();
			}});
		
		ui.onHashAdded(new Listener(){
			public void trigger(Event e) {
				HashAddedEvent ev = (HashAddedEvent) e;
				System.out.println(ev.data);
			}});
		
		ui.eventLoop();
		ui.close();
		*/
	}
}
