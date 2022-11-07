import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Diese Klasse stellt den Server dar, auf welchem der Chat und das Spiel ausgeführt werden.
 * @author Luca, Dairen
 */

public class Server {

    public HashMap<String, ClientHandler> users = new HashMap<String, ClientHandler>();
    private ServerSocket serverSocket;
    public Game game;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Dies ist die Main-Methode. Sie instanziiert einen neuen Server und startet diesen.
     * @param args Kommandozeilenparameter
     */

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("Fehler. Der Server konnte nicht gestartet werden.");
        }
    }

    /**
     * Diese Methode sorgt dafür, dass private Nachrichten verschickt werden können.
     * @param user Username des Empfängers der Nachricht
     * @param message Die Nachricht, die versendet werden soll
     */
    public void singleMessage(String user, String message) {
        users.get(user).writer.println(message);
    }

    /**
     * Diese Methode verschickt eine Nachricht an alle aktiven Mitglieder des Chat-Raums.
     * @param message Die Nachricht, die versendet werden soll
     */
    public void messageForAllUsers(String message) {
        for (ClientHandler clientHandler : users.values()) {
            clientHandler.writer.println(message);
        }
    }

    /**
     * Diese Methode erstellt ein neues Spiel.
     */
    public void createGame(){
        this.game = new Game(this);
    }

    /**
     * Diese Methode fügt neue Mitglieder des Chat-Raums in die HashMap aller Mitglieder hinzu.
     * @param clientHandler Der ClientHandler, der in die HashMap eingefügt werden soll.
     * @return Gibt zurück, ob der ClientHandler in die HashMap eingefügt werden konnte.
     */
    public boolean addClient(ClientHandler clientHandler) {
        if (users.containsKey(clientHandler.username)) {
            return false;
        } else {
            users.put(clientHandler.username, clientHandler);
            return true;
        }
    }

    /**
     * Diese Methode prüft, ob ein Username schon in der Hashmap vorhanden ist.
     * @param username Der Username, der überprüft werden soll
     * @return Gibt zurück, ob der Username schon exisitiert
     */
    public boolean checkName(String username) {
        return !users.containsKey(username);
    }

    /**
     * Diese Methode startet den Server und baut die Verbindungen zu neuen Spielern auf.
     * Hierfür werden neue Threads instanziiert, um mehrere Verbindungen gleichzeitig aufrechtzuerhalten.
     */
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket, this);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Diese Methode gibt, sofern es existiert, das Spiel zurück. Für den Fall, dass noch kein Spiel erstellt wurde, wird nichts zurückgegeben.
     * @return Das aktive Spiel oder null (wenn kein Spiel existiert)
     */
    public Game getGame(){
        if (game != null) {
            return this.game;
        } else return null;
    }

    /**
     * Diese Methode schließt das aktive Spiel, wodurch ein Neustart ermöglicht wird.
     */
    public void closeGame(){
        game = null;
    }
}
