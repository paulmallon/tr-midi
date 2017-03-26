#!/bin/bash
sudo mvn clean install
sudo cp src/main/resources/application.properties ~
sudo cp target/tr-midi-* ~
sudo chmod +x ~/tr-midi*
java -jar ~/tr-midi.jar 

