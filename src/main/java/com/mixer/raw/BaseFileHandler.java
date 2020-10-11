package com.mixer.raw;

import com.mixer.util.DebugRowInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BaseFileHandler {
    RandomAccessFile dbFile;

    public BaseFileHandler(final String dbFileName) throws FileNotFoundException {
        this.dbFile = new RandomAccessFile(dbFileName, "rw");
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
            } else {
                deletedRowNum ++;
            }
            currentPos += 1;
            this.dbFile.seek(currentPos);
            int recordLength = this.dbFile.readInt();
            currentPos += 4;

            if(!isDeleted) {
                byte[] b = new byte[recordLength];
                this.dbFile.read(b);
                Person person = this.readFromByteStream(new DataInputStream(new ByteArrayInputStream(b)));
                Index.getInstance().addNameToIndex(person.getName(), rowNum);
                rowNum++;
            }
            currentPos += recordLength;
        }
        System.out.println("After startup : total rows number in the Database : " + rowNum);
        System.out.println("After startup : total deleted rows  in the Database : " + deletedRowNum);
    }
    Person readFromByteStream(final DataInputStream stream) throws IOException {
        Person person = new Person();
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

    byte[] readRowRecord(long bytePositionOfRow) throws IOException {
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



    public void close() throws IOException {
        this.dbFile.close();
    }

    public List<DebugRowInfo> loadAllDataFromFile() throws IOException {
        if(this.dbFile.length() == 0){
            return new ArrayList<>();
        }
        List<DebugRowInfo> result = new ArrayList<>();
        long currentPosition = 0;
        while (currentPosition < this.dbFile.length()){
            this.dbFile.seek(currentPosition);
            boolean isDeleted = this.dbFile.readBoolean();
            currentPosition += 1;
            int recordLength = this.dbFile.readInt();
            currentPosition += 4;
            byte[] b = new byte[recordLength];
            this.dbFile.read(b);
            Person person = this.readFromByteStream(new DataInputStream(new ByteArrayInputStream(b)));
            result.add(new DebugRowInfo(person,isDeleted));
            currentPosition += recordLength;

        }
        return result;
    }
}
