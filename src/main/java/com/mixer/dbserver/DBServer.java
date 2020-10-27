package com.mixer.dbserver;

import com.mixer.exceptions.DuplicateNameException;
import com.mixer.raw.FileHandler;
import com.mixer.raw.Index;
import com.mixer.raw.Person;
import com.mixer.util.DebugRowInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class for main operations on the database
 */
public class DBServer implements DB{
    private FileHandler fileHandler;
    private final Logger LOGGER = Logger.getLogger("DBServer");
    private final String LOG_LEVEL = "LOG_LEVEL";
    private static final String LOG_FILE_NAME = "config.properties";

    public DBServer(String dbFileName) throws IOException {
        this.fileHandler = new FileHandler(dbFileName);
        this.initialise();
    }
    // initialise the the log level
    // initialise the index
    private void initialise() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(LOG_FILE_NAME));
        boolean hasLogLevel = properties.containsKey(LOG_LEVEL);
        if(!hasLogLevel){
            LOGGER.setLevel(Level.SEVERE);
        } else {
            String logLevel =(String) properties.get(LOG_LEVEL);
            if(logLevel.equalsIgnoreCase("SEVER"))
                LOGGER.setLevel(Level.SEVERE);
            else if(logLevel.equalsIgnoreCase("INFO"))
                LOGGER.setLevel(Level.INFO);
            else if(logLevel.equalsIgnoreCase("DEBUG"))
                LOGGER.setLevel(Level.ALL);
        }

        // initialise the index
        this.fileHandler.loadAllDataToIndex();
    }

    @Override
    public void add(Person person) throws IOException, DuplicateNameException {
        LOGGER.info("Adding person : "+ person);
        this.fileHandler.add(person.getName(),person.getAge(),person.getAddress(),person.getCarPlateNumber(),person.getDescription());
    }

    @Override
    public Person read(long rowNumber) throws IOException {
        LOGGER.info("Reading row in row number : "+ rowNumber);
        return this.fileHandler.readRow(rowNumber);
    }

    @Override
    public void delete(int rowNumber) throws IOException {
        if(rowNumber < 0)
            throw new IOException("Row number is less than zero");
        LOGGER.info("Deleting person in the row number : "+ rowNumber);
        this.fileHandler.deleteRow(rowNumber);
    }

    @Override
    public Person search(String name) throws IOException {
        LOGGER.info("Searching for person : "+ name);
        return this.fileHandler.search(name);
    }

    // search in database rows using Leveinshtein algorithm
    @Override
    public List<Person> searchWithLeveinshtein(String name, int tolerance) throws IOException {
        LOGGER.info("Searching for person with leveinshtein : "+ name +" ,tolerance "+ tolerance);
        return this.fileHandler.searchWithLeveinshtein(name,tolerance);
    }

    // search in database rows by regular expressions
    @Override
    public List<Person> searchWithRegexp(String regexp) throws IOException {
        LOGGER.info("Searching for person with regular expression : "+ regexp );
        return this.fileHandler.searchWithRegexp(regexp);
    }
    // update record by row number
    @Override
    public void update(long rowNumber, Person person) throws IOException, DuplicateNameException {
        LOGGER.info("Updating person, row number : "+ rowNumber +" ,with "+ person);
        this.fileHandler.update(rowNumber,person.getName(),person.getAge(),person.getAddress(),person.getCarPlateNumber(),person.getDescription());
    }
    // update row by its contain (person name)
    @Override
    public void update(String name, Person person) throws IOException, DuplicateNameException {
        LOGGER.info("Updating person, Nmae : "+ name +" ,with "+ person);
        this.fileHandler.update(name,person.getName(),person.getAge(),person.getAddress(),person.getCarPlateNumber(),person.getDescription());

    }

    @Override
    public void close() throws IOException {
        Index.getInstance().clear();
        this.fileHandler.close();
    }
    // Load all data from the db file and return list of debug row information for each record
    public List<DebugRowInfo> listAllRowWithDebug() throws IOException {
        return this.fileHandler.loadAllDataFromFile();
    }

    public void defragmentDatabase() throws IOException,DuplicateNameException {
        LOGGER.info("Defragmenting Database");
        File tmpFile = File.createTempFile("defrag","dat");
        // clear the index to fill it for our temp file
        Index.getInstance().clear();
        //Open temporary file
        FileHandler defragFileHandler = new FileHandler(new RandomAccessFile(tmpFile,"rw"),tmpFile.getName());
        List<DebugRowInfo> debugRowInfos = this.fileHandler.loadAllDataFromFile();
        for(DebugRowInfo dri: debugRowInfos){
            if(dri.isDeleted()){
                continue;
            }
            Person p = dri.person();
            // add record to file and Index
            defragFileHandler.add(p.getName(),p.getAge(),p.getAddress(),p.getCarPlateNumber(),p.getDescription());
        }
        boolean wasDeleted = this.fileHandler.deleteFile();
        if(!wasDeleted){
            tmpFile.delete();
            LOGGER.severe("The database couldn't been deleted during the defragmentation");
            // because index was cleared
            this.initialise();
            throw new IOException("DB can't be defragmented, check logs");
        }
        this.fileHandler.close();
        String oldDataBaseName = this.fileHandler.getDBName();

        // Copy the temp file back to db name
        Files.copy(tmpFile.toPath(), FileSystems.getDefault().getPath("",oldDataBaseName)
        , StandardCopyOption.REPLACE_EXISTING);

        // close the tmp file
        defragFileHandler.close();
        this.fileHandler = new FileHandler(oldDataBaseName);
        Index.getInstance().clear();
        // initialise the index
        this.initialise();

    }


}
