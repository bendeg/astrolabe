# astrolabe
<br>

Basic astrolabe written in Java (v1.8)
Tested on Windows 10 and Linux Debian Buster 
UI in French...
<br>
Works offline (almanach read from local folder, updated last time you ran the application with online access)
<br>
Computations : "Astronomical Algorithms 2nd Edition, Jean Meeus"
<br>
3017 stars (magnitude < 6 from http://simbad.u-strasbg.fr/simbad/)
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
<br>stars3017-alluvb-name.txt
<br>stars3017-alluvb-pm.txt
<br>stars3017-alluvb-radecmagurvb
<br>asterismes.txt
<br>Mercator-projection.jpg
<br>
<br>
Executable JAR file (Java 8)
<br>
To launch : 
<br>
A Double-clic on the file should work
<br>
If not, go to the command line :
<br>
1) cd directory-where-you-saved-JAR file
<br>
2) jar -jar astrolabe_java8_x64.jar 
<br>
For easier launch : make a .BAT file on the Desktop, containing those 2 commands
<br>
<br>
asterismes.txt can be modified by user. A star can be linked to multiple other stars.