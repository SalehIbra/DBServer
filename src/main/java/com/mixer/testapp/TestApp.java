package com.mixer.testapp;

import com.mixer.dbserver.DB;
import com.mixer.dbserver.DBServer;
import com.mixer.raw.Index;
import com.mixer.raw.Person;

public class TestApp {
    public static void main(String[] args) {
        final String dbFile = "Dbserver.db";
        try {
            DB db = new DBServer(dbFile);
            Person person = new Person("John", 44, "Berlin", "www-123", "This is description");
            db.add(person);
            System.out.println("Total number of rows in database:" + Index.getInstance().getTotalNumberOfRows());

            Person updatedPerson = new Person("John1", 44, "Berlin", "www-123", "This is description");
            db.update("John",updatedPerson);
            System.out.println(db.read(0).toString());
            System.out.println(Index.getInstance().getTotalNumberOfRows());


            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
