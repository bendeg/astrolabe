package astrolabe;

import java.io.Serializable;

public class Home implements Serializable {
	static final long serialVersionUID=1;
	double longitude, latitude, cutoff;
	
	public Home(double latitude, double longitude) {
		this.latitude=latitude;
		this.longitude=longitude;
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
