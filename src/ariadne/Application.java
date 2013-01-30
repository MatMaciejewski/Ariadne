package ariadne;

import java.util.List;

import ariadne.data.Catalogue;
import ariadne.data.Database;
import ariadne.data.Hash;
import ariadne.data.Librarian;
import ariadne.data.Settings;
import ariadne.net.Address;
import ariadne.net.Client;
import ariadne.net.Server;
import ariadne.ui.UI;
import ariadne.ui.DelegableUI.FileAddedEvent;
import ariadne.ui.DelegableUI.FilePropertiesEvent;
import ariadne.ui.DelegableUI.HashAddedEvent;
import ariadne.ui.DelegableUI.HashRemovedEvent;
import ariadne.ui.UI.Event;
import ariadne.ui.UI.Listener;
import ariadne.ui.graphic.GraphicUI;

public class Application {
	private static int innerPort;
	private static int outerPort;
	private static Server server;
	private static Client client;
	private static UI ui;
	private static TaskManager manager;
	private static Librarian librarian;
	private static final int CHUNKSIZE = 8192;

	private static void initialise() {
		ui = new GraphicUI();
		manager = new Manager();
		server = new Server(innerPort);
		client = new Client(new Address("127.0.0.1", outerPort));

		Settings.init();
		Settings.load();
		Database.initialize();
		Catalogue.initialize();
		librarian = new Librarian();
		librarian.start();
		prepareUI();
		server.start(1);

		List<Hash> files = Settings.getKeys();
		for (Hash h : files) {
			String path = Settings.getFilePath(h);
			String name = Settings.getFileName(h);
			addHash(h, name, path);
		}
	}

	private static void finalise() {
		librarian.halt();
		server.stop();
		manager.closeAllTasks();
		Settings.save();
	}

	public static void run(int in, int out) {
		innerPort = in;
		outerPort = out;

		initialise();
		ui.open();
		try {
			ui.eventLoop();
		} catch (Exception e) {
			System.out
					.println("CRITICAL ERROR IN eventLoop -------------------------------");
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		ui.close();
		finalise();
	}

	public static Client getClient() {
		return client;
	}

	public static TaskManager getManager() {
		return manager;
	}

	public static UI getUI() {
		return ui;
	}

	public static void addHash(Hash hash, String name, String path) {
		manager.insertTask(hash, path, name);
	}

	public static void addSeedHash(String path, String name, int chunkSize) {
		manager.insertSeedTask(path, name, chunkSize);
	}

	private static void prepareUI() {

		ui.onClosing(new Listener() {
			@Override
			public void trigger(Event e) {
				ui.breakEventLoop();
			}
		});

		ui.onHashAdded(new Listener() {
			@Override
			public void trigger(Event e) {
				HashAddedEvent h = (HashAddedEvent) e;
				try {
					char c = h.data.charAt(0);
					if (c == '@') {
						//add an address
						String sub = h.data.substring(1);
						System.out.println("add peer: " + sub);
					} else {
						String[] parts = h.data.split("#");
						for (int i = 0; i < parts.length; ++i) {
							System.out.println(parts[i]);
						}
						if (parts.length == 2) {
							Hash hash = new Hash(parts[0]);
							String name = parts[1];
							String path = System.getProperty("user.dir");

							addHash(hash, name, path);
						}
					}

				} catch (Exception ex) {
					System.out.println("Invalid hash");
				}
			}
		});

		ui.onFileAdded(new Listener() {
			@Override
			public void trigger(Event e) {
				FileAddedEvent f = (FileAddedEvent) e;
				addSeedHash(f.path, f.name, CHUNKSIZE);
			}

		});

		ui.onHashRemoved(new Listener() {
			@Override
			public void trigger(Event e) {
				HashRemovedEvent h = (HashRemovedEvent) e;
				// h.removeFromDisk
				manager.removeTask(h.hash);
				Settings.remove(h.hash);
			}

		});

		ui.onFileProperties(new Listener() {
			@Override
			public void trigger(Event e) {
				FilePropertiesEvent h = (FilePropertiesEvent) e;
				TaskManager.TaskState t = manager.getTaskState(h.hash);
				if (t != null) {

					ui.displayInfo(t.path, t.name, h.hash.toString(),
							t.fileSize, t.chunkSize, t.chunkCount);
				} else {
					System.out.println("asd");
				}
			}
		});
	}
}