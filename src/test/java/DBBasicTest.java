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
    private String dbFileName = "testdb.db";

    @Before
    public void setUp() throws IOException {
        File file = new File(dbFileName);
        if(file.exists())
            file.delete();
    }

    @Test
    public void testAdd(){
        try (DB db = new DBServer(dbFileName)){
            Person person = new Person("John", 44, "Berlin", "www-123", "This is description");
            db.add(person);
            Assert.assertEquals(Index.getInstance().getTotalNumberOfRows(),1);
        } catch (IOException | DuplicateNameException e) {
            Assert.fail();
        }
    }

    @Test
    public void testRead(){
        try (DB db = new DBServer(dbFileName)){
            Person p = new Person("John", 44, "Berlin", "www-404", "This is description");
            db.add(p);
            Assert.assertEquals(Index.getInstance().getTotalNumberOfRows(),1);
            Person person = db.read(0);
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
        try (DB db = new DBServer(dbFileName)){
            Person person = new Person("John", 44, "Berlin", "www-123", "This is description");
            db.add(person);
            Assert.assertEquals(Index.getInstance().getTotalNumberOfRows(),1);
            db.delete(0);
            Assert.assertEquals(Index.getInstance().getTotalNumberOfRows(),0);
        } catch (IOException | DuplicateNameException e) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdateByName(){
        try (DB db = new DBServer(dbFileName)){
            Person person = new Person("John", 44, "Berlin", "www-123", "This is description");
            db.add(person);

            Person person2 = new Person("John2", 44, "Berlin", "www-123", "This is description");
            db.update("John",person2);

            Person result = db.read(0);
            Assert.assertEquals(result.getName(),"John2");
        } catch (IOException | DuplicateNameException e) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdateByRowNumber(){
        try (DB db = new DBServer(dbFileName)){
            Person person = new Person("John", 44, "Berlin", "www-123", "This is description");
            db.add(person);

            Person person2 = new Person("John2", 44, "Berlin", "www-123", "This is description");
            db.update(0,person2);

            Person result = db.read(0);
            Assert.assertEquals(result.getName(),"John2");
        } catch (IOException | DuplicateNameException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSearch(){
        try (DB db = new DBServer(dbFileName)){
            Person person = new Person("John", 44, "Berlin", "www-123", "This is description");
            db.add(person);

            Person person2 = new Person("John1", 44, "Berlin", "www-123", "This is description");
            db.add(person2);

            Person result = db.search("John1");
            Assert.assertEquals(result.getName(),"John1");
            Assert.assertEquals(result.getAddress(),"Berlin");
            Assert.assertEquals(result.getAge(),44);

        } catch (IOException | DuplicateNameException e) {
            Assert.fail();
        }
    }

/*    @After
    public void after(){
        try {
            this.db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
