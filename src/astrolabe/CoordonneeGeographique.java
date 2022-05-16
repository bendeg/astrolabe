package astrolabe;

import javafx.geometry.Point3D;

public class CoordonneeGeographique {

	double	latitude,
			longitude,
			altitude;
	
	public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getAltitude() {
    return altitude;
  }

  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }

  public double getAzimuth() {
    return azimuth;
  }

  public void setAzimuth(double azimuth) {
    this.azimuth = azimuth;
  }

  public double getElevation() {
    return elevation;
  }

  public void setElevation(double elevation) {
    this.elevation = elevation;
  }

  public double getRa() {
    return ra;
  }

  public void setRa(double ra) {
    this.ra = ra;
  }

  public double getDeclination() {
    return declination;
  }

  public void setDeclination(double declination) {
    this.declination = declination;
  }

  public double getvX() {
    return vX;
  }

  public void setvX(double vX) {
    this.vX = vX;
  }

  public double getvY() {
    return vY;
  }

  public void setvY(double vY) {
    this.vY = vY;
  }

  public double getvZ() {
    return vZ;
  }

  public void setvZ(double vZ) {
    this.vZ = vZ;
  }

  public Point3D getCoordonneeCartesienne() {
    return coordonneeCartesienne;
  }

  public void setCoordonneeCartesienne(Point3D coordonneeCartesienne) {
    this.coordonneeCartesienne = coordonneeCartesienne;
  }

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
