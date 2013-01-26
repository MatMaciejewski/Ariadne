package ariadne.ui.graphic;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.SystemColor;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.LineBorder;

public class FileEntry extends JPanel {
	private JProgressBar progressBar;
	private JLabel descLabel;
	private JLabel mainLabel;
	private JLabel ratesLabel;
	private Color defaultColor;
	
	public FileEntry(){
		setBorder(new EmptyBorder(4, 4, 4, 4));
		setLayout(new BorderLayout(0, 0));
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		add(progressBar, BorderLayout.SOUTH);
		
		mainLabel = new JLabel("fileName");
		mainLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		add(mainLabel, BorderLayout.NORTH);
		
		descLabel = new JLabel("New label");
		descLabel.setFont(new Font("Dialog", Font.ITALIC, 11));
		add(descLabel, BorderLayout.WEST);
		descLabel.setText("retrieving file size information...");
		
		ratesLabel = new JLabel("New label");
		ratesLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
		add(ratesLabel, BorderLayout.EAST);
		
		defaultColor = getBackground();
		
		addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {	
				setBackground(defaultColor.darker());
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				setBackground(defaultColor);
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(arg0.getButton() == 3){
					System.out.println("rightclick");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				
			}});
	}
	
	public Dimension getPreferredSize(){
		Dimension d = getParent().getSize();
		d.height = 64;
		return d;
	}
	
	public Dimension getMaximumSize(){
		return getPreferredSize();
	}
	
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	
	public void setFileName(String s){
		mainLabel.setText(s);
	}
	
	public void setProgress(float size, float percent){
		descLabel.setText( percent*size + "KiB of " + size + "KiB downloaded");
		progressBar.setValue((int) (percent*100));
		progressBar.setString(percent*100 + "%");
	}
	
	public void setRates(float downRate, float upRate){
		ratesLabel.setText("D:" + downRate + "KiB U:" + upRate + "KiB");
	}
}
