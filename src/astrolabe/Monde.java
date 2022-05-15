package astrolabe;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;
//import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

//import java.awt.Toolkit;
import java.io.*;

//import com.sun.javafx.tk.Toolkit;

public class Monde extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {
	static final long serialVersionUID=1;
	
Astrolabe astro;
URL url;
HttpURLConnection httpConn;
BufferedImage image;
Graphics2D g2d;
int 	centreX, centreY,
		hauteur, largeur,
		mouseX, mouseY;

double	ratio;

	public Monde() {
		// TODO Auto-generated constructor stub
	}
	
	public Monde(Astrolabe a) {
		// TODO Auto-generated constructor stub
		this();
		this.astro=a;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		//this.image=new ImageIcon(".\\Mercator-projection.jpg").getImage();
		//this.image=new ImageIcon("./Mercator-projection.jpg").getImage();
		//this.image=new ImageIcon(Toolkit.getDefaultToolkit().getClass().getResource("/astrolabe/Mercator-projection.jpg").getPath()).getImage();
		//Toolkit.;//.       .getClass().getResource("/astrolabe/Mercator-projection.jpg");
		//this.image=new ImageIcon(getToolkit().getImage("Mercator-projection.jpg")).getImage();
		try {
      this.url = new URL("https://github.com/bendeg/astrolabe/raw/master/Mercator-projection.jpg");
			
			this.httpConn = (HttpURLConnection) url.openConnection();
		  //int responseCode = httpConn.getResponseCode();
			InputStream in = httpConn.getInputStream();
    	System.out.println("Chargement de la carte Mercator...");
	    this.image=ImageIO.read(in);

	    //Sauvegarde de l'image dans le répertoire courant
	    File mercatorProjection = new File("Mercator-projection.jpg"); 
	    ImageIO.write(this.image, "JPG", mercatorProjection);
	    
		}
		catch (IOException x) {
		    System.err.println(x);
		    System.out.println("Image 'Mercator-projection' non trouvée à l'URL : " + this.url.toExternalForm());
		    System.out.println("Lecture du fichier local...");
		    try {
  	      File mercatorProjection = new File("Mercator-projection.jpg"); 
  	      this.image = ImageIO.read(mercatorProjection);
		    }
		    catch(IOException ioe) {
	        System.err.println(ioe);		      
		    }
		}
		
		this.setPreferredSize(new Dimension(this.image.getWidth(this), this.image.getHeight(this)));
	}

	public void paintComponent(Graphics g) {
		this.g2d=(Graphics2D) g;
		Ellipse2D ell;
		
		double x, y;
		this.hauteur=this.getHeight();
		this.largeur=this.getWidth();
		this.centreX=this.largeur/2;
		this.centreY=this.hauteur/2;
		this.ratio=this.largeur/(2*Math.PI);
		
		g2d.drawImage(this.image, 0, 0, this);
		g2d.setColor(Color.WHITE);
		x=this.largeur*((this.astro.coordGeo.longitude+180.0)/360.0);
		y=this.centreY-Math.log(Math.tan((Math.PI/4.0) + (Math.toRadians(this.astro.coordGeo.latitude)/2.0)))*this.ratio;
		//System.out.println("x="+x+" y="+y);
		ell=new Ellipse2D.Double(x, y, 10, 10);
		g2d.draw(ell);
		g2d.fill(ell);
		
		for(int i=0; i<this.astro.gps.satellitesGPS.length; i++) {
			if(this.astro.gps.satellitesGPS[i] != null) {
				this.drawSatellite(this.astro.gps.satellitesGPS[i]);
			}
		}
		
		this.drawGPSGroundStations();
		
		g2d.drawString("Satellites GPS visibles ("+this.astro.cutoffAngle+"°) : "+String.valueOf(this.astro.gps.G.length), 10, 15);
		g2d.drawString("GDOP : "+String.valueOf(this.astro.gps.GDOP), 10, 30);
		g2d.drawString("PDOP : "+String.valueOf(this.astro.gps.PDOP), 10, 45);
		
	}
	
