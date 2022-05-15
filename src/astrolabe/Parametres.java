package astrolabe;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Parametres extends JPanel {
	static final long serialVersionUID=1;
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;
	
	Astrolabe astro;
	Graphics2D g2d;
	GridLayout gl;
	JLabel[] labels;
	JButton save=new JButton("Sauvegarder la configuration");
	JToggleButton pleinEcran;
	ParametreTextField textFieldLatitude, textFieldLongitude, textFieldCutoff;
	String[] paramName = {"Sauvegarde", "Latitude", "Longitude", "Cutoff", "Plein écran"};
	
	public Parametres (Astrolabe a){
		this.astro = a;
		
		this.gl=new GridLayout(this.paramName.length, 2);
		this.setLayout(this.gl);
		
		this.labels=new JLabel[this.gl.getColumns()*this.gl.getRows()];
		this.textFieldLatitude=new ParametreTextField(this.astro, "Latitude");
		//this.textFieldLatitude.setText(String.valueOf(astro.latitude));
		this.textFieldLongitude=new ParametreTextField(this.astro, "Longitude");
		//this.textFieldLongitude.setText(String.valueOf(astro.longitude));
		this.textFieldCutoff=new ParametreTextField(this.astro, "Cutoff");
		
		this.save.setBackground(Color.BLACK);
		this.save.setForeground(Color.RED);
		this.save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton source;
				Astrolabe a;
				source=(JButton) e.getSource();
				a= (Astrolabe) source.getRootPane().getParent();
					a.param.serialiser();
			}
		});
		
		this.pleinEcran=new JToggleButton("Sortir du plein écran", true);
		this.pleinEcran.setBackground(Color.BLACK);
		this.pleinEcran.setForeground(Color.RED);
		//this.pleinEcran.
		this.pleinEcran.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JToggleButton source=(JToggleButton) e.getSource();
				Astrolabe a=(Astrolabe) source.getRootPane().getParent();
				GraphicsDevice myDevice=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();;
				DisplayMode[] dm=myDevice.getDisplayModes();
				if(source.isSelected()) {
					source.setText("Désactiver plein écran");
					a.setResizable(false);
					myDevice.setFullScreenWindow(a);
				}
				else {
					source.setText("Activer plein écran");
					myDevice.setDisplayMode(dm[dm.length-1]);
					a.setResizable(true);
					a.pack();
				}
				
			}
		});
		
		
		for(int i=0; i<this.paramName.length; i++) {
			this.labels[i]=new JLabel(this.paramName[i]);

			this.add(this.labels[i].getName(), this.labels[i]);
			switch(this.labels[i].getText()) {
				case "Latitude" :
					this.add(this.textFieldLatitude);
					break;
				case "Longitude" :
					this.add(this.textFieldLongitude);
					break;
				case "Sauvegarde" :
					this.add(this.save);
				case "Cutoff" :
					this.add(this.textFieldCutoff);
				case "Plein écran" :
					this.add(pleinEcran);
					break;
			default: break;
			
			}
			
		}
		
		this.setLayout(gl);
	}
	
	public void paintComponent(Graphics g) {
		g2d = (Graphics2D) g;
		
		g2d.setBackground(Color.BLACK);
		g2d.setColor(Color.RED);
		g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
		
	}
	
	
	public void serialiser() {
		try {
			System.out.println("Serialisation configuration Astrolabe");
			final FileOutputStream fichierOut = new FileOutputStream("astrolabe_home.ser");
			oos = new ObjectOutputStream(fichierOut);
			oos.writeObject(this.astro.home);
			oos.flush();
			
			/*
			System.out.println("Deserialisation");
			final FileInputStream fichierIn = new FileInputStream("maclassetransient.ser");
			ois = new ObjectInputStream(fichierIn);
			maClasse = (MaClasseTransient) ois.readObject();
			System.out.println("MaClasseTransient valeur : " + maClasse.getValeur());
			*/
		} 
		catch (final java.io.IOException e) {
				e.printStackTrace();
			}
		/*
		catch (final ClassNotFoundException e) {
				e.printStackTrace();
		} 
		*/
		finally {
		
			try {
				if (ois != null) {
					ois.close();
				}
				if (oos != null) {
					oos.close();
				}
			}
			catch (final IOException ex) {
					ex.printStackTrace();
			}
		}
	}
	
	public void deserialiser() {
		try {
			System.out.println("Chargement configuration Astrolabe");
			final FileInputStream fichierIn = new FileInputStream("astrolabe_home.ser");
			ois = new ObjectInputStream(fichierIn);
			this.astro.home = (Home) ois.readObject();
//			this.astro.param.textFieldLatitude.setText(String.valueOf(this.astro.home.getLatitude()));
//			this.astro.param.textFieldLongitude.setText(String.valueOf(this.astro.home.getLongitude()));
			//System.out.println("Home valeur : " + this.astro.home.toString()+"astro lat="+this.astro.latitude);
		} 
		catch (final java.io.IOException e) {
				e.printStackTrace();
		}
		
		catch (final ClassNotFoundException e) {
				e.printStackTrace();
		} 
		
		finally {
		
			try {
				if (ois != null) {
					ois.close();
				}
				if (oos != null) {
					oos.close();
				}
			}
			catch (final IOException ex) {
					ex.printStackTrace();
			}
		}
	}
}
