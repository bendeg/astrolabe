package astrolabe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import java.util.HashMap;
import javax.swing.JComboBox;

public class ModeAnimation extends JPanel {
	static final long serialVersionUID=1;
	Astrolabe astro;
	GridBagLayout gb;
  GridBagConstraints gbc;
	transient Graphics2D g2d;	
	JToggleButton automode;
	JToggleButton reinit;
	JTextField anT, moisT, jourT, heureT, minT, secT,
	           dJourT, dHeureT, dMinT, dSecT;
	JLabel dJourL=new JLabel("+ jours"),
			dHeureL=new JLabel("+ heures"),
			dMinL=new JLabel("+ minutes"),
			dSecL=new JLabel("+ secondes"),
			anL=new JLabel("Année"),
			moisL=new JLabel("Mois"),
			jourL=new JLabel("Jour"),
			heureL=new JLabel("Heure"),
			minL=new JLabel("Minute"),
			secL=new JLabel("Seconde");
	
	HashMap<String, String> periodesLune = new HashMap<String, String>();
  HashMap<String, String> periodesTerre = new HashMap<String, String>();
  HashMap<String, String> periodesAll = new HashMap<String, String>();
	JComboBox<String> periodes = new JComboBox<String>();
	
	public ModeAnimation(Astrolabe a) {
	  this.periodesTerre.put("Jour Sidéral", "86164");
	  
	  this.periodesLune.put("Lune : Mois draconitique", "2351136");
	  this.periodesLune.put("Lune : Mois tropique", "2360584");//normalement en moyenne : 2360584,7
	  this.periodesLune.put("Lune : Mois sidéral", "2360591");//2360591,6
	  this.periodesLune.put("Lune : Mois anomalistique", "2380713");
	  this.periodesLune.put("Lune : Mois synodique", "2551442");//2551442,9
	  
	  this.periodesAll.putAll(this.periodesTerre);
	  this.periodesAll.putAll(this.periodesLune);
	  
	  for(int i=0; i<this.periodesAll.size(); i++) {
	    this.periodes.addItem((String)this.periodesAll.keySet().toArray()[i]);
	    //System.out.println("ModeAnimation - keySet : " + this.periodesAll.keySet().toArray()[i]);
	  }
	   this.periodes.addActionListener(new ActionListener() {     
       @Override
       public void actionPerformed(ActionEvent e) {
          //System.out.println(e.toString());
          System.out.println("Value: " + periodesAll.get(periodes.getSelectedItem()));
          astro.anim.dJourT.setText("0");
          astro.anim.dHeureT.setText("0");
          astro.anim.dMinT.setText("0");
          astro.anim.dSecT.setText(periodesAll.get(periodes.getSelectedItem()));
       }
     });

		this.gb = new GridBagLayout();
        this.gbc = new GridBagConstraints();
        this.gbc.fill=GridBagConstraints.BOTH;
        
        this.setLayout(this.gb);
		this.astro=a;
		
		this.reinit=new JToggleButton("Libre !", true);
		this.reinit.setBackground(Color.BLACK);
		this.reinit.setForeground(Color.RED);
		this.reinit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				Astrolabe a;
				JToggleButton source=(JToggleButton) e.getSource();
				
				a=(Astrolabe)source.getRootPane().getParent();
				//a.calc.ldt=ZonedDateTime.now();
				if(source.isSelected()) {
	        source.setBackground(Color.BLACK);
          source.setText("Libre !");
          a.calc.now();
				}
				else {
          source.setBackground(Color.GRAY);
          source.setText("Maintenant !");
//          a.anim.dJourT.setVisible(true);
//          a.anim.dHeureT.setVisible(true);
//          a.anim.dMinT.setVisible(true);
//          a.anim.dSecT.setVisible(true);
//
//          a.anim.anT.setVisible(true);
//          a.anim.moisT.setVisible(true);
//          a.anim.jourT.setVisible(true);
//          a.anim.heureT.setVisible(true);
//          a.anim.minT.setVisible(true);
//          a.anim.secT.setVisible(true);
				}
//				a.anim.dJourT.setText(String.valueOf(0));
//				a.anim.dHeureT.setText(String.valueOf(0));
//				a.anim.dMinT.setText(String.valueOf(0));
//				a.anim.dSecT.setText(String.valueOf(0));
				
//				a.anim.anT.setText(String.valueOf(a.calc.ldt.getYear()));
//				a.anim.moisT.setText(String.valueOf(a.calc.ldt.getMonthValue()));
//				a.anim.jourT.setText(String.valueOf(a.calc.ldt.getDayOfMonth()));
//				a.anim.heureT.setText(String.valueOf(a.calc.ldt.getHour()));
//				a.anim.minT.setText(String.valueOf(a.calc.ldt.getMinute()));
//				a.anim.secT.setText(String.valueOf(a.calc.ldt.getSecond()));
			}
		});
		this.gbc.gridx=0;
		this.gbc.gridy=0;
		this.gb.setConstraints(this.reinit, this.gbc);
		this.add(reinit);
		
		automode=new JToggleButton("Stop !", true);
		automode.setBackground(Color.BLACK);
		automode.setForeground(Color.RED);
		automode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					JToggleButton source=(JToggleButton) e.getSource();
					Astrolabe a=(Astrolabe) source.getRootPane().getParent();
					a.anim.reinit.setBackground(Color.GRAY);
					if(source.isSelected()) {
						source.setText("Stop !");
//				    astro.timer=new Timer();
//				    astro.timer.schedule(new Tictac(astro), 0, 1000);

//            a.anim.dJourT.setVisible(false);
//            a.anim.dHeureT.setVisible(false);
//            a.anim.dMinT.setVisible(false);
//            a.anim.dSecT.setVisible(false);
//
//						a.anim.anT.setVisible(false);
//						a.anim.moisT.setVisible(false);
//						a.anim.jourT.setVisible(false);
//		        a.anim.heureT.setVisible(false);
//		        a.anim.minT.setVisible(false);
//		        a.anim.secT.setVisible(false);

//		        a.anim.anT.setVisible(true);
//            a.anim.moisT.setVisible(true);
//            a.anim.jourT.setVisible(true);
//            a.anim.heureT.setVisible(true);
//            a.anim.minT.setVisible(true);
//            a.anim.secT.setVisible(true);
					}
					else {
						source.setText("Animer !");
//						astro.timer.cancel();
//            a.anim.dJourT.setVisible(true);
//            a.anim.dHeureT.setVisible(true);
//            a.anim.dMinT.setVisible(true);
//            a.anim.dSecT.setVisible(true);
					}
				}
		});
		this.gbc.gridx=1;
		this.gbc.gridy=0;
		this.gb.setConstraints(this.automode, this.gbc);
		this.add(automode);
		
		//delta jour
    this.dJourL.setBackground(Color.BLACK);
    this.dJourL.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=1;
		this.gb.setConstraints(this.dJourL, this.gbc);
		this.add(dJourL);

		this.dJourT=new JTextField("0");
    this.dJourT.setToolTipText("Nombre entier unqiuement !");
    this.dJourT.setVisible(true);
		this.dJourT.setBackground(Color.BLACK);
		this.dJourT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=1;
		this.gb.setConstraints(this.dJourT, this.gbc);
		this.add(dJourT);

		//delta heure
    this.dHeureL.setBackground(Color.BLACK);
    this.dHeureL.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=2;
		this.gb.setConstraints(this.dHeureL, this.gbc);
		this.add(dHeureL);

		this.dHeureT=new JTextField("23");
    this.dHeureT.setToolTipText("Nombre entier unqiuement !");
    this.dHeureT.setVisible(true);
		this.dHeureT.setBackground(Color.BLACK);
		this.dHeureT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=2;
		this.gb.setConstraints(this.dHeureT, this.gbc);
		this.add(dHeureT);

		//delta Minutes
    this.dMinL.setBackground(Color.BLACK);
    this.dMinL.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=3;
		this.gb.setConstraints(this.dMinL, this.gbc);
		this.add(dMinL);

		this.dMinT=new JTextField("56");
    this.dMinT.setToolTipText("Nombre entier unqiuement !");
    this.dMinT.setVisible(true);
		this.dMinT.setBackground(Color.BLACK);
		this.dMinT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=3;
		this.gb.setConstraints(this.dMinT, this.gbc);
		this.add(dMinT);

		//delta seconde
    this.dSecL.setBackground(Color.BLACK);
    this.dSecL.setForeground(Color.RED);
  	this.gbc.gridx=0;
		this.gbc.gridy=4;
		this.gb.setConstraints(this.dSecL, this.gbc);
		this.add(dSecL);

		this.dSecT=new JTextField("4");
		this.dSecT.setToolTipText("Nombre entier unqiuement !");
    this.dSecT.setVisible(true);
		this.dSecT.setBackground(Color.BLACK);
		this.dSecT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=4;
		this.gb.setConstraints(this.dSecT, this.gbc);
		this.add(dSecT);
		
    //Liste des périodes
    this.periodes.setBackground(Color.BLACK);
    this.periodes.setForeground(Color.RED);
    this.gbc.gridx=2;
    this.gbc.gridy=4;
    this.gb.setConstraints(this.periodes, this.gbc);
    this.add(periodes);

		//année
    this.anL.setBackground(Color.BLACK);
    this.anL.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=5;
		this.gb.setConstraints(this.anL, this.gbc);
		this.add(anL);

		this.anT=new JTextField();
    this.anT.setToolTipText("Nombre entier unqiuement !");
		this.anT.setBackground(Color.BLACK);
		this.anT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=5;
		this.gb.setConstraints(this.anT, this.gbc);
		this.anT.setVisible(true);
		this.add(anT);
		
		//mois
    this.moisL.setBackground(Color.BLACK);
    this.moisL.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=6;
		this.gb.setConstraints(this.moisL, this.gbc);
		this.add(moisL);

		this.moisT=new JTextField();
    this.moisT.setToolTipText("Nombre entier unqiuement !");
		this.moisT.setBackground(Color.BLACK);
		this.moisT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=6;
		this.gb.setConstraints(this.moisT, this.gbc);
		this.moisT.setVisible(true);
		this.add(moisT);
		
		//jour
    this.jourL.setBackground(Color.BLACK);
    this.jourL.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=7;
		this.gb.setConstraints(this.jourL, this.gbc);
		this.add(jourL);

		this.jourT=new JTextField();
    this.jourT.setToolTipText("Nombre entier unqiuement !");
		this.jourT.setBackground(Color.BLACK);
		this.jourT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=7;
		this.gb.setConstraints(this.jourT, this.gbc);
		this.jourT.setVisible(true);
		this.add(jourT);
		
		//heure
    this.heureL.setBackground(Color.BLACK);
    this.heureL.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=8;
		this.gb.setConstraints(this.heureL, this.gbc);
		this.add(heureL);

		this.heureT=new JTextField();
    this.heureT.setToolTipText("Nombre entier unqiuement !");
		this.heureT.setBackground(Color.BLACK);
		this.heureT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=8;
		this.gb.setConstraints(this.heureT, this.gbc);
		this.heureT.setVisible(true);
		this.add(heureT);

		//minute
    this.minL.setBackground(Color.BLACK);
    this.minL.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=9;
		this.gb.setConstraints(this.minL, this.gbc);
		this.add(minL);

		this.minT=new JTextField();
    this.minT.setToolTipText("Nombre entier unqiuement !");
		this.minT.setBackground(Color.BLACK);
		this.minT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=9;
		this.gb.setConstraints(this.minT, this.gbc);
		this.minT.setVisible(true);
		this.add(minT);

		//seconde
    this.secL.setBackground(Color.BLACK);
    this.secL.setForeground(Color.RED);
		this.gbc.gridx=0;
		this.gbc.gridy=10;
		this.gb.setConstraints(this.secL, this.gbc);
		this.add(secL);

		this.secT=new JTextField();
    this.secT.setToolTipText("Nombre entier unqiuement !");
    this.secT.setBackground(Color.BLACK);
		this.secT.setForeground(Color.RED);
		this.gbc.gridx=1;
		this.gbc.gridy=10;
		this.gb.setConstraints(this.secT, this.gbc);
		this.secT.setVisible(true);
		this.add(secT);

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
