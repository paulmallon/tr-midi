package com.nullterrier.service;

import com.ecyrd.speed4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

/**
 * Created by PuckStudio on 2016-12-31.
 */
class MidiInputReceiver implements Receiver {
    private DefaultMidiService defaultMidiService;
    private final Logger log = LoggerFactory.getLogger(MidiInputReceiver.class);

    private final String deviceName;
    private final boolean logData;

    public MidiInputReceiver(DefaultMidiService defaultMidiService, String deviceName, boolean logData) {
        this.defaultMidiService = defaultMidiService;
        this.deviceName = deviceName;
        this.logData = logData;
        log.info("New MIDI IN receiver created for " + deviceName);
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        StopWatch sw = defaultMidiService.myStopWatchFactory.getStopWatch();
        if (logData) {
            logBytesRecieved(message);
        }
        sw.stop("receivingMidiMessage:done");
    }

    private void logBytesRecieved(MidiMessage message) {
        byte[] bytes = message.getMessage();
        log.info("Midi message status byte: " + message.getStatus());
        for (int i = 0; i < message.getLength(); i++) {
            log.info(" Byte " + i + ": " + bytes[i]);
        }
    }

    @Override
    public void close() {
        log.info("New MIDI IN receiver for " + deviceName);
    }
}
