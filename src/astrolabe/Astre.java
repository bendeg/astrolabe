package astrolabe;

import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.util.Vector;
import java.awt.*;

public class Astre implements Shape {
  String id;
  String adHeure, adMinute, adSeconde;
  String decDegre, decMinute, decSeconde;
  Double adJ2000;
  Double decJ2000;
  Double ad;
  Double dec;
  Double mag;
  int index;
  Double pmra, pmdec;
  Point2D currentLocation;
  Ellipse2D ellipse;
  Color rvb;
  
  Vector<Astre> nextAstre = new Vector<Astre>();
  
   public Astre getNextAstre(int i) {
    return nextAstre.get(i);
  }

  public void addNextAstre(Astre nextAstre) {
    this.nextAstre.add(nextAstre);
  }

  public Astre(String id, Double ad, Double dec, Double mag, int index) {
    this.id = id;
    this.adJ2000 = ad;
    this.decJ2000 = dec;
    this.ad = ad;
    this.dec = dec;
    this.mag = mag;
    this.index = index;
    this.currentLocation = new Point2D.Double();
    this.ellipse = new Ellipse2D.Double();
    this.rvb=new Color(1f, 1f, 1f);
  }

  public Color getRvb() {
    return rvb;
  }

  public void setRvb(Color rvb) {
    this.rvb = rvb;
  }

  public Astre(String id, String ad, String dec, Double mag, int index) {
    
  }

  public Astre(String id, String ad, Double dec, Double mag, int index) {
    
  }
  
  public Astre(String id, Double ad, String dec, Double mag, int index) {
    
  }
  
  public String toString() {
    //return this.index+1 + ") " + this.id;
    return this.id;
  }
  
  public String getAdHeure() {
    double temp;
    temp = ((this.ad+360.0)%360.0)/15.0;
    //System.out.println("Astre - getADHeure - temp = : " + temp);
    this.adHeure = String.valueOf((int)Math.floor(temp));
    
    temp -= Math.floor(temp);
    temp *= 60;
    //System.out.println("Astre - getADMinute - temp = : " + temp);
    this.adMinute = String.valueOf((int)Math.floor(temp));
    
    temp -= Math.floor(temp);
    temp *= 60;
    //System.out.println("Astre - getADSeconde - temp = : " + temp);
    this.adSeconde = String.valueOf(temp);
    
    return this.adHeure;
  }
  
  public String getAdMinute() {
    return this.adMinute;
  }
  
  public String getAdSeconde() {
    return this.adSeconde;
  }
  
  public String getDecDegre() {
    double temp;
    boolean zeronegatif = false;
    
    temp = this.dec;
    if((temp < 0.0) && (temp > -1.0)) zeronegatif = true;
    
    //System.out.println("Astre - getDecDegre - temp = : " + temp);
    if(temp < 0.0) this.decDegre = String.valueOf((int)-Math.floor(-temp)); 
    else this.decDegre = String.valueOf((int)Math.floor(temp));
   
    if(temp < 0.0) temp -= -Math.floor(-temp); 
    else temp -= Math.floor(temp);
    temp *= 60;
    if(temp < 0.0) temp = -temp;
    //System.out.println("Astre - getDecMinute - temp = : " + temp);
    if(zeronegatif) this.decMinute = String.valueOf((int)-Math.floor(temp));
    else this.decMinute = String.valueOf((int)Math.floor(temp));
    
    temp -= Math.floor(temp);
    temp *= 60;
    //System.out.println("Astre - getDecSeconde - temp = : " + temp);
    if(zeronegatif) this.decSeconde = String.valueOf(-temp);
    else this.decSeconde = String.valueOf(temp);
    
    return this.decDegre;
  }
  
  public String getDecMinute() {
    return this.decMinute;
  }
  
  public String getDecSeconde() {
    return this.decSeconde;
  }
  
  public double setAd(double ad) {
    this.ad=ad;
    return this.ad;
  }
  
  public double setDec(double dec) {
    this.dec = dec;
    return this.dec;
  }
  
  public double getAd() {
    return this.ad;
  }
  
  public double getDec() {
    return this.dec;
  }
  
  public double getAdJ2000() {
    return this.adJ2000;
  }
  
  public double getDecJ2000() {
    return this.decJ2000;
  }
  
  public double getMag() {
    return this.mag;
  }
  
  public double setMag(double mag) {
    this.mag = mag;
    return this.mag;
  }
  
  public void setPm(double pmra, double pmdec) {
    this.pmra = pmra;
    this.pmdec = pmdec;
  }
  
  public double getPmra() {
    return this.pmra;
  }
  
  public double getPmdec() {
    return this.pmdec;
  }
  
  public String setId(String id) {
    this.id = id;
    return this.id;
  }
  
  public String getId() {
    return this.id;
  }
  
  public Point2D getCurrentLocation() {
    return currentLocation;
  }

  public void setCurrentLocation(Point2D currentLocation) {
    this.currentLocation = currentLocation;
  }

  public Rectangle2D getBounds2D() {
    return this.ellipse.getBounds2D();
  }

  public Rectangle getBounds() {
    return this.ellipse.getBounds();
  }
  
  public PathIterator getPathIterator() {
    return null;
  }

  public PathIterator getPathIterator(AffineTransform a) {
    return this.ellipse.getPathIterator(a);
  }

  public PathIterator getPathIterator(AffineTransform a, double d) {
    return this.ellipse.getPathIterator(a, 0.0);
  }
  
  public boolean contains(double d1, double d2) {
    return this.ellipse.contains(d1, d2);
  }
  
  public boolean contains(Point2D p) {
    return this.ellipse.contains(p);
  }

  public boolean contains(double d1, double d2, double d3, double d4) {
    return this.ellipse.contains(d1, d2, d3, d4);
  }

  public boolean contains(Rectangle2D p) {
    return this.ellipse.contains(p);
  }

  public boolean intersects(double d1, double d2, double d3, double d4) {
    return this.ellipse.intersects(d1, d2, d3, d4);
  }

  public boolean intersects(Rectangle2D p) {
    return this.ellipse.intersects(p);
  }
}
