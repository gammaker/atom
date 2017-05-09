package ru.atom.authmm.server;

import org.hibernate.Session;
import org.junit.Test;
import ru.atom.authmm.server.auth.UserDao;
import ru.atom.authmm.server.auth.Database;
import ru.atom.authmm.server.auth.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class UserDaoTest {
    @Test
    public void insertTest() throws Exception {
        Database.setUp();
        Session session = Database.session();

        UserDao userDao = UserDao.getInstance();
        userDao.deleteByNameTxn(session, "TestUser");
        int numUsersBeforeTest = userDao.getAll(Database.session()).size();

        User user = new User("TestUser", "tupass");
        userDao.insertTxn(Database.session(), user);
        assertEquals(numUsersBeforeTest + 1, userDao.getAll(Database.session()).size());

        User foundUser = userDao.getByName(session, "TestUser");
        assertNotNull(foundUser);
        assertEquals("TestUser", foundUser.name());
    }
}
