package astrolabe;

public class Moon {
	Astrolabe astro;
	//Periodic terms for Moon latitude
	//D,M,M',F,El, Er
	long[] moonLongitudeRadiusPeriodicTerms = new long[]{ 
		0,0,1,0,6288774,-20905355,
		2,0,-1,0,1274027,-3699111,
		2,0,0,0,658314,-2955968,
		0,0,2,0,213618,-569925,
		0,1,0,0,-185116,48888,
		0,0,0,2,-114332,-3149,
		2,0,-2,0,58793,246158,
		2,-1,-1,0,57066,-152138,
		2,0,1,0,53322,-170733,
		2,-1,0,0,45758,-204586,
		0,1,-1,0,-40923,-129620,
		1,0,0,0,-34720,108743,
		0,1,1,0,-30383,104755,
		2,0,0,-2,15327,10321,
		0,0,1,2,-12528,0,
		0,0,1,-2,10980,79661,
		4,0,-1,0,10675,-34782,
		0,0,3,0,10034,-23210,
		4,0,-2,0,8548,-21636,
		2,1,-1,0,-7888,24208,
		2,1,0,0,-6766,30824,
		1,0,-1,0,-5163,-8379,
		1,1,0,0,4987,-16675,
		2,-1,1,0,4036,-12831,
		2,0,2,0,3994,-10445,
		4,0,0,0,3861,-11650,
		2,0,-3,0,3665,14403,
		0,1,-2,0,-2689,-7003,
		2,0,-1,2,-2602,0,
		2,-1,-2,0,2390,10056,
		1,0,1,0,-2348,6322,
		2,-2,0,0,2236,-9884,
		0,1,2,0,-2120,5751,
		0,2,0,0,-2069,0,
		2,-2,-1,0,2048,-4950,
		2,0,1,-2,-1773,4130,
		2,0,0,2,-1595,0,
		4,-1,-1,0,1215,-3958,
		0,0,2,2,-1110,0,
		3,0,-1,0,-892,3258,
		2,1,1,0,-810,2616,
		4,-1,-2,0,759,-1897,
		0,2,-1,0,-713,-2117,
		2,2,-1,0,-700,2354,
		2,1,-2,0,691,0,
		2,-1,0,-2,596,0,
		4,0,1,0,549,-1423,
		0,0,4,0,537,-1117,
		4,-1,0,0,520,-1571,
		1,0,-2,0,-487,-1739,
		2,1,0,-2,-399,0,
		0,0,2,-2,-381,-4421,
		1,1,1,0,351,0,
		3,0,-2,0,-340,0,
		4,0,-3,0,330,0,
		2,-1,2,0,327,0,
		0,2,1,0,-323,1165,
		1,1,-1,0,299,0,
		2,0,3,0,294,0,
		2,0,-1,-2,0,8752
	};
	
