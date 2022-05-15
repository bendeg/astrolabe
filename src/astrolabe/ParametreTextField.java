package astrolabe;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.text.Document;

public class ParametreTextField extends JTextField implements ActionListener {
	static final long serialVersionUID=1;
	Astrolabe astro;
	ActionListener al;
	String name;
	double value;
	
	public ParametreTextField() {		
	}

	public ParametreTextField(Astrolabe a, String text) {
		super(text);

		this.astro = a;
		this.name=new String(text);
		this.addActionListener(this);
		
		this.setBackground(Color.BLACK);
		this.setForeground(Color.RED);
		
		switch (this.name){
			case "Latitude" : 
				this.value=a.coordGeo.latitude;
				this.setText(String.valueOf(this.value));
				break;
				
			case "Longitude" : 
				this.value=a.coordGeo.longitude;
				this.setText(String.valueOf(this.value)); break;

			case "Cutoff" : 
				this.value=a.cutoffAngle;
				this.setText(String.valueOf(this.value)); break;

			default : break;
		}	
	}

	public ParametreTextField(int columns) {
		super(columns);
	}

	public ParametreTextField(String text, int columns) {
		super(text, columns);
	}

	public ParametreTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
	}
	
	public void actionPerformed(ActionEvent e){
		System.out.println(name);
		switch (name){
			case "Latitude" : 
				if((Double.valueOf(getText()) >= -90.0) && (Double.valueOf(getText())<=90.0)) {
					astro.coordGeo.latitude=Double.valueOf(getText());
					this.astro.geodesy.GEODESY_ConvertGeodeticCurvilinearToEarthFixedCartesianCoordinates(0, this.astro.coordGeo);
					astro.home.setLatitude(astro.coordGeo.latitude);
					value=astro.coordGeo.latitude;					
				}
				else
					this.setText(String.valueOf(astro.coordGeo.latitude));
				break;
			case "Longitude" : 
				if((Double.valueOf(getText()) >= -180.0) && (Double.valueOf(getText())<=180.0)) {
					astro.coordGeo.longitude=Double.valueOf(getText());
					this.astro.geodesy.GEODESY_ConvertGeodeticCurvilinearToEarthFixedCartesianCoordinates(0, this.astro.coordGeo);
					astro.home.setLongitude(astro.coordGeo.longitude);
					value=astro.coordGeo.longitude;
				}
				else
					this.setText(String.valueOf(astro.coordGeo.longitude));

				break;
			case "Cutoff" : 
				if((Double.valueOf(getText()) >= 0.0) && (Double.valueOf(getText())<=90.0)) {
					astro.cutoffAngle=Double.valueOf(getText());
					this.astro.home.cutoff=this.astro.cutoffAngle;
					value=astro.cutoffAngle;
				}
				else
					this.setText(String.valueOf(astro.cutoffAngle));

				break;
		default : break;
	}
		System.out.println(value);
		//System.exit(0);
	}
}
