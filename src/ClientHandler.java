import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Diese Klasse fungiert als Bindestück zwischen Client und Server. Sie sorgt für die richtige Verarbeitung der
 * Nachrichten und Befehle, die von den Benutzern (Clients) gesendet werden.
 * @author Luca, Dairen
 */

public class ClientHandler implements Runnable {

    public final Server SERVER;
    public final Socket SOCKET;

    public String username;

    public BufferedReader reader;
    public PrintWriter writer;
    public Gamer gamer;

    /**
     * Diese Methode stellt den Konstruktor dar. Sie initialisiert die globalen Variablen und fügt nach Überprüfung den
     * eingegebenen Namen als neuen Username hinzu.
     * @param socket Initialisiert die Globale Variable Socket.
     * @param server Initialisiert die Globale Variable Server.
     */

    public ClientHandler(Socket socket, Server server) {

        this.SERVER = server;
        this.SOCKET = socket;

        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String name = reader.readLine();
            if (name == null || name.equals("") || !server.checkName(name)) {
                name = newUsername();
                username = name;
                gamer = new Gamer(name);
                SERVER.messageForAllUsers("Server: " + name + " joined the chat room.");
                SERVER.addClient(this);
                writer.println("Server: You joined the chat room.");
            } else {
                username = name;
                gamer = new Gamer(name);
                SERVER.messageForAllUsers("Server: " + name + " joined the chat room.");
                SERVER.addClient(this);
                writer.println("Server: You joined the chat room.");
            }

        } catch (Exception e) {
            closeEverything(socket, reader, writer);
            e.printStackTrace();
        }
    }

    /**
     * Diese Methode lässt den Benutzer einen neuen Namen wählen, falls sein ursprünglicher Name nicht akzeptiert wurde.
     * Dieser Name wird auch auf seine Verwendbarkeit hin überprüft.
     * @return Der neue, geprüfte Name
     */
    public String newUsername(){
        try {
            while (true){
                writer.println("SERVER: This username was not accepted. Please enter a new one");
                String name = reader.readLine();
                if (!name.isEmpty() && !name.isBlank() && name != null && SERVER.checkName(name)){
                    return name;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Diese Methode überschreibt die run() Methode von Runnable. Hier werden Eingaben der Benutzer verarbeitet.
     */
    @Override
    public void run() {
        while (SOCKET.isConnected()) {
            try {
                String messageFromClient = reader.readLine();
                if (messageFromClient == null) {
                    closeEverything(SOCKET, reader, writer);
                    break;
                }
                if (messageFromClient.toLowerCase().matches("^[a-zA-Z0-9!@#$%^&*)(]+:\\s\\bbye\\b")) {
                    break;
                } else {
                    int i = messageFromClient.indexOf(" ");
                    String command = "";
                    String message = "";
                    if (!messageFromClient.equals("") && "/".equals(String.valueOf(messageFromClient.charAt(0))) && (i > -1)) {
                        command = messageFromClient.substring(0, i);
                        message = messageFromClient.substring(i + 1);
                    } else {
                        command = messageFromClient;
                    }
                    if (!messageFromClient.equals("") && ((String.valueOf(messageFromClient.charAt(0)).equals("/")) ||
                            "bye".equalsIgnoreCase(messageFromClient))) {
                        switch (command.toUpperCase()) {
                            case "/CREATE":
                                if (SERVER.getGame() != null) {
                                    SERVER.singleMessage(username, "Game already exists.");}
                                else {
                                    SERVER.createGame();
                                    SERVER.messageForAllUsers(username + " created a new Game.");
                                }
                                break;
                            case "/GAMERS":
                                if (SERVER.getGame() != null) {
                                    SERVER.game.gamerList(gamer);
                                }  else {
                                    SERVER.singleMessage(username, "There is no Game yet. Enter /Create to create a new Game.");
                                }
                                break;
                            case "/JOIN":
                                if (SERVER.getGame() != null) {
                                    SERVER.game.addGamer(gamer);
                                } else {
                                    SERVER.singleMessage(username, "There is no Game yet. Enter /Create to create a new Game.");
                                }
                                break;
                            case "/START":
                                if (SERVER.getGame() != null) {
                                    SERVER.game.startGame(gamer);
                                } else {
                                    SERVER.singleMessage(username, "There is no Game yet. Enter /Create to create a new Game.");
                                }
                                break;
                            case "BYE":
                                SERVER.messageForAllUsers(username + " has left the Chat.");
                                SERVER.users.remove(username);
                                closeEverything(SOCKET, reader, writer);

                            case "/PLAY_HANDCARD":
                                if (SERVER.getGame() != null) {
                                    SERVER.game.playHandCard(gamer);
                                } else {
                                    SERVER.singleMessage(username, "There is no Game yet. Enter /Create to create a new Game.");
                                }
                                break;
                            case "/PLAY_NEWCARD":
                                if (SERVER.getGame() != null) {
                                    SERVER.game.playNewCard(gamer);
                                }else {
                                    SERVER.singleMessage(username, "There is no Game yet. Enter /Create to create a new Game.");
                                }
                                break;
                            case "/SELECT_GAMER":
                                if (SERVER.getGame() != null) {
                                    SERVER.game.selectGamer(gamer, message);
                                } else {
                                    SERVER.singleMessage(username, "There is no Game yet. Enter /Create to create a new Game.");
                                }
                                break;
                            case "/GUESS":
                                if (SERVER.getGame() != null) {
                                    SERVER.game.guessCard(gamer, message);
                                } else {
                                    SERVER.singleMessage(username, "There is no Game yet. Enter /Create to create a new Game.");
                                }
                                break;
                            case "/WATCH_HANDCARD":
                                if (SERVER.getGame() != null) {
                                    SERVER.singleMessage(gamer.username, "your handCard is " + gamer.handCard.name + "");
                                } else {
                                    SERVER.singleMessage(username, "There is no Game yet. Enter /Create to create a new Game.");
                                }
                                break;
                            case "/MSG":
                                privateMessage(message);
                                break;
                            case "/SCORE":
                                if (SERVER.getGame() != null) {
                                    SERVER.game.broadcastToken();
                                } else {
                                    SERVER.singleMessage(username, "There is no Game yet. Enter /Create to create a new Game.");
                                }
                                break;
                            case "/HELP":
                                SERVER.singleMessage(username, "/HELP : Shows all commands.");
                                SERVER.singleMessage(username, "/MSG <username> : Sends a privat message to a single user.");
                                SERVER.singleMessage(username, "/CREATE : Creates a Room for a new Game.");
                                SERVER.singleMessage(username, "/JOIN : Joins a existing game.");
                                SERVER.singleMessage(username, "/START : Starts the game game.");
                                SERVER.singleMessage(username, "BYE : Leave the chatroom.");
                                SERVER.singleMessage(username, "/GAMERS : Shows list of all Gamers.");
                                SERVER.singleMessage(username, "/WATCH_HANDCARD : Shows your Hand Card");
                                SERVER.singleMessage(username, "/GUESS : To guess a card.");
                                SERVER.singleMessage(username, "/SELECT_GAMER: : To select a Gamer.");
                                SERVER.singleMessage(username, "/PLAY_NEWCARD : Plays the card which you have drawn");
                                SERVER.singleMessage(username, "/PLAY_HANDCARD : Plays the Handcard card.");
                                break;

                            default:
                                SERVER.singleMessage(username, "Invalid Command");
                                break;
                        }
                    }
                    else {
                        SERVER.messageForAllUsers(username + ": " + messageFromClient);
                    }

                }
            } catch (IOException e) {
                closeEverything(SOCKET, reader, writer);
                break;
            }
        }
    }

    /**
     * Diese Methode verarbeitet private Nachrichten. Erst wird auf die Gültigkeit des Usernames geprüft und
     * anschließend die Nachricht an den entsprechenden Benutzer verschickt.
     * @param message Der Befehl, der von der Methode verarbeitet werden soll
     */
    public void privateMessage(String message) {
        int splitPoint = message.indexOf(" ");
        if (splitPoint > 0) {

            String msg_target = message.substring(0, splitPoint);
            String privat_msg = message.substring(splitPoint + 1);

            if (!SERVER.checkName(msg_target)) {

                SERVER.singleMessage(username, username + " @" + msg_target + ": " + privat_msg);
                SERVER.singleMessage(msg_target, username + " @" + msg_target + ": " + privat_msg);

            } else {

                SERVER.singleMessage(username, "Invalid Username: " + msg_target + "");
            }

        } else {

            SERVER.singleMessage(username, "Target Name or Message is missing.");
        }
    }

    /**
     * Diese Methode schließt für ein ordnungsgemäßes Schließen der Verbindung zum Server alle zugehörigen Instanzen.
     * @param socket Der Socket, der geschlossen werden soll
     * @param bufferedReader Der BufferedReader, der geschlossen werden soll
     * @param bufferedWriter Der BufferedWriter, der geschlossen werden soll
     */
    public void closeEverything(Socket socket, BufferedReader bufferedReader, PrintWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
