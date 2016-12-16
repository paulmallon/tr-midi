package com.nullterrier;

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

    private MidiDevice device;

    public static void main(String[] args) {
        SpringApplication.run(TrMidiApplication.class, args);
    }


    @Autowired
    private MidiService midiService;

    @Override
    public void run(String... strings) {

        midiService.listAllMidiDevices();

        midiService.openMidiDevices();

        midiService.sendSomeMidiNotes();

    }


    @PreDestroy
    public void cleanUp() {
        midiService.closeDevices();
    }
}
