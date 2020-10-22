package com.mixer.testapp;

import com.mixer.dbserver.DB;
import com.mixer.dbserver.DBServer;
import com.mixer.raw.Index;
import com.mixer.raw.Person;
import com.mixer.util.DebugRowInfo;
import java.io.IOException;
import java.util.List;

public class TestApp {
    final static String dbFile = "Dbserver.db";

    public static void main(String[] args) {
        new TestApp().performTest();
    }

    private void performTest() {
        try {
            fillDb(10);
           // testSearch();
           // listAllRecord();
            testSearchWithRegexp();
        } catch (Exception e) {
            e.printStackTrace();
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

}
