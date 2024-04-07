import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class RockPaperScissorsClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8888;

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Get player move
            String playerMove = getPlayerMove();

            // Send move to server
            out.println(playerMove);

            // Receive game result from server
            String result = in.readLine();

            // Display game result
            System.out.println(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getPlayerMove() {
        Scanner scanner = new Scanner(System.in);
        String move;

        do {
            System.out.print("Enter your move (rock, paper, scissors): ");
            move = scanner.nextLine().toLowerCase();
        } while (!isValidMove(move));

        return move;
    }

    private static boolean isValidMove(String move) {
        return move.equals("rock") || move.equals("paper") || move.equals("scissors");
    }
}
