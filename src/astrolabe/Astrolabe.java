package astrolabe;

//import java.net.URI;
import java.net.URL;
import java.util.Timer;
import java.awt.Color;
import java.awt.Dimension;
//import java.awt.DisplayMode;
//import java.awt.GraphicsDevice;
//import java.awt.GraphicsEnvironment;

import javafx.geometry.Point3D;

import javax.swing.JFrame;

import java.awt.event.*;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Astrolabe extends JFrame {
static final long serialVersionUID=1;
	Home home=new Home(0.001, 0.0);
	CoordonneeGeographique coordGeo;
	double cutoffAngle=15.0;
	Geodesy geodesy;
	Gps gps;
	
	Timer timer;
	
	JTabbedPane onglets;
	JSplitPane splitPanel;
	Face face;
	Parametres param;
	Affichage affichage;
	ModeAnimation anim;
	String[] nomsPlanetes={"Mercure", "Vénus", "Terre", "Mars", "Jupiter", "Saturne", "Uranus", "Neptune", "Pluton", "Lune", "Soleil"};
	DonneesPlanetes donneesPlanetes;
	Planete[] planetes;
	Moon moon;
	TableauPlanetes tPlanetes;
	JScrollPane sp, sp2, spMonde;
	Monde monde;
	Sextant sextant;
	
	Calculs calc;

	/*
	double 	latitude,//=50.0+(27.0/60)+(20.0/3600),
			longitude,//=3.0+(57.0/60)+(50.0/3600),
			altitude=60.0,
	*/
	
	double capricorne, equateur, cancer, ecliptique;

	Dimension splitBottomDimension;

	URL url;
	//double XYZObservateur[]={0.0, 0.0, 0.0};

	public double getLongitude() {
		return this.coordGeo.longitude;
	}

	public void setLongitude(double longitude) {
		this.coordGeo.longitude = longitude;
	}

	public double getLatitude() {
		return this.coordGeo.latitude;
	}

	public void setLatitude(double latitude) {
		this.coordGeo.latitude = latitude;
	}

	public Astrolabe ()
	{
		int i;
		this.coordGeo=new CoordonneeGeographique();
		this.coordGeo.coordonneeCartesienne=new Point3D(0.0, 0.0, 0.0);
		this.coordGeo.altitude=60.0;
		this.geodesy=new Geodesy();
		this.gps=new Gps(this);
		
		face=new Face(this);
		calc=new Calculs(this);

		
		//super("Astrolabe");
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		};
		addWindowListener(l);
		
		//initialisation membres
		
		//Cr�ation de la Lune
		this.moon=new Moon(this);
		
		//Cr�ation des plan�tes
		this.planetes=new Planete[this.nomsPlanetes.length];
		for(i=0;i<this.nomsPlanetes.length;i++) {
			//System.out.println(this.nomsPlanetes[i]);
			this.planetes[i]=new Planete(this, this.nomsPlanetes[i]);
		}
		
		
		param=new Parametres(this);
		this.param.deserialiser();
		this.coordGeo.latitude=this.home.getLatitude();
		this.param.textFieldLatitude.setText(String.valueOf(this.coordGeo.latitude));
		this.coordGeo.longitude=this.home.getLongitude();
		this.param.textFieldLongitude.setText(String.valueOf(this.coordGeo.longitude));
		this.cutoffAngle=this.home.cutoff;
		this.param.textFieldCutoff.setText(String.valueOf(this.cutoffAngle));
		
		//v�rification coordonn�es cart�siennes de l'observateur
		
		System.out.println("latitude="+this.coordGeo.latitude+" longitude="+this.coordGeo.longitude+" altitude="+this.coordGeo.altitude);
		this.geodesy.GEODESY_ConvertGeodeticCurvilinearToEarthFixedCartesianCoordinates(
				   0,  //0=WGS1984
				   this.coordGeo
				   );
		
		System.out.println("X Obs ="+this.coordGeo.coordonneeCartesienne.getX());
		System.out.println("Y Obs ="+this.coordGeo.coordonneeCartesienne.getY());
		System.out.println("Z Obs ="+this.coordGeo.coordonneeCartesienne.getZ());
		

		affichage = new Affichage(this);
		this.anim=new ModeAnimation(this);
		donneesPlanetes = new DonneesPlanetes(this);
		tPlanetes = new TableauPlanetes(this);
		monde=new Monde(this);
		sp=new JScrollPane(this.tPlanetes.tableau, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.getViewport().setBackground(Color.BLACK);
		sp2=new JScrollPane(this.face, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		spMonde=new JScrollPane(this.monde, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		timer=new Timer();
		timer.schedule(new Tictac(this), 0, 1000);
		
		//Affichage
		
		//splitpanel
		//Create a split pane with the two scroll panes in it.
		splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, this.param, this.sp2);
		splitPanel.setOneTouchExpandable(true);
		
		//Panneau calculs sextant
		this.sextant=new Sextant(this);
		
		//les onglets
		onglets=new JTabbedPane(JTabbedPane.SCROLL_TAB_LAYOUT);
		//this.onglets.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		onglets.addTab("Paramètres", param);
		onglets.addTab("Affichage", affichage);
		onglets.addTab("Animation", this.anim);
		//onglets.add("Planetes", donneesPlanetes);
		onglets.add("Planetes", sp);
		onglets.add("Monde", this.spMonde);
		onglets.add("Sextant", this.sextant);
		
		this.onglets.setMinimumSize(new Dimension(0,0));
		//this.onglets.setOpaque(true);
		this.splitPanel.add(onglets);	
				
		this.add(this.splitPanel);
		//this.sp2.setPreferredSize(new Dimension(this.splitPanel.getBottomComponent().getWidth(),this.splitPanel.getBottomComponent().getHeight()));
		this.sp2.setWheelScrollingEnabled(false);
		//System.out.println("sp2 scroll mouse ? = "+this.sp2.isWheelScrollingEnabled());
		
		this.splitBottomDimension=new Dimension(800, 800);
		this.face.setPreferredSize(this.splitBottomDimension);
	}
	
	public static void main(String[] args) {
		JFrame astrolabe = new Astrolabe();
		//GraphicsDevice myDevice=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();;
		//DisplayMode[] dm=myDevice.getDisplayModes();
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		    astrolabe.setVisible(true);
		    astrolabe.setExtendedState(JFrame.MAXIMIZED_BOTH);
			astrolabe.setResizable(true);   
		    //myDevice.setFullScreenWindow(astrolabe);
		    //System.out.println("Display mode = "+myDevice.getDisplayMode()+" change support = "+myDevice.isDisplayChangeSupported());
		    /*
		    for(int i=0;i<dm.length;i++)
		    	System.out.println("Display mode("+i+") ="+dm[i].getWidth()+" x "+dm[i].getHeight());
		    */
		} 
		catch (UnsupportedLookAndFeelException e) {
		       // handle exception
		    }
		    catch (ClassNotFoundException e) {
		       // handle exception
		    }
		    catch (InstantiationException e) {
		       // handle exception
		    }
		    catch (IllegalAccessException e) {
		       // handle exception
		    }
		finally {
		    //myDevice.setFullScreenWindow(null);
		}
	}	
}
