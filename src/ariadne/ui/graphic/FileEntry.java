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

	public FileEntry(Hash hash) {
		this.hash = hash;
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
		mainLabel.setText(s);
	}

	public void setProgress(float size, float percent) {
		descLabel.setText(percent * size + "KiB of " + size + "KiB downloaded");
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
	}
	
	public void deselect(){
		setBackground(selected.defaultColor);
		selected = null;
	}
	
	public static FileEntry getSelected(){
		return selected;
	}
	
	public Hash getHash(){
		return hash;
	}
}
