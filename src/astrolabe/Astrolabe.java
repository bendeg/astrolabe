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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

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

	Scanner sc, sc2, sc3, sc4;
	Astre[] stars;
	Vector<String> starsnames;//index de toutes les étoiles
	String[][] asterismes;
	
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
		int i, j;
		this.coordGeo=new CoordonneeGeographique();
		this.coordGeo.coordonneeCartesienne=new Point3D(0.0, 0.0, 0.0);
		this.coordGeo.altitude=60.0;
		this.geodesy=new Geodesy();
		this.gps=new Gps(this);
		
    // Lecture fichier étoiles : radecmag, name, pm
//    File file = new File("stars1935-all-pm.txt"),
//         file2 = new File("stars1935-all-name.txt"),
//         file3 = new File("stars1935-all-radecmag.txt"),
//         file4 = new File("asterismes.txt");
//    File file = new File("stars2885-all-pm.txt"),
//        file2 = new File("stars2885-all-name.txt"),
//        file3 = new File("stars2885-all-radecmag.txt"),
//        file4 = new File("asterismes.txt");
    File file = new File("stars3017-alluvb-pm.txt"),
        file2 = new File("stars3017-alluvb-name.txt"),
        file3 = new File("stars3017-alluvb-radecmag.txt"),
        file4 = new File("asterismes.txt");
    
    String[] tempStr2 = new String[2],
             tempStr3 = new String[5];
    
    //ouverture pour compter les lignes...
    try {
      this.sc = new Scanner(file);
      this.sc4 = new Scanner(file4);
    }
    catch(FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
    
    i = 0;
    j = 0;
    while (sc.hasNextLine()) {
      sc.nextLine();
      if(sc4.hasNextLine()) {
        sc4.nextLine();
        j++;
      }
      i++;
      //System.out.println(tempStr2[0] + "-"+ tempStr2[1]);
      //System.out.println(sc.nextLine());
    }
    System.out.println(file.getAbsolutePath() + " : " + i + " lignes");
    this.stars = new Astre[i];
    System.out.println("Astrolabe - asterismes taille = " + j);
    this.asterismes = new String[j][];
    
    //réouverture des trois scanners
    try {
      this.sc = new Scanner(file);
      this.sc2 = new Scanner(file2);
      this.sc3 = new Scanner(file3);
      this.sc4 = new Scanner(file4);
      this.starsnames = new Vector<String>();
    }
    catch(FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
    
    i = 0;
    String temp = new String();
    float r=0, v=0, b=0;
    double tempMag;
    while (sc.hasNextLine()) {
      if(sc4.hasNextLine()) {
        this.asterismes[i] = new String[2];
        this.asterismes[i] = sc4.nextLine().split(",");
        //System.out.println("Astrolabe - sc4 - i = " + i + " - " + this.asterismes[i][0] + " <-> " + this.asterismes[i][1]);
      }
      tempStr2 = sc.nextLine().split(",");
      tempStr3 = sc3.nextLine().split(",");

      //System.out.println("Astrolabe - while - tmpStr3 " + tempStr3[2] + "," + tempStr3[3] + "," + tempStr3[4]);

      if(tempStr3[3].compareTo("~") == 0) {//pas de Vmag...=> soit B, soit R, soit B et R (il y en a au moins une des deux !
        v = 6;
        if(tempStr3[2].compareTo("~") == 0) {
          b = 6;
          tempMag = Double.valueOf(tempStr3[4]);//--R
        }
        else {
          if(tempStr3[4].compareTo("~") == 0) {
            r = 6;
            tempMag = Double.valueOf(tempStr3[2]);//B--
          }
          else {
            tempMag = (Double.valueOf(tempStr3[2]) + Double.valueOf(tempStr3[4])) / 2.0;//B-R
          }
        }
      }
      else {
        if(tempStr3[2].compareTo("~") == 0) b = 6;
        else b = Float.valueOf(tempStr3[2]);
        v = Float.valueOf(tempStr3[3]);
        if(tempStr3[4].compareTo("~") == 0) r = 6;
        else r = Float.valueOf(tempStr3[4]);
        tempMag = Double.valueOf(tempStr3[3]);        
      }
      temp = sc2.nextLine();
      this.starsnames.add(temp.toString());
      this.stars[i] = new Astre(temp.toString(),
                                Double.valueOf(tempStr3[0]),
                                Double.valueOf(tempStr3[1]),
                                tempMag,
                                i);
      this.stars[i].setPm(Double.valueOf(tempStr2[0]), Double.valueOf(tempStr2[1]));
      
      //principe de normalisation vers l'intervalle [0, 1]
      // x normé = ( x - min x ) / ( x max - min x )
      // vers un intervalle [a, b]
      // x' = a + [( (x - min x)(b - a) ) / (max x - min x)] 
//      this.stars[i].setRvb(new Color( (r+1.46f)/(10+1.46f),
//                                      (v+1.46f)/(7+1.46f),
//                                      (b+1.46f)/(9+1.46f)
//                                     ));
      //System.out.println("Astrolabe - color de " + i + " : " + this.stars[i].getRvb());
      if(i==162) System.out.println("Astrolabe - Files - " + i + " " + this.stars[i].getId() + " " + this.stars[i].getDec());
      i++;
    }
    //System.out.println("Astrolabe - stars taille : " + this.stars.length);
    //System.out.println("Astrolabe - stars - vector index de tau-Cyg : " + this.starsnames.indexOf("tau-Cyg"));
    
    //mise en place des astérismes
    for(i=0; i<this.asterismes.length; i++) {
      //System.out.println("Astrolabe - asterismes - " + this.asterismes[i][0] + " <---> " +  this.asterismes[i][1]);
      this.stars[this.starsnames.indexOf(this.asterismes[i][0])].addNextAstre(this.stars[this.starsnames.indexOf(this.asterismes[i][1])]);
    }
    
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
//		sp2=new JScrollPane(this.face, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    sp2=new JScrollPane(this.face, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
