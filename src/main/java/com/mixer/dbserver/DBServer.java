package com.mixer.dbserver;

import com.mixer.raw.FileHandler;
import com.mixer.raw.Index;
import com.mixer.raw.Person;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DBServer implements DB{
    private FileHandler fileHandler;

    public DBServer(String dbFileName) throws IOException {
        this.fileHandler = new FileHandler(dbFileName);
        this.fileHandler.loadAllDataToIndex();
    }

    @Override
    public void add(String name, int age, String address, String carPlateNumber, String description) throws IOException {
        this.fileHandler.add(name,age,address,carPlateNumber,description);
    }

    @Override
    public Person read(int rowNumber) throws IOException {
        return this.fileHandler.readRow(rowNumber);
    }

    @Override
    public void delete(int rowNumber) throws IOException {
        if(rowNumber < 0)
            throw new IOException("Row number is less than zero");
        this.fileHandler.deleteRow(rowNumber);
    }

    @Override
    public void close() throws IOException {
        Index.getInstance().clear();
        this.fileHandler.close();
    }
}
