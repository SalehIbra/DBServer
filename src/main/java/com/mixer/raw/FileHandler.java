package com.mixer.raw;

import com.mixer.exceptions.DuplicateNameException;

import java.io.*;

public class FileHandler extends BaseFileHandler{


    public FileHandler(String dbFileName) throws FileNotFoundException {
        super(dbFileName);
    }

    public boolean add(String name,
                       int age,
                       String address,
                       String carPlateNumber,
                       String description) throws IOException, DuplicateNameException {

        if(Index.getInstance().hasNameInIndex(name)){
            throw new DuplicateNameException(String.format("Name %s already exists",name ));
        }
        // seek to the end of the file
        long currentPositionToInsert = this.dbFile.length();
        this.dbFile.seek(currentPositionToInsert);

        // isDeleted byte
        // record length : int
        // name length : int
        // name
        // address length : int
        // address
        // carplatenumber length
        // carplatenum
        // description length : int
        // description

        int length = 4 + // name length
                name.length() +
                4 + // age
                4 + // address length
                address.length() +
                4 + // carplate length
                carPlateNumber.length() +
                4 + // description length;
                description.length();

        // it is deleted
        this.dbFile.writeBoolean(false);

        // record length
        this.dbFile.writeInt(length);

        // store the name
        this.dbFile.writeInt(name.length());
        this.dbFile.write(name.getBytes());

        // store age
        this.dbFile.writeInt(age);

        // store the address
        this.dbFile.writeInt(address.length());
        this.dbFile.write(address.getBytes());

        // store the carplatenumber
        this.dbFile.writeInt(carPlateNumber.length());
        this.dbFile.write(carPlateNumber.getBytes());

        // store the description
        this.dbFile.writeInt(description.length());
        this.dbFile.write(description.getBytes());

        Index.getInstance().add(currentPositionToInsert);
        Index.getInstance().addNameToIndex(name,Index.getInstance().getTotalNumberOfRows() - 1);
        return true;
    }

    public Person readRow(long rowNumber) throws IOException {
        long bytePosition = Index.getInstance().getBytePosition(rowNumber);
        if (bytePosition == -1) {
            return null;
        }
        byte[] row = readRowRecord(bytePosition);
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(row));

        return readFromByteStream(stream);
    }



    public void deleteRow(long rowNumber) throws IOException {
        long bytePositionOfRecord = Index.getInstance().getBytePosition(rowNumber);
        if(bytePositionOfRecord == -1) {
            throw new IOException("Row doesn't exists in the index");
        }
        this.dbFile.seek(bytePositionOfRecord);
        this.dbFile.writeBoolean(true);

        //Update the index
        Index.getInstance().remove(rowNumber);

    }

    public void update(long rowNumber, String name, int age, String address, String carPlateNumber, String description) throws IOException, DuplicateNameException {
        // updating a row will change the record length, to avoid that we delete and add
        this.deleteRow(rowNumber);
        this.add(name, age,address,carPlateNumber,description);
    }

    public void update(String nameToModify, String name, int age, String address, String carPlateNumber, String description) throws IOException, DuplicateNameException {
        long rowNumber = Index.getInstance().getRowNumberByName(nameToModify);
        System.out.println(rowNumber);
        this.update(rowNumber,name, age,address,carPlateNumber,description);
    }
}
