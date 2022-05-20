package astrolabe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JComboBox;

public class Affichage extends JPanel {
	static final long serialVersionUID=1;
	GridBagLayout gb;
    GridBagConstraints gbc;
	Astrolabe astro;
	transient Graphics2D g2d;	
	JCheckBox tsl, tympan, almuvert5, almuvert1, astre, noeudASC, noeudDESC, satGPS,
	          checkMercure, checkVenus, checkMars, checkJupiter, checkSaturne, checkUranus, checkNeptune,
	          checkFreeLocation, asterismes, checkSoleil, checkPAS, checkLune, checkLuneTopoCentric,
	          checkModeEclipse;
	JSlider magSlider;
	JLabel magLabel=new JLabel("Magnitude"),
	       astreAD=new JLabel("    Ascension droite (HMS)"),
	       astreDe=new JLabel("    Déclinaison (DMS)"),
	       astreAH = new JLabel("    Angle horaire (HMS)"),
	       astreAHValue = new JLabel(),
	       astreVP = new JLabel("    Viseur polaire"),
	       astreVPValue = new JLabel();
	JTextField astreADHeure, astreADMinute, astreADSeconde, astreDECDegre, astreDECMinute, astreDECSeconde;
	JComboBox<Astre> astres;
	//ComboBoxRenderer renderer = new ComboBoxRenderer();
	
