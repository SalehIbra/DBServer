package com.mixer.dbserver;

import com.mixer.raw.Person;

import java.io.IOException;

public interface DB {

    void add(String name,
                       int age,
                       String address,
                       String carPlateNumber,
                       String description) throws IOException;
    Person read(int rowNumber) throws IOException;
    void delete(int rowNumber) throws IOException;
    void close() throws IOException;

}
