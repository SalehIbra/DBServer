import com.mixer.dbserver.DB;
import com.mixer.dbserver.DBServer;
import com.mixer.exceptions.DuplicateNameException;
import com.mixer.raw.Index;
import com.mixer.raw.Person;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class DBBasicTest {
    private DB db;
    private String dbFileName = "testdb.db";

    @Before
    public void setUp() throws IOException {
        File file = new File(dbFileName);
        if(file.exists())
            file.delete();

        this.db = new DBServer(dbFileName);
    }

    @Test
    public void testAdd(){
        try {
            Person person = new Person("John", 44, "Berlin", "www-123", "This is description");
            db.add(person);
            Assert.assertEquals(Index.getInstance().getTotalNumberOfRows(),1);
        } catch (IOException | DuplicateNameException e) {
            Assert.fail();
        }
    }

    @Test
    public void testRead(){
        try {
            Person p = new Person("John", 44, "Berlin", "www-404", "This is description");
            db.add(p);
            Assert.assertEquals(Index.getInstance().getTotalNumberOfRows(),1);
            Person person = this.db.read(0);
            Assert.assertEquals(person.getName(),"John");
            Assert.assertEquals(person.getAge(),44);
            Assert.assertEquals(person.getAddress(),"Berlin");
            Assert.assertEquals(person.getCarPlateNumber(),"www-404");
            Assert.assertEquals(person.getDescription(),"This is description");

        } catch (IOException | DuplicateNameException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDelete(){
        try {
            Person person = new Person("John", 44, "Berlin", "www-123", "This is description");
            db.add(person);
            Assert.assertEquals(Index.getInstance().getTotalNumberOfRows(),1);
            this.db.delete(0);
            Assert.assertEquals(Index.getInstance().getTotalNumberOfRows(),0);
        } catch (IOException | DuplicateNameException e) {
            Assert.fail();
        }
    }

    @After
    public void after(){
        try {
            this.db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
