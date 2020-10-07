package com.mixer.testapp;

import com.mixer.raw.FileHandler;
import com.mixer.raw.Index;
import com.mixer.raw.Person;

import java.io.FileNotFoundException;

public class TestApp {
    public static void main(String[] args){

        try {
            FileHandler fileHandler = new FileHandler("Dbserver.db");
            fileHandler.add("John",44,"Berlin","www-123","This is description");
            fileHandler.close();

            fileHandler = new FileHandler("Dbserver.db");
            Person person = fileHandler.readRow(0);

            System.out.println("total number of rows in database:"+ Index.getInstance().getTotalNumberOfRows());
            System.out.println(person);

            fileHandler.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
