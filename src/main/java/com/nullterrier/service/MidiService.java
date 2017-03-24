package com.nullterrier.service;

/**
 * Created by pm on 2016-12-13.
 */
public interface MidiService {
    void init();
    void listAllMidiDevices();
    boolean openMidiDevices();
    void closeDevices();
    void sendSomeMidiNotes();

    boolean haveOpenOutDevice();
    boolean haveOpenInDevice();
}
