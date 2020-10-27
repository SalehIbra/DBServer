package com.mixer.testapp;

import com.mixer.dbserver.DB;
import com.mixer.dbserver.DBServer;
import com.mixer.exceptions.DuplicateNameException;
import com.mixer.raw.Index;
import com.mixer.raw.Person;
import com.mixer.util.DebugRowInfo;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class TestApp {
    final static String dbFile = "Dbserver.db";

    public static void main(String[] args) {
        new TestApp().performTest();
    }

    private void performTest() {
        try {
            fragmentDatabase();
//            listAllRecord();
//            defragmentDB();
//            System.out.println("-------- after defragmentation --------");
//            listAllRecord();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void defragmentDB() throws IOException, DuplicateNameException {
        try(DBServer db = new DBServer(dbFile)) {
            db.defragmentDatabase();
        }
    }
    void testSearchWithRegexp() throws IOException {
        try (DB db = new DBServer(dbFile)) {
           // List<Person> result = db.searchWithRegexp("(.*)ohn1");
             List<Person> result = db.searchWithRegexp("John(.*)");
            System.out.println("----- Search with Regular expression ------");
            result.forEach(System.out::println);
        }
    }

    void testLeveinshtein() throws IOException {
        try (DB db = new DBServer(dbFile)) {
            List<Person> result = db.searchWithLeveinshtein("John", 0);
            System.out.println("----- Search with Leveinshtein ------");
            result.forEach(System.out::println);
        }
    }
    
    void testSearch() throws IOException {
        try (DB db = new DBServer(dbFile)) {
            Person person = db.search("John1");
            System.out.println("Found Person : "+ person);
        }
    }

    void fillDb(int number) throws Exception {
        try (DB db = new DBServer(dbFile)) {
            for (int i = 0; i < number; i++) {
                Person person = new Person("John" + i, 44, "Berlin", "www-123", "This is description");
                db.add(person);
            }
        }
    }

    void delete(int rowNumber) throws Exception {
        try (DB db = new DBServer(dbFile)) {
            db.delete(rowNumber);
        }
    }

    void listAllRecord() throws Exception {
        try (DBServer db = new DBServer(dbFile)) {
            List<DebugRowInfo> result = db.listAllRowWithDebug();
            System.out.println("Total row number :" + Index.getInstance().getTotalNumberOfRows());
            for (DebugRowInfo dri : result) {
                System.out.println(dri.toString());
            }
            // result.stream().map(DebugRowInfo::toString).forEach(System.out::println);
        }


    }
    public void fragmentDatabase() throws Exception {
        try (DB db = new DBServer(dbFile)) {
            // add 100  rows
            for (int i : IntStream.range(0, 100).toArray()) {
                Person person = new Person("John" + i, 44, "Berlin", "www-123", "This is description");
                db.add(person);
            }
            //update the rows
            for (int i : IntStream.range(0, 100).toArray()) {
                if (i % 2 == 0) {
                    db.update("John" + i, new Person("John" + i + "_updated", 44, "Berlin", "www-123", "This is description"));
                }
            }
        }
    }

}
