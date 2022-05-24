package astrolabe;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Dimension2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Font;

public class Face extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {
	static final long serialVersionUID=1;
	
	Ellipse2D.Double masque, masqueZenith;
	transient Graphics2D g2d;
	Astrolabe astro;
	AffineTransform affine, affineTemp;
	Color color;
	int mouseX, mouseY;
	int mousewheel_init=1, mousewheel=0, mousewheelSensibility=100;
	double centreX, centreY, tempDouble;
	transient Stroke dotted = new BasicStroke(1.0f,                      // Width
            BasicStroke.CAP_SQUARE,    // End cap
            BasicStroke.JOIN_MITER,    // Join style
            1.0f,                     // Miter limit
            new float[] {1.0f,5.0f}, // Dash pattern
            0.0f);                     // Dash phase

	final static BasicStroke normal =
            new BasicStroke(1.0f,
                            BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER);
	final static BasicStroke gras =
            new BasicStroke(2.0f,
                            BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER);
	final static float dash1[] = {10.0f};
    final static BasicStroke dashed =
        new BasicStroke(1.0f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f, dash1, 0.0f);
    
    double hemiCoefDec, hemiCoefRA;
    Font currentFont;
    
	public Face(Astrolabe astro) {
    //this.setLayout(new GridLayout());
		this.astro=astro;
		this.color=new Color(255,255,255);
		this.addMouseWheelListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		//System.out.println("scrollpane width = "+this.getParent().getSize().getWidth()+" height = "+this.getParent().getSize().getHeight());
		//System.out.println("panel width = "+this.getSize().getWidth()+" height = "+this.getSize().getHeight());
		//System.out.println("dimension = "+this.astro.splitBottomDimension.getWidth()+" "+this.astro.splitBottomDimension.getWidth());
		//System.out.println("scroll h = "+this.astro.sp2.getHorizontalScrollBar().getMaximum()+" val = "+this.astro.sp2.getHorizontalScrollBar().getValue());
		//System.out.println("scroll v = "+this.astro.sp2.getVerticalScrollBar().getMaximum()+" val = "+this.astro.sp2.getVerticalScrollBar().getValue());
		//this.astro.capricorne=this.getHeight()*this.mousewheel/2;
		g2d = (Graphics2D) g;
		this.centreX=this.getPreferredSize().getWidth()/2.0;
		this.centreY=this.getPreferredSize().getHeight()/2.0;
		
		//Font...
		this.currentFont = g2d.getFont();
		//System.out.println(this.currentFont.getSize2D());
		
		//System.out.println(g2d.getBackground());
		Point2D temp=new Point(0,0), tempOmbre = new Point(0, 0);
		Ellipse2D ell;
		this.astro.tPlanetes.populate();
		
		this.astro.capricorne=this.centreY-(this.getPreferredSize().getHeight()/10.0);
		//astro.equateur=astro.capricorne/Math.tan(Math.toRadians(45.0+23.5/2.0));
		astro.equateur=astro.capricorne/Math.tan(Math.toRadians(45.0+this.astro.calc.eclipticTrueObliquity/2.0));
    //astro.cancer=astro.equateur*Math.tan(Math.toRadians(45.0-23.5/2.0));
    astro.cancer=astro.equateur*Math.tan(Math.toRadians(45.0-this.astro.calc.eclipticTrueObliquity/2.0));
    astro.ecliptique=((astro.capricorne+astro.cancer)/2.0);
		
		//System.out.println(astro.capricorne+" -- "+astro.equateur+" -- "+astro.cancer);
		
		
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
		g2d.setColor(Color.RED);
	
		//Masquer tout ce qui est en dehors du capricorne
		masque=new Ellipse2D.Double(this.centreX-(int)astro.capricorne, this.centreY-(int)astro.capricorne, (int)astro.capricorne*2, (int)astro.capricorne*2);
		g2d.clip(masque);
		
		//dessin du tympan
		if(astro.affichage.tympan.isSelected())
		  this.dessinerTympan();
		
		//enlever le masque du tympan
		g2d.setClip(null);

		//Ne dessiner que dans la partie droite du JSplitpane
		g2d.setClip(this.astro.sp2.getHorizontalScrollBar().getValue(), this.astro.sp2.getVerticalScrollBar().getValue(), this.getParent().getWidth(), this.getParent().getHeight());
				
		//dessin de l'araignée
		//traçage des lignes cardinales
		g2d.setStroke(normal);
		g2d.draw(new Line2D.Double(0,this.centreY, this.getWidth(), this.centreY));
		g2d.draw(new Line2D.Double(this.centreX, 0, this.centreX, this.getHeight()));

    //coefficients selon hémisphère
    if(this.astro.home.latitude>=0) {
      this.astro.face.hemiCoefDec=1.0;
      this.astro.face.hemiCoefRA=0.0;
    }
    else {
      this.astro.face.hemiCoefDec=-1.0;
      this.astro.face.hemiCoefRA=180.0;
    }
		
		//graduations horaires
		double angleHoraire = 0.0;
		while(angleHoraire<360.0) {
		  if(angleHoraire%15==0) {//heures...
  		  g2d.draw(new Line2D.Double(this.centreX+(Math.cos(Math.toRadians(angleHoraire))*this.astro.capricorne),
  		      this.centreY-(Math.sin(Math.toRadians(angleHoraire))*this.astro.capricorne),
  		      this.centreX+(Math.cos(Math.toRadians(angleHoraire))*(this.astro.capricorne+20)),
            this.centreY-(Math.sin(Math.toRadians(angleHoraire))*(this.astro.capricorne+20))
  		      ));
  		  this.affine = g2d.getTransform();
  		  this.affineTemp = new AffineTransform();
        //this.affineTemp.concatenate(this.affine);
  		  if(angleHoraire/15.0<10.0)
  		    this.affineTemp.rotate(-2*(this.currentFont.getSize2D())/(2*Math.PI*this.astro.capricorne), this.centreX, this.centreY);
  		  else
          this.affineTemp.rotate(-2*(this.currentFont.getSize2D())/(Math.PI*this.astro.capricorne), this.centreX, this.centreY);
  		  this.affineTemp.rotate(angleHoraire*this.astro.face.hemiCoefDec*Math.PI/180.0,
            (float)this.centreX+(float)(Math.cos(Math.toRadians(-angleHoraire*this.astro.face.hemiCoefDec+90))*(this.astro.capricorne+25)),
            (float)this.centreY-(float)(Math.sin(Math.toRadians(-angleHoraire*this.astro.face.hemiCoefDec+90))*(this.astro.capricorne+25))
        );
        g2d.transform(this.affineTemp);
        g2d.drawString(String.valueOf((int)angleHoraire/15),
            (float)this.centreX+(float)(Math.cos(Math.toRadians(-angleHoraire*this.astro.face.hemiCoefDec+90))*(this.astro.capricorne+25)),
            (float)this.centreY-(float)(Math.sin(Math.toRadians(-angleHoraire*this.astro.face.hemiCoefDec+90))*(this.astro.capricorne+25))
        );
        g2d.setTransform(affine);
		  }
      else if (angleHoraire%7.5==0) {//30 minutes...
        g2d.draw(new Line2D.Double(this.centreX+(Math.cos(Math.toRadians(angleHoraire))*this.astro.capricorne),
            this.centreY-(Math.sin(Math.toRadians(angleHoraire))*this.astro.capricorne),
            this.centreX+(Math.cos(Math.toRadians(angleHoraire))*(this.astro.capricorne+15)),
            this.centreY-(Math.sin(Math.toRadians(angleHoraire))*(this.astro.capricorne+15))
            ));
      }
		  else if (angleHoraire%(15/6.0)==0) {//10 minutes...
        g2d.draw(new Line2D.Double(this.centreX+(Math.cos(Math.toRadians(angleHoraire))*this.astro.capricorne),
            this.centreY-(Math.sin(Math.toRadians(angleHoraire))*this.astro.capricorne),
            this.centreX+(Math.cos(Math.toRadians(angleHoraire))*(this.astro.capricorne+10)),
            this.centreY-(Math.sin(Math.toRadians(angleHoraire))*(this.astro.capricorne+10))
            ));
		  }
      else if (angleHoraire%(15/60.0)==0) {//minutes...
        g2d.draw(new Line2D.Double(this.centreX+(Math.cos(Math.toRadians(angleHoraire))*this.astro.capricorne),
            this.centreY-(Math.sin(Math.toRadians(angleHoraire))*this.astro.capricorne),
            this.centreX+(Math.cos(Math.toRadians(angleHoraire))*(this.astro.capricorne+5)),
            this.centreY-(Math.sin(Math.toRadians(angleHoraire))*(this.astro.capricorne+5))
            ));
		  }
		  angleHoraire += 0.25;
		}
		
		//dessin du tropique du Capricorne, Equateur, Cancer
		g2d.draw(new Ellipse2D.Double(this.centreX-(int)astro.capricorne, this.centreY-(int)astro.capricorne, (int)astro.capricorne*2, (int)astro.capricorne*2));
		g2d.draw(new Ellipse2D.Double(this.centreX-(int)astro.equateur, this.centreY-(int)astro.equateur, (int)astro.equateur*2, (int)astro.equateur*2));
		g2d.draw(new Ellipse2D.Double(this.centreX-(int)astro.cancer, this.centreY-(int)astro.cancer, (int)astro.cancer*2, (int)astro.cancer*2));
		//System.out.println(astro.capricorne);

		//rotation de l'ensemble...
		affine=g2d.getTransform();
		//if(this.astro.coordGeo.latitude>0)
			affine.rotate(this.hemiCoefDec*Math.toRadians(90.0+this.astro.calc.localSiderealTime), (double)this.centreX, (double) this.centreY);
		//else//hémisphère SUD...foireux...
			//affine.rotate(-Math.toRadians(90.0+this.astro.calc.localSiderealTime), (double)this.centreX, (double) this.centreY);
			//affine.scale(2.0, 2.0);//pas bon, tout est "épaissi" (ligne, texte, points,etc...)
			//affine.shear(2.0, 2.0);//pas bon : étirement !
		g2d.setTransform(affine);
		
		//dessin des étoiles
		//System.out.println("taille stars2 = "+this.stars2.length%3);
		int i=0;
    while(i<this.astro.stars.length) {     
    this.astro.stars[i].getCurrentLocation().setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.stars[i].getDec()/2.0))*Math.cos(Math.toRadians(this.astro.stars[i].getAd()*this.hemiCoefDec+this.hemiCoefRA/*sur ?*/)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.stars[i].getDec()/2.0))*Math.sin(Math.toRadians(this.astro.stars[i].getAd()*this.hemiCoefDec+this.hemiCoefRA/*sur ?*/)));
    g2d.setColor(this.astro.stars[i].getRvb());
    g2d.setStroke(normal);
    if(this.hemiCoefDec*this.astro.stars[i].getDec() > -30.0)
      if(this.astro.affichage.magSlider.getValue()>= this.astro.stars[i].getMag()) {
        if(this.astro.stars[i].getMag()<2.7) {
          //ell=new Ellipse2D.Double(this.astro.stars[i].getCurrentLocation().getX()-2, this.astro.stars[i].getCurrentLocation().getY()-2, 4, 4);
          this.astro.stars[i].ellipse.setFrame(this.astro.stars[i].getCurrentLocation().getX()-2, this.astro.stars[i].getCurrentLocation().getY()-2, 4, 4);
				}
        else if(this.astro.stars[i].getMag()<4.2) {
          this.astro.stars[i].ellipse.setFrame(this.astro.stars[i].getCurrentLocation().getX()-1, this.astro.stars[i].getCurrentLocation().getY()-1, 2, 2);
				}
				else {
				  this.astro.stars[i].ellipse.setFrame(this.astro.stars[i].getCurrentLocation().getX(), this.astro.stars[i].getCurrentLocation().getY(), 1, 1);
				}
//        g2d.draw(ell);
//        g2d.fill(ell);
        g2d.draw(this.astro.stars[i].ellipse);
        g2d.fill(this.astro.stars[i].ellipse);
			}

    g2d.setColor(Color.WHITE);
    //dessin des astérismes partant de l'étoile
      g2d.setStroke(dotted);
      for(int j=0; j<this.astro.stars[i].nextAstre.size(); j++) {
        if(this.astro.affichage.asterismes.isSelected()) {
          //if(this.astro.stars[i].getNextAstre() != null) {
            g2d.draw(new Line2D.Double(this.astro.stars[i].getCurrentLocation(), this.astro.stars[i].getNextAstre(j).getCurrentLocation()));
          //}
        }
      }
      i++;
		}

    g2d.setStroke(normal);
		//dessin de Neptune
    if(this.astro.affichage.checkNeptune.isSelected()) {
  		temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[7].declination/2))*Math.cos(this.hemiCoefDec*Math.toRadians(this.astro.planetes[7].ra+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[7].declination/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.planetes[7].ra+this.hemiCoefRA)));
  		g2d.setColor(Color.CYAN);
      tempDouble = Math.sqrt(4*4+4*4);//rayon
  		ell=new Ellipse2D.Double(temp.getX() - tempDouble, temp.getY() - tempDouble, 2 * tempDouble, 2 * tempDouble);
  		g2d.draw(ell);
  		g2d.fill(ell);
  		//g2d.fillOval(temp.x-5, temp.y-5, 10, 10);
    }
    
		//dessin d'Uranus
    if(this.astro.affichage.checkUranus.isSelected()) {
  		temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-(this.hemiCoefDec*this.astro.planetes[6].declination/2)))*Math.cos(this.hemiCoefDec*Math.toRadians(this.astro.planetes[6].ra+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[6].declination/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.planetes[6].ra+this.hemiCoefRA)));
  		g2d.setColor(Color.GREEN);
      tempDouble = Math.sqrt(4*4+4*4);//rayon
      ell=new Ellipse2D.Double(temp.getX() - tempDouble, temp.getY() - tempDouble, 2 * tempDouble, 2 * tempDouble);
  		g2d.draw(ell);
  		g2d.fill(ell);
  		//g2d.fillOval(temp.x-5, temp.y-5, 10, 10);
    }
    
		//dessin de Saturne
    if(this.astro.affichage.checkSaturne.isSelected()) {
  		temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[5].declination/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.planetes[5].ra+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[5].declination/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.planetes[5].ra+this.hemiCoefRA)));
  		g2d.setColor(Color.ORANGE);
      tempDouble = Math.sqrt(4*4+4*4);//rayon
      ell=new Ellipse2D.Double(temp.getX() - tempDouble, temp.getY() - tempDouble, 2 * tempDouble, 2 * tempDouble);
  		g2d.draw(ell);
  		g2d.fill(ell);
  		//g2d.fillOval(temp.x-5, temp.y-5, 10, 10);
    }
    
		//dessin de Jupiter
    if(this.astro.affichage.checkJupiter.isSelected()) {
  		temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[4].declination/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.planetes[4].ra+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[4].declination/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.planetes[4].ra+this.hemiCoefRA)));
  		g2d.setColor(Color.MAGENTA);
      tempDouble = Math.sqrt(4*4+4*4);//rayon
      ell=new Ellipse2D.Double(temp.getX() - tempDouble, temp.getY() - tempDouble, 2 * tempDouble, 2 * tempDouble);
  		g2d.draw(ell);
  		g2d.fill(ell);
  		//g2d.fillOval(temp.x-5, temp.y-5, 10, 10);
    }
    
		//dessin de Mars
    if(this.astro.affichage.checkMars.isSelected()) {
  		temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[3].declination/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.planetes[3].ra+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[3].declination/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.planetes[3].ra+this.hemiCoefRA)));
  		g2d.setColor(Color.RED);
      tempDouble = Math.sqrt(4*4+4*4);//rayon
      ell=new Ellipse2D.Double(temp.getX() - tempDouble, temp.getY() - tempDouble, 2 * tempDouble, 2 * tempDouble);
  		g2d.draw(ell);
  		g2d.fill(ell);
  		//g2d.fillOval(temp.x-5, temp.y-5, 10, 10);
    }
    
		//dessin de Vénus
		//temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.astro.venusDeclination/2))*Math.cos(Math.toRadians(astro.venusRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-astro.venusDeclination/2))*Math.sin(Math.toRadians(astro.venusRA)));
	  if(this.astro.affichage.checkVenus.isSelected()) {
  		temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[1].declination/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.planetes[1].ra+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[1].declination/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.planetes[1].ra+this.hemiCoefRA)));
  		g2d.setColor(Color.WHITE);
      tempDouble = Math.sqrt(4*4+4*4);//rayon
      ell=new Ellipse2D.Double(temp.getX() - tempDouble, temp.getY() - tempDouble, 2 * tempDouble, 2 * tempDouble);
  		g2d.draw(ell);
  		g2d.fill(ell);
  		//g2d.fillOval(temp.x-5, temp.y-5, 10, 10);
	  }
	  
		//dessin de Mercure
		//temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-astro.mercuryDeclination/2))*Math.cos(Math.toRadians(astro.mercuryRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-astro.mercuryDeclination/2))*Math.sin(Math.toRadians(astro.mercuryRA)));
  	if(this.astro.affichage.checkMercure.isSelected()) {
  		temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[0].declination/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.planetes[0].ra+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.planetes[0].declination/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.planetes[0].ra+this.hemiCoefRA)));
  		g2d.setColor(Color.DARK_GRAY);
      tempDouble = Math.sqrt(4*4+4*4);//rayon
      ell=new Ellipse2D.Double(temp.getX() - tempDouble, temp.getY() - tempDouble, 2 * tempDouble, 2 * tempDouble);
  		g2d.draw(ell);
  		g2d.fill(ell);
  		//g2d.fillOval(temp.x-5, temp.y-5, 10, 10);
		}
		
		//dessin de l'écliptique à midi heure solaire locale au moment du solstice d'hiver
		g2d.setColor(Color.YELLOW);
		g2d.setStroke(normal);
		ell=new Ellipse2D.Double(this.centreX-(int)astro.ecliptique, this.centreY-(int)astro.ecliptique-this.hemiCoefDec*((int)astro.capricorne-(int)astro.cancer)/2.0, (int)astro.ecliptique*2.0, (int)astro.ecliptique*2.0);
			
		g2d.draw(ell);
		//g2d.drawOval(this.centreX-(int)astro.ecliptique, this.centreY-(int)astro.ecliptique-((int)astro.capricorne-(int)astro.cancer)/2, (int)astro.ecliptique*2, (int)astro.ecliptique*2);
		
		//dessin ligne Temps Sidéral Local
		if(this.astro.affichage.tsl.isSelected()) {
      temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0))*Math.cos(Math.toRadians(this.hemiCoefRA)),
                       this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0))*Math.sin(Math.toRadians(this.hemiCoefRA)));
      g2d.setColor(Color.WHITE);
      g2d.draw(new Line2D.Double(this.centreX-(int)astro.capricorne*this.hemiCoefDec, this.centreY, temp.getX(), temp.getY()));
		}
		
		//dessin de l'astre recherché et son angle horaire
		
		if(this.astro.affichage.astre.isSelected()) {//déclinaison : le signe "-" uniquement dans la valeur "degré"
		  if(Double.parseDouble(this.astro.affichage.astreDECDegre.getText()) < 0.0 )
		    this.tempDouble = Double.parseDouble(this.astro.affichage.astreDECDegre.getText()) - Double.parseDouble(this.astro.affichage.astreDECMinute.getText())/60.0 - Double.parseDouble(this.astro.affichage.astreDECSeconde.getText())/3600.0;
		  else
		    this.tempDouble = Double.parseDouble(this.astro.affichage.astreDECDegre.getText()) + Double.parseDouble(this.astro.affichage.astreDECMinute.getText())/60.0 + Double.parseDouble(this.astro.affichage.astreDECSeconde.getText())/3600.0;
		  
      temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(this.tempDouble)/2))*Math.cos(Math.toRadians(this.hemiCoefDec*(Double.parseDouble(this.astro.affichage.astreADHeure.getText())*15.0+Double.parseDouble(this.astro.affichage.astreADMinute.getText())/60.0*15.0+Double.parseDouble(this.astro.affichage.astreADSeconde.getText())/3600.0*15.0)+this.hemiCoefRA)),
                       this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(this.tempDouble)/2))*Math.sin(Math.toRadians(this.hemiCoefDec*(Double.parseDouble(this.astro.affichage.astreADHeure.getText())*15.0+Double.parseDouble(this.astro.affichage.astreADMinute.getText())/60.0*15.0+Double.parseDouble(this.astro.affichage.astreADSeconde.getText())/3600.0*15.0)+this.hemiCoefRA)));
      g2d.setColor(Color.ORANGE);
      ell=new Ellipse2D.Double(temp.getX()-7, temp.getY()-7, 14, 14);
      g2d.draw(ell);
      //g2d.fill(ell);
      //g2d.fillOval(temp.x-5, temp.y-5, 10, 10);
      g2d.setStroke(dashed);
      g2d.draw(new Line2D.Double(
          this.centreX-astro.capricorne*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(0)/2))*Math.cos(Math.toRadians(this.hemiCoefDec*(Double.parseDouble(this.astro.affichage.astreADHeure.getText())*15.0+Double.parseDouble(this.astro.affichage.astreADMinute.getText())/60.0*15.0+Double.parseDouble(this.astro.affichage.astreADSeconde.getText())/3600.0*15.0)+this.hemiCoefRA)),
          this.centreY+astro.capricorne*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(0)/2))*Math.sin(Math.toRadians(this.hemiCoefDec*(Double.parseDouble(this.astro.affichage.astreADHeure.getText())*15.0+Double.parseDouble(this.astro.affichage.astreADMinute.getText())/60.0*15.0+Double.parseDouble(this.astro.affichage.astreADSeconde.getText())/3600.0*15.0)+this.hemiCoefRA)),
          temp.getX(),
          temp.getY()
      ));
      this.astro.affichage.astreAHValue.setText(this.astro.calc.angleDecimalToHMS(this.astro.calc.calculateAngleHoraireFromEquatorial(Double.parseDouble(this.astro.affichage.astreADHeure.getText())*15.0+Double.parseDouble(this.astro.affichage.astreADMinute.getText())/60.0*15.0+Double.parseDouble(this.astro.affichage.astreADSeconde.getText())/3600.0*15.0)));
      this.astro.affichage.astreVPValue.setText(this.astro.calc.angleDecimalToHMS(this.astro.calc.viseurPolaire(Double.parseDouble(this.astro.affichage.astreADHeure.getText())*15.0+Double.parseDouble(this.astro.affichage.astreADMinute.getText())/60.0*15.0+Double.parseDouble(this.astro.affichage.astreADSeconde.getText())/3600.0*15.0)));
		}
		
		//dessin du soleil
    if(this.astro.affichage.checkSoleil.isSelected()) {
  		temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.calc.sunDeclination/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.calc.sunRightAscension+this.hemiCoefRA)),
  		                 this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.calc.sunDeclination/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.calc.sunRightAscension+this.hemiCoefRA)));
  		g2d.setColor(Color.YELLOW);
      g2d.setStroke(normal);
      if(this.astro.affichage.checkModeEclipse.isSelected()) {
        tempOmbre.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(this.astro.calc.sunDeclination + this.astro.calc.angleApparentSoleil)/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.calc.sunRightAscension+ this.astro.calc.angleApparentSoleil+this.hemiCoefRA)),
            this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(this.astro.calc.sunDeclination + this.astro.calc.angleApparentSoleil)/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.calc.sunRightAscension+ this.astro.calc.angleApparentSoleil+this.hemiCoefRA)));
        tempDouble = Math.sqrt( 
            (temp.getX()-tempOmbre.getX()) * (temp.getX()-tempOmbre.getX())
            +
            (temp.getY()-tempOmbre.getY()) * (temp.getY()-tempOmbre.getY())
            );
        ell = new Ellipse2D.Double(temp.getX() - tempDouble, temp.getY()- tempDouble, 2 * tempDouble, 2 * tempDouble);
      }
      else {
        ell = new Ellipse2D.Double(temp.getX()-7, temp.getY()-7, 14, 14);
      }
  		g2d.draw(ell);
  		g2d.fill(ell);
  		//g2d.fillOval(temp.x-5, temp.y-5, 10, 10);
  		g2d.draw(new Line2D.Double(this.centreX, this.centreY, temp.getX(), temp.getY()));
    }
    
    //dessin du PAS
    if(this.astro.affichage.checkPAS.isSelected()) {
      temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(-this.astro.calc.sunDeclination)/2))*Math.cos(Math.toRadians(this.hemiCoefDec*(this.astro.calc.sunRightAscension+180.0)+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(-this.astro.calc.sunDeclination)/2))*Math.sin(Math.toRadians(this.hemiCoefDec*(this.astro.calc.sunRightAscension+180.0)+this.hemiCoefRA)));
      g2d.setStroke(dashed);
      g2d.setColor(Color.YELLOW);
      g2d.draw(new Line2D.Double(this.centreX, this.centreY, temp.getX(), temp.getY()));
      
      if(this.astro.affichage.checkModeEclipse.isSelected()) {
        g2d.setColor(Color.GRAY);
        g2d.setStroke(dotted);
        tempOmbre.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(-this.astro.calc.sunDeclination - this.astro.calc.angleApparentOmbre)/2  ))*Math.cos(Math.toRadians(this.hemiCoefDec*(this.astro.calc.sunRightAscension+this.astro.calc.angleApparentOmbre+180.0)+this.hemiCoefRA)),
            this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(-this.astro.calc.sunDeclination - this.astro.calc.angleApparentOmbre)/2  ))*Math.sin(Math.toRadians(this.hemiCoefDec*(this.astro.calc.sunRightAscension+this.astro.calc.angleApparentOmbre+180.0)+this.hemiCoefRA)));
        tempDouble = Math.sqrt( 
            (temp.getX()-tempOmbre.getX()) * (temp.getX()-tempOmbre.getX())
            +
            (temp.getY()-tempOmbre.getY()) * (temp.getY()-tempOmbre.getY())
            );
        ell = new Ellipse2D.Double(temp.getX() - tempDouble, temp.getY()- tempDouble, 2 * tempDouble, 2 * tempDouble);
        g2d.draw(ell);

        tempOmbre.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(-this.astro.calc.sunDeclination - this.astro.calc.angleApparentPenombre)/2  ))*Math.cos(Math.toRadians(this.hemiCoefDec*(this.astro.calc.sunRightAscension+this.astro.calc.angleApparentPenombre+180.0)+this.hemiCoefRA)),
            this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(-this.astro.calc.sunDeclination - this.astro.calc.angleApparentPenombre)/2  ))*Math.sin(Math.toRadians(this.hemiCoefDec*(this.astro.calc.sunRightAscension+this.astro.calc.angleApparentPenombre+180.0)+this.hemiCoefRA)));
        tempDouble = Math.sqrt( 
            (temp.getX()-tempOmbre.getX()) * (temp.getX()-tempOmbre.getX())
            +
            (temp.getY()-tempOmbre.getY()) * (temp.getY()-tempOmbre.getY())
            );
        ell = new Ellipse2D.Double(temp.getX() - tempDouble, temp.getY()- tempDouble, 2 * tempDouble, 2 * tempDouble);
        g2d.draw(ell);
      }
    }
    
    //dessin de la Lune
		if(this.astro.affichage.checkLune.isSelected()) {
  		temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.moon.moonDeclination/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.moon.moonRA+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.moon.moonDeclination/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.moon.moonRA+this.hemiCoefRA)));
      tempOmbre.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(this.astro.moon.moonDeclination + this.astro.calc.angleApparentLune)/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.moon.moonRA+this.astro.calc.angleApparentLune+this.hemiCoefRA)),
                            this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*(this.astro.moon.moonDeclination + this.astro.calc.angleApparentLune)/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.moon.moonRA+this.astro.calc.angleApparentLune+this.hemiCoefRA)));
  		g2d.setColor(Color.GRAY);
      g2d.setStroke(normal);
      if(this.astro.affichage.checkModeEclipse.isSelected() ) {
      tempDouble = Math.sqrt( 
          (temp.getX()-tempOmbre.getX()) * (temp.getX()-tempOmbre.getX())
          +
          (temp.getY()-tempOmbre.getY()) * (temp.getY()-tempOmbre.getY())
          );
      ell = new Ellipse2D.Double(temp.getX() - tempDouble, temp.getY()- tempDouble, 2 * tempDouble, 2 * tempDouble);
      }
      else {
        ell = new Ellipse2D.Double(temp.getX()-7, temp.getY()-7, 14, 14);
      }
  		g2d.draw(ell);
  		g2d.fill(ell);
  		//g2d.fillOval(temp.x-5, temp.y-5, 10, 10);
  		g2d.draw(new Line2D.Double(this.centreX, this.centreY, temp.getX(), temp.getY()));
		}
    //dessin noeud ascendant de la Lune
		if(this.astro.affichage.noeudASC.isSelected()) {
  		temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.moon.luneNoeudAscendantDeclinaison/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.moon.luneNoeudAscendantRA+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.moon.luneNoeudAscendantDeclinaison/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.moon.luneNoeudAscendantRA+this.hemiCoefRA)));
      g2d.setStroke(dashed);
  		g2d.setColor(Color.GRAY);
  		g2d.draw(new Ellipse2D.Double(temp.getX()-5, temp.getY()-5, 10, 10));
  		g2d.draw(new Line2D.Double(this.centreX, this.centreY, temp.getX(), temp.getY()));
		}

		//dessin noeud descendant de la Lune
    if(this.astro.affichage.noeudDESC.isSelected()) {
  		temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.moon.luneNoeudDescendantDeclinaison/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.moon.luneNoeudDescendantRA+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.moon.luneNoeudDescendantDeclinaison/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.moon.luneNoeudDescendantRA+this.hemiCoefRA)));
      g2d.setStroke(dashed);
  		g2d.setColor(Color.GRAY);
  		g2d.draw(new Ellipse2D.Double(temp.getX()-5, temp.getY()-5, 10, 10));
  		g2d.draw(new Line2D.Double(this.centreX, this.centreY, temp.getX(), temp.getY()));
    }
    
		//dessin satellites GPS		
		//Rectangle2D rect;
    if(this.astro.affichage.satGPS.isSelected()) {
  		for(i=0; i<this.astro.gps.satellitesGPS.length; i++) {
  			if(this.astro.gps.satellitesGPS[i] != null) {
  				temp.setLocation(this.centreX-astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.gps.satellitesGPS[i].coordonneeGeographique.declination/2))*Math.cos(Math.toRadians(this.hemiCoefDec*this.astro.gps.satellitesGPS[i].coordonneeGeographique.ra+this.hemiCoefRA)), this.centreY+astro.equateur*Math.tan(Math.toRadians(45.0-this.hemiCoefDec*this.astro.gps.satellitesGPS[i].coordonneeGeographique.declination/2))*Math.sin(Math.toRadians(this.hemiCoefDec*this.astro.gps.satellitesGPS[i].coordonneeGeographique.ra+this.hemiCoefRA)));
  				if(this.astro.gps.satellitesGPS[i].coordonneeGeographique.elevation>=this.astro.cutoffAngle)
  					g2d.setColor(Color.YELLOW);
  				else
  					g2d.setColor(Color.MAGENTA);
  				//rect=new Rectangle2D.Double(temp.getX()-3, temp.getY()-3, 6, 6);
  				//g2d.draw(rect);
  				//g2d.fill(rect);
          this.affine = g2d.getTransform();
          this.affineTemp = new AffineTransform();
          affineTemp.rotate(-this.hemiCoefDec*Math.toRadians(90.0+this.astro.calc.localSiderealTime), (float)temp.getX(), (float)temp.getY());
          g2d.transform(this.affineTemp);

  				g2d.drawString(String.valueOf(this.astro.gps.satellitesGPS[i].id), (float)temp.getX(), (float)temp.getY());
  		    g2d.setTransform(affine); 		  
  			}
  		}
    }
}
	
	private void dessinerTympan() {
		double distance, rayon, phi=this.astro.coordGeo.getLatitude(), hauteur,
				horizondistance, horizonrayon,
				centrezenith, estouestrayon, droitecentres,
				distancefoyer, rayonvertical, azimut;
		
		if(phi != 0.0)
			if(phi<0)
				phi=-phi;
		//dessin des almucantarats
		//distance centre astrolabe au centre de l'almucantarat :
		//equateur * cos latitude / ( sin latitude + sin hauteur )
		//
		//rayon de l'almucantarat :
		//equateur * cos hauteur / ( sin latitude + sin hauteur )
		

		//dessin de l'horizon (hauteur=0)
		g2d.setStroke(gras);
		horizondistance=astro.equateur*Math.cos(Math.toRadians(phi))/Math.sin(Math.toRadians(phi));
		horizonrayon=astro.equateur/Math.sin(Math.toRadians(phi));
		g2d.draw(new Ellipse2D.Double(this.centreX-horizonrayon, this.centreY-horizondistance-horizonrayon, horizonrayon*2.0, horizonrayon*2.0));
		//System.out.println(distance+" -- "+rayon);
		//masquer tout sauf la sph�re locale

		//dessin des lignes de cr�puscule
		g2d.setStroke(dashed);
		hauteur=-6.0;
		while(hauteur>=-18.0){
			distance=astro.equateur*Math.cos(Math.toRadians(phi))/(Math.sin(Math.toRadians(phi))+Math.sin(Math.toRadians(hauteur)));
			rayon=Math.abs(astro.equateur*Math.cos(Math.toRadians(hauteur))/(Math.sin(Math.toRadians(phi))+Math.sin(Math.toRadians(hauteur))));
			System.out.println("Face - hauteur : " + hauteur + " -- distance : " + distance + " -- rayon : " + rayon);
			g2d.draw(new Ellipse2D.Double(this.centreX-rayon, this.centreY-distance-rayon, rayon*2.0, rayon*2.0));
			hauteur-=6.0;
		}
		g2d.setStroke(normal);

		//en plus du capricorne, on masque selon l'horizon
		masque=new Ellipse2D.Double(this.centreX-horizonrayon, this.centreY-horizondistance-horizonrayon, horizonrayon*2.0, horizonrayon*2.0);
		g2d.clip(masque);
		//g2d.drawOval(0, 0, 500, 500);

		//simulation aube/cr�puscule
		if(this.astro.calc.sunHeight < 0.0){
			if(this.astro.calc.sunHeight>=-18.0) {
				//System.out.println("couleur b = "+(18.0+this.astro.calc.sunHeight)/18.0*255.0);
				g2d.setBackground(new Color(0,0,(int)((18.0+this.astro.calc.sunHeight)/18.0*255.0)));
			}
			else {
				g2d.setBackground(Color.BLACK);
			}
		}
		else {
			g2d.setBackground(Color.BLUE);
		}
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
		g2d.setColor(Color.RED);
		
		//if(astro.affichage.tympan.isSelected()) {
			//dessin des almucantarats
			g2d.setStroke(normal);
			if(this.astro.affichage.almuvert5.isSelected()){
				this.astro.affichage.almuvert1.setEnabled(true);
				hauteur=0.0;
			}
			else {
				this.astro.affichage.almuvert1.setSelected(false);
				this.astro.affichage.almuvert1.setEnabled(false);
				hauteur=10.0;
			}
			
			while(hauteur<=85.0){
				if((int)hauteur%10==0){
					g2d.setStroke(normal);
				}
				else if((int)hauteur%5==0){
					g2d.setStroke(dashed);
				} 
				else {
					g2d.setStroke(dotted);
				}
				distance=astro.equateur*Math.cos(Math.toRadians(phi))/(Math.sin(Math.toRadians(phi))+Math.sin(Math.toRadians(hauteur)));
				rayon=astro.equateur*Math.cos(Math.toRadians(hauteur))/(Math.sin(Math.toRadians(phi))+Math.sin(Math.toRadians(hauteur)));
				//System.out.println(hauteur+" -- "+distance+" -- "+rayon);
				g2d.draw(new Ellipse2D.Double(this.centreX-rayon, this.centreY-distance-rayon, rayon*2.0, rayon*2.0));

				if(this.astro.affichage.almuvert1.isSelected())
					hauteur+=1.0;
				else 
					if(this.astro.affichage.almuvert5.isSelected())
						hauteur+=5.0;
					else
						hauteur+=10.0;
			}
			//}
		
		hauteur=85.0;
		distance=astro.equateur*Math.cos(Math.toRadians(phi))/(Math.sin(Math.toRadians(phi))+Math.sin(Math.toRadians(hauteur)));
		rayon=astro.equateur*Math.cos(Math.toRadians(hauteur))/(Math.sin(Math.toRadians(phi))+Math.sin(Math.toRadians(hauteur)));
		masqueZenith=new Ellipse2D.Double(this.centreX-rayon, this.centreY-distance-rayon, rayon*2.0, rayon*2.0);
		
		//distance centre-z�nith
		//equateur * cos latitude / ( sin latitude + 1 )
		centrezenith=astro.equateur*Math.cos(Math.toRadians(phi))/(Math.sin(Math.toRadians(phi))+1.0);
		//g2d.fillOval(this.centreX-5, this.centreY-(int)centrezenith-5, 10, 10);
		g2d.setStroke(gras);
		//dessin verticaux
		//rayon 1er vertical=(R/2)*[tan(45-phi/2)+tan(45+phi/2)]
		//
		estouestrayon=(astro.equateur/2.0)*(Math.tan(Math.toRadians(45.0-phi/2.0))+Math.tan(Math.toRadians(45.0+phi/2.0)));
		droitecentres=estouestrayon-centrezenith;
		//System.out.println("droite : "+centrezenith+"--"+estouestrayon+"--"+droitecentres);
		//g2d.drawLine(0, this.centreY+(int)droitecentres, this.getWidth(), this.centreY+(int)droitecentres);
		g2d.draw(new Ellipse2D.Double(this.centreX-estouestrayon, this.centreY+droitecentres-estouestrayon, estouestrayon*2.0, estouestrayon*2.0));
		
		//if(astro.affichage.tympan.isSelected()) {
			
			//les autres verticaux
			//distance du foyer = rayon 1er vertical * tan de l'azimut
			//rayon du vertical = sqrt(distance_foyer^2 + rayon 1er vertical^2)
			//TODO dessin foireux � partir de la latitude 85�...
			
			if(this.astro.affichage.almuvert5.isSelected())
					azimut=0.0;
			else
				azimut=10.0;

			while(azimut<=90.0) {
				if((int)azimut%10==0){
					g2d.setStroke(normal);
				}
				else if((int)azimut%5==0){
					g2d.setStroke(dotted);
				} 
				else {
					g2d.setStroke(dotted);
				}
				
				if(azimut==45 || azimut==135
					|| azimut==225 || azimut==315)
					g2d.setStroke(dashed);
					

				distancefoyer=estouestrayon*Math.tan(Math.toRadians(azimut));
				rayonvertical=Math.sqrt(distancefoyer*distancefoyer+estouestrayon*estouestrayon);
				//System.out.println("vertical distance ="+distancefoyer+" rayon = "+rayonvertical);
//				g2d.drawOval(this.centreX+(int)distancefoyer-(int)rayonvertical, this.centreY+(int)droitecentres-(int)rayonvertical, (int)rayonvertical*2, (int)rayonvertical*2);
				g2d.draw(new Arc2D.Double(this.centreX+distancefoyer-rayonvertical, this.centreY+droitecentres-rayonvertical, rayonvertical*2.0, rayonvertical*2.0, 180, -180, Arc2D.OPEN));
//				g2d.drawOval(this.centreX-(int)distancefoyer-(int)rayonvertical, this.centreY+(int)droitecentres-(int)rayonvertical, (int)rayonvertical*2, (int)rayonvertical*2);
				g2d.draw(new Arc2D.Double(this.centreX-distancefoyer-rayonvertical, this.centreY+droitecentres-rayonvertical, rayonvertical*2.0, rayonvertical*2.0, 180, -180, Arc2D.OPEN));
				
				if(this.astro.affichage.almuvert5.isSelected())
						azimut+=5.0;
				else
					azimut+=10.0;
			}
			
			//masquage de la zone au z�nith (trop de lignes passent par l�...)
			g2d.clip(masqueZenith);
			g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
			//}
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		Dimension dim=new Dimension();
		Rectangle rect, oldRect;
		//int centrex, centrey, bottomx, bottomy;
		//int shift=1;
		
		//shift=(this.mousewheelSensibility)/2;
//    if(this.mousewheel>0 || e.getWheelRotation()>0){
    if(this.mousewheel>=0){
      this.mousewheel+=(e.getWheelRotation()*this.mousewheelSensibility);
      //if(e.getWheelRotation()<0) shift=-shift;
    }
    else {
      this.mousewheel = 0;
    }
    //System.out.println("mousewheel : " + this.mousewheel + " - mousewheelSensitivity : " + this.mousewheelSensibility);
		dim.setSize(this.astro.splitBottomDimension.getWidth()+this.mousewheel, this.astro.splitBottomDimension.getHeight()+this.mousewheel);
		oldRect=this.getVisibleRect();
		//System.out.println("scroll hamout = "+this.astro.sp2.getHorizontalScrollBar().getVisibleAmount()+"scroll hmax = "+this.astro.sp2.getHorizontalScrollBar().getMaximum());
		//System.out.println("scroll vamout = "+this.astro.sp2.getVerticalScrollBar().getVisibleAmount()+"scroll vmax = "+this.astro.sp2.getVerticalScrollBar().getMaximum());
		//centrex=oldRect.x+oldRect.width/2;
		//centrey=oldRect.y+oldRect.height/2;
		//bottomx=oldRect.x+oldRect.width;
		//bottomy=oldRect.y+oldRect.height;
		//System.out.println("oldrect width = "+oldRect.width+" prefwidth="+oldRect.getWidth()/this.getPreferredSize().getWidth());
//		if(this.mousewheel>0 || e.getWheelRotation()>0){
//			this.mousewheel+=(e.getWheelRotation()*this.mousewheelSensibility);
//			if(e.getWheelRotation()<0) shift=-shift;
//		}
		//dim=new Dimension(this.getWidth()+this.mousewheel, this.getHeight()+this.mousewheel);
		
		this.mouseX=e.getX();
		this.mouseY=e.getY();
		//System.out.println("mouse : X = "+this.mouseX+" Y = "+this.mouseY+" mousewheel = "+this.mousewheel+" e precisewheelrot = "+e.getWheelRotation());
		//System.out.println("X orig= "+this.getX()+" Y orig="+this.getY()+" x+width="+(this.getX()+oldRect.width));
		//System.out.println("Prefer width = "+ this.getPreferredSize().getWidth());
		//System.out.println("Prefer height = "+ this.getPreferredSize().getHeight());
		//System.out.println("oldRect X = "+oldRect.x+"  Y = "+oldRect.y+" centrex ="+centrex+" centrey = "+centrey+" bottom X = "+bottomx+" bottom Y = "+bottomy);
		//System.out.println("rect X = "+rect.x+"  Y = "+rect.y);
		this.setPreferredSize(dim);

		//ces 2 lignes ne servent à rien apparemment...commentées ou pas, c'est kif-kif bourriquot...
		rect=new Rectangle(this.mouseX-(oldRect.width/2), this.mouseY-(oldRect.height/2), oldRect.width, oldRect.height);
//tests    rect=new Rectangle(oldRect.x-this.mouseX+this.mousewheel, oldRect.y-this.mouseY+this.mousewheel, oldRect.width, oldRect.height);
//    rect=new Rectangle(this.mouseX-(dim.width*this.mouseX/oldRect.width), this.mouseY-(dim.height*this.mouseY/oldRect.height), oldRect.width, oldRect.height);
		this.scrollRectToVisible(rect);	
		
		//this.setLocation(this.mouseX, this.mouseY);
		this.revalidate();
	}

	public void mousePressed(MouseEvent e) {
	       //System.out.println("Mouse pressed; # of clicks: "
	         //           + e.getClickCount()+" e="+ e.toString());
	    }

	    public void mouseReleased(MouseEvent e) {
	       //saySomething("Mouse released; # of clicks: "
	                    //+ e.getClickCount(), e);
	    }

	    public void mouseEntered(MouseEvent e) {
	       //saySomething("Mouse entered", e);
	    }

	    public void mouseExited(MouseEvent e) {
	       //saySomething("Mouse exited", e);
	    }

	    public void mouseClicked(MouseEvent e) {
	       Rectangle rect, oldRect, starSeeker = new Rectangle(3, 3);
	       boolean hit = false;
	       int deltaX, deltaY;
	       
	       //System.out.println("Mouse clicked (# of clicks: "
	       //+ e.getClickCount() + ")"+ e.toString());
	       if(e.getButton() == MouseEvent.BUTTON1) {
  	       oldRect=this.getVisibleRect();
  	       this.mouseX=e.getX();
  	       this.mouseY=e.getY();
  			
  	       rect=new Rectangle(this.mouseX-(oldRect.width/2), this.mouseY-(oldRect.height/2), oldRect.width, oldRect.height);
  	       this.scrollRectToVisible(rect);	
  	       this.revalidate();
	       }
	       else {
	         //System.out.println("clic droit");
	         //System.out.println("preferred size : " + this.getSize());
           //System.out.println("visible size : " + this.getVisibleRect().getWidth());
           deltaX = this.getSize().width - this.getVisibleRect().getSize().width;
           deltaY = this.getSize().height - this.getVisibleRect().getSize().height;
           //System.out.println("deltaX : " + deltaX + " - deltaY : " + deltaY);
           
           starSeeker.setBounds(e.getX()-2-this.getVisibleRect().x+this.astro.splitPanel.getDividerLocation()+8, e.getY()-2-this.getVisibleRect().y, 4, 4);
	         //System.out.println("starSeeker : " + starSeeker.x + "," + starSeeker.y);
	         for(int i=0; i<this.astro.stars.length; i++) {
	           if(this.g2d.hit(starSeeker, this.astro.stars[i].ellipse, false)) {
	             //System.out.println("hit! : " + this.astro.stars[i].getId());
	             hit = true;
	             this.astro.affichage.astres.setSelectedIndex(this.astro.starsnames.indexOf(this.astro.stars[i].getId()));
	           }
	         }
	       }
	    }
	    
	    public void mouseDragged(MouseEvent e) {
	    	int deltaX=this.mouseX-e.getX(), 
	    		deltaY=this.mouseY-e.getY();
	    	JScrollBar hsb=this.astro.sp2.getHorizontalScrollBar(),
	    			   vsb=this.astro.sp2.getVerticalScrollBar();
	    	
    		//System.out.println("mouse dragged =");
    		hsb.setValue(hsb.getValue()+deltaX);
    		vsb.setValue(vsb.getValue()+deltaY);
    		//System.out.println("Face - mouseDragged - rect x,y : " + this.getVisibleRect().x + "," + this.getVisibleRect().y + " - mouse : " + e.getX() + "," + e.getY());
	    }
	    
	    public void mouseMoved(MouseEvent e) {
    		//System.out.println("mouse moved =");
    		this.mouseX=e.getX();
    		this.mouseY=e.getY();	    	
	    }
}
