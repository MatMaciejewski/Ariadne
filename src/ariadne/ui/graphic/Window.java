package ariadne.ui.graphic;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.BoxLayout;

import ariadne.data.Hash;

import java.util.HashMap;
import java.util.Map;

class Window {

	private JFrame frame;
	private JTextField hashTextField;
	private GraphicUI.Delegate delegate;
	private JPanel headerPanel;
	private JPanel statusPanel;
	private JPanel contentPanel;
	private JButton hashAddedButton;
	private JLabel statusLabel;
	private JScrollPane contentScrollPane;
	private Map<Hash, FileEntry> entries;

	public Window(GraphicUI.Delegate d) {
		delegate = d;
		entries = new HashMap<Hash, FileEntry>();
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setBounds(100, 100, 640, 480);

		headerPanel = new JPanel();
		headerPanel.setPreferredSize(new Dimension(10, 48));
		headerPanel.setMinimumSize(new Dimension(10, 48));
		frame.getContentPane().add(headerPanel, BorderLayout.NORTH);
		headerPanel.setLayout(new BorderLayout(0, 0));

		hashAddedButton = new JButton("Download");
		hashAddedButton.setMinimumSize(new Dimension(120, 48));
		hashAddedButton.setMaximumSize(new Dimension(120, 48));
		hashAddedButton.setPreferredSize(new Dimension(120, 48));
		headerPanel.add(hashAddedButton, BorderLayout.EAST);

		hashTextField = new JTextField();
		headerPanel.add(hashTextField, BorderLayout.CENTER);
		hashTextField.setColumns(10);

		statusPanel = new JPanel();
		statusPanel.setBackground(SystemColor.desktop);
		statusPanel.setPreferredSize(new Dimension(10, 24));
		statusPanel.setMaximumSize(new Dimension(32767, 24));
		statusPanel.setMinimumSize(new Dimension(10, 24));
		frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setLayout(new BorderLayout(0, 0));

		statusLabel = new JLabel("Some status data...");
		statusPanel.add(statusLabel, BorderLayout.CENTER);

		contentScrollPane = new JScrollPane();
		contentScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(contentScrollPane, BorderLayout.CENTER);

		contentPanel = new JPanel();
		contentScrollPane.setViewportView(contentPanel);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		initMenu();
		initEvents();

	}

	public void showEntry(Hash hash, String name, float size, float percent, float downRate, float upRate, float ratio) {
		FileEntry e = entries.get(hash);
		if (e == null) {
			e = new FileEntry();
			contentPanel.add(e);
			frame.repaint();
			entries.put(hash, e);
		}
		e.setFileName(name);
		e.setProgress(size, percent);
		e.setRates(downRate, upRate);
	}

	public void dropEntry(Hash hash) {
		FileEntry e = entries.remove(hash);
		if (e != null) {
			contentPanel.remove(e);
		}
	}

	private void initMenu() {

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		JMenuItem mntmNewMenuItem = new JMenuItem("Generate hash...");
		mnNewMenu.add(mntmNewMenuItem);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Download hash...");
		mnNewMenu.add(mntmNewMenuItem_1);

		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Quit");
		mnNewMenu.add(mntmNewMenuItem_2);

		JMenu mnNewMenu_1 = new JMenu("Edit");
		menuBar.add(mnNewMenu_1);

		JMenuItem mntmCoTuMa = new JMenuItem("Co tu ma być?");
		mnNewMenu_1.add(mntmCoTuMa);

		JMenu mnNewMenu_2 = new JMenu("Help");
		menuBar.add(mnNewMenu_2);

		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Check for updates");
		mnNewMenu_2.add(mntmNewMenuItem_3);

		JMenuItem mntmNewMenuItem_4 = new JMenuItem("About HashChaser");
		mnNewMenu_2.add(mntmNewMenuItem_4);
	}

	private void initEvents() {
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				delegate.closing();
			}
		});

		hashAddedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delegate.hashAdded(hashTextField.getText());
			}
		});
	}

	public void show() {
		frame.setVisible(true);
	}

	public void hide() {
		frame.setVisible(false);
	}

	public void close() {
		frame.dispose();
	}
}
