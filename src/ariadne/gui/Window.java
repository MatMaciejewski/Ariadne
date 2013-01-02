package ariadne.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.SystemColor;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Window{

	private JFrame frame;
	private JTextField textField;
	private GUI.QueueDelegate delegate;

	public Window(GUI.QueueDelegate delegate) {
		this.delegate = delegate;
		initialize();
	}
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 640, 480);
		
		JPanel header = new JPanel();
		header.setPreferredSize(new Dimension(10, 48));
		header.setMinimumSize(new Dimension(10, 48));
		frame.getContentPane().add(header, BorderLayout.NORTH);
		header.setLayout(new BorderLayout(0, 0));
		
		JButton btnNewButton = new JButton("Download");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delegate.hashAdded(textField.getText());
			}
		});
		btnNewButton.setMinimumSize(new Dimension(120, 48));
		btnNewButton.setMaximumSize(new Dimension(120, 48));
		btnNewButton.setPreferredSize(new Dimension(120, 48));
		header.add(btnNewButton, BorderLayout.EAST);
		
		textField = new JTextField();
		header.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JPanel content = new JPanel();
		frame.getContentPane().add(content, BorderLayout.CENTER);
		
		JPanel status = new JPanel();
		status.setBackground(SystemColor.desktop);
		status.setPreferredSize(new Dimension(10, 24));
		status.setMaximumSize(new Dimension(32767, 24));
		status.setMinimumSize(new Dimension(10, 24));
		frame.getContentPane().add(status, BorderLayout.SOUTH);
		status.setLayout(new BorderLayout(0, 0));
		
		JLabel lblSomeStatusData = new JLabel("Some status data...");
		status.add(lblSomeStatusData, BorderLayout.CENTER);
		
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
		
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
               delegate.quit();
            }
        });
	}
	
	public void show(){
		frame.setVisible(true);
	}

	public void close(){
		frame.dispose();
	}
}
