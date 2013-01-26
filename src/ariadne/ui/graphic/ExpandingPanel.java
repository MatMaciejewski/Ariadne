package ariadne.ui.graphic;

import java.awt.Dimension;

import javax.swing.JPanel;

public class ExpandingPanel extends JPanel {
	public Dimension getPreferredSize(){
		Dimension p = super.getSize();
		Dimension q = getParent().getSize();
		p.width = q.width;
		return p;
	}
	
	public Dimension getMaximumSize(){
		return getPreferredSize();
	}
	
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
}
