package astrolabe;

import java.awt.Color;
import javax.swing.JTable;

public class TableauPlanetes {
	static final long serialVersionUID=1;
	
Astrolabe astro;
JTable tableau;
String[] nomsPlanetes={"", "Mercure", "Vénus", "Terre", "Mars", "Jupiter", "Saturne", "Uranus", "Neptune", "Pluton", "Lune", "Soleil"};
String[] donnees={"Ascension droite (h:m:s)",
					"Déclinaison (°)",
					"Longitude (°)",
					"Latitude (°)",
					"Longitude géocentrique (°)",
					"Latitude géocentrique (°)",
					"Distance Soleil (UA)",
					"Hauteur (°)",
					"Azimuth (°)",
					"Angle horaire"};
String[][] valeurs;

	public TableauPlanetes(Astrolabe a) {
		//super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		int i, j;

		this.astro=a;
		//this.valeurs=new String[this.donnees.length][this.nomsPlanetes.length];
		this.valeurs=new String[this.donnees.length][this.nomsPlanetes.length];
		//en-t�te ligne
		
		for(i=0;i<this.donnees.length;i++) {
			for(j=0;j<this.nomsPlanetes.length;j++)
				if(j==0) {
					this.valeurs[i][j]=this.donnees[i];
				}
				else this.valeurs[i][j]=new String();
		}
		this.populate();
		this.tableau=new JTable(this.valeurs, this.nomsPlanetes);
		this.tableau.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//System.out.println("tableau : "+tableau.getColumnCount());
		//System.out.println("tableau : "+tableau.getRowCount());
		for(i=0;i<this.nomsPlanetes.length;i++) {
					this.tableau.getColumnModel().getColumn(i).setPreferredWidth(150);
		}

		/*
		tableau.setCellRenderer( new DefaultTableCellRenderer() {
		    public Component getTableCellRenderer(JTable table, Object value, ...) {
		        super.getTableCellRenderer(...);

		        if ( value should be highlighted ) {
		            setBackground( Color.RED );
		        }
		        return this;
		    };
		*/
		
		this.tableau.setBackground(Color.BLACK);
		this.tableau.setForeground(Color.RED);
		this.tableau.setGridColor(Color.BLACK);
	}

	public void populate() {
		int i, j;
		
		for(i=0;i<this.donnees.length;i++)
			for(j=0;j<this.astro.nomsPlanetes.length;j++){	
				switch(i){
				case 0 : 
						switch(j) {
							case 9://Moon
								this.valeurs[i][j+1]=this.astro.calc.angleDecimalToHMS(this.astro.moon.moonRA/15.0);
								break;
							case 10://soleil
								this.valeurs[i][j+1]=this.astro.calc.angleDecimalToHMS(((this.astro.calc.sunRightAscension+360.0)%360.0)/15.0);
								break;
							default: this.valeurs[i][j+1]=this.astro.calc.angleDecimalToHMS(this.astro.planetes[j].ra/15.0);
							break;
						}
						
				break;
				case 1 : 
					switch(j) {
						case 9://Moon
								this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.moon.moonDeclination);
								break;
						case 10://Soleil
								this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.calc.sunDeclination);
							break;
						default: this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.planetes[j].declination);
						break;
					}
				break;
				case 2 : 
					this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.planetes[j].helioLambda);
					//Planete ext�rieure en opposition (+ ou - 2,5�)?
					if(j > 2)
						if(Math.abs(this.astro.planetes[j].helioLambda-this.astro.planetes[2].helioLambda)<5){
							//System.out.println("Plan�te du tableau en phase opposition (+- 2,5�) = "+this.astro.planetes[j].nom);
						}
						else;
					else
						if((j!=2) && (Math.abs(this.astro.planetes[j].helioLambda-this.astro.planetes[2].helioLambda)<5)){
							//System.out.println("Plan�te du tableau en phase conjonction (+- 2,5�) = "+this.astro.planetes[j].nom);
						}
						else;
				break;
				case 3 : this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.planetes[j].helioBeta);
				break;
				case 4 : 
					if(j==9)//Moon
						this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.moon.moonGeoLambda);
					else this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.planetes[j].geoLambda);
				break;
				case 5 : 
					if(j==9)//Moon
						this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.moon.moonGeoBeta);
					else this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.planetes[j].geoBeta);
				break;
				case 6 : 
					if(j==9)//Moon
						this.valeurs[i][j+1]=String.valueOf(this.astro.moon.earthMoonDistance);
					else this.valeurs[i][j+1]=String.format("%1.10f", this.astro.planetes[j].helioRadius);
				break;
				case 7 :
					switch(j) {
					case 9://Moon
						this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.moon.moonHauteur);
							break;
					case 10://Soleil
							this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.calc.sunHeight);
						break;
					default: this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.planetes[j].hauteur);
						break;
					}
					break;
				case 8 :
					switch(j) {
					case 9://Moon
						this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.moon.moonAzimut);
						break;
					case 10://Soleil
						this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.calc.sunAzimut);
						break;
					default:
						 this.valeurs[i][j+1]=this.astro.calc.angleDecimalToDMS(this.astro.planetes[j].azimuth);
						 break;
					}
					break;
				case 9://Angle horaire, erreur dans le calcul AH = LST - AD
				  switch(j) {
          case 9://Moon
            this.valeurs[i][j+1]=this.astro.calc.angleDecimalToHMS(((this.astro.calc.localSiderealTime - this.astro.moon.moonRA+360.0)%360.0)/15.0);
            break;
          case 10://soleil
            this.valeurs[i][j+1]=this.astro.calc.angleDecimalToHMS(((this.astro.calc.localSiderealTime - this.astro.calc.sunRightAscension+360.0)%360.0)/15.0);
            break;
          default: this.valeurs[i][j+1]=this.astro.calc.angleDecimalToHMS(((this.astro.calc.localSiderealTime - this.astro.planetes[j].ra+360.0)%360.0)/15.0);
            break;
				  }
				  
				    break;
				default:break;
				}
			}
		
		//les valeurs du tableau vers console
		/*
		for(i=0;i<this.donnees.length;i++)
			for(j=0;j<this.nomsPlanetes.length-3;j++) {
				//System.out.println("tableau ("+i+","+j+")="+this.valeurs[i][j]);
				//this.tableau.setValueAt(this.valeurs[j][i+1], j, i);
		}
		*/

	}

}
