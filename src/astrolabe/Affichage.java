package astrolabe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JSlider;

public class Affichage extends JPanel {
	static final long serialVersionUID=1;
	GridBagLayout gb;
    GridBagConstraints gbc;
	Astrolabe astro;
	transient Graphics2D g2d;	
	JCheckBox tympan, almuvert5, almuvert1 ;
	JSlider magSlider;
	JLabel magLabel=new JLabel("Magnitude");
	
	public Affichage(Astrolabe a) {
		astro=a;
		this.gb=new GridBagLayout();
        this.gbc = new GridBagConstraints();
        this.gbc.fill=GridBagConstraints.BOTH;
        this.setLayout(this.gb);
        
		tympan=new JCheckBox("Tympan", true);
		tympan.setBackground(Color.BLACK);
		tympan.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=0;
		this.gb.setConstraints(this.tympan, this.gbc);
		this.add(tympan);
		
		almuvert5=new JCheckBox("Graduations 5° du tympan", false);
		this.almuvert5.setBackground(Color.BLACK);
		this.almuvert5.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=1;
		this.gb.setConstraints(this.almuvert5, this.gbc);
		this.add(almuvert5);
		
		almuvert1=new JCheckBox("Graduations 1° du tympan", false);
		this.almuvert1.setBackground(Color.BLACK);
		this.almuvert1.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=2;
		this.gb.setConstraints(this.almuvert1, this.gbc);
		this.add(almuvert1);
		
		
		this.gbc.gridx=0;
		this.gbc.gridy=3;
		this.gb.setConstraints(this.magLabel, this.gbc);
		this.add(this.magLabel);

		magSlider=new JSlider(JSlider.HORIZONTAL, -1, 5, 5);
		this.gbc.gridx=1;
		this.gbc.gridy=2;
		this.gb.setConstraints(this.magSlider, this.gbc);
		this.add(magSlider);
	}

	public void paintComponent(Graphics g){
		g2d = (Graphics2D) g;
		
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
		g2d.setColor(Color.RED);
	}

}
