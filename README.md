# astrolabe
<br>

Basic astrolabe written in Java (v1.8)
<br>Inspired by <a href="https://www.softpaz.com/software/download-the-electric-astrolabe-windows-45796.htm">The Electric Astrolabe</a>
<p>Tested on Windows 10 and Linux Debian Buster</p>
UI in French...
<br>
Works offline (GPS almanach read from local folder, updated last time you ran the application with online access)
<br>
Computations : "Astronomical Algorithms 2nd Edition, Jean Meeus"
<br>
3017 stars (magnitude < 6 from <a href="http://simbad.u-strasbg.fr/simbad/">SIMBAD Astronomical Database - CDS (Strasbourg)</a>)
<br>  
Precession and proper motion taken into account
<br>
Asterisms (classic view)
<br>
Planets (except Pluto) 
<br>
Moon
<br>
GPS constellation (C library translated into Java)
<br>
GDOP/PDOP
<br>
You can animate a bit
<br>
Zoom in&out with mouse roller (a bit tricky but it works...)
<br>
Choose location on Earth (avoid latitudes greater than +/- 85° (extremely laaaaaaarge verticals !)
<br>
<br>
In tab "Affichage", you can locate a listed star by typing its name in the combo box (Polaris by default)
<br>
OR
<br>
Right-clic on one
<br>
OR
<br>
You can freely (checkbox "libre") enter its RA/DEC (for negative values, only use "-" in RA heure or DEC degre text fields, not in "RA minute/DEC minute/RA seconde/DEC seconde) 
<br>
<br>
Extract 5 files from JAR file in same directory as jar file :
<ol>
<li>stars3017-alluvb-name.txt</li>
<li>stars3017-alluvb-pm.txt</li>
<li>stars3017-alluvb-radecmagurvb.txt</li>
<li>asterismes.txt (can be modified by user. A star can be linked to multiple other stars)</li>
<li>Mercator-projection.jpg</li>
</ol>
Executable JAR file (Java 8)
<br>
To launch : 
<br>
A Double-clic on the file should work
<br>
If not, go to the command line :
<ol>
  <li>cd directory-where-you-saved-JAR-file</li>
  <li>jar -jar astrolabe_java8_x64.jar</li>
</ol>  
For easier launch : make a .BAT file on the Desktop, containing those 2 commands