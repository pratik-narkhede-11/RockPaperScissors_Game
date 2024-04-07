import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RockPaperScissorsServer {

   private static final int PORT = 8888;
    private static final int MAX_CLIENTS = 2;
    private final BlockingQueue<Socket> clientQueue; // Instance variable

    public RockPaperScissorsServer() {
        clientQueue = new LinkedBlockingQueue<>(MAX_CLIENTS); // Initialize in constructor
    }

    public static void main(String[] args) throws Exception {
        RockPaperScissorsServer server = new RockPaperScissorsServer(); // Create an instance

        ServerSocket serverSocket = new ServerSocket(PORT);

        // Spawn a separate thread for managing client connections
        new Thread(new ClientConnectionManager(serverSocket, server.clientQueue)).start();

       

        // Start game loop for two clients (use server instance)
        while (server.clientQueue.size() < MAX_CLIENTS) {
             if(server.clientQueue.size()==0){
            System.out.println("Waiting for both (2) players...");
             }

            if(server.clientQueue.size()==1){
            System.out.println("Waiting for other player...");
            }
           
           Thread.sleep(5000); 

        }
        System.out.println("Both Players are Active !");

        Socket player1Socket = server.clientQueue.take();
        Socket player2Socket = server.clientQueue.take();

        // Game logic and communication happen here (replace with actual implementation)
        String result = playGame(player1Socket, player2Socket);

        // Send result to both players and close connections
        sendResult(player1Socket, result);
        sendResult(player2Socket, result);

        player1Socket.close();
        player2Socket.close();
    }

    private static String playGame(Socket player1Socket, Socket player2Socket) throws IOException {
		BufferedReader in1 = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
		BufferedReader in2 = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()));

		// Get player moves
		String player1Move = in1.readLine();
		String player2Move = in2.readLine();

		// Determine winner
		String result;
		if (player1Move.equals(player2Move)) {
			result = "It's a tie!";
		} else if (player1Move.equals("rock") && player2Move.equals("scissors") ||
				   player1Move.equals("paper") && player2Move.equals("rock") ||
				   player1Move.equals("scissors") && player2Move.equals("paper")) {
			result = "Player 1 wins!";
		} else {
			result = "Player 2 wins!";
		}

		return result;
	}


    private static void sendResult(Socket socket, String result) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(result);
    }
}

class ClientConnectionManager implements Runnable {

    private ServerSocket serverSocket;
    private BlockingQueue<Socket> clientQueue;

    public ClientConnectionManager(ServerSocket serverSocket, BlockingQueue<Socket> clientQueue) {
        this.serverSocket = serverSocket;
        this.clientQueue = clientQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientQueue.put(clientSocket); // Add client to queue
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}