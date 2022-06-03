package astrolabe;


//import java.awt.Toolkit;
import java.util.TimerTask;
import java.awt.Color;
import java.awt.Toolkit;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Tictac extends TimerTask {
	Astrolabe astro;
	String heureEte;
	long weekGPS1980, weekGPS, secondGPS, rollovers;
	SatelliteGPS tempGPS;
		
	public Tictac (Astrolabe a)
	{
		astro=a;
		this.astro.calc.now();
	}
	
	@Override
	public void run() {
			this.astrolabeUpdate();
	}
	
	public void astrolabeUpdate() {
		
		try {
		
		//TODO
		//Pas trouv� comment d�tecter un changement de fuseau � l'�x�cution... :-(
/*
		if(ZonedDateTime.now().getZone().getId()!= this.astro.calc.ldt.getZone().getId()){
			System.out.println("Zone modifi�e..."+ZonedDateTime.now().getZone().getId()+" <- "+this.astro.calc.ldt.getZone().getId());
		}
		System.out.println("Zones..."+ZonedDateTime.now(Clock.systemDefaultZone()).getZone().getId()+" <- "+this.astro.calc.ldt.getZone().getId());
*/	
		this.astro.calc.dst=this.astro.calc.ldt.getZone().getRules().isDaylightSavings(this.astro.calc.ldt.toInstant());
		//System.out.println(this.astro.calc.dst);
		this.astro.calc.fuseau=this.astro.calc.ldt.getOffset().getTotalSeconds()/3600.0;
		//System.out.println("Fuseau = GMT+"+this.astro.calc.fuseau);

//  if (this.astro.anim.dJourT.getText().split("[^0-9]")[0].compareTo("") == 0) this.astro.anim.dJourT.setText("0");
//  else this.astro.anim.dJourT.setText(this.astro.anim.dJourT.getText().split("[^0-9]")[0]);
//  if (this.astro.anim.dHeureT.getText().split("[^0-9]")[0].compareTo("") == 0) this.astro.anim.dHeureT.setText("0");
//  else this.astro.anim.dHeureT.setText(this.astro.anim.dHeureT.getText().split("[^0-9]")[0]);
//  if (this.astro.anim.dMinT.getText().split("[^0-9]")[0].compareTo("") == 0) this.astro.anim.dMinT.setText("0");
//  else this.astro.anim.dMinT.setText(this.astro.anim.dMinT.getText().split("[^0-9]")[0]);
//  if (this.astro.anim.dSecT.getText().split("[^0-9]")[0].compareTo("") == 0) this.astro.anim.dSecT.setText("0");
//  else this.astro.anim.dSecT.setText(this.astro.anim.dSecT.getText().split("[^0-9]")[0]);
//
//  if (this.astro.anim.anT.getText().split("[^0-9]")[0].compareTo("") == 0) this.astro.anim.anT.setText("0");
//  else this.astro.anim.anT.setText(this.astro.anim.anT.getText().split("[^0-9]")[0]);
//  if (this.astro.anim.moisT.getText().split("[^0-9]")[0].compareTo("") == 0) this.astro.anim.moisT.setText("0");
//  else this.astro.anim.moisT.setText(this.astro.anim.moisT.getText().split("[^0-9]")[0]);
//  if (this.astro.anim.jourT.getText().split("[^0-9]")[0].compareTo("") == 0) this.astro.anim.jourT.setText("0");
//  else this.astro.anim.jourT.setText(this.astro.anim.jourT.getText().split("[^0-9]")[0]);
//  if (this.astro.anim.heureT.getText().split("[^0-9]")[0].compareTo("") == 0) this.astro.anim.heureT.setText("0");
//  else this.astro.anim.heureT.setText(this.astro.anim.heureT.getText().split("[^0-9]")[0]);
//  if (this.astro.anim.minT.getText().split("[^0-9]")[0].compareTo("") == 0) this.astro.anim.minT.setText("0");
//  else this.astro.anim.minT.setText(this.astro.anim.minT.getText().split("[^0-9]")[0]);
//  if (this.astro.anim.secT.getText().split("[^0-9]")[0].compareTo("") == 0) this.astro.anim.secT.setText("0");
//  else this.astro.anim.secT.setText(this.astro.anim.secT.getText().split("[^0-9]")[0]);
		
		if(!this.astro.anim.automode.isSelected()){
		  //Mode "manuel" est sélectionné (changement de date manuel)
      //this.astro.calc.ldt=java.time.LocalDateTime.now();
		  this.astro.calc.ldt=this.astro.calc.ldt.withYear(Integer.parseInt(this.astro.anim.anT.getText()));
			this.astro.calc.ldt=this.astro.calc.ldt.withMonth(Integer.parseInt(this.astro.anim.moisT.getText()));
			this.astro.calc.ldt=this.astro.calc.ldt.withDayOfMonth(Integer.parseInt(this.astro.anim.jourT.getText()));
      this.astro.calc.ldt=this.astro.calc.ldt.withHour(Integer.parseInt(this.astro.anim.heureT.getText()));
      this.astro.calc.ldt=this.astro.calc.ldt.withMinute(Integer.parseInt(this.astro.anim.minT.getText()));
      this.astro.calc.ldt=this.astro.calc.ldt.withSecond(Integer.parseInt(this.astro.anim.secT.getText()));
		}
		else {
		  //Mode auto (activé par défaut au démarrage)
      //System.out.println("ajout jour : "+this.astro.anim.jour.getText());
		  if(this.astro.anim.reinit.isSelected()) {
		    this.astro.calc.now();//forcer la mise à jour => ldt = temps de la machine
		  }
		  else {
        this.astro.calc.ldt=this.astro.calc.ldt.plusDays(Long.parseLong(this.astro.anim.dJourT.getText()));
        
        this.astro.calc.ldt=this.astro.calc.ldt.plusHours(Long.parseLong(this.astro.anim.dHeureT.getText()));
        
        this.astro.calc.ldt=this.astro.calc.ldt.plusMinutes(Long.parseLong(this.astro.anim.dMinT.getText()));
  			
  			this.astro.calc.ldt=this.astro.calc.ldt.plusSeconds(Long.parseLong(this.astro.anim.dSecT.getText().split("[^0-9^\\-]")[0]));
  		}
			this.astro.anim.anT.setText(String.valueOf(this.astro.calc.ldt.getYear()));
			this.astro.anim.moisT.setText(String.valueOf(this.astro.calc.ldt.getMonthValue()));
			this.astro.anim.jourT.setText(String.valueOf(this.astro.calc.ldt.getDayOfMonth()));
      this.astro.anim.heureT.setText(String.valueOf(this.astro.calc.ldt.getHour()));
      this.astro.anim.minT.setText(String.valueOf(this.astro.calc.ldt.getMinute()));
      this.astro.anim.secT.setText(String.valueOf(this.astro.calc.ldt.getSecond()));

			if(Long.valueOf(this.astro.anim.dJourT.getText())!=0 || 
				Long.valueOf(this.astro.anim.dHeureT.getText())!=0 ||
				Long.valueOf(this.astro.anim.dMinT.getText())!=0 ||
				Long.valueOf(this.astro.anim.dSecT.getText())!=0){
				this.astro.anim.reinit.setBackground(Color.GRAY);
			}
		}
		}
		catch(NumberFormatException nfe) {
		  System.err.println(nfe.toString());
			System.out.println("Erreur de format nombre : "+nfe.toString());
		}
		catch(DateTimeException dte){
      System.err.println(dte.toString());
			System.out.println("Erreur de format de date : "+dte.toString());
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
      System.err.println(aioobe.toString());
      System.out.println("Erreur indice tableau : "+ aioobe.toString());		  
		}
		
		if(this.astro.calc.ldt.getZone().getRules().isDaylightSavings(this.astro.calc.ldt.toInstant()))
			this.heureEte="été";
		else this.heureEte="hiver";

		/*
		if(astro.calc.dst) heureEte=new String("été");
		else heureEte=new String("hiver");
		 */
	
    this.astro.calc.calculateAll();
    
//    System.out.println("Tictac - date suivante (mois synodique) : " + this.astro.calc.ldt.toLocalDateTime().toString()
//        + " " + this.astro.calc.ldt.plusSeconds(2551443).toLocalDateTime().toString()
//        );
    
    astro.setTitle("Astrolabe - Date = "+this.astro.calc.ldt.toLocalDate().toString()
						+" - H. locale ("+heureEte+") = "+String.format("%tT", this.astro.calc.ldt.toLocalTime())
						+" ("+ZoneId.systemDefault().toString()+")"
						+" - UT = " + String.format("%tT", this.astro.calc.ut)
						+" - H. sidérale = " + String.format("%tT", this.astro.calc.st)
						+" - H. solaire = "+String.format("%tT", this.astro.calc.hsl)
						+" - Jour julien = " + String.format("%1.10f", this.astro.calc.jd)
						+" - EdT = "+ this.astro.calc.angleDecimalToHMS(this.astro.calc.eot/15.0)
						);
		
		astro.repaint();

		//Compte à rebours pour Soleil au méridien local
		if(this.astro.anim.dSecT.getText().equals("1") &&
			this.astro.calc.hsl.getHour()==11 &&
			this.astro.calc.hsl.getMinute()==59 &&
			(this.astro.calc.hsl.getSecond()>50 ||this.astro.calc.hsl.getSecond()==0))
			Toolkit.getDefaultToolkit().beep();

		//temp GPS		
		this.weekGPS1980=this.astro.calc.ut.toEpochSecond()-ZonedDateTime.of(1980, 1, 6, 0, 0, 0, 0, ZoneId.of("UT")).toEpochSecond();
		//this.secondGPS=this.astro.calc.ut.toEpochSecond()-ZonedDateTime.of(1980, 1, 6, 0, 0, 0, 0, ZoneId.of("UT")).toEpochSecond();
		this.secondGPS=this.weekGPS1980%(7*24*60*60);
		//this.weekGPS=(this.secondGPS/(long)Gps.SECONDS_IN_WEEK)%1024;
		this.weekGPS1980=(long)(this.weekGPS1980/7/24/3600);
		//this.secondGPS=this.secondGPS%(long)Gps.SECONDS_IN_WEEK;
		this.rollovers=this.weekGPS1980/1024;
		
		this.secondGPS+=this.rollovers*17;
		this.weekGPS=this.weekGPS1980%1024;
		
		//System.out.println("GPS : "+this.weekGPS+" - "+weekGPS1980+" semaines et "+this.rollovers+" rollovers depuis le 06 Jan 1980");
		//System.out.println("GPS : semaine n�"+this.weekGPS+" - "+this.secondGPS+" secondes depuis le d�but de la semaine");
		
		//test PRN
		int count=0;
		for(int i=0; i<this.astro.gps.satellitesGPS.length; i++) {
			if(this.astro.gps.satellitesGPS[i]!=null) {
				tempGPS=this.astro.gps.satellitesGPS[i];
				//System.out.println("satellite n�"+tempGPS.id+" week="+tempGPS.week+" time="+tempGPS.timeOfApplicability);
				this.astro.gps.GPS_ComputeSatellitePositionVelocityAzimuthElevationDoppler_BasedOnAlmanacData(
					this.astro.coordGeo,
					this.weekGPS,
					this.secondGPS,
					tempGPS);
				//System.out.println("PRN n�"+tempGPS.id+" : X= "+tempGPS.X/1000+" Y= "+tempGPS.Y/1000+" Z="+tempGPS.Z/1000);
			   	//System.out.println("PRN n�"+tempGPS.id+" : ra = "+tempGPS.ra+" d�clinaison = "+tempGPS.declination);
				if(tempGPS.coordonneeGeographique.elevation >= this.astro.cutoffAngle) {
					count++;
					
					//System.out.println("TicTac - PRN n�"+tempGPS.id+" - Week="+tempGPS.week+" - toa="+tempGPS.timeOfApplicability+" - azimuth="+tempGPS.coordonneeGeographique.azimuth+" elevation="+tempGPS.coordonneeGeographique.elevation);
					/*
					System.out.println("TicTac - PRN n�"+tempGPS.id+" - Lat="+tempGPS.coordonneeGeographique.latitude+" - Lon="+tempGPS.coordonneeGeographique.longitude+" - alt="+tempGPS.coordonneeGeographique.altitude);
					System.out.println("TicTac - PRN n�"+tempGPS.id+" X="+tempGPS.coordonneeGeographique.coordonneeCartesienne.getX()
		   						+ "Y="+tempGPS.coordonneeGeographique.coordonneeCartesienne.getY()
		   						+ " Z="+tempGPS.coordonneeGeographique.coordonneeCartesienne.getZ());
		   			*/
				}
				tempGPS.coordonneeGeographique.ra=this.astro.calc.calculateAngleHoraireFromAzimutal(tempGPS.coordonneeGeographique.azimuth, tempGPS.coordonneeGeographique.elevation);
				tempGPS.coordonneeGeographique.declination=this.astro.calc.calculateDeclinaisonFromAzimutal(tempGPS.coordonneeGeographique.azimuth, tempGPS.coordonneeGeographique.elevation);
			}
		}

		//System.out.println("TicTac - GPS count="+count);
		//System.out.println("Nbre satellites visibles ="+count);
		this.astro.gps.G=new double[count][4];
		int iG=0;
		for(int i=0;i<32;i++){
			if(this.astro.gps.satellitesGPS[i] != null) {
				if(this.astro.gps.satellitesGPS[i].coordonneeGeographique.elevation>this.astro.cutoffAngle) {
					//System.out.println("Tictac - G - satellite n�"+this.astro.gps.satellitesGPS[i].id+" elev="+this.astro.gps.satellitesGPS[i].coordonneeGeographique.elevation);
					this.astro.gps.G[iG][0]=-Math.cos(Math.toRadians(this.astro.gps.satellitesGPS[i].coordonneeGeographique.elevation))*Math.sin(Math.toRadians(this.astro.gps.satellitesGPS[i].coordonneeGeographique.azimuth));
					this.astro.gps.G[iG][1]=-Math.cos(Math.toRadians(this.astro.gps.satellitesGPS[i].coordonneeGeographique.elevation))*Math.cos(Math.toRadians(this.astro.gps.satellitesGPS[i].coordonneeGeographique.azimuth));
					this.astro.gps.G[iG][2]=-Math.sin(Math.toRadians(this.astro.gps.satellitesGPS[i].coordonneeGeographique.elevation));
					this.astro.gps.G[iG][3]=1.0;						
					iG++;
				}
			}
		}
		
		//Calcul GDOP/PDOP
		//System.out.println("matrice G");
		//this.astro.calc.printMatrice(this.astro.gps.G);
		
		this.astro.gps.transposeeG=new double[4][count];
		this.astro.calc.transposeMatrice(this.astro.gps.G, this.astro.gps.transposeeG);
		//System.out.println("transpos�e G="+this.astro.gps.transposeeG);
		//System.out.println("transpos�e G");
		//this.astro.calc.printMatrice(this.astro.gps.transposeeG);
		
		this.astro.gps.produitGtG=new double[4][4];
		this.astro.calc.produitMatrices(this.astro.gps.transposeeG, this.astro.gps.G, this.astro.gps.produitGtG);
	
		//System.out.println("matrice GtG");
		//this.astro.calc.printMatrice(this.astro.gps.produitGtG);
		
		/*
		determinant=this.astro.calc.determinantMatrice(this.astro.gps.produitGtG);
		System.out.println("d�terminant GtG="+determinant);
		
		this.astro.gps.Q=new double[4][4];
		this.astro.calc.transposeMatrice(this.astro.gps.produitGtG, this.astro.gps.Q);
		
		System.out.println("Q (matrice GtG apr�s transposition)");
		this.astro.calc.printMatrice(this.astro.gps.Q);
		
		System.out.println("matrice Q avant produit scal");
		this.astro.calc.printMatrice(this.astro.gps.Q);

		this.astro.calc.produitScalaireMatrice(this.astro.gps.Q, 1.0/determinant);
		System.out.println("matrice Q finale");
		this.astro.calc.printMatrice(this.astro.gps.Q);
		*/
		
		double[][] augM={
				{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0},
				{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0},
				{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0},
				{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0}
		};

		
		this.astro.calc.augmenterMatrice(this.astro.gps.produitGtG, augM);
		this.astro.calc.gaussJordan(augM);
		//this.astro.calc.printMatrice(augM);
		
		this.astro.gps.Q=augM;
		
		//GDOP=Math.sqrt(this.astro.gps.Q[0][0]+this.astro.gps.Q[1][1]+this.astro.gps.Q[2][2]+this.astro.gps.Q[3][3]);
		this.astro.gps.GDOP=Math.sqrt(this.astro.gps.Q[0][4]+this.astro.gps.Q[1][5]+this.astro.gps.Q[2][6]+this.astro.gps.Q[3][7]);
		//System.out.println("GDOP = "+GDOP);
		
		this.astro.gps.PDOP=Math.sqrt(this.astro.gps.Q[0][4]+this.astro.gps.Q[1][5]+this.astro.gps.Q[2][6]);
		//System.out.println("PDOP = "+PDOP);
		
		/*
		double[][] verif=new double[4][4];
		this.astro.gps.inverse=new double[4][4];
		for(int i=0;i<this.astro.gps.inverse.length; i++)
			for(int j=0;j<this.astro.gps.inverse[0].length;j++)
				this.astro.gps.inverse[i][j]=this.astro.gps.Q[i][j+4];
		*/
		
		//this.astro.calc.produitMatrices(this.astro.gps.produitGtG, this.astro.gps.inverse, verif);
		//System.out.println("matrice verif");
		//this.astro.calc.printMatrice(verif);
		
		//FIN Calcul GDOP/PDOP
		
	}
}
