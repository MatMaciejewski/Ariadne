package ariadne.ui.graphic;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import ariadne.data.Catalogue;
import ariadne.data.Hash;
import ariadne.net.Address;
import ariadne.net.Port;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AssignPeerWindow extends JFrame {

	private JPanel contentPane;
	private JTextField portField;
	private JTextField ipField;
	private Hash hash;

	/**
	 * Create the frame.
	 */
	public AssignPeerWindow(final Hash hash) {
		this.hash = hash;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 206);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		final JLabel statusLabel = new JLabel("Assign peer to hash");
		contentPane.add(statusLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		portField = new JTextField();
		portField.setText("25566");
		portField.setBounds(128, 80, 216, 19);
		panel.add(portField);
		portField.setColumns(10);
		
		ipField = new JTextField();
		ipField.setColumns(10);
		ipField.setBounds(128, 36, 216, 19);
		panel.add(ipField);
		
		JLabel lblNewLabel = new JLabel("IP");
		lblNewLabel.setBounds(12, 38, 70, 15);
		panel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Port");
		lblNewLabel_1.setBounds(12, 82, 70, 15);
		panel.add(lblNewLabel_1);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JButton assignButton = new JButton("Assign");
		assignButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					InetAddress addr = InetAddress.getByName(ipField.getText());
					int port = Integer.parseInt(portField.getText());
					
					Address a = new Address((Inet4Address)addr, new Port(port));
					
					Catalogue.addPeer(hash, a, 300000);
					AssignPeerWindow.this.dispose();
				} catch (Exception e1) {
					statusLabel.setText("Invalid IP or port");
				}
				
			}
		});
		panel_1.add(assignButton, BorderLayout.EAST);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AssignPeerWindow.this.dispose();
			}
		});
		panel_1.add(cancelButton, BorderLayout.WEST);
	}
}
