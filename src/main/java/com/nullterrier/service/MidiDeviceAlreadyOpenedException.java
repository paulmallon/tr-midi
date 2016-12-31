package com.nullterrier.service;

/**
 * Created by PuckStudio on 2016-12-31.
 */
public class MidiDeviceAlreadyOpenedException extends Throwable {

    public  MidiDeviceAlreadyOpenedException(String message) {
        super(message);
    }

}
