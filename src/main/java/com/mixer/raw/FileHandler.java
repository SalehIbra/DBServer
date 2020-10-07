package com.mixer.raw;

import java.io.*;

public class FileHandler {
    private RandomAccessFile dbFile;

    public FileHandler(String dbFileName) throws FileNotFoundException {
        this.dbFile = new RandomAccessFile(dbFileName, "rw");
    }

    public boolean add(String name,
                       int age,
                       String address,
                       String carPlateNumber,
                       String description) throws IOException {
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
        return true;
    }

    public Person readRow(int rowNumber) throws IOException {
        long bytePosition = Index.getInstance().getBytePosition(rowNumber);
        if (bytePosition == -1) {
            return null;
        }
        byte[] row = readRowRecord(bytePosition);
        Person person = new Person();
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(row));

        int nameLength = stream.readInt();
        byte[] b = new byte[nameLength];
        stream.read(b);
        person.setName(new String(b));

        //age
        person.setAge(stream.readInt());

        //address
        b = new byte[stream.readInt()];
        stream.read(b);
        person.setAddress(new String(b));

        //car plate number
        b = new byte[stream.readInt()];
        stream.read(b);
        person.setCarPlateNumber(new String(b));

        //desc
        b = new byte[stream.readInt()];
        stream.read(b);
        person.setDescription(new String(b));

        return person;
    }

    private byte[] readRowRecord(long bytePositionOfRow) throws IOException {
        this.dbFile.seek(bytePositionOfRow);
        if (this.dbFile.readBoolean())
            return new byte[0];

        this.dbFile.seek(bytePositionOfRow + 1);
        int recordLength = this.dbFile.readInt();

        this.dbFile.seek(bytePositionOfRow + 5);

        byte[] data = new byte[recordLength];
        this.dbFile.read(data);

        return data;
    }

    public void loadAllDataToIndex() throws IOException {
        if (this.dbFile.length() == 0)
            return;
        long currentPos = 0;
        int rowNum = 0;
        int deletedRowNum = 0;
        while (currentPos < this.dbFile.length()) {
            this.dbFile.seek(currentPos);
            boolean isDeleted = this.dbFile.readBoolean();
            if (!isDeleted) {
                Index.getInstance().add(currentPos);
                rowNum++;
            } else {
                deletedRowNum ++;
            }
            currentPos += 1;
            this.dbFile.seek(currentPos);
            int recordLength = this.dbFile.readInt();
            currentPos += 4;
            currentPos += recordLength;
        }
        System.out.println("After startup : total rows number in the Database : " + rowNum);
        System.out.println("After startup : total deleted rows  in the Database : " + deletedRowNum);
    }

    public void close() throws IOException {
        this.dbFile.close();
    }

    public void deleteRow(int rowNumber) throws IOException {
        long bytePositionOfRecord = Index.getInstance().getBytePosition(rowNumber);
        if(bytePositionOfRecord == -1) {
            throw new IOException("Row doesn't exists in the index");
        }
        this.dbFile.seek(bytePositionOfRecord);
        this.dbFile.writeBoolean(true);

        //Update the index
        Index.getInstance().remove(rowNumber);

    }
}
