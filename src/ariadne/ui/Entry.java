package ariadne.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.SystemColor;

public class Entry extends JPanel {

	/**
	 * Create the panel.
	 */
	public Entry() {
		setLayout(new BorderLayout(0, 0));
		setBorder(new EmptyBorder(5,5,5,5));
		
		JProgressBar progressBar = new JProgressBar();
		add(progressBar, BorderLayout.CENTER);
		
		JLabel lblNewLabel = new JLabel("New label");
		add(lblNewLabel, BorderLayout.NORTH);
	}
	public Dimension getPreferredSize(){
		return new Dimension(getParent().getWidth(), 60);
	}
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	public Dimension getMaximumSize(){
		return getPreferredSize();
	}
}
