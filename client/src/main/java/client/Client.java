package client;

import model.DataAccessException;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public interface Client {
    default void run() {
        System.out.println(startMessage());
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine().trim();

            try {
                result = eval(line);
                if (result == null) {
                    return;
                }
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        try {
            quit();
        } catch (Exception e) {
            System.exit(0);
        }
        System.out.println();
    }

    default void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }

    default String eval(String input) {
        if (input == null) {
            return null;
        }
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (cmd.equals("quit")) {
                return "quit";
            } else {
                String result = evaluateCommand(cmd, params);
                return (result != null) ? result : help();
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    String startMessage();

    String help();

    String evaluateCommand(String cmd, String[] params) throws DataAccessException;

    void quit() throws DataAccessException;
}
