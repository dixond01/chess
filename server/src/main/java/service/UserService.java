package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;
import service.result.RegisterResult;

public class UserService {
    private final UserDAO userDAO;

    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, DataAccessException{
        UserData userData = userDAO.getUser(registerRequest.username());
        if (userData != null) {
            throw new AlreadyTakenException("username already taken");
        }
        userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(userData);
        AuthData authData = authDAO.createAuth(userData.username());
        return new RegisterResult(authData.username(), authData.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws UnauthorizedException, DataAccessException {
        UserData userData = userDAO.getUser(loginRequest.username());
        if (userData == null || !BCrypt.checkpw(loginRequest.password(), userData.password())) {
            throw new UnauthorizedException("username or password incorrect");
        }
        AuthData authData = authDAO.createAuth(userData.username());
        return new LoginResult(authData.username(), authData.authToken());
    }
    public void logout(LogoutRequest logoutRequest) throws UnauthorizedException, DataAccessException{
        AuthData authData = authDAO.getAuth(logoutRequest.authToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }
        authDAO.deleteAuth(authData);
    }
}
