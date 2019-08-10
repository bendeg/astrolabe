package astrolabe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class ModeAnimation extends JPanel {
	static final long serialVersionUID=1;
	Astrolabe astro;
	GridBagLayout gb;
    GridBagConstraints gbc;
	transient Graphics2D g2d;	
	JToggleButton manual;
	JButton reinit;
	JTextField anT, moisT, jourT, dJourT, dHeureT, dMinT, dSecT;
	JLabel dJourL=new JLabel("+ jours"),
			dHeureL=new JLabel("+ heures"),
			dMinL=new JLabel("+ minutes"),
			dSecL=new JLabel("+ secondes"),
			anL=new JLabel("Année"),
			moisL=new JLabel("Mois"),
			jourL=new JLabel("Jour");

	public ModeAnimation(Astrolabe a) {
		this.gb = new GridBagLayout();
        this.gbc = new GridBagConstraints();
        this.gbc.fill=GridBagConstraints.BOTH;
        
        this.setLayout(this.gb);
		this.astro=a;
		
		this.reinit=new JButton("Maintenant !");
		this.reinit.setBackground(Color.BLACK);
		this.reinit.setForeground(Color.RED);
		this.reinit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				Astrolabe a;
				JButton source=(JButton) e.getSource();
				
				source.setBackground(Color.BLACK);
				a=(Astrolabe)source.getRootPane().getParent();
				//a.calc.ldt=ZonedDateTime.now();
				a.calc.now();
				a.anim.dJourT.setText(String.valueOf(0));
				a.anim.dHeureT.setText(String.valueOf(0));
				a.anim.dMinT.setText(String.valueOf(0));
				a.anim.dSecT.setText(String.valueOf(1));
				a.anim.anT.setText(String.valueOf(a.calc.ldt.getYear()));
				a.anim.moisT.setText(String.valueOf(a.calc.ldt.getMonthValue()));
				a.anim.jourT.setText(String.valueOf(a.calc.ldt.getDayOfMonth()));
			}
		});
		this.gbc.gridx=0;
		this.gbc.gridy=0;
		this.gb.setConstraints(this.reinit, this.gbc);
		this.add(reinit);
		
		manual=new JToggleButton("Stop !", true);
		manual.setBackground(Color.BLACK);
		manual.setForeground(Color.RED);
		manual.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					JToggleButton source=(JToggleButton) e.getSource();
					Astrolabe a=(Astrolabe) source.getRootPane().getParent();
					a.anim.reinit.setBackground(Color.GRAY);
					if(source.isSelected()) {
						source.setText("Stop !");
						a.anim.anT.setVisible(false);
						a.anim.moisT.setVisible(false);
						a.anim.jourT.setVisible(false);
					}
					else {
						source.setText("Animer !");
						a.anim.anT.setVisible(true);
						a.anim.moisT.setVisible(true);
						a.anim.jourT.setVisible(true);
					}
				}
		});
		this.gbc.gridx=1;
		this.gbc.gridy=0;
		this.gb.setConstraints(this.manual, this.gbc);
		this.add(manual);
		
		//delta jour
		this.gbc.gridx=0;
		this.gbc.gridy=1;
		this.gb.setConstraints(this.dJourL, this.gbc);
		this.add(dJourL);

		this.dJourT=new JTextField("0");
		this.dJourT.setBackground(Color.BLACK);
		this.dJourT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=1;
		this.gb.setConstraints(this.dJourT, this.gbc);
		this.add(dJourT);

		//delta heure
		this.gbc.gridx=0;
		this.gbc.gridy=2;
		this.gb.setConstraints(this.dHeureL, this.gbc);
		this.add(dHeureL);

		this.dHeureT=new JTextField("0");
		this.dHeureT.setBackground(Color.BLACK);
		this.dHeureT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=2;
		this.gb.setConstraints(this.dHeureT, this.gbc);
		this.add(dHeureT);

		//delta Minutes
		this.gbc.gridx=0;
		this.gbc.gridy=3;
		this.gb.setConstraints(this.dMinL, this.gbc);
		this.add(dMinL);

		this.dMinT=new JTextField("0");
		this.dMinT.setBackground(Color.BLACK);
		this.dMinT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=3;
		this.gb.setConstraints(this.dMinT, this.gbc);
		this.add(dMinT);

		//delta seconde
		this.gbc.gridx=0;
		this.gbc.gridy=4;
		this.gb.setConstraints(this.dSecL, this.gbc);
		this.add(dSecL);

		this.dSecT=new JTextField("1");
		this.dSecT.setBackground(Color.BLACK);
		this.dSecT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=4;
		this.gb.setConstraints(this.dSecT, this.gbc);
		this.add(dSecT);
		
		//année
		this.gbc.gridx=0;
		this.gbc.gridy=5;
		this.gb.setConstraints(this.anL, this.gbc);
		this.add(anL);

		this.anT=new JTextField();
		this.anT.setBackground(Color.BLACK);
		this.anT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=5;
		this.gb.setConstraints(this.anT, this.gbc);
		this.anT.setVisible(false);
		this.add(anT);
		
		//mois
		this.gbc.gridx=0;
		this.gbc.gridy=6;
		this.gb.setConstraints(this.moisL, this.gbc);
		this.add(moisL);

		this.moisT=new JTextField();
		this.moisT.setBackground(Color.BLACK);
		this.moisT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=6;
		this.gb.setConstraints(this.moisT, this.gbc);
		this.moisT.setVisible(false);
		this.add(moisT);
		
		//jour
		this.gbc.gridx=0;
		this.gbc.gridy=7;
		this.gb.setConstraints(this.jourL, this.gbc);
		this.add(jourL);

		this.jourT=new JTextField();
		this.jourT.setBackground(Color.BLACK);
		this.jourT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=7;
		this.gb.setConstraints(this.jourT, this.gbc);
		this.jourT.setVisible(false);
		this.add(jourT);
	}

	public ModeAnimation(LayoutManager arg0) {
		super(arg0);
	}

	public ModeAnimation(boolean arg0) {
		super(arg0);
	}

	public ModeAnimation(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
	}

	public void paintComponent(Graphics g){
		g2d = (Graphics2D) g;
		
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
		g2d.setColor(Color.RED);
	}
}
