package com.mixer.dbserver;

import com.mixer.exceptions.DuplicateNameException;
import com.mixer.raw.Person;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface DB extends Closeable {

    void add(Person person) throws IOException, DuplicateNameException;
    Person read(long rowNumber) throws IOException;
    void update(long rowNumber,final Person person) throws IOException, DuplicateNameException;
    void update(String name,final Person person) throws IOException, DuplicateNameException;
    void delete(int rowNumber) throws IOException;
    // Not List<Person>, we are adding now distinct names to our db
    Person search(final String name) throws IOException;
    List<Person> searchWithLeveinshtein(final String name,int tolerance) throws IOException;
    List<Person> searchWithRegexp(final String regexp) throws IOException;

    void close() throws IOException;

}
