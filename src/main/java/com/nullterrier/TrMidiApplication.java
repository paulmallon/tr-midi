package com.nullterrier;


import com.nullterrier.service.DefaultMidiService;
import com.nullterrier.service.MidiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class TrMidiApplication implements CommandLineRunner {

    @Value("${dummy:true}")
    private boolean dummy;

    @Value("${list:false}")
    private boolean listDevices;

    private static final Logger log = LoggerFactory.getLogger(DefaultMidiService.class);

    public static void main(String[] args) {
        SpringApplication.run(TrMidiApplication.class, args);
    }


    @Autowired
    private MidiService midiService;


    @Override
    public void run(String... strings) throws InterruptedException {

        midiService.init();

        if(listDevices) {
            midiService.listAllMidiDevices();
        }


        boolean openMidiDevices = midiService.openMidiDevices();

        midiService.initAppleMidi();

        if (openMidiDevices && dummy ) {
        while (true) {
          //  midiService.sendSomeMidiNotes();
             Thread.sleep(10);
          }
        }
    }


    @PreDestroy
    public void cleanUp() {
        midiService.closeDevices();
        midiService.stopAppleMidiI();
    }
}