	public void drawSatellite(SatelliteGPS sat){
		double x, y;

		x=this.largeur*((sat.coordonneeGeographique.longitude+180.0)/360.0);
		y=this.centreY-Math.log(Math.tan((Math.PI/4.0) + (Math.toRadians(sat.coordonneeGeographique.latitude)/2.0)))*this.ratio;
		//System.out.println("x="+x+" y="+y);
		//rect=new Rectangle2D.Double(x, y, 10, 10);
		if(sat.coordonneeGeographique.elevation>=this.astro.cutoffAngle)
			g2d.setColor(Color.YELLOW);
		else
			g2d.setColor(Color.BLUE);
		g2d.drawString("gps"+String.valueOf(sat.id), (float)x, (float)y);
	}

	public void drawGPSGroundStations(){
		Ellipse2D ell;
		double x, y;
		int i;

		g2d.setColor(Color.CYAN);
		for(i=0; i<Gps.ground_stations.length; i+=3) {
			//System.out.println("Monde - Station "+(i/3)+" Long="+Gps.ground_stations[i+1]);
			if(Gps.ground_stations[i+1]>180.0)
				x=this.largeur*((Gps.ground_stations[i+1]-360.0+180.0)/360.0);
			else
				x=this.largeur*((Gps.ground_stations[i+1]+180.0)/360.0);
			y=this.centreY-Math.log(Math.tan((Math.PI/4.0) + (Math.toRadians(Gps.ground_stations[i])/2.0)))*this.ratio;
			ell=new Ellipse2D.Double(x, y, 10, 10);
			g2d.draw(ell);
			//g2d.fill(ell);
		}
		//System.out.println("nbre de station de controle="+i);
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		/*
		Dimension dim=new Dimension();
		Rectangle rect, oldRect;
		//int centrex, centrey, bottomx, bottomy;
		int shift=0;
		
		shift=(this.mousewheelSensibility)/2;
		dim.setSize(this.astro.splitBottomDimension.getWidth()+this.mousewheel, this.astro.splitBottomDimension.getHeight()+this.mousewheel);
		oldRect=this.getVisibleRect();
		//System.out.println("scroll hamout = "+this.astro.sp2.getHorizontalScrollBar().getVisibleAmount()+"scroll hmax = "+this.astro.sp2.getHorizontalScrollBar().getMaximum());
		//System.out.println("scroll vamout = "+this.astro.sp2.getVerticalScrollBar().getVisibleAmount()+"scroll vmax = "+this.astro.sp2.getVerticalScrollBar().getMaximum());
		//centrex=oldRect.x+oldRect.width/2;
		//centrey=oldRect.y+oldRect.height/2;
		//bottomx=oldRect.x+oldRect.width;
		//bottomy=oldRect.y+oldRect.height;
		//System.out.println("oldrect width = "+oldRect.width+" prefwidth="+oldRect.getWidth()/this.getPreferredSize().getWidth());
		if(this.mousewheel>0 || e.getWheelRotation()>0){
			this.mousewheel+=(e.getWheelRotation()*this.mousewheelSensibility);
			if(e.getWheelRotation()<0) shift=-shift;
		}
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
		
		rect=new Rectangle(this.mouseX-(oldRect.width/2), this.mouseY-(oldRect.height/2), oldRect.width, oldRect.height);
		this.scrollRectToVisible(rect);	
		//this.setLocation(this.mouseX, this.mouseY);
		this.revalidate();
		*/
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
	       Rectangle rect, oldRect;

	       //System.out.println("Mouse clicked (# of clicks: "
	       //+ e.getClickCount() + ")"+ e.toString());
	       
	       oldRect=this.getVisibleRect();
	       this.mouseX=e.getX();
	       this.mouseY=e.getY();
			
	       rect=new Rectangle(this.mouseX-(oldRect.width/2), this.mouseY-(oldRect.height/2), oldRect.width, oldRect.height);
	       this.scrollRectToVisible(rect);	
	       this.revalidate();
	    }
	    
	    public void mouseDragged(MouseEvent e) {
	    	int deltaX=this.mouseX-e.getX(), 
	    		deltaY=this.mouseY-e.getY();
	    	JScrollBar hsb=this.astro.spMonde.getHorizontalScrollBar(),
	    			   vsb=this.astro.spMonde.getVerticalScrollBar();
	    	
    		//System.out.println("mouse dragged =");
    		hsb.setValue(hsb.getValue()+deltaX);
    		vsb.setValue(vsb.getValue()+deltaY);
	    }
	    
	    public void mouseMoved(MouseEvent e) {
    		//System.out.println("mouse moved =");
    		this.mouseX=e.getX();
    		this.mouseY=e.getY();	    	
	    }

	
}
