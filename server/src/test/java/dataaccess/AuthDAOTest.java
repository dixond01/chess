package dataaccess;

import model.AuthData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {

    private AuthDAO getDataAccess(Class<? extends AuthDAO> databaseClass) throws DataAccessException {
        AuthDAO authDAO;
        if (databaseClass.equals(SQLAuthDAO.class)) {
            authDAO = new SQLAuthDAO();
        } else {
            authDAO = new MemoryAuthDAO();
        }
        authDAO.deleteAllAuths();
        return authDAO;
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void deleteAuthSuccess(Class<? extends AuthDAO> authDAOClass) throws DataAccessException{
        AuthDAO authDAO = getDataAccess(authDAOClass);

        AuthData authData1 = authDAO.createAuth("username1");
        authDAO.createAuth("username2");
        authDAO.createAuth("username3");

        authDAO.deleteAuth(authData1);

        Collection<AuthData> actual = authDAO.listAuths().values();
        assertEquals(2, actual.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void deleteAuthFailure(Class<? extends AuthDAO> authDAOClass) throws DataAccessException{
        AuthDAO authDAO = getDataAccess(authDAOClass);

        authDAO.createAuth("username1");
        authDAO.createAuth("username2");
        authDAO.createAuth("username3");

        authDAO.deleteAuth(new AuthData("token", "username1"));

        Collection<AuthData> actual = authDAO.listAuths().values();
        assertEquals(3, actual.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void deleteAllAuthsSuccess(Class<? extends AuthDAO> authDAOClass) throws DataAccessException{
        AuthDAO authDAO = getDataAccess(authDAOClass);

        authDAO.createAuth("username1");
        authDAO.createAuth("username2");
        authDAO.createAuth("username3");

        authDAO.deleteAllAuths();

        Collection<AuthData> actual = authDAO.listAuths().values();
        assertEquals(0, actual.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createAuthSuccess(Class<? extends AuthDAO> authDAOClass) throws DataAccessException{
        AuthDAO authDAO = getDataAccess(authDAOClass);

        authDAO.createAuth("username1");

        Collection<AuthData> actual = authDAO.listAuths().values();
        assertEquals(1, actual.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createAuthFailure(Class<? extends AuthDAO> authDAOClass) throws DataAccessException{
        AuthDAO authDAO = getDataAccess(authDAOClass);

        assertThrows(DataAccessException.class, () ->
                authDAO.createAuth(null));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void getAuthSuccess(Class<? extends AuthDAO> authDAOClass) throws DataAccessException{
        AuthDAO authDAO = getDataAccess(authDAOClass);

        AuthData authData = authDAO.createAuth("username1");

        AuthData actual = authDAO.getAuth(authData.authToken());

        assertEquals("username1", actual.username());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void getAuthFailure(Class<? extends AuthDAO> authDAOClass) throws DataAccessException{
        AuthDAO authDAO = getDataAccess(authDAOClass);

        authDAO.createAuth("username1");
        AuthData actual = null;

        try {
            actual = authDAO.getAuth("token");
        } catch (DataAccessException e) {
            //do nothing
        }

        assertNull(actual);
    }
}
