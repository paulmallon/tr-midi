package com.nullterrier;

import com.ecyrd.speed4j.StopWatch;
import com.ecyrd.speed4j.StopWatchFactory;
import com.nullterrier.service.DefaultMidiService;
import com.nullterrier.service.MidiService;
import de.humatic.nmj.NetworkMidiSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;
import javax.sound.midi.MidiDevice;

@SpringBootApplication
public class TrMidiApplication implements CommandLineRunner {

    @Value("${dummy:true}")
    private boolean dummy;

    public NetworkMidiSystem networkMidiSystem;

    private static final Logger log = LoggerFactory.getLogger(DefaultMidiService.class);

    public static void main(String[] args) {
        SpringApplication.run(TrMidiApplication.class, args);
    }


    @Autowired
    private MidiService midiService;


    @Override
    public void run(String... strings) throws InterruptedException {
        NetworkMidiSystem networkMidiSystem = NetworkMidiSystem.get();
        try {
            networkMidiSystem.openInput(1, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        midiService.init();
        midiService.listAllMidiDevices();

        boolean openMidiDevices = midiService.openMidiDevices();

        if (openMidiDevices && dummy ) {
            while (true) {
                midiService.sendSomeMidiNotes();
                Thread.sleep(10);
            }
        }
    }


    @PreDestroy
    public void cleanUp() {
        midiService.closeDevices();
    }
}
