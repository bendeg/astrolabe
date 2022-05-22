package astrolabe;

import java.io.Serializable;

public class Home implements Serializable {
	static final long serialVersionUID=1;
	double longitude, latitude, altitude, cutoff;
	
  public Home(double latitude, double longitude, double altitude) {
		this.latitude=latitude;
		this.longitude=longitude;
		this.altitude=altitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
  public double getAltitude() {
    return altitude;
  }
  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public double getCutoff() {
		return cutoff;
	}
	public void setCutoff(double cutoff) {
		this.cutoff = cutoff;
	}
	
}
