package astrolabe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.net.URISyntaxException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.nio.file.Files;
import java.nio.file.Path;
//import java.nio.file.Paths;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

import javafx.geometry.Point3D;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class Gps {

static double
	SECONDS_IN_WEEK=604800.0,
	LIGHTSPEED=299792458.0,
	
	GPS_FREQUENCYL1=1575.42E6,
	GPS_FREQUENCYL2=1227.60E6,
	GPS_WAVELENGTHL1=0.19029367279836488047631742646405,
	GPS_WAVELENGTHL2=0.24421021342456825,

	GPS_CLOCK_CORRECTION_RELATIVISTIC_CONSTANT_F=-4.7633E-10,//!< combined constant defined in ICD-GPS-200C p. 88     [s]/[sqrt(m)]
	GPS_UNIVERSAL_GRAVITY_CONSTANT=3.986005E14,       //!< gravity constant defined on ICD-GPS-200C p. 98      [m^3/s^2]
	GPS_RATIO_OF_SQUARED_FREQUENCIES_L1_OVER_L2=1.4, //!< (f_L1/f_L2)^2 = (1575.42/1227.6)^2 = (77/60)^2
	GPS_WGS84_EARTH_ROTATION_RATE=7.2921151467E-5,  //!< constant defined on ICD-GPS-200C p. 98            [rad/s]
	
	TWO_TO_THE_POWER_OF_55=68.0,
	TWO_TO_THE_POWER_OF_43=208.0,
	TWO_TO_THE_POWER_OF_33=.0,
	TWO_TO_THE_POWER_OF_31=.0,
	TWO_TO_THE_POWER_OF_29=0912.0,
	TWO_TO_THE_POWER_OF_19=8.0;

static double ground_stations[]={
	//Latitude, longitude, hauteur par rapport à l'ellipsoïde WGS84 (époque 2005)
	38.80293817,255.47540411,1911.778
	,-7.95132931,345.58786964,106.281
	,-7.26984216,72.37092367,-64.371
	,8.72250188,167.73052378,39.652
	,21.56149239,201.76066695,425.789
	,28.48373823,279.42769502,-24.083
	,-34.72897999,138.64736789,34.955
	,-34.91359039,303.82369872,40.720
	,51.11761208,359.09487379,139.647
	,26.20914126,50.60814545,-13.857
	,-0.21515709,281.50639195,2922.453
	,38.92056511,282.93368418,59.003
	,64.68789166,212.88698460,177.236
	,-41.57619313,173.74075198,147.227
	,-25.74634537,28.22403818,1416.334
	,37.07756761,127.02403352,51.755
	,-17.57702921,210.39381226,99.836
};

	Astrolabe astro;
	SatelliteGPS[] satellitesGPS;
	double[][] G, transposeeG, produitGtG, Q, inverse;
	double GDOP, PDOP;
	Path file;
	
	public Gps() {
	  String gpsurl = null, gpsmimetype = null;
	  
	  try (InputStream input = new FileInputStream("application.properties")) {
	    Properties prop = new Properties();
	      prop.load(input);
	      gpsurl = new String(prop.getProperty("gps-url"));
	      gpsmimetype = new String(prop.getProperty("gps-mimetype"));
//	      System.out.println(prop.getProperty("gps-url"));
//        System.out.println(gpsurl);
//        System.out.println(mimetype);
	      input.close();
	  } catch (IOException ex) {
	      ex.printStackTrace();
	  }
	  
	  
		//Lire l'almanach des satellites GPS
		//et créer les 32 satellites GPS
		//fichier YUMA : ".\current.alm"
		//source internet : http://www.navcen.uscg.gov/?pageName=gpsAlmanacs
		//file URI = http://www.navcen.uscg.gov/?pageName=currentAlmanac&format=yuma
	  
    System.out.println("Création connexion vers site almanach GPS...");
	  try {
	    //14/02/2019 : navcen.uscg.gov KO => utiliser : https://celestrak.com/GPS/almanac/Yuma/almanac.yuma.txt
    	//URL url = new URL("https://navcen.uscg.gov/?pageName=currentAlmanac&format=yuma");//obsolète
	    //URL url = new URL("https://navcen.uscg.gov/sites/default/files/gps/almanac/current_yuma.alm");//application/octet-stream
		  //URL url = new URL("https://celestrak.com/GPS/almanac/Yuma/almanac.yuma.txt");//text/plain
//	    System.out.println(gpsurl);
//	    System.out.println(gpsmimetype);
	    
		  URL url = new URL(gpsurl);
          HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
	        System.out.println("httpConn code réponse : " + httpConn.getResponseCode());
	        System.out.println("GPS - httpConn - content type : " + httpConn.getContentType());
	        
	        if( (httpConn.getResponseCode() == 200) 
	          && (httpConn.getContentType().compareTo(gpsmimetype) == 0) )
            //&& (httpConn.getContentType().compareTo("application/octet-stream") == 0) )
	        {
	            System.out.println("GPS : loadAlmanach()");
	            this.loadAlmanach(httpConn);
	        }
          else {
            System.out.println("Pas reçu un fichier texte ou URL incorrecte :-(");
            System.out.println("URL : " + httpConn.getURL().toExternalForm());
            //System.out.println("Chargement à partir du fichier local...");
            loadAlmanachFromFile();
          }
	        
		//vérification création satellites
		/*
		for (int i=0; i<this.satellitesGPS.length; i++) {
			if(this.satellitesGPS[i] != null)//certains satellites ne sont pas renseign�s dans l'almanach (exemple : satellite GPS n�4)
				System.out.println("Satellite #"+this.satellitesGPS[i].id+" rate Of Right Ascen = "+this.satellitesGPS[i].rateOfRightAscen);
			else
				System.out.println("Satellite #"+i+" non défini dans l'almanach");
		}
		*/
      } 
  	  catch (IOException x) {
          System.err.println(x);
          loadAlmanachFromFile();
      }
	  }
	  
public int loadAlmanachFromFile() {
  File file = new File("almanac.yuma.txt");
  Scanner sc;
  
  //ouverture et lecture du fichier
  try {
    sc = new Scanner(file);
    String line = new String();
    String[] stringTemp;
    int index=0;
    this.satellitesGPS=new SatelliteGPS[32];
    System.out.println("Chargement du dernier alamanach GPS officiel enregistré dans le dossier local...");

    while (sc.hasNextLine()) {
      line = sc.nextLine();
      //System.out.println(line);
      if(!line.isEmpty())
        if(!line.startsWith("*")) {
          //System.out.println("index : "+index);
          stringTemp=line.split(":");
          //System.out.println("paramètre : "+stringTemp[0]+"  valeur : "+stringTemp[1].trim());
          switch(stringTemp[0].trim()){
          case "ID":
            index=Integer.valueOf(stringTemp[1].trim())-1;
            this.satellitesGPS[index]=new SatelliteGPS();
            //System.out.println("Cr�ation sat n� : "+(index+1)+" => "+this.satellitesGPS[index].toString());
            this.satellitesGPS[index].id=index+1;
            break;
          case "Health":
            //System.out.println("Health : "+Integer.valueOf(stringTemp[1].trim()));  
            this.satellitesGPS[index].health=Integer.valueOf(stringTemp[1].trim());
            //System.out.println("Health : "+this.satellitesGPS[index].health);
            break;
          case "Eccentricity":
            this.satellitesGPS[index].eccentricity=Double.valueOf(stringTemp[1].trim());
            //System.out.println("Eccentricity : "+this.satellitesGPS[index].eccentricity);
            break;
          case "Time of Applicability(s)":
            this.satellitesGPS[index].timeOfApplicability=Double.valueOf(stringTemp[1].trim());
            //System.out.println("Time of Applicability(s) : "+this.satellitesGPS[index].timeOfApplicability);
            break;
          case "Orbital Inclination(rad)":
            this.satellitesGPS[index].orbitalInclination=Double.valueOf(stringTemp[1].trim());
            //System.out.println("Orbital Inclination(rad) : "+this.satellitesGPS[index].orbitalInclination);
            break;
          case "Rate of Right Ascen(r/s)":
            this.satellitesGPS[index].rateOfRightAscen=Double.valueOf(stringTemp[1].trim());
            //System.out.println("Rate of Right Ascen(r/s) : "+this.satellitesGPS[index].rateOfRightAscen);
            break;
          case "SQRT(A)  (m 1/2)":
            this.satellitesGPS[index].sqrtA=Double.valueOf(stringTemp[1].trim());
            //System.out.println("SQRT(A)  (m 1/2) : "+this.satellitesGPS[index].sqrtA);
            break;
          case "Right Ascen at Week(rad)":
            this.satellitesGPS[index].rightAscenAtWeek=Double.valueOf(stringTemp[1].trim());
            //System.out.println("Right Ascen at Week(rad) : "+this.satellitesGPS[index].rightAscenAtWeek);
            break;
          case "Argument of Perigee(rad)":
            this.satellitesGPS[index].argumentOfPerigee=Double.valueOf(stringTemp[1].trim());
            //System.out.println("Argument of Perigee(rad) : "+this.satellitesGPS[index].argumentOfPerigee);
            break;
          case "Mean Anom(rad)":
            this.satellitesGPS[index].meanAnom=Double.valueOf(stringTemp[1].trim());
            //System.out.println("Mean Anom(rad) : "+this.satellitesGPS[index].meanAnom);
            break;
          case "Af0(s)":
            this.satellitesGPS[index].af0=Double.valueOf(stringTemp[1].trim());
            //System.out.println("Af0(s) : "+this.satellitesGPS[index].af0);
            break;
          case "Af1(s/s)":
            this.satellitesGPS[index].af1=Double.valueOf(stringTemp[1].trim());
            //System.out.println("Af1(s/s) : "+this.satellitesGPS[index].af1);
            break;
          case "week":
            this.satellitesGPS[index].week=Long.valueOf(stringTemp[1].trim());
            //System.out.println("week : "+this.satellitesGPS[index].week);
            break;
          default: break;
          }
        }
        else; //line empty
    }
  }
  catch(FileNotFoundException e) {
    System.out.println(e.getMessage());
    System.err.println("Chargement almanach KO : pas de fichier local ET problème de connexion internet !");
    System.exit(-1);
  }

  return 0;
}
	  
public int loadAlmanach(HttpURLConnection httpConn) {
	  
  try {
    InputStream in = httpConn.getInputStream();     
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    
    FileWriter fw = new FileWriter("almanac.yuma.txt");//sauvegarde sur disque au cas où internet OK 
         		  
    String line = null;
    this.satellitesGPS=new SatelliteGPS[32];
    System.out.println("Reçu dernier almanach GPS officiel : enregistrement dans le dossier local...");
    while ((line = reader.readLine()) != null) {
      //System.out.println(line);
      fw.write(line+"\n");

      }
    fw.close();
    }
    catch(PatternSyntaxException pse){            
      System.out.println(pse.toString());
      return -1;
    }
    catch(ArrayIndexOutOfBoundsException aioobe) {
      System.out.println(aioobe.toString());
      return -1;
    }
    catch (IOException x) {
      System.err.println(x);
    }
  return loadAlmanachFromFile();
}

public Gps(Astrolabe a) {
	this();
	this.astro=a;
}

public void GPS_ComputeSatellitePositionVelocityAzimuthElevationDoppler_BasedOnAlmanacData(
	CoordonneeGeographique coordGeoUser,
	long 			gpsweek,      //!< user gps week (0-1024+)                                      [week]
	double         	gpstow,       //!< user time of week                                            [s]
	SatelliteGPS satellite
   )
 {
   double tow;        // user time of week adjusted with the clock corrections [s]
   long week; // user week adjusted with the clock correction if needed [week]
 
   int i; // counter
 
   i = (int)satellite.id; // get rid of a debug msg :)
 
   // initialize to zero
   satellite.coordonneeGeographique.coordonneeCartesienne=Point3D.ZERO;
   satellite.coordonneeGeographique.vX=0.0;
   satellite.coordonneeGeographique.vY=0.0;
   satellite.coordonneeGeographique.vZ=0.0;
 
   this.GPS_ComputeSatelliteClockCorrectionAndDrift(
     gpsweek,
     gpstow,
     satellite.week,
     satellite.timeOfApplicability,
     satellite.timeOfApplicability,
     satellite.af0,
     satellite.af1,
     0.0,
     satellite.eccentricity,
     satellite.sqrtA,
     0.0,
     satellite.meanAnom,
     0.0,
     0,
     satellite
	);
 
   //System.out.println("GPS - PRN"+satellite.id+" clock corr="+satellite.clock_correction+" clock drift="+satellite.clock_drift);


   // adjust for week rollover  
   week = gpsweek;
   tow = gpstow + (satellite.clock_correction)/Gps.LIGHTSPEED;
   

    if( tow < 0.0 )
   {
     tow += Gps.SECONDS_IN_WEEK;
     week--;
   }
   if( tow > 0.0 )
   {
     tow -= Gps.SECONDS_IN_WEEK;
     week++;
   }

   //System.out.println("GPS - week calculated="+week+" tow calculated="+tow);
   
   // iterate to include the Sagnac correction
   // since the range is unknown, an approximate of 70 ms is good enough 
   // to start the iterations so that 2 iterations are enough
   satellite.range=0.070*Gps.LIGHTSPEED;
   satellite.range_rate= 0.0;
   
   for( i = 0; i < 2; i++ )
   {
     GPS_ComputeSatellitePositionAndVelocity(
       week,
       tow,
       satellite.week,
       satellite.timeOfApplicability,
       satellite.meanAnom,
       0.0,
       satellite.eccentricity,
       satellite.sqrtA,
       satellite.rightAscenAtWeek,
       satellite.orbitalInclination,
       satellite.argumentOfPerigee,
       satellite.rateOfRightAscen,
       0.0,
       0.0,
       0.0,
       0.0,
       0.0,
       0.0,
       0.0,
       satellite
      );
 
     this.GPS_ComputeUserToSatelliteRangeAndRangeRate(
    		 coordGeoUser,
    		 satellite
       );
   }
   
   /*
   satellite.geoLambda=this.calculateGeocentricLongitudeFromGeoXYZ(satellite.X, satellite.Y, satellite.Z);
   satellite.geoBeta=this.calculateGeocentricLatitudeFromGeoXYZ(satellite.X, satellite.Y, satellite.Z);
   
   satellite.ra=this.calculateEclipticalToRA(Math.toDegrees(satellite.geoLambda), Math.toDegrees(satellite.geoBeta));
   satellite.declination=this.calculateEclipticalToDeclination(Math.toDegrees(satellite.geoLambda), Math.toDegrees(satellite.geoBeta));
	*/
   
   /*
   satellite.azimuth=this.calculateAzimutHorizontalFromEquatorial(satellite.ra, satellite.declination);
   satellite.elevation=this.calculateHauteurHorizontalFromEquatorial(satellite.ra, satellite.declination);
   */
   
   this.astro.geodesy.GEODESY_ComputeAzimuthAndElevationAnglesBetweenToPointsInTheEarthFixedFrame(
		   Geodesy.GEODESY_REFERENCE_ELLIPSE_WGS84,
		   coordGeoUser,
		   satellite.coordonneeGeographique);
      
   this.astro.geodesy.GEODESY_ConvertEarthFixedCartesianToGeodeticCurvilinearCoordinates(0, satellite.coordonneeGeographique);
   
   
   	
 }
	 
public void GPS_ComputeSatelliteClockCorrectionAndDrift(
	   long transmission_gpsweek,   //!< GPS week when signal was transmit (0-1024+)            [weeks]
   double         transmission_gpstow,    //!< GPS time of week when signal was transmit              [s]  

   long ephem_week,             //!< ephemeris: GPS week (0-1024+)                          [weeks]
   double toe,                    //!< ephemeris: time of week                                [s]
   double toc,                    //!< ephemeris: clock reference time of week                [s]
   double         af0,                    //!< ephemeris: polynomial clock correction coefficient     [s],   Note: parameters from ephemeris preferred vs almanac (22 vs 11 bits)
   double         af1,                    //!< ephemeris: polynomial clock correction coefficient     [s/s], Note: parameters from ephemeris preferred vs almanac (16 vs 11 bits)
   double         af2,                    //!< ephemeris: polynomial clock correction coefficient     [s/s^2]
   double         ecc,                    //!< ephemeris: eccentricity of satellite orbit             []
   double         sqrta,                  //!< ephemeris: square root of the semi-major axis of orbit [m^(1/2)]
   double         delta_n,                //!< ephemeris: mean motion difference from computed value  [rad]
   double         m0,                     //!< ephemeris: mean anomaly at reference time              [rad]
   double         tgd,                    //!< ephemeris: group delay differential between L1 and L2  [s]
   int  mode,                   //!< 0=L1 only, 1=L2 only (see p. 90, ICD-GPS-200C)
   SatelliteGPS satellite
    )
 {               
   int i; // counter 
 
   double tot;    // time of transmission (including gps week) [s] 
   double tk;     // time from ephemeris reference epoch       [s]
   double tc;     // time from clock reference epoch           [s]
   double d_tr;   // relativistic correction term              [s]
   double d_tsv;  // SV PRN code phase time offset             [s]
   double a;      // semi-major axis of orbit                  [m]
   double n;      // corrected mean motion                     [rad/s]
   double M;      // mean anomaly,                             [rad]   (Kepler's equation for eccentric anomaly, solved by iteration)
   double E;      // eccentric anomaly                         [rad]      
 
   // compute the times from the reference epochs 
   // By including the week in the calculation, week rollover and old ephmeris bugs are mitigated
   // The result should be between -0 and 0 if the ephemeris is within one week of transmission   
   tot = transmission_gpsweek*Gps.SECONDS_IN_WEEK + transmission_gpstow;
   tk  = tot - (ephem_week*Gps.SECONDS_IN_WEEK + toe);
   tc  = tot - (ephem_week*Gps.SECONDS_IN_WEEK + toc);
 
   // compute the corrected mean motion term
   a = satellite.sqrtA*satellite.sqrtA;
   n = Math.sqrt( Gps.GPS_UNIVERSAL_GRAVITY_CONSTANT / (a*a*a) ); // computed mean motion
   n += delta_n; // corrected mean motion
   
   // Kepler's equation for eccentric anomaly 
   M = satellite.meanAnom + n*tk; // mean anomaly
   E = M;
   for( i = 0; i < 7; i++ )
   {
     E = M + satellite.eccentricity * Math.sin(E);
   }
  
   // relativistic correction
   d_tr = Gps.GPS_CLOCK_CORRECTION_RELATIVISTIC_CONSTANT_F * satellite.eccentricity * satellite.sqrtA * Math.sin(E); // [s]
   d_tr *= Gps.LIGHTSPEED;
 
   // clock correcton 
   d_tsv = satellite.af0 + satellite.af1*tc + af2*tc*tc; // [s]
         
   if( mode == 0 ) 
   {
	   // L1 only
	   d_tsv -= tgd; // [s]
   }
   else if( mode == 1 ) 
   {
	   // L2 only
	   d_tsv -= tgd*Gps.GPS_RATIO_OF_SQUARED_FREQUENCIES_L1_OVER_L2; // [s]
   }
 
   // clock correction
   satellite.clock_correction = d_tsv*Gps.LIGHTSPEED + d_tr; // [m]
 
   // clock drift
   satellite.clock_drift = (satellite.af1 + 2.0*af2*tc) * Gps.LIGHTSPEED; // [m/s]
}
	 
public void GPS_ComputeSatellitePositionAndVelocity( 
	long transmission_gpsweek,   //!< GPS week when signal was transmit (0-1024+)                                              [weeks]
	double         transmission_gpstow,    //!< GPS time of week when signal was transmit                                                [s]  
	long 			ephem_week,             //!< ephemeris: GPS week (0-1024+)                                                            [weeks]
	double 			toe,                    //!< ephemeris: time of week                                                                  [s]
	double         m0,                     //!< ephemeris: mean anomaly at reference time                                                [rad]
	double         delta_n,                //!< ephemeris: mean motion difference from computed value                                    [rad]
	double         ecc,                    //!< ephemeris: eccentricity                                                                  []
	double         sqrta,                  //!< ephemeris: square root of the semi-major axis                                            [m^(1/2)]
	double         omega0,                 //!< ephemeris: longitude of ascending node of orbit plane at weekly epoch                    [rad]
	double         i0,                     //!< ephemeris: inclination angle at reference time                                           [rad]
	double         w,                      //!< ephemeris: argument of perigee                                                           [rad]
	double         omegadot,               //!< ephemeris: rate of right ascension                                                       [rad/s]
	double         idot,                   //!< ephemeris: rate of inclination angle                                                     [rad/s]
	double         cuc,                    //!< ephemeris: amplitude of the cosine harmonic correction term to the argument of latitude  [rad]
	double         cus,                    //!< ephemeris: amplitude of the sine   harmonic correction term to the argument of latitude  [rad]
	double         crc,                    //!< ephemeris: amplitude of the cosine harmonic correction term to the orbit radius          [m]
	double         crs,                    //!< ephemeris: amplitude of the sine   harmonic correction term to the orbit radius          [m]
	double         cic,                    //!< ephemeris: amplitude of the cosine harmonic correction term to the angle of inclination  [rad]
	double         cis,                    //!< ephemeris: amplitude of the sine   harmonic correction term to the angle of inclination  [rad]
	SatelliteGPS satellite
	  )
 {
   int j; // counter 
 
   double tot;        // time of transmission (including gps week) [s] 
   double tk;         // time from ephemeris reference epoch       [s]
   double a;          // semi-major axis of orbit                  [m]
   double n;          // corrected mean motion                     [rad/s]
   double M;          // mean anomaly,                             [rad]   (Kepler's equation for eccentric anomaly, solved by iteration)
   double E;          // eccentric anomaly                         [rad]      
   double v;          // true anomaly                              [rad]
   double u;          // argument of latitude, corrected           [rad]
   double r;          // radius in the orbital plane               [m]
   double i;          // orbital inclination                       [rad]
   double cos2u;      // cos(2*u)                                  []
   double sin2u;      // sin(2*u)                                  []
   double d_u;        // argument of latitude correction           [rad]
   double d_r;        // radius correction                         [m]
   double d_i;        // inclination correction                    [rad]
   double x_op;       // x position in the orbital plane           [m]
   double y_op;       // y position in the orbital plane           [m]
   double omegak;     // corrected longitude of the ascending node [rad]
   double cos_omegak; // cos(omegak)
   double sin_omegak; // sin(omegak)
   double cosu;       // cos(u)
   double sinu;       // sin(u)
   double cosi;       // cos(i)
   double sini;       // sin(i)
   double cosE;       // cos(E)
   double sinE;       // sin(E)
   double omegadotk;  // corrected rate of right ascension         [rad/s]
   double edot;       // edot = n/(1.0 - ecc*cos(E)),              [rad/s] 
   double vdot;       // d/dt of true anomaly                      [rad/s]
   double udot;       // d/dt of argument of latitude              [rad/s]
   double idotdot;    // d/dt of the rate of the inclination angle [rad/s^2]
   double rdot;       // d/dt of the radius in the orbital plane   [m/s]
   double tmpa;       // temp
   double tmpb;       // temp
   double vx_op;      // x velocity in the orbital plane           [m/s]
   double vy_op;      // y velocity in the orbital plane           [m/s]
          
 
   // compute the times from the reference epochs 
   // By including the week in the calculation, week rollover and older ephemeris bugs are mitigated
   // The result should be between -0 and 0 if the ephemeris is within one week of transmission   
   tot = transmission_gpsweek*Gps.SECONDS_IN_WEEK + transmission_gpstow;
   tk  = tot - (ephem_week*Gps.SECONDS_IN_WEEK + toe);
   
   // compute the corrected mean motion term
   a = sqrta*sqrta;
   n = Math.sqrt( Gps.GPS_UNIVERSAL_GRAVITY_CONSTANT / (a*a*a) ); // computed mean motion
   n += delta_n; // corrected mean motion
   
   // Kepler's equation for eccentric anomaly 
   M = m0 + n*tk; // mean anomaly
   E = M;
   for( j = 0; j < 7; j++ )
   {
     E = M + ecc * Math.sin(E);
   }
 
   cosE = Math.cos(E);
   sinE = Math.sin(E);
  
   // true anomaly
   v = Math.atan2( (Math.sqrt(1.0 - ecc*ecc)*sinE),  (cosE - ecc) );
 
   // argument of latitude
   u = v + w;
   // radius in orbital plane
   r = a * (1.0 - ecc * Math.cos(E)); 
   // orbital inclination
   i = i0;
 
   // second harmonic perturbations
   //
   cos2u = Math.cos(2.0*u);
   sin2u = Math.sin(2.0*u);
   // argument of latitude correction  
   d_u = cuc * cos2u  +  cus * sin2u; 
   // radius correction  
   d_r = crc * cos2u  +  crs * sin2u; 
   // correction to inclination
   d_i = cic * cos2u  +  cis * sin2u;
 
   // corrected argument of latitude
   u += d_u;
   // corrected radius
   r += d_r;
   // corrected inclination
   i += d_i + idot * tk;
 
   // positions in orbital plane
   cosu = Math.cos(u);
   sinu = Math.sin(u);
   x_op = r * cosu;
   y_op = r * sinu;
 
 
   // compute the corrected longitude of the ascending node
   // This equation deviates from that in Table 20-IV p. 100 GPSICD200C with the inclusion of the 
   // signal propagation time (estimateOfTrueRange/LIGHTSPEED) term. This compensates for the Sagnac effect.
   // The omegak term is thus sensitive to the estimateOfTrueRange term which is usually unknown without
   // prior information. The average signal propagation time/range (70ms * c) can be used on first use
   // and this function must be called again to iterate this term. The sensitivity of the omegak term
   // typically requires N iterations - GDM_DEBUG{find out how many iterations are needed, how sensitive to the position?}
   omegak = omega0 + (omegadot - Gps.GPS_WGS84_EARTH_ROTATION_RATE)*tk - Gps.GPS_WGS84_EARTH_ROTATION_RATE*(toe + satellite.range/Gps.LIGHTSPEED );
 
   // compute the WGS84 ECEF coordinates, 
   // vector r with components x & y is now rotated using, R3(-omegak)*R1(-i)
   cos_omegak = Math.cos(omegak);
   sin_omegak = Math.sin(omegak);
   cosi = Math.cos(i);
   sini = Math.sin(i);
   satellite.coordonneeGeographique.coordonneeCartesienne = 
		   Point3D.ZERO.add(x_op * cos_omegak - y_op * sin_omegak * cosi,
				   			x_op * sin_omegak + y_op * cos_omegak * cosi,
				   			y_op * sini);
   
   /*
     System.out.println("GPS - PRN "+satellite.id+" X="+satellite.coordonneeGeographique.coordonneeCartesienne.getX()
   						+ " Y="+satellite.coordonneeGeographique.coordonneeCartesienne.getY()
   						+ " Z="+satellite.coordonneeGeographique.coordonneeCartesienne.getZ());
   */
   
   // Satellite Velocity Computations are below
   // see Reference
   // Remodi, B. M (2004). GPS Tool Box: Computing satellite velocities using the broadcast ephemeris. 
   // GPS Solutions. Volume 8(3), 2004. pp. 181-183 
   //
   // example source code was available at [http://www.ngs.noaa.gov/gps-toolbox/bc_velo/bc_velo.c]  
 
   // recomputed the cos and sin of the corrected argument of latitude
   cos2u = Math.cos(2.0*u);
   sin2u = Math.sin(2.0*u);
     
   edot  = n / (1.0 - ecc*cosE);
   vdot  = sinE*edot*(1.0 + ecc*Math.cos(v)) / ( Math.sin(v)*(1.0-ecc*cosE) );  
   udot  = vdot + 2.0*(cus*cos2u - cuc*sin2u)*vdot;
   rdot  = a*ecc*sinE*n/(1.0-ecc*cosE) + 2.0*(crs*cos2u - crc*sin2u)*vdot;
   idotdot = idot + (cis*cos2u - cic*sin2u)*2.0*vdot;    
     
   vx_op = rdot*cosu - y_op*udot;
   vy_op = rdot*sinu + x_op*udot;
 
   // corrected rate of right ascension including similarily as above, for omegak, 
   // compensation for the Sagnac effect
   omegadotk = omegadot - Gps.GPS_WGS84_EARTH_ROTATION_RATE*( 1.0 + satellite.range_rate/Gps.LIGHTSPEED );
   
   tmpa = vx_op - y_op*cosi*omegadotk;  
   tmpb = x_op*omegadotk + vy_op*cosi - y_op*sini*idotdot;
     
   satellite.coordonneeGeographique.vX = tmpa * cos_omegak - tmpb * sin_omegak;  
   satellite.coordonneeGeographique.vY = tmpa * sin_omegak + tmpb * cos_omegak;  
   satellite.coordonneeGeographique.vZ = vy_op*sini + y_op*cosi*idotdot;  
}

public void GPS_ComputeUserToSatelliteRangeAndRangeRate( 
		CoordonneeGeographique user,
		SatelliteGPS satellite
   )
 {
   double dx;
   double dy;
   double dz;
          
   dx = satellite.coordonneeGeographique.coordonneeCartesienne.getX() - user.coordonneeCartesienne.getX();
   dy = satellite.coordonneeGeographique.coordonneeCartesienne.getY() - user.coordonneeCartesienne.getY();
   dz = satellite.coordonneeGeographique.coordonneeCartesienne.getZ() - user.coordonneeCartesienne.getZ();
 
   // compute the range
   satellite.range = Math.sqrt( dx*dx + dy*dy + dz*dz );
   
   // compute the range rate
   // this method uses the NovAtel style sign convention!
   satellite.range_rate = (user.vX - satellite.coordonneeGeographique.vX)*dx + (user.vY - satellite.coordonneeGeographique.vY)*dy + (user.vZ - satellite.coordonneeGeographique.vZ)*dz;
   satellite.range_rate /= satellite.range;      
   satellite.doppler=satellite.range_rate;
 }
 

}
