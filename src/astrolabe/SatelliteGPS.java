package astrolabe;

public class SatelliteGPS {
	int 	id,
			health;
	
	double 	eccentricity,
			timeOfApplicability,
			orbitalInclination,
			rateOfRightAscen,
			sqrtA,
			rightAscenAtWeek,
			argumentOfPerigee,
			meanAnom,
			af0,
			af1;
	
	long 	week;

	double 	clock_correction,  //!< clock correction for this satellite for this epoch           [m]
	   		clock_drift,       //!< clock drift correction for this satellite for this epoch     [m/s]
	   		doppler;            //!< satellite doppler with respect to the user position          [m/s], Note: User must convert to Hz

	double 	range,         //!< user to satellite range            [m]
	   		range_rate;     //!< user to satellite range rate       [m/s]
	
	CoordonneeGeographique coordonneeGeographique;
	//double latitude, longitude, altitude;
	
	double geoLambda, geoBeta, ra, declination;
	
	public SatelliteGPS() {
		// TODO Auto-generated constructor stub
		this.coordonneeGeographique=new CoordonneeGeographique();
	}

}
