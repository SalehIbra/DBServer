package com.mixer.testapp;

import com.mixer.dbserver.DB;
import com.mixer.dbserver.DBServer;
import com.mixer.exceptions.DuplicateNameException;
import com.mixer.raw.Index;
import com.mixer.raw.Person;
import com.mixer.util.DebugRowInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TestApp {
    final static String dbFile = "Dbserver.db";

    public static void main(String[] args) {
        new TestApp().performTest();
    }

    private void performTest() {
        try {
   //         fragmentDatabase();
//            listAllRecord();
//            defragmentDB();
//            System.out.println("-------- after defragmentation --------");
//            listAllRecord();
            deleteDatabase();
            doMultipleThreadTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // to avoid deleting manually when restart my app
    void deleteDatabase(){
        File file = new File("Dbserver.db");
        if (file.exists())
            file.delete();
    }

    void doMultipleThreadTest() throws IOException {
        // create task that is going to wait for three threads before it starts
        CountDownLatch latch = new CountDownLatch(3);

        try (DBServer db = new DBServer(dbFile)){
            Runnable runnableAdd = ()-> {
              while (true){
                  int i = new Random().nextInt(4000);
                  Person person = new Person("John" + i, 44, "Berlin", "www-123", "This is description");
                  try {
                      db.add(person);
                  } catch (IOException e) {
                      e.printStackTrace();
                  } catch (DuplicateNameException e) {
                      e.printStackTrace();
                  }
              }
            };

            Runnable runnableUpdate = ()-> {
                while (true){
                    int i = new Random().nextInt(4000);
                    Person person = new Person("John"+ i +"_updated", 44, "Berlin", "www-123", "This is description");
                    try {
                        db.update("John"+ i,person);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DuplicateNameException e) {
                        e.printStackTrace();
                    }
                }
            };

            Runnable runnableListAll = ()-> {
                while (true){
                    try {
                        db.listAllRowWithDebug();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            // user thread pools and submit them for execution to an instance of a thread pool
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            executorService.submit(runnableListAll);
            executorService.submit(runnableUpdate);
            executorService.submit(runnableAdd);
            // The main task waits for four threads, these will be endless process
            latch.await();
        } catch (InterruptedException e){
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
