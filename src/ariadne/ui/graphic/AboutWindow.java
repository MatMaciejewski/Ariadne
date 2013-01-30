package ariadne.ui.graphic;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AboutWindow extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public AboutWindow(String path, String name, String hash, long size, int chunkSize, int chunkCount) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 460, 240);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JLabel lblNewLabel = new JLabel("File name:");
		lblNewLabel.setBounds(32, 12, 100, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Location:");
		lblNewLabel_1.setBounds(32, 39, 100, 15);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Hash:");
		lblNewLabel_2.setBounds(32, 66, 100, 15);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Size:");
		lblNewLabel_3.setBounds(32, 93, 100, 15);
		contentPane.add(lblNewLabel_3);
		
		JLabel lblChhunkSize = new JLabel("Chunk size:");
		lblChhunkSize.setBounds(32, 120, 100, 15);
		contentPane.add(lblChhunkSize);
		
		JLabel lblChunkCount = new JLabel("Chunk count:");
		lblChunkCount.setBounds(32, 147, 100, 15);
		contentPane.add(lblChunkCount);
		
		JLabel nameLabel = new JLabel("filename");
		nameLabel.setBounds(144, 12, 296, 15);
		contentPane.add(nameLabel);
		
		JLabel pathLabel = new JLabel("location");
		pathLabel.setBounds(144, 39, 296, 15);
		contentPane.add(pathLabel);
		
		JLabel hashLabel = new JLabel("hash");
		hashLabel.setBounds(144, 66, 296, 15);
		contentPane.add(hashLabel);
		
		JLabel sizeLabel = new JLabel("Unknown");
		sizeLabel.setBounds(144, 93, 296, 15);
		contentPane.add(sizeLabel);
		
		JLabel chunkSizeLabel = new JLabel("Unknown");
		chunkSizeLabel.setBounds(144, 120, 296, 15);
		contentPane.add(chunkSizeLabel);
		
		JLabel chunkCountLabel = new JLabel("Unknown");
		chunkCountLabel.setBounds(144, 147, 296, 15);
		contentPane.add(chunkCountLabel);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AboutWindow.this.dispose();
			}
		});
		btnOk.setBounds(323, 174, 117, 25);
		contentPane.add(btnOk);
		
		nameLabel.setText(name);
		pathLabel.setText(path);
		hashLabel.setText(hash);
		if(size > 0)  		sizeLabel.setText(FileEntry.printProperly(size) + " (" + size + " bytes)");
		if(chunkSize > 0)	chunkSizeLabel.setText(FileEntry.printProperly(chunkSize) + " (" + chunkSize + " bytes)");
		if(chunkCount > 0)	chunkCountLabel.setText(""+chunkCount);
	}
}
