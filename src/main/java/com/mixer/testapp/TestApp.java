package com.mixer.testapp;

import com.mixer.dbserver.DB;
import com.mixer.dbserver.DBServer;
import com.mixer.exceptions.DuplicateNameException;
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
    private void performTest(){
        try {
            fillDb(10);
            delete(0);
            delete(2);
            delete(5);
            listAllRecord();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void fillDb(int number) throws Exception{
        DB db = new DBServer(dbFile);
        for (int i = 0; i < number ; i++){
            Person person = new Person("John"+i, 44, "Berlin", "www-123", "This is description");
            db.add(person);
        }
        db.close();
    }
    void delete(int rowNumber) throws Exception{
        DB db = new DBServer(dbFile);
        db.delete(rowNumber);
        db.close();
    }
    void listAllRecord() throws Exception {
        DBServer db = new DBServer(dbFile);
        List<DebugRowInfo> result = db.listAllRowWithDebug();
        System.out.println("Total row number :"+ Index.getInstance().getTotalNumberOfRows());
        for(DebugRowInfo dri:result){
            System.out.println(dri.toString());
        }
        db.close();


    }

}
