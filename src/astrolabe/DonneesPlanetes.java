package astrolabe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class DonneesPlanetes extends JPanel {
	static final long serialVersionUID=1;
	
	Astrolabe astro;
	Graphics2D g2d;
	GridBagLayout gbl;
	GridBagConstraints gbc;
	
	String[] donnees={"Ascension droite (H:M:S)", "Déclinaison (°)", "Longitude (°)", "Latitude (°)", "Longitude géocentrique (°)", "Latitude géocentrique (°)", "Distance Soleil (UA)"};
	JLabel[] nomsPlanetes, nomsDonnees;
	JLabel[][] valeurs;
	
	public DonneesPlanetes(Astrolabe a) {
		int i, j;
		astro=a;
		
		this.setOpaque(true);
		this.setBackground(Color.BLACK);
		this.gbl = new GridBagLayout();
		this.setLayout(this.gbl);
		this.gbc=new GridBagConstraints();
		this.nomsPlanetes=new JLabel[this.astro.nomsPlanetes.length];
		this.nomsDonnees=new JLabel[this.donnees.length];
		
		//En-t�tes colonnes (plan�tes)
		this.gbc.fill = GridBagConstraints.HORIZONTAL;
		this.gbc.anchor=GridBagConstraints.EAST;
		this.gbc.insets=new Insets(10,10,10,10);
		this.gbc.gridy=0;
		for(i=0;i<this.nomsPlanetes.length;i++) {
			this.gbc.gridx=i+1;
			this.nomsPlanetes[i]=new JLabel(this.astro.nomsPlanetes[i]);
			this.nomsPlanetes[i].setBackground(Color.BLACK);
			this.nomsPlanetes[i].setForeground(Color.RED);
			this.gbl.setConstraints(this.nomsPlanetes[i], this.gbc);
			this.add(this.nomsPlanetes[i]);
		}
		
		//En-t�tes lignes (donn�es)
		this.gbc.fill = GridBagConstraints.VERTICAL;
		this.gbc.anchor=GridBagConstraints.EAST;
		this.gbc.insets=new Insets(10,10,10,10);
		this.gbc.gridx=0;
		for(i=0;i<this.nomsDonnees.length;i++) {
			this.gbc.gridy=i+1;
			this.nomsDonnees[i]=new JLabel(this.donnees[i]);
			this.nomsDonnees[i].setBackground(Color.BLACK);
			this.nomsDonnees[i].setForeground(Color.RED);
			this.gbl.setConstraints(this.nomsDonnees[i], this.gbc);
			this.add(this.nomsDonnees[i]);
		}		

		//les valeurs du tableau
		this.valeurs=new JLabel[this.astro.nomsPlanetes.length][this.nomsDonnees.length];
		for(i=0; i<this.nomsPlanetes.length;i++)
			for(j=0;j<this.nomsDonnees.length;j++) {
				this.valeurs[i][j]=new JLabel("null");
				this.valeurs[i][j].setForeground(Color.ORANGE);
				this.gbc.gridx=i+1;
				this.gbc.gridy=j+1;
				this.gbl.setConstraints(this.valeurs[i][j], this.gbc);
				this.add(this.valeurs[i][j]);
			}
				
	}
	
	public void populate() {
		int i, j;
		
		for(i=0;i<this.nomsPlanetes.length;i++)
			for(j=0;j<this.nomsDonnees.length;j++){	
				switch(j){
				case 0 : this.valeurs[i][j].setText(String.valueOf(this.astro.calc.angleDecimalToHMS(this.astro.planetes[i].ra/15.0)));
				break;
				case 1 : this.valeurs[i][j].setText(String.valueOf(this.astro.calc.angleDecimalToDMS(this.astro.planetes[i].declination)));
				break;
				case 2 : this.valeurs[i][j].setText(String.valueOf(this.astro.calc.angleDecimalToDMS(this.astro.planetes[i].helioLambda)));
				break;
				case 3 : this.valeurs[i][j].setText(String.valueOf(this.astro.calc.angleDecimalToDMS(this.astro.planetes[i].helioBeta)));
				break;
				case 4 : this.valeurs[i][j].setText(String.valueOf(this.astro.calc.angleDecimalToDMS(this.astro.planetes[i].geoLambda)));
				break;
				case 5 : this.valeurs[i][j].setText(String.valueOf(this.astro.calc.angleDecimalToDMS(this.astro.planetes[i].geoBeta)));
				break;
				case 6 : this.valeurs[i][j].setText(String.format("%1.10f", this.astro.planetes[i].helioRadius));
				break;
				
				default:break;
				}
			}
	}

	public void paintComponent(Graphics g){
		 g2d = (Graphics2D) g;
		
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
		 		
		this.populate();
	}

}
