package ariadne.ui.graphic;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import ariadne.data.Hash;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class FileEntry extends JPanel {
	private Hash hash;
	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;
	private JLabel descLabel;
	private JLabel mainLabel;
	private JLabel ratesLabel;
	private Color defaultColor;
	private static FileEntry selected;
	private Window w;
	private String name;

	public FileEntry(Hash hash, Window w) {
		this.w = w;
		this.hash = hash;
		this.name = "unknown";
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
		final FileEntry current = this;

		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				select();
				setBackground(defaultColor.darker().darker());
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (selected != current)
					setBackground(defaultColor.darker());
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (selected != current)
					setBackground(defaultColor);
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (arg0.getButton() == 3) {
					System.out.println("rightclick");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {

			}
		});
	}

	public Dimension getPreferredSize() {
		Dimension d = getParent().getSize();
		d.height = 64;
		return d;
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public void setFileName(String s) {
		name = s;
		mainLabel.setText(s);
	}
	
	public String printProperly(long bytes){
		float val = bytes;
		
		String unit = "B";
		if(val >= 1024){
			val /= 1024;
			unit = "KiB";
		}
		
		if(val >= 1024){
			unit = "MiB";
			val /= 1024;
		}
		
		if(val >= 1024){
			unit = "GiB";
			val /= 1024;
		}
		
		return val + " " + unit;
	}

	public void setProgress(long size, long posessed) {
		descLabel.setText(printProperly(posessed) +  " of " + printProperly(size) + " downloaded");
		
		float percent = (size == 0) ? 0 : 100*posessed/size;
		progressBar.setValue((int) (percent * 100));
		progressBar.setString(percent * 100 + "%");
	}

	public void setRates(float downRate, float upRate) {
		ratesLabel.setText("D:" + downRate + "KiB U:" + upRate + "KiB");
	}
	
	public void select(){
		if(getSelected() != null) getSelected().deselect();
		setBackground(defaultColor.darker().darker());
		selected = this;
		w.setEntryHash(hash.toString() + "#" + name);
	}
	
	public void deselect(){
		setBackground(selected.defaultColor);
		selected = null;
		w.setEntryHash("");
	}
	
	public static FileEntry getSelected(){
		return selected;
	}
	
	public Hash getHash(){
		return hash;
	}
}
