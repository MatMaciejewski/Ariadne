package ariadne.ui.graphic;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import ariadne.data.Hash;
import ariadne.ui.DelegableUI;

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
	public void showEntry(Hash hash, String name, long size, long posessed, long downRate, long upRate, float ratio) {
		final Hash _hash = hash;
		final String _name = name;
		final long _size = size;
		final long _posessed = posessed;
		final long _downRate = downRate;
		final long _upRate = upRate;
		final float _ratio = ratio;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				window.showEntry(_hash, _name, _size, _posessed, _downRate, _upRate, _ratio);
			}
		});
	}
}
