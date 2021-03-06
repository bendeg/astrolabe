package astrolabe;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRules;
import java.util.GregorianCalendar;

//PI (3.1415926535898) // value from GPS-ICD pp. 101 used in orbit curve fit calculations by the control segment

public class Calculs {

//source "constants.h"
//http://gnsstk.sourceforge.net/constants_8h.html

	Astrolabe astro;
	
	GregorianCalendar calendar;
	LocalTime st;
	ZonedDateTime zdt, ut, ldt, hsl;
	ZoneId zid;
	ZoneRules zr;
	int currentYear=0;
	
	//Time
	// td : Dynamical time (atomic clock, not "Temps Dynamique" !)
	// T : used temporarily for
	//H : hour angle (temporarily used for any body location calculation)
	//deltaT : time difference between TD and UT => deltaT=TD-UT 
	//jde, td, mjd, siderealTime, T, H,
	//	deltaT,

	double jd, fuseau, jde, td, mjd, localSiderealTime, utSiderealtime, T, H, deltaT;
	double[] periodicTerms;
	boolean dst;

	//Sun
	//sunC : center of the sun
	//omega : temporarily used for 
	//eot : Equation of Time
	
	double sunMeanLongitude, sunMeanAnomaly,
	sunC, sunTrueLongitude, sunTrueAnomaly,
	sunRightAscension, sunDeclination, sunSize,
	eclipticMeanObliquity, eclipticTrueObliquity,
	omega, sunApparentLongitude,
	eot, sunLocalTime,
	sunAzimut, sunHeight,
	earthOrbitEccentricity, sunRadiusVector,
	
	//Nutation
	nutationLongitude, nutationObliquity,
	//Precession
	precessionZeta, precessionZ, precessionTheta
	;

	//pour calcul des RA et d�clinaison des �toiles selon pr�cession
	double[] stars;
//=======================================================================
//=======================================================================
//=======================================================================
	public Calculs(Astrolabe a) {
		astro=a;		
		
		//recopie des magnitudes du tableau d'�toiles d'origine
		this.stars=new double[this.astro.face.stars2.length];
		for(int i=0;i<this.astro.face.stars2.length/3;i++) {
			this.stars[3*i+2]=this.astro.face.stars2[3*i+2];
		}

	}
	
	public ZonedDateTime now() {
		this.astro.calc.ldt=ZonedDateTime.now();
		return this.astro.calc.ldt;
	}
	
	public double calculateHauteurHorizontalFromEquatorial(double ra, double declination) {
		double H=this.localSiderealTime - ra;
		
		return Math.toDegrees(Math.asin(
				(Math.sin(Math.toRadians(this.astro.coordGeo.latitude))*
					Math.sin(Math.toRadians(declination)))
						+
					(Math.cos(Math.toRadians(this.astro.coordGeo.latitude))*
					Math.cos(Math.toRadians(declination))*
					Math.cos(Math.toRadians(H)))
				));
	}

	public double calculateAzimutHorizontalFromEquatorial(double ra, double declination) {
		double H=this.localSiderealTime - ra;
		
		return (Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(H)),
				(Math.cos(Math.toRadians(H))*Math.sin(Math.toRadians(this.astro.coordGeo.latitude)))
				-(Math.tan(Math.toRadians(declination))*Math.cos(Math.toRadians(this.astro.coordGeo.latitude)))))+180.0)%360.0;
	}
	
	public double calculDeltaT() {
		int y;
		double t;
		
		//Calcul deltaT
		//pour ann�es entre 1600 et 2000 : voir Astronomical Algorithms (table 10.A, p.79)
		//TODO pas trouv� de formule pour la p�riode 1600->1800, 1998 et 1999 => tr�s grosse erreur !
		//y=ldt.getYear();
		y=ut.getYear();
		t=(y-2000.0)/100.0;
		//System.out.println("Calcul deltaT : y="+y+" t="+t);
		
		if((y>=1800) && (y<=1997)) {
			periodicTerms=new double[]{-1.02, 91.02, 265.90, -839.16, -1545.20
			, 3603.62, 4385.98, -6993.23, -6090.04, 6298.12, 4102.86, -2137.64
			, -1081.51};	
			this.deltaT = this.calculatePolynomial(t, periodicTerms,0);
		}

		if(y<948){
			periodicTerms=new double[]{2177.0, 497.0, 44.1};
			this.deltaT=this.calculatePolynomial(t, periodicTerms, 0);
		}
		
		if(((y>=948) && (y<=1600)) || (y>=2000)){
			periodicTerms=new double[]{102.0, 102.0, 25.3};
			this.deltaT = this.calculatePolynomial(t, periodicTerms,0);
		}
		
		if((y>=1800) && (y<=1997)){
			
			t=(y-1900.0)/100.0;
			
			if(y<=1899){
				periodicTerms=new double[]{-2.50, 228.95, 5218.61, 56282.84, 324011.78,
										1061660.75, 2087298.89, 2513807.78,
										1818961.41, 727058.63, 123563.95};
				this.deltaT = this.calculatePolynomial(t, periodicTerms,0);
			}
			else {
				periodicTerms=new double[]{-2.44, 87.2, 815.20, -2637.80, -18756.33,
									124906.15, -303191.19, 372919.88,
									-232424.66, 58353.42};
				this.deltaT = this.calculatePolynomial(t, periodicTerms,0);
			}
		}
		if(y>=2000 && y<=2100){
			this.deltaT += 0.37 * (y-2100);
		}
		
		//System.out.println("deltaT = "+(int)this.deltaT);;
		
		return this.deltaT;
	}
	
	public double calculatePolynomial(double polynomialParam, double[] params, int index){
		//System.out.println("params len = "+params.length+" val = "+params[index]+" index <"+index);
		if(index<params.length-1) {
			return params[index]+(polynomialParam*this.calculatePolynomial(polynomialParam, params, index+1));
		}
		else {
			return params[params.length-1];
		}
	}