	//Periodic terms for Moon latitude
	//D,M,M',F,Eb
	long[] moonLatitudePeriodicTerms = new long[]{ 
		0,0,0,1,5128122,
		0,0,1,1,280602,
		0,0,1,-1,277693,
		2,0,0,-1,173237,
		2,0,-1,1,55413,
		2,0,-1,-1,46271,
		2,0,0,1,32573,
		0,0,2,1,17198,
		2,0,1,-1,9266,
		0,0,2,-1,8822,
		2,-1,0,-1,8216,
		2,0,-2,-1,4324,
		2,0,1,1,4200,
		2,1,0,-1,-3359,
		2,-1,-1,1,2463,
		2,-1,0,1,2211,
		2,-1,-1,-1,2065,
		0,1,-1,-1,-1870,
		4,0,-1,-1,1828,
		0,1,0,1,-1794,
		0,0,0,3,-1749,
		0,1,-1,1,-1565,
		1,0,0,1,-1491,
		0,1,1,1,-1475,
		0,1,1,-1,-1410,
		0,1,0,-1,-1344,
		1,0,0,-1,-1335,
		0,0,3,1,1107,
		4,0,0,-1,1021,
		4,0,-1,1,833,
		0,0,1,-3,777,
		4,0,-2,1,671,
		2,0,0,-3,607,
		2,0,2,-1,596,
		2,-1,1,-1,491,
		2,0,-2,1,-451,
		0,0,3,-1,439,
		2,0,2,1,422,
		2,0,-3,-1,421,
		2,1,-1,1,-366,
		2,1,0,1,-351,
		4,0,0,1,331,
		2,-1,1,1,315,
		2,-2,0,-1,302,
		0,0,1,3,-283,
		2,1,1,-1,-229,
		1,1,0,-1,223,
		1,1,0,1,223,
		0,1,-2,-1,-220,
		2,1,-1,-1,-220,
		1,0,1,1,-185,
		2,-1,-2,-1,181,
		0,1,2,1,-177,
		4,0,-2,-1,176,
		4,-1,-1,-1,166,
		1,0,1,-1,-164,
		4,0,1,-1,132,
		1,0,-1,-1,-119,
		4,-1,0,-1,115,
		2,-2,0,1,107
	};

	//meanElongationMoonFromSun = Mean elongation of the Moon from the Sun
	//meanAnomalySun = Mean anomaly of the Sun (Earth)
	//meanAnomalyMoon = Mean anomaly of the Moon
	//argumentLatitudeMoon = Moon's argument of latitude
	//moonLongitudeAscendignNode = Longitude of the ascending node of the Moon's mean orbit on the ecliptic, measured
	//from the mean equinox of the date
	//sunMeanLongitude = mean longitudes of the Sun
	//moonMeanLongitude = mean longitudes of the Moon

	double 	
	earthMoonDistance, moonEquatorialHorizontalParallax,
	moonMeanElongationFromSun, moonMeanAnomalySun, moonMeanAnomaly, moonArgumentLatitude,
	moonLongitudeAscendignNode, moonMeanLongitude,
	
	luneNoeudAscendantRA, luneNoeudAscendantDeclinaison,
	luneNoeudDescendantRA, luneNoeudDescendantDeclinaison,
	
	moonRA, moonDeclination, moonHauteur, moonAzimut,
	E, sumL, sumR, sumB, A1, A2, A3,
	moonGeoLambda, moonGeoBeta;

	double[] periodicTerms;
	
	public Moon(Astrolabe a) {
		this.astro=a;
	}
	
