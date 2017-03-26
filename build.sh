#!/bin/bash -v
sudo mvn clean install -DskipTests
sudo cp target/tr-midi-*.jar /home/pi
sudo chmod +x /home/pi/tr-midi*.jar