public double calculateSun() {
		//Then the geometric mean longitude of the Sun, referred to the mean equinox of
		//the date, is given by
		//Lo = 280.46646 + 36000.76983T + 0.0003032T�
		this.sunMeanLongitude=(280.46646 + 36000.76983*this.T + 0.0003032*this.T*this.T)%360;
		//System.out.println("Sun Mean longitude = "+this.sunMeanLongitude);
		
		//The mean anomaly of the Sun is
		//M = 357.52911 + 35999.05029*T - 0.0001537*T^�
		this.sunMeanAnomaly=357.52911 + (35999.05029*this.T) - (0.0001537*this.T*this.T);
		//System.out.println("Sun Mean Anomaly = "+this.sunMeanAnomaly);
		
		//The eccentricity of the Earth's orbit is
		//e = 0.016708634 - 0.000042037*T - 0.000000 1267*T^�
		this.earthOrbitEccentricity=0.016708634 - 0.000042037*this.T - 0.0000001267*this.T*this.T;
		//System.out.println("Earth orbit eccentricity = "+this.earthOrbitEccentricity);
		
		//Find the Sun's equation of the center C as follows
		//C = + (1.914602 - 0.004817*T - 0.000014*T^�)*sin M
		//+ (0.019993 - O.OOO101*T)*sin 2M
		//+ 0.000289*sin 3M
		this.sunC = (1.914602 - 0.004817*this.T - 0.000014*this.T*this.T)*Math.sin(Math.toRadians(this.sunMeanAnomaly))
				+ (0.019993 - 0.000101*this.T)*Math.sin(Math.toRadians(2*this.sunMeanAnomaly))
				+ 0.000289*Math.sin(Math.toRadians(3*this.sunMeanAnomaly));
		//System.out.println("Center of the sun = "+this.sunC);
		
		//Then the Sun's true longitude is
		//and its true anomaly is
		this.sunTrueLongitude=this.sunMeanLongitude + this.sunC;
		//System.out.println("Sun true longitude = "+this.sunTrueLongitude);
		this.sunTrueAnomaly = this.sunMeanLongitude + this.sunC;
		//System.out.println("Sun true anomaly = "+this.sunTrueAnomaly);
		
		//The Sun's radius vector, or the distance between the centers of the Sun and the
		//Earth, expressed in astronomical units, is given by
		//R = [1.000001018*(1 - e^�)]/(1 + e*cos v)
		this.sunRadiusVector=1.000001018*(1 - this.earthOrbitEccentricity*this.earthOrbitEccentricity)/(1 + this.earthOrbitEccentricity*Math.cos(Math.toRadians(this.sunTrueAnomaly)));
		//System.out.println("Sun radius vector (astronomical units) = "+this.sunRadiusVector);
		
		//The mean obliquity of the ecliptic is given by the following formula, adopted
		//by the International Astronomical Union [1]:
		//eo = 23�26'21".448 - 46".8150*T - 0".00059*T^� + 0".001813*T^�
		this.eclipticMeanObliquity=(23+(26/60.0)+(21.448/3600.0))
									-((46.8150/3600.0)*this.T)
									-((0.00059/3600.0)*this.T*this.T)
									+((0.001813/3600.0)*this.T*this.T*this.T);
		//System.out.println("Mean obliquity of ecliptic = "+this.eclipticMeanObliquity);
		
		//Sun's right ascension ex and declination 0 can be calculated from the following
		//expressions where e, the obliquity of the ecliptic, is given by (22.2).
		this.sunRightAscension=Math.toDegrees(Math.atan2(Math.cos(Math.toRadians(this.eclipticMeanObliquity))* Math.sin(Math.toRadians(this.sunTrueLongitude)), Math.cos(Math.toRadians(this.sunTrueLongitude))));
		this.sunDeclination=Math.toDegrees(Math.asin(Math.sin(Math.toRadians(this.eclipticMeanObliquity))*Math.sin(Math.toRadians(this.sunTrueLongitude))));
		double h=((this.sunRightAscension%360.0)+360.0)/15.0,
				m=(h-Math.floor(h))*60.0,
				s=(m-Math.floor(m))*60.0;
		//System.out.println("sun right ascension = "+(int)h+":"+(int)m+":"+s);
		//System.out.println("sun declination = "+this.sunDeclination);
		
		//Taille du Soleil (zoom fort et �clipses)
		this.sunSize=this.sunDeclination+0.25;
		
		//If the apparent longitude A of the Sun, referred to the true equinox of the date,
		//is required, 0 should be corrected for the nutation and the aberration. Unless high
		//accuracy is required, this can be performed as follows.
		//Omega = 125.04 - 1934.136*T
		//lambda = true longitude - 0.00569 - 0.00478 sin Omega
		this.omega=125.04 - 1934.136*this.T;
		this.sunApparentLongitude=this.sunTrueLongitude - 0.00569 - (0.00478*Math.sin(Math.toRadians(this.omega)));
		//System.out.println("Omega = "+this.omega);
		//System.out.println("Sun apparent longitude = "+this.sunApparentLongitude);
		
		//Alternatively, the equation of time can be obtained, with somewhat less
		//accuracy, by means of the following formula given by Smart [1]:
		//E = [ y*sin (2Lo) ] - [ 2e sin M ] + [ 4ey sin M cos (2Lo) ]
		//-[1/2*( y� sin 4Lo)] - [ 5/4 e^� sin (2M)]
		//where
		//y = tan^� (ee/2), ee being the obliquity of the ecliptic,
		//Lo Sun's mean longitude,
		//e eccentricity of the Earth's orbit,
		//M Sun's mean anomaly
		double y=Math.tan(Math.toRadians(this.eclipticMeanObliquity/2.0));
		y*=y;
		this.eot=(y*Math.sin(Math.toRadians(2.0*this.sunMeanLongitude))
				-(2.0*this.earthOrbitEccentricity*Math.sin(Math.toRadians(this.sunMeanAnomaly)))
				+(4.0*this.earthOrbitEccentricity*y*Math.sin(Math.toRadians(this.sunMeanAnomaly))*Math.cos(Math.toRadians(2.0*this.sunMeanLongitude)))
				-(0.5*y*y*Math.sin(Math.toRadians(4.0*this.sunMeanLongitude)))
				-((5.0/4.0)*(this.earthOrbitEccentricity*this.earthOrbitEccentricity)*Math.sin(Math.toRadians(2.0*this.sunMeanAnomaly)))
				);
		this.eot=Math.toDegrees(this.eot);
		//System.out.println("Equation of Time (deg decimal) = "+this.eot);
		//double hh=this.eot/15.0;
		
		/*
		//TODO attention signe ! mais valeur eot correcte :-) 
		h=this.eot/15.0;
		m=(h-(int)(h))*60.0;
		s=(m-(int)(m))*60.0;
		//System.out.println("Equation of Time = "+(int)m+" min "+s+" sec");	
		
		//correction de temps en longitude
		h=(astro.longitude-(fuseau*15.0))/15.0;
		m=(h-(int)(h))*60.0;
		s=(m-(int)(m))*60.0;
		//System.out.println("h= "+h+" m="+m+" s="+s);

		//System.out.println("Correction en longitude = "+(int)h+" heure "+(int)m+" min "+s+" sec");
		*/
		
		//temps solaire local
		/*
		if(this.ldt.getZone().getRules().isDaylightSavings(this.ldt.toInstant())) {
			h=this.fuseau+1.0;
		}
		else
		*/
			h=this.fuseau;
			
		//System.out.println("Correction longitude et EdT ="+this.angleDecimalToHMS(h));
		h-=(astro.coordGeo.longitude+this.eot)/15.0;
		//System.out.println("Correction longitude et EdT ="+this.angleDecimalToHMS(h));
		m=(h-Math.floor(h))*60.0;
		s=((m-Math.floor(m))*60.0);
		//this.hsl=LocalDateTime.now().minusHours((long)h).minusMinutes((long)m).minusSeconds((long)s);
		this.hsl=this.ldt.minusHours((long)h).minusMinutes((long)m).minusSeconds((long)s);
		//this.hsl=this.hsl.plusSeconds((long)this.deltaT);
		//System.out.println("Heure solaire locale = "+ldt.now().minusHours((long)h).minusMinutes((long)m).minusSeconds((long)s));
		
		//Calculation of the local horizontal coordinates:
		//tan A = sin H / [	cos H sin lat - tan dec cos lat ]
		//sin h = sin lat sin dec + cos lat cos dec cos H
		//System.out.println("Temps sideral = "+this.localSiderealTime/15.0+" soleil AR = "+(this.sunRightAscension)/15.0);
		this.H=Math.abs(this.localSiderealTime-(this.sunRightAscension));
		
		//TODO atan
		/*
		this.sunAzimut=Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(this.H+180.0))
				,(Math.cos(Math.toRadians(this.H+180.0))*Math.sin(Math.toRadians(astro.latitude))
						-Math.tan(Math.toRadians(this.sunDeclination))*Math.cos(Math.toRadians(astro.latitude))
						)));
		if(this.sunAzimut<0.0) this.sunAzimut+=360.0;
		//this.sunAzimut=this.sunAzimut);
		//System.out.println("Angle horaire = "+this.H/15.0);
		//System.out.println("Azimut Soleil = "+this.sunAzimut);
		this.sunHeight=Math.asin((Math.sin(Math.toRadians(astro.latitude))*Math.sin(Math.toRadians(this.sunDeclination))
				+Math.cos(Math.toRadians(astro.latitude))*Math.cos(Math.toRadians(this.sunDeclination))*Math.cos(Math.toRadians(this.H))));
		this.sunHeight=Math.toDegrees(this.sunHeight);
		//System.out.println("Soleil hauteur = "+this.sunHeight);
		*/
		this.sunAzimut=this.calculateAzimutHorizontalFromEquatorial(this.sunRightAscension, this.sunDeclination);
		this.sunHeight=this.calculateHauteurHorizontalFromEquatorial(this.sunRightAscension, this.sunDeclination);
		return this.sunMeanLongitude;
	}

	public void calculateLocalSiderealTime(double jd) {
		double theta0, hs, ms, ss;
		
		this.T=(this.jd-2451545.0)/36525.0;
		
		//System.out.println("T="+T);
		theta0=(280.46061837 + (360.98564736629 * (this.jd - 2451545.0))+ (0.000387933*T*T) - (T*T*T/38710000))%360;
		//System.out.println("theta0="+theta0);
		
		//TODO : rajout +360 : probl�me avec certaines ann�es (ex:1969 => heures n�gatives...???
		this.localSiderealTime=((theta0+=astro.coordGeo.longitude)+360)%360;
		
		this.utSiderealtime=(this.localSiderealTime-this.astro.coordGeo.longitude)%360.0;
		//System.out.println("localsiderealtime = "+this.localSiderealTime+" UT sidereal Time = "+this.utSiderealtime);
		
		hs=Math.floor(((this.localSiderealTime)/15.0));
		ms=Math.floor((this.localSiderealTime/15.0-hs)*60.0);
		ss=(((this.localSiderealTime/15.0-hs)*60.0)-ms)*60.0;
		//System.out.println("sidereal time = "+ this.localSiderealTime);
		//System.out.println("hs="+(int)hs+" ms="+(int)ms+" ss="+(int)ss);
		try {
			this.st=LocalTime.of((int)hs, (int)ms, (int)ss);
		}
		catch(DateTimeException dte){
			System.out.println("Calculs - calculLocalSiderealTime - Erreur de date/heure : "+dte.toString());
		}
	}
	
	public void calculateJulianDay(/*int year, int month, int day, int hour, int min, int sec*/) {
		int a, b, year, month, day, hour, min, sec;
		double dayfrac;
		
/*
 		System.out.println(ldt.getYear());
		System.out.println(ldt.getMonth().getValue());
		System.out.println(ldt.getDayOfMonth());
		System.out.println(ldt.getHour());
		System.out.println(ldt.getMinute());
		System.out.println(ldt.getSecond());
*/
		//heure GMT
		//this.ut=ZonedDateTime.now(ZoneId.of("UT"));
		this.ut=ZonedDateTime.ofInstant(this.ldt.toInstant(), ZoneId.of("UT"));
		//System.out.println(ut.toString());

		//if(!this.astro.anim.manual.isSelected())
			//this.ldt = java.time.LocalDateTime.now();
		
		//TODO probl�me d�calage heure locale et UT
		/*
		year=this.ldt.getYear();
		month=this.ldt.getMonthValue();
		day=this.ldt.getDayOfMonth();
		*/
		//System.out.println("heure locale="+ldt.getHour());
		year=this.ut.getYear();
		month=this.ut.getMonthValue();
		day=this.ut.getDayOfMonth();
		

		//heure GMT
		hour=this.ut.getHour();
		//if(dst)hour+=1.0;
		/*
		if(this.dst)
			hour=(this.ldt.getHour()-2)%24;
		else {
			hour=(this.ldt.getHour()-1)%24;
		}
		*/
		//System.out.println("heure GMT="+hour);
		//TODO probl�me d�calage heure locale et UT
		/*
	 	min=this.ldt.getMinute();
		sec=this.ldt.getSecond();		
		 */
	 	min=this.ut.getMinute();
		sec=this.ut.getSecond();		

		
		//test : 1957 October 4.81, sputnik 1 launch time :-)
		//Julian day should be : 2436116.31
		/*
		year=1957;
		month=10;
		dayfrac=4.81;
		*/
		
		
		//TODO deltaT OK ??
		dayfrac=(day+0.0)+(hour/24.0)+(min/24.0/60.0)+((sec)/24.0/3600.0);
		if((month == 1) || (month ==2)){
			year=year-1;
			month=month+12;
		}
		
		a=year/100;
		if(year<1582){
			b=0;
		}
		else {
			b=2-a+(a/4);
		}
		
		/*
		System.out.println("year = " + year);
		System.out.println("month = " + month);
		System.out.println("day = " + dayfrac);
		System.out.println("a = " + a);
		System.out.println("b = " + b);
		System.out.println("jd = " + jd);
*/	
		//System.out.println("Julianb day = " + ((int)(365.25*(year+4716.0))+(int)(30.6001*(month+1.0))+dayfrac+b-1524.5));
		this.jd=(int)(365.25*(year+4716))+(int)(30.6001*(month+1))+dayfrac+b-1524.5;
		
		//The Modified Julian Day (MID) sometimes appears in modem work, for
		//instance when mentioning orbital elements of artificial satellites. Contrary to the JD,
		//the Modified Julian Day begins at Greenwich mean midnight.
		//mjd initial => 1858 17 November at 0h UT
		this.mjd=this.jd-240000.5;
		
		//...where JDE is the Julian Ephemeris Day;
		//it differs from the Julian Day (JD) by the
		//small quantity deltaT (Chapitre 22, p.143,
		//"Nutation and the Obliquity of the Ecliptic"
		this.jde=this.jd+(this.deltaT/24.0/3600.0);
		//System.out.println("JD = "+this.jd+" JDE="+this.jde);
		
		calculateLocalSiderealTime(this.jd);
}
	
	public double calculateEcliptic() {
		
		//Nutation
		
		//mean longitudes of the Sun and the Moon
		this.sunMeanLongitude = 280.4665 + 36000.7698*this.T;
		this.sunMeanLongitude = ((this.sunMeanLongitude%360.0)+360.0)%360.0;
		//System.out.println("sunMeanLongitude = "+this.sunMeanLongitude);
		
		//nutation in longitude and obliquity (in arc seconds !)
		this.nutationLongitude = (-17.20*Math.sin(Math.toRadians(this.astro.moon.moonLongitudeAscendignNode)))- (1.32*Math.sin(Math.toRadians(2*this.sunMeanLongitude))) - (0.23*Math.sin(Math.toRadians(2*this.astro.moon.moonMeanLongitude))) + (0.21*Math.sin(Math.toRadians(2*this.astro.moon.moonLongitudeAscendignNode)));
		this.nutationObliquity = (9.20*Math.cos(Math.toRadians(this.astro.moon.moonLongitudeAscendignNode))) + (0.57*Math.cos(Math.toRadians(2*this.sunMeanLongitude))) + (0.10*Math.cos(Math.toRadians(2*this.astro.moon.moonMeanLongitude))) - (0.09*Math.cos(Math.toRadians(2*this.astro.moon.moonLongitudeAscendignNode)));
		//System.out.println("nutationLongitude = "+this.nutationLongitude);
		//System.out.println("nutationObliquity = "+this.nutationObliquity);
		
		
		//Obliquity
		
		//The mean obliquity of the ecliptic is given by the following formula, adopted
		//by the International Astronomical Union [1]
		this.eclipticMeanObliquity=23.0+(26.0/60.0);
		this.periodicTerms=new double[]{21.448, -46.8150, -0.00059, 0.001813};
		this.eclipticMeanObliquity += this.calculatePolynomial(this.T, this.periodicTerms, 0)/3600.0;
		//System.out.println("eclipticMeanObliquity = "+this.eclipticMeanObliquity);
		
		//The true obliquity of the ecliptic is e = e0 + deltae, where deltae is the nutation in
		//obliquity
		this.eclipticTrueObliquity=this.eclipticMeanObliquity+(this.nutationObliquity/3600.0);
		//System.out.println("eclipticTrueObliquity = "+this.angleDecimalToDMS(this.eclipticTrueObliquity));
		
		return 0.0;
	}
	
	public void calculatePlanets() {
		if(this.ldt.getSecond()%5 == 0)
			Planete.needsRecalculation=true;
		
		//Mercure
		this.astro.planetes[0].calculate();
		//System.out.println(this.astro.planetes[0].nom);

		//Venus
		this.astro.planetes[1].calculate();
		//System.out.println(this.astro.planetes[1].nom);
			
		//Terre
		this.astro.planetes[2].calculate();
		//System.out.println(this.astro.planetes[2].nom);

		//Mars
		this.astro.planetes[3].calculate();
		//System.out.println(this.astro.planetes[3].nom);

		//Jupiter
		this.astro.planetes[4].calculate();
		//System.out.println(this.astro.planetes[4].nom);

		//Saturne
		this.astro.planetes[5].calculate();
		//System.out.println(this.astro.planetes[5].nom);
		
		//Uranus
		this.astro.planetes[6].calculate();
		//System.out.println(this.astro.planetes[6].nom);

		//Neptune
		this.astro.planetes[7].calculate();
		//System.out.println(this.astro.planetes[7].nom);

		Planete.needsRecalculation=false;
	}
	
	public void calculateAll() {
		
		if(this.currentYear != this.astro.calc.ldt.getYear()) {
			this.calculatePrecession(2451545.0, this.jd);
			
			for(int i=0;i<this.astro.face.stars2.length/3;i++) {
				this.stars[3*i]=this.precessionCorrectRA(this.astro.face.stars2[3*i], this.astro.face.stars2[3*i+1]);
				this.stars[3*i+1]=this.precessionCorrectDeclination(this.astro.face.stars2[3*i], this.astro.face.stars2[3*i+1]);
			}
		}
		else this.currentYear=this.astro.calc.ldt.getYear();
		
			this.calculateSun();
			this.calculateEcliptic();
			this.astro.moon.calculateMoon();
			this.calculatePlanets();
	}
	
	/*
	public void moonEclipticalToEquatorial() {
		//Transformation from ecliptical into equatorial coordinates:
		//tan AR=(sin lambda cos e - tan beta sin obliquity) / cos lambda
		//sin declination=	sin beta cos e + cos beta sin e sin lambda
		//TODO atan
		astro.moonRA = Math.atan(
				((Math.sin(Math.toRadians(astro.moonGeoLambda))
					*Math.cos(Math.toRadians(astro.eclipticTrueObliquity)))
					-(Math.tan(Math.toRadians(astro.moonGeoBeta))
					*Math.sin(Math.toRadians(astro.eclipticTrueObliquity)))
				)
				/
				Math.cos(Math.toRadians(astro.moonGeoLambda))	
				);
		astro.moonRA=(Math.toDegrees(astro.moonRA)%360.0)+360.0;
		System.out.println("Moon AR = "+(astro.moonRA+360.0)+" dms = "+this.angleDecimalToDMS(astro.moonRA/15.0));
		
		astro.moonDeclination=Math.asin(
							(Math.sin(Math.toRadians(astro.moonGeoBeta))
							*Math.cos(Math.toRadians(astro.eclipticTrueObliquity)))
							+
							(Math.cos(Math.toRadians(astro.moonGeoBeta))
							*Math.sin(Math.toRadians(astro.eclipticTrueObliquity))
							*Math.sin(Math.toRadians(astro.moonGeoLambda)))
					);
		astro.moonDeclination=Math.toDegrees(astro.moonDeclination)+0.0;
		System.out.println("Moon declination = "+astro.moonDeclination+" dms = "+this.angleDecimalToDMS(astro.moonDeclination));
	}
	*/
	
	public double calculatePrecession(double startEpoch, double finalEpoch) {
		//Chapter21, p.133
		// "J" => Julian year
		
		//!!! Before making the reduction from inital to 
		//final equatorial coordinates due to precession,
		//the effect of the star's proper motion should be calculated !!!
		
		//The International Astronomical Union has decided that from 1984 onwards the
		//astronomical ephemerides should use the following system
		
		//In this Chapter, we consider the problem of converting 
		//the right ascension and the declination of a star,
		//given for an epoch and an equinox, to the corresponding
		//values for another epoch and equinox
		//
		//Only the mean place of a star, and hence the
		//effects of the precession and proper motion,
		//will be considered here
		//The problem of finding the apparent place of a star
		//will be considered in Chapter 23
		//
		//precessionZeta, precessionZ, precessionTheta => exprim�s en secondes d'angle !
		
		double T, t;
		
		T=(startEpoch - 2451545.00)/36525.0;////T -> from J2000.0 = JDE 2451545.00 exactly
		t=(finalEpoch - startEpoch)/36525;
		
		this.precessionZeta  = t*(2306.2181 + 1.39656*T - 0.000139*T*T);
		this.precessionZeta +=	t*t*(0.30188 - 0.000344*T);
		this.precessionZeta += t*t*t*0.017998;
		
		this.precessionZ  = t*(2306.2181 + 1.39656*T - 0.000139*T*T);
		this.precessionZ += t*t*(1.09468 + 0.000066*T);
		this.precessionZ += t*t*t*0.018203;
		
		this.precessionTheta  = t*(2004.3109 - 0.85330*T - 0.000217*T*T);
		this.precessionTheta -= t*t*(0.42665 + 0.000217*T);
		this.precessionTheta -= t*t*t*0.041833;
		
		/*
		 System.out.println("Precession de "+startEpoch+" vers "+finalEpoch
				+" : Zeta="+this.precessionZeta
				+" z=+"+this.precessionZ
				+" theta="+this.precessionTheta);
		 */
		
		return 0.0;
	}
	
	public double precessionCorrectRA (double ra, double declination) {
		double A, B;
		
		A=Math.cos(Math.toRadians(declination)) *
				Math.sin(Math.toRadians(ra+(this.precessionZeta/3600.0)));
		
		B=	(Math.cos(Math.toRadians(this.precessionTheta/3600.0)) *
			 Math.cos(Math.toRadians(declination)) *
			 Math.cos(Math.toRadians(ra+(this.precessionZeta/3600.0)))
			 )
			 -
			 (Math.sin(Math.toRadians(this.precessionTheta/3600.0)) *
				Math.sin(Math.toRadians(declination))
			);
		
		//System.out.println("Correction RA ="+Math.toDegrees(Math.atan2(A, B))+(this.precessionZ/3600.0));
		return Math.toDegrees(Math.atan2(A, B))/*+(this.precessionZ/3600.0)*/;
	}
	
	public double precessionCorrectDeclination (double ra, double declination) {
		double C;
		
		C=(Math.sin(Math.toRadians(this.precessionTheta/3600.0)) *
			 Math.cos(Math.toRadians(declination)) *
			 Math.cos(Math.toRadians(ra+(this.precessionZeta/3600.0)))
			 )
			 +
			 (Math.cos(Math.toRadians(this.precessionTheta/3600.0)) *
			  Math.sin(Math.toRadians(declination))
			);
		
		return Math.toDegrees(Math.asin(C));
	}
	
	public String angleDecimalToDMS(double angle) {
		int d, m;
		double temp, s;
		
		temp=Math.abs(angle);
		d=(int)Math.floor(temp);
		temp=(temp-d)*60.0;
		m=(int)Math.floor(temp);
		temp=(temp-m)*60.0;
		s=temp;
		
		if(angle>=0)
			return new String(d+"° "+m+" ' "+(int)s+" ''");
		else
			return new String("-"+d+"° "+m+" ' "+(int)s+" ''");
	}
	
	public String angleDecimalToHMS(double angle) {
		int d, m, s;
		double temp;
		
		temp=Math.abs(angle);
		d=(int)Math.floor(temp);
		temp=(temp-d)*60.0;
		m=(int)Math.floor(temp);
		temp=(temp-m)*60.0;
		s=(int)temp;
		
		if(angle>=0)
			return new String(d+" h "+m+" m "+s+" s ");
		else
			return new String("-"+d+" h "+m+" m "+s+" s");
	}

	public double getGeocentricXCoordinate(double lambda, double beta, double radius) {
		return (radius
				*Math.cos(Math.toRadians(beta))
				*Math.cos(Math.toRadians(lambda))
				)
				-
				(this.astro.planetes[2].helioRadius//.earthRadius
						*Math.cos(Math.toRadians(this.astro.planetes[2].helioBeta))//earthBeta))
						*Math.cos(Math.toRadians(this.astro.planetes[2].helioLambda)));//.earthLambda)));
	}
	
	public double getGeocentricYCoordinate(double lambda, double beta, double radius) {
		return (radius
				*Math.cos(Math.toRadians(beta))
				*Math.sin(Math.toRadians(lambda))
				)
				-
				(this.astro.planetes[2].helioRadius//.earthRadius
						*Math.cos(Math.toRadians(this.astro.planetes[2].helioBeta))//.earthBeta))
						*Math.sin(Math.toRadians(this.astro.planetes[2].helioLambda)));//.earthLambda)));
	}
	
	public double getGeocentricZCoordinate(double lambda, double beta, double radius) {
		return (radius
				*Math.sin(Math.toRadians(beta))
				)
				-
				(this.astro.planetes[2].helioRadius//.earthRadius
						*Math.cos(Math.toRadians(this.astro.planetes[2].helioBeta))//.earthBeta))
					);
	}
	
	public double calculateGeocentricLongitudeFromGeoXYZ(double x, double y, double z) {
		return Math.atan2(y, x);
	}
	
	public double calculateGeocentricLatitudeFromGeoXYZ(double x, double y, double z) {
		//TODO atan
		return Math.atan(z/Math.sqrt(x*x+y*y));		
	}

	public double calculateHelioToGeoLongitude(double lambda, double beta, double radius) {
		double x, y;
		
		//Ro, Bo et Lo => coordonn�es h�liocentriques de la Terre
		//x = R cos B cos L - Ro cos Bo cos Lo
		//y = R cos B sin L - Ro cos Bo sin Lo
		//z = R sin B       - Ro sin Bo 
				
		x=(radius
			*Math.cos(Math.toRadians(beta))
			*Math.cos(Math.toRadians(lambda))
			)
			-
			(this.astro.planetes[2].helioRadius//.earthRadius
					*Math.cos(Math.toRadians(this.astro.planetes[2].helioBeta))//.earthBeta))
					*Math.cos(Math.toRadians(this.astro.planetes[2].helioLambda)));// .earthLambda)));
		
		y=(radius
				*Math.cos(Math.toRadians(beta))
				*Math.sin(Math.toRadians(lambda))
				)
				-
				(this.astro.planetes[2].helioRadius//.earthRadius
						*Math.cos(Math.toRadians(this.astro.planetes[2].helioBeta))//earthBeta))
						*Math.sin(Math.toRadians(this.astro.planetes[2].helioLambda)));//earthLambda)));

		return Math.atan2(y, x);
	}

	public double calculateHelioToGeoLatitude(double lambda, double beta, double radius) {
double x, y, z;
		
		//Ro, Bo et Lo => coordonn�es h�liocentriques de la Terre
		//x = R cos B cos L - Ro cos Bo cos Lo
		//y = R cos B sin L - Ro cos Bo sin Lo
		//z = R sin B       - Ro sin Bo 
				
		x=(radius
			*Math.cos(Math.toRadians(beta))
			*Math.cos(Math.toRadians(lambda))
			)
			-
			(this.astro.planetes[2].helioRadius
					*Math.cos(Math.toRadians(this.astro.planetes[2].helioBeta))
					*Math.cos(Math.toRadians(this.astro.planetes[2].helioLambda)));
		
		y=(radius
				*Math.cos(Math.toRadians(beta))
				*Math.sin(Math.toRadians(lambda))
				)
				-
				(this.astro.planetes[2].helioRadius
						*Math.cos(Math.toRadians(this.astro.planetes[2].helioBeta))
						*Math.sin(Math.toRadians(this.astro.planetes[2].helioLambda)));
		
		z=(radius
				*Math.sin(Math.toRadians(beta))
				)
				-
				(this.astro.planetes[2].helioRadius
						*Math.sin(Math.toRadians(this.astro.planetes[2].helioBeta))
					);
		//TODO atan	
		return Math.atan(z/Math.sqrt(x*x+y*y));		
	}
	
	public double calculateEclipticalToRA(double lambda, double beta) {
		double temp;
		//TODO atan
		temp = Math.atan(
				((Math.sin(Math.toRadians(lambda))
					*Math.cos(Math.toRadians(this.eclipticTrueObliquity)))
					-(Math.tan(Math.toRadians(beta))
					*Math.sin(Math.toRadians(this.eclipticTrueObliquity)))
				)
				/
				Math.cos(Math.toRadians(lambda))	
				);
		
		//TODO atan RA
		if(Math.cos(Math.toRadians(lambda))<0.0)
			return (Math.toDegrees(temp)+180.0)%360.0;
		else
			return (Math.toDegrees(temp)+360)%360.0;
	}

	public double calculateEclipticalToDeclination(double lambda, double beta) {
		double temp;
		
		temp=Math.asin(
				(Math.sin(Math.toRadians(beta))
				*Math.cos(Math.toRadians(this.eclipticTrueObliquity)))
				+
				(Math.cos(Math.toRadians(beta))
				*Math.sin(Math.toRadians(this.eclipticTrueObliquity))
				*Math.sin(Math.toRadians(lambda)))
		);
		return Math.toDegrees(temp);
	}
	
	public double angleDegreGeocentriqueVersEcliptique (double angle, double r, double r2) {
		System.out.println("geo long="+angle+" ecli long="+Math.toDegrees(Math.atan2(r*Math.sin(Math.toRadians(angle))+Math.abs(r-r2), r*Math.cos(Math.toRadians(angle)))));
		return Math.toDegrees(Math.atan2(r*Math.sin(Math.toRadians(angle))+Math.abs(r-r2), r*Math.cos(Math.toRadians(angle))));
	}
	
	public double calculateAngleHoraireFromAzimutal(double azimuth, double elevation) {
		double H= Math.atan2(
						( Math.sin(Math.toRadians(azimuth+180.0)))
						,
						(
						(Math.cos(Math.toRadians(azimuth+180.0)) * Math.sin(Math.toRadians(this.astro.coordGeo.latitude)))
						+
						(Math.tan(Math.toRadians(elevation)) * Math.cos(Math.toRadians(this.astro.coordGeo.latitude)))
						)
						);
		return this.astro.calc.localSiderealTime-Math.toDegrees(H);
	}
	
	public double calculateDeclinaisonFromAzimutal(double azimuth, double elevation) {
		double declination=Math.asin(
										(
										Math.sin(Math.toRadians(this.astro.coordGeo.latitude))
										*
										Math.sin(Math.toRadians(elevation))
										)
									-
										(
										Math.cos(Math.toRadians(this.astro.coordGeo.latitude))
										*
										Math.cos(Math.toRadians(elevation))
										*
										Math.cos(Math.toRadians(azimuth+180.0))
										)
									);
		return Math.toDegrees(declination);		
	}
	
	public boolean produitMatrices(double[][] m1, double[][] m2, double[][] result) {
		//"result" est une matrice ZERO qui doit �tre cr��e par l'appelant
		
		if(m1==null || m2==null || result==null)
			return false;
		
		//Compatibilit� des matrices
		if(		(result.length != m1.length)//lignes result = lignes m1
			|| 	(result[0].length != m2[0].length)//colonnes result = colonnes m2
			||	(m1[0].length != m2.length)//colonnes m1 = lignes m2
			)
			return false;
		
		for(int i=0; i<m1.length; i++)
			for(int j=0; j<m2[0].length; j++) {
				result[i][j]=0.0;
				for(int k=0; k<m1[0].length; k++)
					result[i][j] += m1[i][k] * m2[k][j];
			}
		
		return true;
	}

	public boolean transposeMatrice(double[][] m, double[][] result) {
		//"result" est une matrice qui doit �tre cr��e par l'appelant
		
		if(m==null || result==null)
			return false;
		
		//Compatibilit� des matrices
		if(		(result.length != m[0].length)//lignes result = colonnes m1
			|| 	(result[0].length != m.length)//colonnes result = lignes m1
			)
			return false;
		
		//boolean matriceCarree = (m.length == m[0].length);
		
		for(int i=0; i<m.length; i++)
			for(int j=0; j<m[0].length; j++) {
				
				//if(matriceCarree && (i == j)) break;

				result[j][i] = m[i][j];
			}		
		
		return true;
	}
	
	public boolean matriceZero(double[][] m) {
		
		if(m==null)
			return false;
		
		for(int i=0; i<m.length; i++)
			for(int j=0; j<m[0].length; j++)
				m[i][j]=0.0;
		
		return true;
		
	}
	
	public void gaussJordan(double[][] A) {
		// print array A to output file
		//double[][] toprint=A;
	      //System.out.println("Gauss : DEBUT");

	      //this.printMatrice(toprint);
	      int n=A.length;
	      int m=A[0].length;
	      
	      // perform Gauss-Jordan Elimination algorithm
	      int i = 0;
	      int j = 0;
	      while( i<n && j<m ){

	         //look for a non-zero entry in col j at or below row i
	         int k = i;
	         while( k<=n && A[k][j]==0 ) k++;

	         // if such an entry is found at row k
	         if( k<=n ){

	            //  if k is not i, then swap row i with row k
	            if( k!=i ) {
	               this.swap(A, i, k, j/*, A_1*/);
	     	      //this.printMatrice(toprint);
            }

	            // if A[i][j] is not 1, then divide row i by A[i][j]
	            if( A[i][j]!=1 ){
	               this.divide(A, i, j/*, A_1*/);
	     	      //this.printMatrice(toprint);
	            }

	            // eliminate all other non-zero entries from col j by subtracting from each
	            // row (other than i) an appropriate multiple of row i
	            this.eliminate(A, i, j/*, A_1*/);
	  	      //this.printMatrice(toprint);
	            i++;
	         }
	         j++;
	      }
	      //System.out.println("Gauss : FIN");
	}
	
	   // divide()
	   // divide row i by A[i][j]
	   // pre: A[i][j]!=0, A[i][q]==0 for 1<=q<j
	   // post: A[i][j]==1;
	   public void divide(double[][] A, int i, int j){
	      int m = A[0].length;

	    //System.out.println("divide...");
	    for(int q=j; q<m; q++) {
	    //System.out.println("A[i][j]="+A[i][j]);
	    	if(q!=j) A[i][q] /= A[i][j];
	      }
	    A[i][j] = 1;
	   }

	
	   // eliminate()
	   // subtract an appropriate multiple of row i from every other row
	   // pre: A[i][j]==1, A[i][q]==0 for 1<=q<j
	   // post: A[p][j]==0 for p!=i
	   public void eliminate(double[][] A, int i, int j){
	      int n = A.length;
	      int m = A[0].length;
          
	      //System.out.println("eliminate...");
          for(int p=0; p<n; p++){
	         if( p!=i && A[p][j]!=0 ){
	            for(int q=j; q<m; q++){
	               if(q!=j) A[p][q] -= A[p][j]*A[i][q];
	            }
	            A[p][j] = 0;
	         }
	      }
	   }
	
	      
	   // swap()
	   // swap row i with row k
	   // pre: A[i][q]==A[k][q]==0 for 1<=q<j
	   public void swap(double[][] A, int i, int k, int j){
	      int m = A[0].length;
	      double temp;//, tempB;

	      //System.out.println("swap...");

	      for(int q=j; q<m; q++){
	    	  temp = A[i][q];
	         A[i][q] = A[k][q];
	         A[k][q] = temp;
	         
	      }
	   }

	
	public boolean produitScalaireMatrice(double[][] m, double scalaire) {
		System.out.println("lig*col="+m.length+"*"+m[0].length);
		for(int i=0;i<m.length;i++)
			for(int j=0;j<m[0].length;j++)
				m[i][j] *= scalaire;
		return true;
	}
	
	public boolean printMatrice(double[][] m) {
		if(m != null) {
			System.out.println();
			for(int i=0;i<m.length;i++) {
				System.out.println();
				for(int j=0;j<m[0].length; j++)
					System.out.print(" "+m[i][j]);
			}
			System.out.println();
		}
		else return false;
		
		return true;
	}
	
	public void augmenterMatrice(double[][] m, double[][] augmentM) {
		for(int i=0; i<4; i++)
			for(int j=0;j<4;j++)
				augmentM[i][j]=m[i][j];
	}
}
