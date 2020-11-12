package com.mixer.raw;

import com.mixer.util.DebugRowInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class have all helper methods to File handler
 */
public class BaseFileHandler {
    RandomAccessFile dbFile;
    final String dbFileName;
    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    final Lock readLock = readWriteLock.readLock();
    final Lock writeLock = readWriteLock.writeLock();

    public BaseFileHandler(final String dbFileName) throws FileNotFoundException {
        this.dbFileName = dbFileName;
        this.dbFile = new RandomAccessFile(dbFileName, "rw");
    }

    public BaseFileHandler(RandomAccessFile randomAccessFile, final String dbFileName) {
        this.dbFileName = dbFileName;
        this.dbFile = randomAccessFile;
    }

    // Read all data from the db file and fill the index map (name , rownumber)  with the
    // required data to access database records.
    public void loadAllDataToIndex() throws IOException {
        readLock.lock();
        try {
            if (this.dbFile.length() == 0)
                return;
            long currentPos = 0;
            int rowNum = 0;
            int deletedRowNum = 0;
            synchronized (this) {
                while (currentPos < this.dbFile.length()) {
                    this.dbFile.seek(currentPos);
                    boolean isDeleted = this.dbFile.readBoolean();
                    if (!isDeleted) {
                        Index.getInstance().add(currentPos);
                    } else {
                        deletedRowNum++;
                    }
                    currentPos += 1;
                    this.dbFile.seek(currentPos);
                    int recordLength = this.dbFile.readInt();
                    currentPos += 4;

                    if (!isDeleted) {
                        byte[] b = new byte[recordLength];
                        this.dbFile.read(b);
                        Person person = this.readFromByteStream(new DataInputStream(new ByteArrayInputStream(b)));
                        Index.getInstance().addNameToIndex(person.getName(), rowNum);
                        rowNum++;
                    }
                    currentPos += recordLength;
                }
            }
            System.out.println("After startup : total rows number in the Database : " + rowNum);
            System.out.println("After startup : total deleted rows  in the Database : " + deletedRowNum);
        } finally {
            readLock.unlock();
        }

    }

    // to read the data (byte array stream) from the file (data after is deleted column)
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

    // read one row data and return the whole row without isDeleted and record length columns
    byte[] readRowRecord(long bytePositionOfRow) throws IOException {
        readLock.lock();
        try {
            this.dbFile.seek(bytePositionOfRow);
            if (this.dbFile.readBoolean())
                return new byte[0];

            this.dbFile.seek(bytePositionOfRow + 1);
            int recordLength = this.dbFile.readInt();

            this.dbFile.seek(bytePositionOfRow + 5);

            byte[] data = new byte[recordLength];
            this.dbFile.read(data);

            return data;
        } finally {
            readLock.unlock();
        }

    }


    public void close() throws IOException {
        this.dbFile.close();
    }

    // Load all data from the db file and return list of debug row information for each record
    public List<DebugRowInfo> loadAllDataFromFile() throws IOException {
        readLock.lock();
        try {
            if (this.dbFile.length() == 0) {
                return new ArrayList<>();
            }
            synchronized (this) {
                List<DebugRowInfo> result = new ArrayList<>();
                long currentPosition = 0;
                while (currentPosition < this.dbFile.length()) {
                    this.dbFile.seek(currentPosition);
                    boolean isDeleted = this.dbFile.readBoolean();
                    currentPosition += 1;
                    int recordLength = this.dbFile.readInt();
                    currentPosition += 4;
                    byte[] b = new byte[recordLength];
                    this.dbFile.read(b);
                    Person person = this.readFromByteStream(new DataInputStream(new ByteArrayInputStream(b)));
                    result.add(new DebugRowInfo(person, isDeleted));
                    currentPosition += recordLength;

                }
                return result;
            }
        } finally {
            readLock.unlock();
        }

    }

    public boolean deleteFile() throws IOException {
        writeLock.lock();
        try {
            this.dbFile.close();
            if (new File(this.dbFileName).delete()) {
                System.out.println("File has been deleted");
                return true;
            } else {
                System.out.println("File has not been deleted");
                return false;
            }
        } finally {
            writeLock.unlock();
        }

    }

    public String getDBName() {
        return this.dbFileName;
    }
}
