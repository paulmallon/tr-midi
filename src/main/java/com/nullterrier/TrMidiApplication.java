package com.nullterrier;

import com.ecyrd.speed4j.StopWatch;
import com.ecyrd.speed4j.StopWatchFactory;
import com.nullterrier.service.DefaultMidiService;
import com.nullterrier.service.MidiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;
import javax.sound.midi.MidiDevice;

@SpringBootApplication
public class TrMidiApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultMidiService.class);

    public static void main(String[] args) {
        SpringApplication.run(TrMidiApplication.class, args);
    }


    @Autowired
    private MidiService midiService;

    @Override
    public void run(String... strings) throws InterruptedException {
        midiService.listAllMidiDevices();
        midiService.openMidiDevices();
//        if (midiService.haveOpenOutDevice()) {
//            while (true) {
//                midiService.sendSomeMidiNotes();
//                Thread.sleep(100);
//            }
//        }
    }


    @PreDestroy
    public void cleanUp() {
        midiService.closeDevices();
    }
}
