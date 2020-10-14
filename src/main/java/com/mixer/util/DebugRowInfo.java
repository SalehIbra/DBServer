package com.mixer.util;

import com.mixer.raw.Person;

/**
 * This class used to debug information about database file rows
 */
public final class DebugRowInfo {
    private Person person;
    private boolean isDeleted;

    public DebugRowInfo(Person person, boolean isDeleted) {
        this.person = person;
        this.isDeleted = isDeleted;
    }

    public Person person(){
        return person;
    }

    public boolean isDeleted(){
        return isDeleted;
    }

    @Override
    public String toString() {
        return "DebugRowInfo{" +
                "person=" + person +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
