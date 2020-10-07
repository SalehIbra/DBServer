package com.mixer.testapp;

import com.mixer.dbserver.DB;
import com.mixer.dbserver.DBServer;
import com.mixer.raw.FileHandler;
import com.mixer.raw.Index;
import com.mixer.raw.Person;

import java.io.FileNotFoundException;

public class TestApp {
    public static void main(String[] args) {
        final String dbFile = "Dbserver.db";
        try {
            DB db = new DBServer(dbFile);
            db.add("John", 44, "Berlin", "www-123", "This is description");
            db.close();

            db = new DBServer(dbFile);
            Person person = db.read(0);

            System.out.println("Total number of rows in database:" + Index.getInstance().getTotalNumberOfRows());
            System.out.println(person);
            System.out.println(Index.getInstance().getTotalNumberOfRows());

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