	public void calculateMoon() {
		//Calcul de la longitude et la latitude GEOCENTRIQUES de la Lune
		// +
		// Transformation en coordonn�es EQUATORIALES

		//mean longitude of the Sun and the Moon (L')
		this.periodicTerms=new double[]{218.3164477, 481267.88123421, -0.0015786, 1.0/538841, -1.0/65194000};
		this.moonMeanLongitude = this.astro.calc.calculatePolynomial(this.astro.calc.T, this.periodicTerms, 0);
		this.moonMeanLongitude = ((this.moonMeanLongitude%360.0)+360.0)%360.0;
		//System.out.println("moonMeanLongitude = "+this.moonMeanLongitude);
	
		//Mean elongation of the Moon from the Sun (D)
		this.periodicTerms=new double[]{297.8501921, 445267.1114034, -0.0018819, 1.0/545868, -1.0/113065000};
		this.moonMeanElongationFromSun =this.astro.calc.calculatePolynomial(this.astro.calc.T, this.periodicTerms, 0); 
		this.moonMeanElongationFromSun = ((this.moonMeanElongationFromSun%360.0)+360.0)%360.0;
		//System.out.println("moonMeanElongationFromSun = "+this.moonMeanElongationFromSun);
		
		//Mean anomaly of the Sun (M)
		this.periodicTerms=new double[]{357.5291092, 35999.0502909, -0.0001536, 1.0/24490000};
		this.moonMeanAnomalySun=this.astro.calc.calculatePolynomial(this.astro.calc.T, this.periodicTerms, 0);
		this.moonMeanAnomalySun = ((this.moonMeanAnomalySun%360.0)+360.0)%360.0;
		//System.out.println("moonMeanAnomalySun = "+this.moonMeanAnomalySun);
		
		//Mean anomaly of the Moon (M')
		this.periodicTerms=new double[]{134.9633964, 477198.8675055,	0.0087414, 1.0/69699, -1.0/14712000};
		this.moonMeanAnomaly=this.astro.calc.calculatePolynomial(this.astro.calc.T, this.periodicTerms, 0);
		this.moonMeanAnomaly = ((this.moonMeanAnomaly%360.0)+360.0)%360.0;
		//System.out.println("moonMeanAnomaly = "+this.moonMeanAnomaly);
		
		//Moon's argument of latitude (F)
		this.periodicTerms=new double[]{93.2720950, 483202.0175233, -0.0036539, -1.0/3526000, 1.0/863310000};
		this.moonArgumentLatitude=this.astro.calc.calculatePolynomial(this.astro.calc.T, this.periodicTerms, 0);
		this.moonArgumentLatitude = ((this.moonArgumentLatitude%360.0)+360.0)%360.0;
		//System.out.println("moonArgumentLatitude = "+this.moonArgumentLatitude);
		
		//Longitude of the ascending node of the Moon's mean orbit on the ecliptic, measured
		//from the mean equinox of the date
		//If an accuracy of 0':5 in ill/; and of 0': 1 in ll.e are sufficient
		//this.periodicTerms=new double[]{125.04452, -1934.136261, 0.0020708, 1/450000};
		this.moonLongitudeAscendignNode=125.04452-1934.136261*this.astro.calc.T;
		this.moonLongitudeAscendignNode = ((this.moonLongitudeAscendignNode%360.0)+360.0)%360.0;
		//System.out.println("moonLongitudeAscendignNode = "+this.moonLongitudeAscendignNode);
		
		//essai noeuds ascendant lune...
		this.luneNoeudAscendantDeclinaison=this.astro.calc.calculateEclipticalToDeclination(this.moonLongitudeAscendignNode, 0.0);
		//System.out.println("lune asc node Dec = "+this.luneNoeudAscendantDeclinaison);
		this.luneNoeudAscendantRA=this.astro.calc.calculateEclipticalToRA(this.moonLongitudeAscendignNode, 0.0);
		//System.out.println("lune asc node RA = "+this.luneNoeudAscendantRA);
		
		//essai noeuds descendant lune...
		this.luneNoeudDescendantDeclinaison=this.astro.calc.calculateEclipticalToDeclination(this.moonLongitudeAscendignNode+180.0, 0.0);
		//System.out.println("lune desc node Dec = "+this.luneNoeudDescendantDeclinaison);
		this.luneNoeudDescendantRA=this.astro.calc.calculateEclipticalToRA(this.moonLongitudeAscendignNode+180.0, 0.0);
		//System.out.println("lune desc node RA = "+this.luneNoeudDescendantRA);
		
		//calcul une 2ème fois ???
		//this.moonMeanLongitude = 218.3165 + 481267.8813*this.astro.calc.T;
		//this.moonMeanLongitude = ((this.moonMeanLongitude%360.0)+360.0)%360.0;
		//System.out.println("moonMeanLongitude = "+this.moonMeanLongitude);

		//Three further arguments (again, in degrees) are needed
		this.A1 = 119.75 + 131.849*this.astro.calc.T;
		this.A1 = ((this.A1%360.0)+360.0)%360.0;
		this.A2 = 53.09 + 479264.290*this.astro.calc.T;
		this.A2 = ((this.A2%360.0)+360.0)%360.0;
		this.A3 = 313.45 + 481266.484*this.astro.calc.T;
		this.A3 = ((this.A3%360.0)+360.0)%360.0;
		//System.out.println("A1= "+this.A1+" A2="+this.A2+" A3="+this.A3);
		
		//However, the terms whose argument contains the angle M depend on the
		//eccentricity of the Earth's orbit around the Sun, which presently is decreasing with
		//time. For this reason, the amplitude of these terms is actually variable. To take this
		//effect into account, multiply the terms whose argument contains M or -M by E,
		//and those containing 2M or - 2M by E 2, where
		this.E=1.0 - 0.002516*this.astro.calc.T - 0.0000074*this.astro.calc.T*this.astro.calc.T;
		//System.out.println("Lune E="+E);
		
		//Calculate the sums r.Z and r.r of the terms given in Table 47.A
		//System.out.println("Nbre lignes modulo 6 Table47A = "+this.moonLongitudeRadiusPeriodicTerms.length%6);
		//System.out.println("Nbre lignes = "+(this.moonLongitudeRadiusPeriodicTerms.length)/6.0);
		this.sumL=0.0;
		this.sumR=0.0;
		this.sumB=0.0;
		double tempE;
		
		for(int i=0; i<=this.moonLongitudeRadiusPeriodicTerms.length-6; i+=6){
			//Table 47A
			/*
			System.out.println("L"+(i/6)+" "+this.moonLongitudeRadiusPeriodicTerms[i]
									+" "+this.moonLongitudeRadiusPeriodicTerms[i+1]
									+" "+this.moonLongitudeRadiusPeriodicTerms[i+2]											
									+" "+this.moonLongitudeRadiusPeriodicTerms[i+3]											
									+" "+this.moonLongitudeRadiusPeriodicTerms[i+4]											
									+" "+this.moonLongitudeRadiusPeriodicTerms[i+5]											
							);
			*/
		  
		  tempE=1.0;
			if(Math.abs(this.moonLongitudeRadiusPeriodicTerms[i+1])==2) 
				tempE=this.E*this.E;
			if (Math.abs(this.moonLongitudeRadiusPeriodicTerms[i+1])==1)
				tempE=this.E;
			
			this.sumL+=this.moonLongitudeRadiusPeriodicTerms[i+4]
			    *tempE
					*Math.sin(Math.toRadians(
							+this.moonLongitudeRadiusPeriodicTerms[i]*this.moonMeanElongationFromSun
							+this.moonLongitudeRadiusPeriodicTerms[i+1]*this.moonMeanAnomalySun
							+this.moonLongitudeRadiusPeriodicTerms[i+2]*this.moonMeanAnomaly									
							+this.moonLongitudeRadiusPeriodicTerms[i+3]*this.moonArgumentLatitude
									));
			
			this.sumR+=this.moonLongitudeRadiusPeriodicTerms[i+5]
			    *tempE
					*Math.cos(Math.toRadians(
							+this.moonLongitudeRadiusPeriodicTerms[i]*this.moonMeanElongationFromSun
							+this.moonLongitudeRadiusPeriodicTerms[i+1]*this.moonMeanAnomalySun
							+this.moonLongitudeRadiusPeriodicTerms[i+2]*this.moonMeanAnomaly									
							+this.moonLongitudeRadiusPeriodicTerms[i+3]*this.moonArgumentLatitude
									));
		}
		//System.out.println("SumL= "+this.sumL+" SumR= "+this.sumR);
		
			//Table 47B
		for(int i=0; i<=this.moonLatitudePeriodicTerms.length-5; i+=5){
			/*
			System.out.println("L"+(i/5)+" "+this.moonLatitudePeriodicTerms[i]
									+" "+this.moonLatitudePeriodicTerms[i+1]
									+" "+this.moonLatitudePeriodicTerms[i+2]											
									+" "+this.moonLatitudePeriodicTerms[i+3]											
									+" "+this.moonLatitudePeriodicTerms[i+4]											
							);
			*/
			
		  tempE=1.0;
			if(Math.abs(this.moonLatitudePeriodicTerms[i+1])==2)
				tempE=this.E*this.E;
			if (Math.abs(this.moonLatitudePeriodicTerms[i+1])==1)
				tempE=this.E;
			
			this.sumB+=this.moonLatitudePeriodicTerms[i+4]
			    *tempE
					*Math.sin(Math.toRadians(
							+this.moonLatitudePeriodicTerms[i]*this.moonMeanElongationFromSun
							+this.moonLatitudePeriodicTerms[i+1]*this.moonMeanAnomalySun
							+this.moonLatitudePeriodicTerms[i+2]*this.moonMeanAnomaly									
							+this.moonLatitudePeriodicTerms[i+3]*this.moonArgumentLatitude
									));
		}
		//System.out.println("SumB= "+this.sumB);
		
		//Moreover, add the following additive terms to sumL and to sumB. The terms
		//involving A I are due to the action of Venus, the term involving A2 is due to Saturne,
		//while those involving L' are due to the flattening of the Earth
		//Additive to sumL
		this.sumL+=3958.0*Math.sin(Math.toRadians(this.A1));
		this.sumL+=1962.0*Math.sin(Math.toRadians(this.moonMeanLongitude - this.moonArgumentLatitude));
		this.sumL+=318.0*Math.sin(Math.toRadians(this.A2));
	
		//Additive to sumB
		this.sumB+=-2235.0*Math.sin(Math.toRadians(this.moonMeanLongitude));
		this.sumB+=382.0*Math.sin(Math.toRadians(this.A3));
		this.sumB+=175.0*Math.sin(Math.toRadians(this.A1 - this.moonArgumentLatitude));
		this.sumB+=175.0*Math.sin(Math.toRadians(this.A1 + this.moonArgumentLatitude));
		this.sumB+=127.0*Math.sin(Math.toRadians(this.moonMeanLongitude - this.moonMeanAnomaly));
		this.sumB+=115.0*Math.sin(Math.toRadians(this.moonMeanLongitude + this.moonMeanAnomaly));
		//System.out.println("SumL= "+this.sumL+" SumR= "+this.sumR+" SumB= "+this.sumB);
		
		//The coordinates of the Moon are then given by
		this.moonGeoLambda=this.moonMeanLongitude+(this.sumL/1000000.0);//degree
		this.moonGeoBeta=this.sumB/1000000.0;//degree
		this.earthMoonDistance=385000.56+(this.sumR/1000.0);//kilometer
		//System.out.println("Moon longitude = "+this.moonGeoLambda);
		//System.out.println("Moon latitude = "+this.moonGeoBeta);
		//System.out.println("Earth-Moon distance = "+this.earthMoonDistance);
		
		//The equatorial horizontal parallax (pi) of the Moon can then be obtained from
		this.moonEquatorialHorizontalParallax=Math.toDegrees(Math.asin(6378.14/this.earthMoonDistance));
		//System.out.println("Lune, parallaxe équatoriale horizontale ="+this.astro.calc.angleDecimalToDMS(this.moonEquatorialHorizontalParallax));
		
		//On peut DIRECTEMENT transformer la longitude et la latitude
		//car elles sont GEOCENTRIQUE !
		this.moonRA=this.astro.calc.calculateEclipticalToRA(this.moonGeoLambda, this.moonGeoBeta);
		//System.out.println("Moon AR = "+(this.moonRA)+" dms = "+this.angleDecimalToDMS(this.moonRA/15.0));
		
		this.moonDeclination=this.astro.calc.calculateEclipticalToDeclination(this.moonGeoLambda,  this.moonGeoBeta);
		//System.out.println("Moon declination = "+this.moonDeclination+" dms = "+this.angleDecimalToDMS(this.moonDeclination));
	
		this.moonHauteur=this.astro.calc.calculateHauteurHorizontalFromEquatorial(this.moonRA, this.moonDeclination);
		
		this.moonAzimut=this.astro.calc.calculateAzimutHorizontalFromEquatorial(this.moonRA, this.moonDeclination);
		
	}
}
