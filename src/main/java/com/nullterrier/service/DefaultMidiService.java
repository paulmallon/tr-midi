package com.nullterrier.service;

import com.sun.media.sound.MidiInDeviceProvider;
import com.sun.media.sound.MidiOutDeviceProvider;
import com.sun.media.sound.RealTimeSequencerProvider;
import com.sun.media.sound.SoftSynthesizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sound.midi.*;

import static sun.audio.AudioDevice.device;

/**
 * Created by pm on 2016-12-13.
 */
@Component
public class DefaultMidiService implements MidiService {

    private static final Logger log = LoggerFactory.getLogger(DefaultMidiService.class);


    @Value("${in:}")
    private String midiInName;

    @Value("${out:}")
    private String midiOutname;

    private MidiDevice.Info[] midiDeviceInfos;
    private MidiDevice midiInDevice;
    private MidiDevice midiOutDevice;
    private MidiDevice.Info midiOutDeviceInfo;
    private MidiDevice.Info midiInDeviceInfo;
    Receiver receiver;

    @Override
    public void listAllMidiDevices()  {
        midiDeviceInfos = MidiSystem.getMidiDeviceInfo();

        StringBuilder sb = new StringBuilder();
        for (MidiDevice.Info info : midiDeviceInfos) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(info);
            } catch (MidiUnavailableException e) {
                log.info("Unable to obtain midi device " + info.getName());
            }
            sb.append(info.toString() + ", ");
        }
        log.info("List of all MIDI ports: " + sb.toString().substring(0, sb.toString().length() - 2));
    }

    @Override
    public void openMidiDevices() {
        if (this.midiOutname.isEmpty() || this.midiInName.isEmpty()) {
            log.info("MIDI device must be specified by --in=\"some midi device name\" and --out=\"some midi device name\" ");
            return;
        }

        for (MidiDevice.Info i : midiDeviceInfos) {
            if (i.getName().equals(this.midiOutname)) {
                OpenMidiOutDevice(i);
                continue;
            }

            if (i.getName().equals(this.midiInName)) {
                getMidiInDevice(i);
                continue;
            }

            if (midiInDevice != null && midiOutDevice != null) {
                break;
            }
        }

        if (midiInDevice != null && midiOutDevice != null) {
            log.info("Sucessfully opend MIDI IN " + midiInName + " and OUT " + midiOutname);
        } else {
            log.warn("Could not oppen both MIDI in and out device ");
        }

    }

    @Override
    public void sendSomeMidiNotes() {
        ShortMessage myMsg = new ShortMessage();

        try {

                myMsg.setMessage(ShortMessage.NOTE_ON, 0, 72, 93);
                midiOutDevice.getReceiver().send(myMsg, -1); // -1 means no time stamp
                Thread.sleep(500);
                myMsg.setMessage(ShortMessage.NOTE_OFF, 0, 72, 93);
                midiOutDevice.getReceiver().send(myMsg, -1); // -1 means no time stamp

        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    private boolean getMidiInDevice(MidiDevice.Info i) {
        midiInDeviceInfo = i;
        MidiDevice d;
        try {
            d = MidiSystem.getMidiDevice(i);
        } catch (MidiUnavailableException e) {
            log.warn("MidiUnavailableException exception caught while tryging to get MIDI device " + i.getName());
            return false;
        }
        try {
            if (d.getMaxTransmitters() == -1) {
                midiInDevice = d;
                if (!(midiInDevice.isOpen())) {
                    midiInDevice.open();
                    log.info("MIDI INT port " + i.getName() + " opened.");
                    Transmitter transmitter = midiInDevice.getTransmitter();
                    transmitter.setReceiver(new MidiInputReceiver(midiInDevice.getDeviceInfo().getName()));
                }
            }
        } catch (MidiUnavailableException e) {
            log.warn("MidiUnavailableException exception caught while opening MIDI IN port on device " + i.getName());
            midiInDevice = null;
        }

        return true;
    }

    private boolean OpenMidiOutDevice(MidiDevice.Info i) {
        midiOutDeviceInfo = i;
        MidiDevice d;
        try {
            d = MidiSystem.getMidiDevice(i);
        } catch (MidiUnavailableException e) {
            log.warn("MidiUnavailableException exception caught while tryging to get MIDI device " + i.getName());
            return false;
        }

        try {
            if (d.getMaxReceivers() == -1) {
                midiOutDevice = d;
                if (!(midiOutDevice.isOpen())) {
                    midiOutDevice.open();
                    log.info("MIDI OUT port " + i.getName() + " opened.");

                }
            }
        } catch (MidiUnavailableException e) {
            log.warn("MidiUnavailableException exception caught while opening MIDI OUT port on device " + i.getName());
            midiOutDevice = null;
        }

        return true;
    }



    private class MidiInputReceiver implements Receiver {
        private final Logger log = LoggerFactory.getLogger(MidiInputReceiver.class);

        public MidiInputReceiver(String name) {
            log.info("New MIDI IN receiver created for " + name);
        }

        @Override
        public void send(MidiMessage message, long timeStamp) {
            log.info("MIDI message received");

            byte[] bytes = message.getMessage();
            for(int i=0;i<message.getLength();i++) {
                log.info(" Byte " + i + ": "+ bytes[i]);
            }

        }

        @Override
        public void close() {

        }
    }



    @Override
    public void closeDevices() {

        if (midiOutDevice != null && midiOutDevice.isOpen()) {
            midiOutDevice.close();
            log.info("Midi out device " + midiOutDeviceInfo.getName() + " closed.");
        }

        if (midiInDevice != null && midiInDevice.isOpen()) {
            midiInDevice.close();
            log.info("Midi in device " + midiInDeviceInfo.getName() + " closed.");
        }
    }
}
