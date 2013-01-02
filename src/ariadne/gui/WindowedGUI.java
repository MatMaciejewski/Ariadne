package ariadne.gui;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class WindowedGUI extends GUI {

	private Window window;

	public WindowedGUI() {
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
				window = new Window(getDelegate());
			}
		});
	}

	@Override
	public void removeFileData(String hash) {
	}

	@Override
	public void setFileData(String hash, String name, float progress, float download, float upload) {
	
	}

	@Override
	public void stop() {
		window.close();
	}

	@Override
	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				window.show();
			}
		});
	}
}
