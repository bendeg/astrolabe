package astrolabe;

import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Sextant extends JPanel {
	static final long serialVersionUID=1;
	
Astrolabe astro;

double permanentOffset=0.0,
		dipSeaHorizon=0.0,
		atmosphericPressure=0.0,// in millibars
		airTemperature=0.0,// in degrees Kelvin
		altitude=0.0,// in degrees
		height=0.0;//in meters

JTextArea altitudeT;
JButton calcLongB, calcLatB;
JCheckBox risingChck, northHemisphere;

	public Sextant() {
		// TODO Auto-generated constructor stub
	}

	public Sextant(Astrolabe a) {
		// TODO Auto-generated constructor stub
		this();
		this.astro=a;
		
		this.setBackground(Color.BLACK);
		
		this.altitudeT=new JTextArea("45.0");
		
		this.calcLongB=new JButton("Calculer longitude");
		this.calcLongB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton source;
				Sextant sextant;
				Astrolabe a;
				
				source=(JButton) e.getSource();
				a=(Astrolabe) source.getRootPane().getParent();
				sextant= (Sextant) source.getParent();
				//System.out.println("longitude :"+sextant.calculateLongitudeFromAltitude(sextant.risingChck.isSelected()));
				a.param.textFieldLongitude.setText(String.valueOf(sextant.calculateLongitudeFromAltitude(sextant.risingChck.isSelected())));
			}
		});
		
		this.calcLatB=new JButton("Calculer latitude");
		this.calcLatB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton source;
				Sextant sextant;
				Astrolabe a;
				
				source=(JButton) e.getSource();
				a=(Astrolabe) source.getRootPane().getParent();
				sextant= (Sextant) source.getParent();
				//System.out.println("latitude :"+sextant.calculateLatitudeFromLAN(sextant.northHemisphere.isSelected()));
				a.param.textFieldLatitude.setText(String.valueOf(sextant.calculateLatitudeFromLAN(sextant.northHemisphere.isSelected())));
			}
		});
		
		this.risingChck=new JCheckBox("Soleil montant", true);
		this.northHemisphere=new JCheckBox("Hémisphère Nord", true);
		
		this.add(this.altitudeT);
		this.add(this.calcLongB);
		this.add(this.risingChck);
		this.add(this.calcLatB);
		this.add(this.northHemisphere);
	}

	public Sextant(LayoutManager arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public Sextant(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public Sextant(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}
	
	public double calculateDip() {
		return 1.753*Math.sqrt(this.height);
	}
	
	public double calculateRefraction() {
		//Source : http://www.madinstro.net/sundry/navsext.html
		//This formula has been checked against the published figures 
		//in the Admiralty's Almanac, Brown's Almanac and Norie's tables.
		//The agreement is better than 0.01' of arc for all altitudes between 7 and 90 degrees
		//and better than +/- 1' of arc between 7 and 0 degrees. 
		
		return (0.267*this.atmosphericPressure/this.airTemperature)
				/
				(Math.atan(Math.toRadians(this.altitude+(0.04848/(Math.atan(Math.toRadians(this.altitude)+0.028))))));
	}

	public double calculateLatitudeFromLAN(boolean northHemisphere) {
		double hemiCoef;
		
		//TODO parfois probl�me NaN quand au Sud... 
		if(northHemisphere) hemiCoef=1.0;
		else hemiCoef=-1.0;
		
		this.altitude=Double.valueOf(this.altitudeT.getText());
		
		if(this.astro.calc.sunDeclination != 0.0)
			/*
			return Math.toDegrees(Math.asin(
									Math.sin(Math.toRadians(this.altitude))
									/
									Math.sin(Math.toRadians(this.astro.calc.sunDeclination))
									));

			 */
			return hemiCoef*(hemiCoef*this.astro.calc.sunDeclination+Math.toDegrees(Math.acos(Math.sin(Math.toRadians(this.altitude)))));
		else
			return hemiCoef*(Math.toDegrees(Math.acos(Math.sin(Math.toRadians(this.altitude)))));
	}

	public double calculateLongitudeFromAltitude(boolean rising) {
		double H=0.0, longitude;
		
		this.altitude=Double.valueOf(this.altitudeT.getText());
		
		H=Math.acos(
				(Math.sin(Math.toRadians(this.altitude))-(Math.sin(Math.toRadians(this.astro.coordGeo.latitude))*Math.sin(Math.toRadians(this.astro.calc.sunDeclination))))
				/
				(Math.cos(Math.toRadians(this.astro.coordGeo.latitude))*Math.cos(Math.toRadians(this.astro.calc.sunDeclination)))
				);
		
		if(rising)
			longitude= -((this.astro.calc.utSiderealtime)-Math.toDegrees(-H)-this.astro.calc.sunRightAscension+360.0)%360.0;
		else
			longitude= 360.0-((this.astro.calc.utSiderealtime)-Math.toDegrees(H)-this.astro.calc.sunRightAscension+360.0)%360.0;
		
		if(longitude>=0 && longitude>180.0) return -(360.0-longitude);
		if(longitude<0 && longitude<-180.0) return (360.0+longitude);
		
		return longitude;
	}
}
