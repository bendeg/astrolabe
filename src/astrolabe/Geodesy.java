package astrolabe;

//import java.util.Vector;

import javafx.geometry.Point3D;

public class Geodesy {

static double
	PI=3.1415926535897932384626433832795,
	TWOPI=6.283185307179586476925286766559,
	HALFPI=1.5707963267948966192313216916398,
	QUARTERPI=0.78539816339744830961566084581988,
	DEG2RAD=0.017453292519943295769236907684886,
	RAD2DEG=57.295779513082320876798154814105;

static int
	GEODESY_REFERENCE_ELLIPSE_WGS84=0,  
	GEODESY_REFERENCE_ELLIPSE_AIRY=1,  
	GEODESY_REFERENCE_ELLIPSE_MODIFED_AIRY=2,  
	GEODESY_REFERENCE_ELLIPSE_AUSTRALIAN_NATIONAL=3,  
	GEODESY_REFERENCE_ELLIPSE_BESSEL_1841=4,  
	GEODESY_REFERENCE_ELLIPSE_CLARKE_1866=5,  
	GEODESY_REFERENCE_ELLIPSE_CLARKE_1880=6,  
	GEODESY_REFERENCE_ELLIPSE_EVEREST_INDIA_1830=7,  
	GEODESY_REFERENCE_ELLIPSE_EVEREST_BRUNEI_E_MALAYSIA=8,  
	GEODESY_REFERENCE_ELLIPSE_EVEREST_W_MALAYSIA_SINGAPORE=9,  
	GEODESY_REFERENCE_ELLIPSE_GRS_1980=10,  
	GEODESY_REFERENCE_ELLIPSE_HELMERT_1906=11,  
	GEODESY_REFERENCE_ELLIPSE_HOUGH_1960=12,  
	GEODESY_REFERENCE_ELLIPSE_INTERNATIONAL_1924=13,  
	GEODESY_REFERENCE_ELLIPSE_SOUTH_AMERICAN_1969=14,  
	GEODESY_REFERENCE_ELLIPSE_WGS72=15;  


static double[][] paramDatums=
{		//demi-grand axe "a", applatissement inverse "f", demi-petit axe "b", eccentricit� au carr� "e2"
		{6378137.0,   298.257223563, 6356752.3142451793, 0.00669437999014132},//GEODESY_REFERENCE_ELLIPSE_WGS84,                          
		{6377563.396, 299.3249647,   6356256.9092444032, 0.00667053999776051},//GEODESY_REFERENCE_ELLIPSE_AIRY,                           
		{6377340.189, 299.3249647,   6356034.4479456525, 0.00667053999776060},//GEODESY_REFERENCE_ELLIPSE_MODIFED_AIRY,                   
		{6378160.0,   298.25,        6356774.7191953063, 0.00669454185458760},//GEODESY_REFERENCE_ELLIPSE_AUSTRALIAN_NATIONAL,            
		{6377397.155, 299.1528128,   6356078.9628181886, 0.00667437223180205},//GEODESY_REFERENCE_ELLIPSE_BESSEL_1841,                    
		{6378206.4,   294.9786982,   6356583.7999989809, 0.00676865799760959},//GEODESY_REFERENCE_ELLIPSE_CLARKE_1866,                    
		{6378249.145, 293.465,       6356514.8695497755, 0.00680351128284912},//GEODESY_REFERENCE_ELLIPSE_CLARKE_1880,                    
		{6377276.345, 300.8017,      6356075.4131402392, 0.00663784663019987},//GEODESY_REFERENCE_ELLIPSE_EVEREST_INDIA_1830,             
		{6377298.556, 300.8017,      6356097.5503008962, 0.00663784663019965},//GEODESY_REFERENCE_ELLIPSE_EVEREST_BRUNEI_E_MALAYSIA,      
		{6377304.063, 300.8017,      6356103.0389931547, 0.00663784663019970},//GEODESY_REFERENCE_ELLIPSE_EVEREST_W_MALAYSIA_SINGAPORE,   
		{6378137.0,   298.257222101, 6356752.3141403561, 0.00669438002290069},//GEODESY_REFERENCE_ELLIPSE_GRS_1980,                       
		{6378200.0,   298.30,        6356818.1696278909, 0.00669342162296610},//GEODESY_REFERENCE_ELLIPSE_HELMERT_1906,                   
		{6378270.0,   297.00,        6356794.3434343431, 0.00672267002233347},//GEODESY_REFERENCE_ELLIPSE_HOUGH_1960,                     
		{6378388.0,   297.00,        6356911.9461279465, 0.00672267002233323},//GEODESY_REFERENCE_ELLIPSE_INTERNATIONAL_1924,             
		{6378160.0,   298.25,        6356774.7191953063, 0.00669454185458760},//GEODESY_REFERENCE_ELLIPSE_SOUTH_AMERICAN_1969,            
		{6378135.0,   298.26,        6356750.5200160937, 0.00669431777826668},//GEODESY_REFERENCE_ELLIPSE_WGS72,                          
};

public boolean GEODESY_ConvertGeodeticCurvilinearToEarthFixedCartesianCoordinates(
			   int  referenceEllipse,  //!< reference ellipse enumerated []
			   CoordonneeGeographique coordonneeGeographique
			   ) {  
	double a;      // semi-major axis of reference ellipse [m]
	double e2;     // first eccentricity of reference ellipse []
	double N;      // prime vertical radius of curvature [m]
	double sinlat; // sin of the latitude
	double dtmp;   // temp
	double latitude=Math.toRadians(coordonneeGeographique.getLatitude());
	double longitude=Math.toRadians(coordonneeGeographique.getLongitude());
	
	if(referenceEllipse <0 || referenceEllipse>15 || latitude<-Math.PI/2 || latitude>Math.PI/2)
		   return false;
	
	// get necessary reference ellipse parameters
	a=Geodesy.paramDatums[referenceEllipse][0];
	e2=Geodesy.paramDatums[referenceEllipse][3];
	
	sinlat = Math.sin(latitude);             
	N = a / Math.sqrt(1.0 - e2 * sinlat*sinlat );      
	dtmp = (N + coordonneeGeographique.getAltitude()) * Math.cos(latitude);
	
	//System.out.println("GEODESY - user lat="+coordonneeGeographique.latitude+" lon="+coordonneeGeographique.longitude+" alt=" +coordonneeGeographique.altitude);
	coordonneeGeographique.setCoordonneeCartesienne(Point3D.ZERO.add(	dtmp * Math.cos(longitude),
			   														dtmp * Math.sin(longitude),
			   														( (1.0 - e2)*N + coordonneeGeographique.getAltitude()) * sinlat));
	return true;
}

public boolean GEODESY_ConvertEarthFixedCartesianToGeodeticCurvilinearCoordinates(
   int referenceEllipse,  //!< reference ellipse enumerated []
   CoordonneeGeographique coordonneeGeographique
   )
 {
   double a=Geodesy.paramDatums[referenceEllipse][0];      // semi-major axis of reference ellipse [m]
   double b=Geodesy.paramDatums[referenceEllipse][2];      // semi-minor axis of reference ellipse [m]
   double e2=Geodesy.paramDatums[referenceEllipse][3];     // first eccentricity of reference ellipse []
   double N;      // prime vertical radius of curvature [m]
   double p;      // sqrt( x^2 + y^2 ) [m]
   double dtmp;   // temp
   double sinlat; // sin(lat)
   double lat;    // temp geodetic latitude  [rad]
   double lon;    // temp geodetic longitude [rad]
   double hgt;    // temp geodetic height    [m]
   //boolean result;
   
   // get necessary reference ellipse parameters
   /*
   result = GEODESY_GetReferenceEllipseParameters_A_B_E2( referenceEllipse, &a, &b, &e2 );
   if( result == FALSE )
   {
     *latitude  = 0;
     *longitude = 0;  
     *height    = 0;  
     return FALSE;
   }
   */
   
   if( coordonneeGeographique.getCoordonneeCartesienne().getX() == 0.0 && coordonneeGeographique.getCoordonneeCartesienne().getY() == 0.0 ) 
   {
     // at a pole    
     // most likely to happen while using a simulator
     
     // longitude is really unknown
     lon = 0.0; 
     
     if( coordonneeGeographique.getCoordonneeCartesienne().getZ() < 0 )
     {
       hgt = -coordonneeGeographique.getCoordonneeCartesienne().getZ() - b;
       lat = -HALFPI;
     }
     else
     {
       hgt = coordonneeGeographique.getCoordonneeCartesienne().getZ() - b;
       lat = HALFPI;
     }
   }
   else
   {
     p = Math.sqrt( coordonneeGeographique.getCoordonneeCartesienne().getX()*coordonneeGeographique.getCoordonneeCartesienne().getX()
    		 + coordonneeGeographique.getCoordonneeCartesienne().getY()*coordonneeGeographique.getCoordonneeCartesienne().getY() );
 
     // unique solution for longitude
     // best formula for any longitude and applies well near the poles
     // pp. 178 reference [2]
     lon = 2.0 * Math.atan2( coordonneeGeographique.getCoordonneeCartesienne().getY() , ( coordonneeGeographique.getCoordonneeCartesienne().getX() + p ) );
     
     // set approximate initial latitude assuming a height of 0.0
     lat = Math.atan( coordonneeGeographique.getCoordonneeCartesienne().getZ() / (p * (1.0 - e2)) );
     hgt = 0.0;
     do
     { 
       dtmp = hgt;
       sinlat = Math.sin(lat);
       N   = a / Math.sqrt( 1.0 - e2*sinlat*sinlat );
       hgt = p / Math.cos(lat) - N;
       lat = Math.atan( coordonneeGeographique.getCoordonneeCartesienne().getZ() / (p * ( 1.0 - e2*N/(N + hgt) )) );      
 
     } while( Math.abs( hgt - dtmp ) > 0.0001 );  // 0.1 mm convergence for height
   }
 
   coordonneeGeographique.setLatitude(Math.toDegrees(lat));
   coordonneeGeographique.setLongitude(Math.toDegrees(lon));  
   coordonneeGeographique.setAltitude(hgt);
   return true;
 }

public boolean GEODESY_ComputeAzimuthAndElevationAnglesBetweenToPointsInTheEarthFixedFrame(
   int referenceEllipse, //!< reference ellipse enumerated []
   CoordonneeGeographique from,
   CoordonneeGeographique to
   /*
   double fromX, //!< earth centered earth fixed vector from point X component [m]
   double fromY, //!< earth centered earth fixed vector from point Y component [m]
   double fromZ, //!< earth centered earth fixed vector from point Z component [m]
   double toX,   //!< earth centered earth fixed vector to point X component   [m]
   double toY,   //!< earth centered earth fixed vector to point Y component   [m]
   double toZ,   //!< earth centered earth fixed vector to point Z component   [m]
   */
   /*
   double* elevation,  //!< elevation angle [rad]
   double* azimuth     //!< azimuth angle   [rad]
   */
   /*
   SatelliteGPS satellite
   */
   )
 {
   //double lat=Math.toRadians(from.latitude); // reference geodetic latitude  ('from' point) [rad]
   //double lon=Math.toRadians(from.longitude); // reference geodetic longitude ('from' point) [rad]
   double[] ecefVector={0.0, 0.0, 0.0};
   /*
   double dX;  // ECEF X vector component between 'from' and 'to' point (m)
   double dY;  // ECEF Y vector component between 'from' and 'to' point (m)
   double dZ;  // ECEF Z vector component between 'from' and 'to' point (m)
   */
   double[] rotationVector={0.0, 0.0, 0.0};
   /*
   double dN;  // LG northing vector component between 'from' and 'to' point (m)
   double dE;  // LG easting  vector component between 'from' and 'to' point (m)
   double dUp; // LG vertical vector component between 'from' and 'to' point (m)
   */
   double tmp; // temp value
   boolean result;
 
   to.elevation = 0;
   to.azimuth = 0; 
 
   // get the reference geodetic curvilinear coordinates from the 'from' point
   result = GEODESY_ConvertEarthFixedCartesianToGeodeticCurvilinearCoordinates(
     Geodesy.GEODESY_REFERENCE_ELLIPSE_WGS84,
     from
     );
   if( result == false )
     return result;      
   
   //System.out.println("from lat="+from.latitude+" from lon="+from.longitude+" from alt="+from.altitude);
   
   // vector between the two points in the earth fixed frame
   ecefVector[0] = to.coordonneeCartesienne.getX() - from.coordonneeCartesienne.getX();
   ecefVector[1] = to.coordonneeCartesienne.getY() - from.coordonneeCartesienne.getY();
   ecefVector[2] = to.coordonneeCartesienne.getZ() - from.coordonneeCartesienne.getZ();
   
   //System.out.println("ecefVector[0]="+ecefVector[0]+" ecefVector[1]="+ecefVector[1]+" ecefVector[2]="+ecefVector[2]);
 
   
   //System.out.println("rotationVector[0]="+rotationVector[0]+" rotationVector[1]="+rotationVector[1]+" rotationVector[2]"+rotationVector[2]);
   //System.out.println("from lon="+from.longitude+" from lat="+from.latitude+" from alt="+from.altitude);
   // rotate the vector to the local geodetic frame
   result = GEODESY_RotateVectorFromEarthFixedFrameToLocalGeodeticFrame(
		   from,
		   ecefVector,
		   rotationVector
		   );
   if( result == false )
     return result;    
   //System.out.println("rotationVector[0]="+rotationVector[0]+" rotationVector[1]="+rotationVector[1]+" rotationVector[2]"+rotationVector[2]);
   
   
   // compute the elevation
   tmp = Math.sqrt( rotationVector[0]*rotationVector[0] + rotationVector[1]*rotationVector[1] + rotationVector[2]*rotationVector[2]);
   to.elevation = Math.asin( rotationVector[2] / tmp );
   //essai...
   //to.elevation = Math.asin( rotationVector[2] / tmp );
   
   // compute the azimuth
   to.azimuth = Math.atan2(rotationVector[0], rotationVector[1]);
 
/*
   //test sans rotation===========
   tmp = Math.sqrt( ecefVector[0]*ecefVector[0] + ecefVector[1]*ecefVector[1]);
   to.elevation = Math.asin( ecefVector[2] / tmp );
   //essai...
   //to.elevation = Math.asin( rotationVector[2] / tmp );
   
   // compute the azimuth
   to.azimuth = Math.atan2(ecefVector[1], ecefVector[0]);
   //test sans rotation===========
*/
   // by convention, azimuth will be between 0 to 2 PI
   if( to.azimuth < 0.0 )
     to.azimuth += Geodesy.TWOPI;
   
   to.azimuth=((Math.toDegrees(to.azimuth)));
   /*
   if(to.azimuth >180.0) to.azimuth+=90.0;
   else to.azimuth-=90.0;
   */
  to.elevation=Math.toDegrees(to.elevation);
   
   return true;
 }

public boolean GEODESY_RotateVectorFromEarthFixedFrameToLocalGeodeticFrame(
   CoordonneeGeographique reference,  	//!< reference geodetic latitude                 [deg]
   										//!< reference geodetic longitude                [deg]
   double[] ecefVector, 				//!< earth centered earth fixed vector component [m]
   double[] rotationVector				//!< local geodetic vector component    [m]
   )
 {
   double sinlat;
   double coslat;
   double sinlon;
   double coslon;
   boolean result;
  
   result = GEODESY_IsLatitudeValid( reference.latitude );
   if( result == false )
   {
     ecefVector[0]=0;//*dN = 0;
     ecefVector[1]=0;//*dE = 0;
     ecefVector[2]=0;//*dUp = 0;
     return result;    
   }
 
   sinlat = Math.sin(Math.toRadians(reference.latitude));
   coslat = Math.cos(Math.toRadians(reference.latitude));
   sinlon = Math.sin(Math.toRadians(reference.longitude));
   coslon = Math.cos(Math.toRadians(reference.longitude));
   
   /*
   rotationVector[0] = -sinlat*coslon * ecefVector[0]  -  sinlat*sinlon * ecefVector[1]  +  coslat * ecefVector[2];
   rotationVector[1] = -sinlon        * ecefVector[0]  +  coslon        * ecefVector[1];
   rotationVector[2] =  coslat*coslon * ecefVector[0]  +  coslat*sinlon * ecefVector[1]  +  sinlat * ecefVector[2];  
 */
   
   	//faux mais on essaye...voir fichier gps_calcul.pdf dans t�l�chargements"
   /*
  	rotationVector[0] = - sinlon * ecefVector[0] - coslon*sinlat 	* ecefVector[1] + coslon*coslat * ecefVector[2];
  	rotationVector[1] =   coslon * ecefVector[0] - sinlon*sinlat 	* ecefVector[1] + sinlon*coslat * ecefVector[2];
   	rotationVector[2] =  						   coslat 		    * ecefVector[1] +        sinlat * ecefVector[2];  
	*/
   rotationVector[0] = - sinlon 			* ecefVector[0] + coslon 			* ecefVector[1] ;
 	rotationVector[1] = - coslon*sinlat  	* ecefVector[0] - sinlon*sinlat 	* ecefVector[1] + coslat * ecefVector[2];
  	rotationVector[2] =+ coslon*coslat 		* ecefVector[0] + sinlon*coslat			 * ecefVector[1] +        sinlat * ecefVector[2];  
	
   
   return true;
 }

public boolean GEODESY_IsLatitudeValid( 
   double latitude //!< expecting a value -90 to 90 [deg]
   )
 {
   // check for valid latitude out of range
   if( latitude > 90.0 || latitude < -90.0 )  
     return false;
   else
     return true;
}

}
