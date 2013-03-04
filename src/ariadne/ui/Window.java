package ariadne.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.JToolBar;
import javax.swing.JButton;
import java.awt.Dimension;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import java.awt.Component;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JSeparator;
import java.awt.SystemColor;
import javax.swing.BoxLayout;
import javax.swing.ScrollPaneConstants;


public class Window extends JFrame {
	static {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "AppName");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
	}
	
	private JPanel scrollPanel;

	private void initializeMenu(){
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		JMenuItem mntmNewMenuItem = new JMenuItem("Add file");
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmSelectAll = new JMenuItem("Select all");
		mnEdit.add(mntmSelectAll);
		
		JMenuItem mntmSelectNone = new JMenuItem("Select none");
		mnEdit.add(mntmSelectNone);
		
		JSeparator separator = new JSeparator();
		mnEdit.add(separator);
		
		JMenuItem mntmDelete = new JMenuItem("Delete");
		mnEdit.add(mntmDelete);
		
		JMenuItem mntmProperties = new JMenuItem("Properties");
		mnEdit.add(mntmProperties);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		JMenuItem mntmHideToolbar = new JMenuItem("Hide toolbar");
		mnView.add(mntmHideToolbar);
		
		JMenuItem mntmHideStatusbar = new JMenuItem("Hide statusbar");
		mnView.add(mntmHideStatusbar);
		
		JSeparator separator_2 = new JSeparator();
		mnView.add(separator_2);
		
		JMenuItem mntmSortByName = new JMenuItem("Sort by name");
		mnView.add(mntmSortByName);
		
		JMenuItem mntmSortByProgress = new JMenuItem("Sort by progress");
		mnView.add(mntmSortByProgress);
		
		JMenu mnWindow = new JMenu("Transfers");
		menuBar.add(mnWindow);
		
		JMenuItem mntmPauseSelected = new JMenuItem("Pause selected");
		mnWindow.add(mntmPauseSelected);
		
		JMenuItem mntmResuleSelected = new JMenuItem("Resume selected");
		mnWindow.add(mntmResuleSelected);
		
		JSeparator separator_1 = new JSeparator();
		mnWindow.add(separator_1);
		
		JMenuItem mntmPauseAll = new JMenuItem("Pause all");
		mnWindow.add(mntmPauseAll);
		
		JMenuItem mntmResumeAll = new JMenuItem("Resume all");
		mnWindow.add(mntmResumeAll);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAboutAriadne = new JMenuItem("About Ariadne");
		mnHelp.add(mntmAboutAriadne);
		
		JMenuItem mntmCheckForUpdates = new JMenuItem("Check for updates");
		mnHelp.add(mntmCheckForUpdates);
	}
	private void initializeStructure(){
		JPanel contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel upBar = new JPanel();
		contentPane.add(upBar, BorderLayout.NORTH);
		upBar.setLayout(new BorderLayout(0, 0));
		
		JToolBar toolBar = new JToolBar();
		toolBar.setMargin(new Insets(4, 4, 4, 4));
		upBar.add(toolBar, BorderLayout.NORTH);
		toolBar.setFloatable(false);
		
		JButton toolAddButton = new JButton("Add");
		toolAddButton.setFocusable(false);
		toolAddButton.setMargin(new Insets(2, 2, 2, 2));
		toolBar.add(toolAddButton);
		
		JButton toolRemoveButton = new JButton("Remove");
		toolRemoveButton.setFocusable(false);
		toolRemoveButton.setMargin(new Insets(2, 2, 2, 2));
		toolBar.add(toolRemoveButton);
		
		JPanel hashBar = new JPanel();
		hashBar.setMinimumSize(new Dimension(30, 30));
		hashBar.setPreferredSize(new Dimension(30, 30));
		hashBar.setBorder(new EmptyBorder(3,3,3,3));
		upBar.add(hashBar, BorderLayout.SOUTH);
		hashBar.setLayout(new BorderLayout(0, 0));
		
		JButton btnNewButton_2 = new JButton("Add");
		hashBar.add(btnNewButton_2, BorderLayout.EAST);
		
		JTextField textField = new JTextField();
		hashBar.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JPanel statusBar = new JPanel();
		statusBar.setBorder(new EmptyBorder(3,3,3,3));
		contentPane.add(statusBar, BorderLayout.SOUTH);
		statusBar.setLayout(new BorderLayout(0, 0));
		
		JLabel statusLabel = new JLabel("Program status");
		statusLabel.setForeground(Color.GRAY);
		statusLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		statusBar.add(statusLabel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(null);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		scrollPanel = new JPanel();
		scrollPanel.setBackground(SystemColor.textHighlight);
		scrollPanel.setPreferredSize(new Dimension(10, 10));
		scrollPanel.setMinimumSize(new Dimension(10, 10));
		scrollPane.setViewportView(scrollPanel);
		
	}
	
	public void addEntry(Component c){
		scrollPanel.add(c);
		int h = scrollPanel.getHeight();
		scrollPanel.setPreferredSize(new Dimension(scrollPanel.getWidth(), h+60));
	}
	
	public Window() {
		setTitle("Ariadne");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 721, 493);
		initializeMenu();
		initializeStructure();
		
		
		
		scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
		scrollPanel.setPreferredSize(new Dimension(0,0));
		scrollPanel.add(new Entry());
		scrollPanel.add(new Entry());
		scrollPanel.add(new Entry());
		scrollPanel.add(new Entry());
		
		System.out.println(scrollPanel.getSize());
	}
}
