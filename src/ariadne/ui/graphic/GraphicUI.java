package ariadne.ui.graphic;

import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * TODO:
 * 1. eventLoop
 * 2. GraphicUI.Delegate (eventy)
 * 3. Server
 * 4. Client
 */

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ariadne.data.Hash;
import ariadne.ui.DelegableUI;
import ariadne.ui.UI;
import ariadne.ui.UI.Event;
import ariadne.ui.UI.Listener;

public class GraphicUI extends DelegableUI {
	
	private Window window;
	
	
	public GraphicUI(){
		final Delegate d = this.getDelegateInstance();
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				window = new Window(d);
			}
		});
	}
	
	@Override
	public void open() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				window.show();
			}
		});
	}

	@Override
	public void close() {
		this.breakEventLoop();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				window.hide();
				window.close();
			}
		});
	}

	@Override
	public void dropEntry(Hash hash) {
		final Hash h = hash;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				window.dropEntry(h);
			}
		});
	}

	@Override
	public void showEntry(Hash hash, String name, float percent, float download, float upload) {
		final Hash h = hash;
		final String n = name;
		System.out.println("show");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				System.out.println("event fired");
				window.showEntry(h, n);
			}
		});
	}
}
