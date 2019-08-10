package astrolabe;

import javafx.geometry.Point3D;

public class CoordonneeGeographique {

	double	latitude,
			longitude,
			altitude;
	
	double 	azimuth,
			elevation,
			ra,
			declination;
	
	double 	vX=0.0,
			vY=0.0,
			vZ=0.0;
	
	Point3D coordonneeCartesienne;
				
	public CoordonneeGeographique() {
		// TODO Auto-generated constructor stub
	}

}
