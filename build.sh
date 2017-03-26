#!/bin/bash -v
sudo mvn clean install -DskipTests
sudo cp target/tr-midi-* /home/pi
sudo chmod +x /home/pi/tr-midi*
java -jar /home/pi/tr-midi.jar 

