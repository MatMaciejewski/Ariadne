package ariadne.ui.graphic;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JLabel;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.SwingConstants;

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
	private JFileChooser fc;
	private JTextField generatedHashField;

	public Window(GraphicUI.Delegate d) {
		delegate = d;
		entries = new HashMap<Hash, FileEntry>();
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Ariadne");
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
		statusPanel.setBackground(statusPanel.getBackground().darker());
		statusPanel.setPreferredSize(new Dimension(10, 24));
		statusPanel.setMaximumSize(new Dimension(32767, 24));
		statusPanel.setMinimumSize(new Dimension(10, 24));
		frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setLayout(new BorderLayout(0, 0));

		statusLabel = new JLabel("Ariadne v1.0 alpha");
		statusLabel.setPreferredSize(new Dimension(200, 15));
		statusPanel.add(statusLabel, BorderLayout.WEST);
		
		generatedHashField = new JTextField();
		generatedHashField.setHorizontalAlignment(SwingConstants.RIGHT);
		generatedHashField.setEditable(false);
		generatedHashField.setMinimumSize(new Dimension(300, 19));
		generatedHashField.setMaximumSize(generatedHashField.getMinimumSize());
		generatedHashField.setPreferredSize(generatedHashField.getMinimumSize());
		statusPanel.add(generatedHashField, BorderLayout.CENTER);
		generatedHashField.setColumns(10);

		contentScrollPane = new JScrollPane();
		contentScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(contentScrollPane, BorderLayout.CENTER);

		contentPanel = new ExpandingPanel();
		contentScrollPane.setViewportView(contentPanel);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		fc = new JFileChooser();
		
		initMenu();
		initEvents();

	}
	
	public void setEntryHash(String h){
		generatedHashField.setText(h);
	}

	public void showEntry(Hash hash, String name, long size, long posessed, long downRate, long upRate, float ratio){
		FileEntry e = entries.get(hash);
		if (e == null) {
			e = new FileEntry(hash, this);
			contentPanel.add(e);
			frame.repaint();
			entries.put(hash, e);
		}
		e.setFileName(name);
		e.setProgress(size, posessed);
		e.setRates(downRate, upRate);
	}

	public void dropEntry(Hash hash) {
		FileEntry e = entries.remove(hash);
		if (e != null) {
			contentPanel.remove(e);
			generatedHashField.setText("");
			frame.repaint();
		}
	}
	
	public void hashRemoved(Hash hash, boolean alsoDelete){
		delegate.hashRemoved(hash, alsoDelete);
	}
	
	public void aboutToDisplay(Hash h){
		delegate.fileProperties(h);
	}

	private void initMenu() {

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		final JMenuItem hashGenMenuItem = new JMenuItem("Generate hash...");
		hashGenMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 //Handle open button action.
			        int returnVal = fc.showOpenDialog(hashGenMenuItem);

			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            delegate.fileAdded(file.getName(), file.getParent());
			        }
			}
		});
		fileMenu.add(hashGenMenuItem);
		JMenuItem quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delegate.closing();
			}
		});
		fileMenu.add(quitMenuItem);
		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
		JMenuItem pauseMenuItem = new JMenuItem("Pause");
		editMenu.add(pauseMenuItem);
		JMenuItem resumeMenuItem = new JMenuItem("Resume");
		editMenu.add(resumeMenuItem);
		JMenuItem removeMenuItem = new JMenuItem("Remove...");
		removeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 if(FileEntry.getSelected() != null){
					 hashRemoved(FileEntry.getSelected().getHash(), false);
				 }
			}
		});
		editMenu.add(removeMenuItem);
		JMenuItem remDelMenuItem = new JMenuItem("Delete...");
		remDelMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 if(FileEntry.getSelected() != null){
					 hashRemoved(FileEntry.getSelected().getHash(), true);
				 }
			}
		});
		editMenu.add(remDelMenuItem);
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		JMenuItem updatesMenuItem = new JMenuItem("Check for updates");
		helpMenu.add(updatesMenuItem);

		JMenuItem aboutMenuItem = new JMenuItem("About HashChaser");
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("info");
			}
		});
		helpMenu.add(aboutMenuItem);
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
				hashTextField.setText("");
			}
		});
		
		contentPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FileEntry s = FileEntry.getSelected();
				if(s != null) s.deselect();
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
