package com.mixer.dbserver;

import com.mixer.exceptions.DuplicateNameException;
import com.mixer.raw.FileHandler;
import com.mixer.raw.Index;
import com.mixer.raw.Person;
import com.mixer.util.DebugRowInfo;

import java.io.IOException;
import java.util.List;

/**
 * This class for main operations on the database
 */
public class DBServer implements DB{
    private FileHandler fileHandler;

    public DBServer(String dbFileName) throws IOException {
        this.fileHandler = new FileHandler(dbFileName);
        this.fileHandler.loadAllDataToIndex();
    }

    @Override
    public void add(Person person) throws IOException, DuplicateNameException {
        this.fileHandler.add(person.getName(),person.getAge(),person.getAddress(),person.getCarPlateNumber(),person.getDescription());
    }

    @Override
    public Person read(long rowNumber) throws IOException {
        return this.fileHandler.readRow(rowNumber);
    }

    @Override
    public void delete(int rowNumber) throws IOException {
        if(rowNumber < 0)
            throw new IOException("Row number is less than zero");
        this.fileHandler.deleteRow(rowNumber);
    }

    @Override
    public Person search(String name) throws IOException {
        return this.fileHandler.search(name);
    }

    @Override
    public void update(long rowNumber, Person person) throws IOException, DuplicateNameException {
        this.fileHandler.update(rowNumber,person.getName(),person.getAge(),person.getAddress(),person.getCarPlateNumber(),person.getDescription());
    }

    @Override
    public void update(String name, Person person) throws IOException, DuplicateNameException {
        this.fileHandler.update(name,person.getName(),person.getAge(),person.getAddress(),person.getCarPlateNumber(),person.getDescription());

    }

    @Override
    public void close() throws IOException {
        Index.getInstance().clear();
        this.fileHandler.close();
    }

    public List<DebugRowInfo> listAllRowWithDebug() throws IOException {
        return this.fileHandler.loadAllDataFromFile();
    }
}
