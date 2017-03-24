package com.nullterrier.service;

import com.ecyrd.speed4j.StopWatch;
import com.ecyrd.speed4j.StopWatchFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sound.midi.*;

/**
 * Created by pm on 2016-12-13.
 */
@Component
public class DefaultMidiService implements MidiService {

    private static final Logger log = LoggerFactory.getLogger(DefaultMidiService.class);
    StopWatchFactory myStopWatchFactory = StopWatchFactory.getInstance("loggingFactory");

    @Value("${in:\"empty\"}")
    private String midiInName;

    @Value("${out:\"empty\"}")
    private String midiOutName;

    @Value("${logData:true}")
    private String logData;


    private MidiDevice midiInDevice;
    private MidiDevice midiOutDevice;

    private MidiDevice.Info midiOutDeviceInfo;
    private MidiDevice.Info midiInDeviceInfo;

    private ShortMessage dummyMessage;

    private boolean verboseLogging;


    public DefaultMidiService() {

    }


    @Override
    public void init() {
        if (logData.toLowerCase().equals("true")) {
            verboseLogging = true;
            log.info("Verbose MIDI data logging enabled.");
        } else {
            verboseLogging = false;
        }

        if (this.midiOutName.isEmpty() || this.midiInName.isEmpty()) {
            log.info("MIDI device must be specified by --in=\"some midi device name\" and --out=\"some midi device name\" ");
            return;
        } else {
            log.info("MIDI devices from application.properties IN: " + midiInName + ", OUT: " + midiOutName);
        }

        try {
            dummyMessage = new ShortMessage();
            dummyMessage.setMessage(ShortMessage.NOTE_ON, 0, 72, 93);
        } catch (InvalidMidiDataException e) {
            log.info("InvalidMidiDataExceptio caught" + e.getMessage());
        }
    }

    @Override
    public void listAllMidiDevices() {
        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();

        StringBuilder sb = new StringBuilder();

        for (MidiDevice.Info info : midiDeviceInfo) {
            try {
                MidiSystem.getMidiDevice(info); //just call the method to catch any exceptions it my throw
            } catch (MidiUnavailableException e) {
                log.info("Unable to obtain midi device " + info.getName() + ". MidiUnavailableException caught. ");
            }
            sb.append(info.toString() + ", ");
        }
        log.info("List of all MIDI ports: " + sb.toString().substring(0, sb.toString().length() - 2));
    }

    @Override
    public boolean openMidiDevices() {
        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();

        for (MidiDevice.Info deviceInfo : midiDeviceInfo) {
            if (midiInDevice != null && midiOutDevice != null) {
                break;
            }

            if (deviceInfo.getName().equals(this.midiOutName)) {
                try {
                    MidiDevice device = MidiSystem.getMidiDevice(deviceInfo);

                    if (device.getMaxReceivers() == -1) {
                        if (!(device.isOpen())) {
                            device.open();

                            //log.info("MIDI OUT port " + deviceInfo.getName() + " opened.");

                            midiOutDevice = device;
                            midiOutDeviceInfo = deviceInfo;
                        } else {
                            throw new MidiDeviceAlreadyOpenedException("MIDI OUT port " + deviceInfo.getName() + " is already opened by somwone else.");
                        }
                    }
                } catch (MidiDeviceAlreadyOpenedException e) {
                    log.warn("MIDI OUT device " + deviceInfo.getName() + "is already opened. \n" + e.getStackTrace());
                } catch (MidiUnavailableException e) {
                    log.warn("MIDI OUT device " + deviceInfo.getName() + "is unavailable" + e.getStackTrace());
                }
            }

            if (deviceInfo.getName().equals(this.midiInName)) {
                try {
                    MidiDevice device = MidiSystem.getMidiDevice(deviceInfo);

                    if (device.getMaxTransmitters() == -1) {

                        if (!(device.isOpen())) {
                            device.open();
                           // log.info("MIDI IN port " + deviceInfo.getName() + " opened.");

                            midiInDevice = device;
                            midiInDeviceInfo = deviceInfo;

                            Transmitter transmitter = midiInDevice.getTransmitter();
                            transmitter.setReceiver(new MidiInputReceiver(this, midiInDevice.getDeviceInfo().getName(), verboseLogging));
                        } else {
                            throw new MidiDeviceAlreadyOpenedException("MIDI IN port is already " + deviceInfo.getName() + " opened somewhere else.");
                        }
                    }
                } catch (MidiDeviceAlreadyOpenedException e) {
                    log.warn("MIDI IN device " + deviceInfo.getName() + "is already opened. \n" + e.getStackTrace());
                } catch (MidiUnavailableException e) {
                    log.warn("MIDI IN device " + deviceInfo.getName() + " is unavailable" + e.getStackTrace());
                }
            }
        }

        if (midiOutDevice != null) {
            log.info("Successfully opened MIDI OUT " + midiOutName);
        } else {
            log.warn("Could not opened MIDI OUT device {}", midiOutName);
            return false;
        }


        if (midiInDevice != null) {
            log.info("Successfully opened MIDI IN " + midiInName);
        } else {
            log.warn("Could not MIDI in device {}", midiInName);
            return false;
        }

        return true;
    }

    @Override
    public void sendSomeMidiNotes() {
        StopWatch sw = myStopWatchFactory.getStopWatch();
        try {
            midiOutDevice.getReceiver().send(dummyMessage, -1); // -1 means no time stamp
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        sw.stop("SendingDummyMidiMessage");
    }

    @Override
    public boolean haveOpenOutDevice() {
        return isDeviceOpen(midiOutDevice);
    }

    private boolean isDeviceOpen(MidiDevice midiDevice) {
        if (midiDevice != null && midiDevice.isOpen()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean haveOpenInDevice() {
        return isDeviceOpen(midiInDevice);
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
