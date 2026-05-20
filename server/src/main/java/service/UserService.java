package service;

import dataaccess.AlreadyTakenException;
import dataaccess.AuthDAO;
import dataaccess.InvalidLoginException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;
import service.result.RegisterResult;

import java.util.Objects;

public class UserService {
    private final UserDAO userDAO;

    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException{
        UserData userData = userDAO.getUser(registerRequest.username());
        if (userData != null) {
            throw new AlreadyTakenException();
        }
        userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(userData);
        AuthData authData = authDAO.createAuth(userData.username());
        return new RegisterResult(authData.username(), authData.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws InvalidLoginException {
        UserData userData = userDAO.getUser(loginRequest.username());
        if (userData == null || !Objects.equals(userData.password(), loginRequest.password())) {
            throw new InvalidLoginException();
        }
        AuthData authData = authDAO.createAuth(userData.username());
        return new LoginResult(authData.username(), authData.authToken());
    }
//    public void logout(LogoutRequest logoutRequest) {}
}