	public Affichage(Astrolabe a) {
		astro=a;
//		this.astres = new JComboBox<Astre>(this.astro.face.astres);
    this.astres = new JComboBox<Astre>(this.astro.stars);
    this.astres.setToolTipText("<html>Tapez un nom arabe d'étoile comme 'Vega' ou 'psi-and' pour Ψ Andromède<br>Re-sélectionner si un autre moment est choisi par après</html>");
		//this.astro.calc.calculateAll();
	  this.astres.addActionListener(new ActionListener() {     
	     @Override
	     public void actionPerformed(ActionEvent e) {
	        //System.out.println(e.toString());
	        //System.out.println("Value: " + astres.getSelectedIndex());
          astreADHeure.setText(astro.stars[astres.getSelectedIndex()].getAdHeure());
          astreADMinute.setText(astro.stars[astres.getSelectedIndex()].getAdMinute());
          astreADSeconde.setText(String.format(" %2.10s ", astro.stars[astres.getSelectedIndex()].getAdSeconde()));
          astreDECDegre.setText(astro.stars[astres.getSelectedIndex()].getDecDegre());
          astreDECMinute.setText(astro.stars[astres.getSelectedIndex()].getDecMinute());
          astreDECSeconde.setText(String.format(" %2.10s ", astro.stars[astres.getSelectedIndex()].getDecSeconde()));
          checkFreeLocation.setSelected(false);
	     }
	   });
		
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
		this.magLabel.setForeground(Color.RED);
		this.add(this.magLabel);

		magSlider=new JSlider(JSlider.HORIZONTAL, -1, 6, 6);
		this.gbc.gridx=1;
		this.gbc.gridy=3;
		this.gb.setConstraints(this.magSlider, this.gbc);
		this.add(magSlider);
		
    tsl=new JCheckBox("Temps sidéral local", true);
    tsl.setBackground(Color.BLACK);
    tsl.setForeground(Color.RED);
    this.gbc.gridx=0;
    this.gbc.gridy=4;
    this.gb.setConstraints(this.tsl, this.gbc);
    this.add(tsl);
    
    //localisation astre
    astre=new JCheckBox("Localisation (Polaire par défaut)", true);
    astre.setBackground(Color.BLACK);
    astre.setForeground(Color.RED);
    this.gbc.gridx=0;
    this.gbc.gridy=5;
    this.gb.setConstraints(this.astre, this.gbc);
    this.add(astre);

    //Liste des astres
    this.astres.setBackground(Color.BLACK);
    this.astres.setForeground(Color.RED);
    this.gbc.gridx=1;
    this.gbc.gridy=5;
    this.gb.setConstraints(this.astres, this.gbc);
    this.add(astres);

    this.checkFreeLocation = new JCheckBox("libre", false);
    this.checkFreeLocation.setBackground(Color.BLACK);
    this.checkFreeLocation.setForeground(Color.RED);
    this.gbc.gridx=2;
    this.gbc.gridy=5;
    this.gb.setConstraints(this.checkFreeLocation, this.gbc);
    this.add(this.checkFreeLocation);
    
    //JLabel astreAD
    this.gbc.gridx=0;
    this.gbc.gridy=6;
    this.gb.setConstraints(this.astreAD, this.gbc);
    this.astreAD.setForeground(Color.RED);
    this.add(this.astreAD);

    //astre AD : Heure
    this.astreADHeure=new JTextField("00");
    this.astreADHeure.setBackground(Color.BLACK);
    this.astreADHeure.setForeground(Color.RED);
    this.astreADHeure.setHorizontalAlignment(JTextField.CENTER);
    this.gbc.gridx=1;
    this.gbc.gridy=6;
    this.gb.setConstraints(this.astreADHeure, this.gbc);
    this.add(this.astreADHeure);

    //astre AD : Minute
    this.astreADMinute=new JTextField("00");
    this.astreADMinute.setBackground(Color.BLACK);
    this.astreADMinute.setForeground(Color.RED);
    this.astreADMinute.setHorizontalAlignment(JTextField.CENTER);
    this.gbc.gridx=2;
    this.gbc.gridy=6;
    this.gb.setConstraints(this.astreADMinute, this.gbc);
    this.add(this.astreADMinute);

    //astre AD : Seconde
    this.astreADSeconde=new JTextField("00");
    this.astreADSeconde.setBackground(Color.BLACK);
    this.astreADSeconde.setForeground(Color.RED);
    this.astreADSeconde.setPreferredSize(new Dimension(150, 20));
    this.astreADSeconde.setHorizontalAlignment(JTextField.CENTER);
    this.gbc.gridx=3;
    this.gbc.gridy=6;
    this.gb.setConstraints(this.astreADSeconde, this.gbc);
    this.add(this.astreADSeconde);

    //JLabel astreDe
    this.gbc.gridx=0;
    this.gbc.gridy=7;
    this.gb.setConstraints(this.astreDe, this.gbc);
    this.astreDe.setForeground(Color.RED);
    this.add(this.astreDe);

    //astre DEC : degré
    this.astreDECDegre=new JTextField("00");
    this.astreDECDegre.setBackground(Color.BLACK);
    this.astreDECDegre.setForeground(Color.RED);
    this.astreDECDegre.setHorizontalAlignment(JTextField.CENTER);
    this.gbc.gridx=1;
    this.gbc.gridy=7;
    this.gb.setConstraints(this.astreDECDegre, this.gbc);
    this.add(this.astreDECDegre);

    //astre DEC : minute
    this.astreDECMinute=new JTextField("00");
    this.astreDECMinute.setBackground(Color.BLACK);
    this.astreDECMinute.setForeground(Color.RED);
    this.astreDECMinute.setHorizontalAlignment(JTextField.CENTER);
    this.gbc.gridx=2;
    this.gbc.gridy=7;
    this.gb.setConstraints(this.astreDECMinute, this.gbc);
    this.add(this.astreDECMinute);

    //astre DEC : seconde
    this.astreDECSeconde=new JTextField("00");
    this.astreDECSeconde.setBackground(Color.BLACK);
    this.astreDECSeconde.setForeground(Color.RED);
    this.astreDECSeconde.setPreferredSize(new Dimension(150, 20));
    this.astreDECSeconde.setHorizontalAlignment(JTextField.CENTER);
    this.gbc.gridx=3;
    this.gbc.gridy=7;
    this.gb.setConstraints(this.astreDECSeconde, this.gbc);
    this.add(this.astreDECSeconde);

    //JLabel astreAH
    this.gbc.gridx=0;
    this.gbc.gridy=8;
    this.gb.setConstraints(this.astreAH, this.gbc);
    this.astreAH.setForeground(Color.RED);
    this.add(this.astreAH);

    //JLabel astreAHValue
    this.gbc.gridx=1;
    this.gbc.gridy=8;
    this.gb.setConstraints(this.astreAHValue, this.gbc);
    this.astreAHValue.setForeground(Color.RED);
    this.add(this.astreAHValue);

    //JLabel astreVP
    this.gbc.gridx=0;
    this.gbc.gridy=9;
    this.gb.setConstraints(this.astreVP, this.gbc);
    this.astreVP.setForeground(Color.RED);
    this.add(this.astreVP);

    //JLabel astreVPValue
    this.gbc.gridx=1;
    this.gbc.gridy=9;
    this.gb.setConstraints(this.astreVPValue, this.gbc);
    this.astreVPValue.setForeground(Color.RED);
    this.add(this.astreVPValue);

    //noeud ascendant
    this.noeudASC=new JCheckBox("Noeud ascendant", true);
    this.noeudASC.setBackground(Color.BLACK);
    this.noeudASC.setForeground(Color.RED);
    this.gbc.gridx=0;
    this.gbc.gridy=10;
    this.gb.setConstraints(this.noeudASC, this.gbc);
    this.add(this.noeudASC);

    //noeud descendant
    this.noeudDESC=new JCheckBox("Noeud descendant", true);
    this.noeudDESC.setBackground(Color.BLACK);
    this.noeudDESC.setForeground(Color.RED);
    this.gbc.gridx=1;
    this.gbc.gridy=10;
    this.gb.setConstraints(this.noeudDESC, this.gbc);
    this.add(this.noeudDESC);

    //Satellites GPS
    this.satGPS=new JCheckBox("Constellation GPS", true);
    this.satGPS.setBackground(Color.BLACK);
    this.satGPS.setForeground(Color.RED);
    this.gbc.gridx=0;
    this.gbc.gridy=11;
    this.gb.setConstraints(this.satGPS, this.gbc);
    this.add(this.satGPS);
    
    //Mercure
    this.checkMercure=new JCheckBox("Mercure", true);
    this.checkMercure.setBackground(Color.BLACK);
    this.checkMercure.setForeground(Color.DARK_GRAY);
    this.gbc.gridx=0;
    this.gbc.gridy=12;
    this.gb.setConstraints(this.checkMercure, this.gbc);
    this.add(this.checkMercure);
    
    //Vénus
    this.checkVenus=new JCheckBox("Vénus", true);
    this.checkVenus.setBackground(Color.BLACK);
    this.checkVenus.setForeground(Color.WHITE);
    this.gbc.gridx=0;
    this.gbc.gridy=13;
    this.gb.setConstraints(this.checkVenus, this.gbc);
    this.add(this.checkVenus);
    
    //Mars
    this.checkMars=new JCheckBox("Mars", true);
    this.checkMars.setBackground(Color.BLACK);
    this.checkMars.setForeground(Color.RED);
    this.gbc.gridx=0;
    this.gbc.gridy=14;
    this.gb.setConstraints(this.checkMars, this.gbc);
    this.add(this.checkMars);

    //Jupiter
    this.checkJupiter=new JCheckBox("Jupiter", true);
    this.checkJupiter.setBackground(Color.BLACK);
    this.checkJupiter.setForeground(Color.MAGENTA);
    this.gbc.gridx=0;
    this.gbc.gridy=15;
    this.gb.setConstraints(this.checkJupiter, this.gbc);
    this.add(this.checkJupiter);

    //Saturne
    this.checkSaturne=new JCheckBox("Saturne", true);
    this.checkSaturne.setBackground(Color.BLACK);
    this.checkSaturne.setForeground(Color.ORANGE);
    this.gbc.gridx=0;
    this.gbc.gridy=16;
    this.gb.setConstraints(this.checkSaturne, this.gbc);
    this.add(this.checkSaturne);

    //Uranus
    this.checkUranus=new JCheckBox("Uranus", true);
    this.checkUranus.setBackground(Color.BLACK);
    this.checkUranus.setForeground(Color.GREEN);
    this.gbc.gridx=0;
    this.gbc.gridy=17;
    this.gb.setConstraints(this.checkUranus, this.gbc);
    this.add(this.checkUranus);

    //Neptune
    this.checkNeptune=new JCheckBox("Neptune", true);
    this.checkNeptune.setBackground(Color.BLACK);
    this.checkNeptune.setForeground(Color.CYAN);
    this.gbc.gridx=0;
    this.gbc.gridy=18;
    this.gb.setConstraints(this.checkNeptune, this.gbc);
    this.add(this.checkNeptune);
    
    //Astérismes
    this.asterismes=new JCheckBox("Astérismes", true);
    this.asterismes.setBackground(Color.BLACK);
    this.asterismes.setForeground(Color.RED);
    this.gbc.gridx=0;
    this.gbc.gridy=19;
    this.gb.setConstraints(this.asterismes, this.gbc);
    this.add(this.asterismes);

    checkSoleil=new JCheckBox("Soleil", true);
    checkSoleil.setBackground(Color.BLACK);
    checkSoleil.setForeground(Color.YELLOW);
    this.gbc.gridx=0;
    this.gbc.gridy=20;
    this.gb.setConstraints(this.checkSoleil, this.gbc);
    this.add(checkSoleil);

    checkPAS=new JCheckBox("P.A.S. (point anti-solaire)", true);
    checkPAS.setBackground(Color.BLACK);
    checkPAS.setForeground(Color.YELLOW);
    this.gbc.gridx=1;
    this.gbc.gridy=20;
    this.gb.setConstraints(this.checkPAS, this.gbc);
    this.add(checkPAS);

    checkLune=new JCheckBox("Lune", true);
    checkLune.setBackground(Color.BLACK);
    checkLune.setForeground(Color.GRAY);
    this.gbc.gridx=0;
    this.gbc.gridy=21;
    this.gb.setConstraints(this.checkLune, this.gbc);
    this.add(checkLune);

    checkLuneTopoCentric=new JCheckBox("Correction topocentrique", false);
    checkLuneTopoCentric.setBackground(Color.BLACK);
    checkLuneTopoCentric.setForeground(Color.GRAY);
    this.gbc.gridx=1;
    this.gbc.gridy=21;
    this.gb.setConstraints(this.checkLuneTopoCentric, this.gbc);
    this.add(checkLuneTopoCentric);
	
    checkModeEclipse=new JCheckBox("Lune/Soleil à l'échelle + ombre de la Terre) ", false);
    checkModeEclipse.setBackground(Color.BLACK);
    checkModeEclipse.setForeground(Color.RED);
    this.gbc.gridx=0;
    this.gbc.gridy=22;
    this.gb.setConstraints(this.checkModeEclipse, this.gbc);
    this.add(checkModeEclipse);
	}

	public void paintComponent(Graphics g){
		g2d = (Graphics2D) g;
		
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
		g2d.setColor(Color.RED);
	}

}
