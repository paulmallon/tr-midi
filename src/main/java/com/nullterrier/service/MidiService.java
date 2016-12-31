package com.nullterrier.service;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

/**
 * Created by pm on 2016-12-13.
 */
public interface MidiService {

    void listAllMidiDevices();
    void openMidiDevices();
    void closeDevices();
    void sendSomeMidiNotes();

    boolean haveOpenOutDevice();
    boolean haveOpenInDevice();
}
