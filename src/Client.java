import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Diese Klasse stellt einen Client dar, der sich zum Chat-Raum des Spiels LOVE LETTER verbinden möchte.
 * Hierbei werden Nachrichten von anderen Spielern oder Anweisungen vom Spiel an den Benutzer weitergegeben.
 * Zusätzlich kann der Benutzer Nachrichten verschicken.
 * @author Luca, Dairen
 */

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    Scanner scanner = new Scanner(System.in);

    /**
     * Diese Klasse stellt den Konstruktor dar. Dieser erstellt einen neuen Socket sowie den BufferedReader und BufferedWriter.
     * @param socket Der Socket, der global im Client gespeichert werden soll
     */
    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Diese Methode stellt die Main-Methode dar. Sie erstellt einen neuen Socket und mit diesem dann einen neuen Client.
     * @param args Konsolenargumente
     */
    public static void main(String[] args) {
        try {
            System.out.println("  _      ______      ________   _      ______ _______ _______ ______ _____  \n" +
                    " | |    / __ \\ \\    / /  ____| | |    |  ____|__   __|__   __|  ____|  __ \\ \n" +
                    " | |   | |  | \\ \\  / /| |__    | |    | |__     | |     | |  | |__  | |__) |\n" +
                    " | |   | |  | |\\ \\/ / |  __|   | |    |  __|    | |     | |  |  __| |  _  / \n" +
                    " | |___| |__| | \\  /  | |____  | |____| |____   | |     | |  | |____| | \\ \\ \n" +
                    " |______\\____/   \\/   |______| |______|______|  |_|     |_|  |______|_|  \\_\\\n" +
                    "                                                                            \n" +
                    "                                                                            ");
            System.out.println("Enter your username for the group chat: ");
            //String username = scanner.nextLine();
            Socket socket = new Socket("localhost", 1234);
            Client client = new Client(socket);
            client.listenForMessage();
            client.sendMessage();
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("Dieser Client konnte nicht erstellt werden.");
        }
    }

    /**
     * Diese Methode ist für das Versenden der Nachrichten verantwortlich. Zusätzlich prüft sie auf das Schlüsselwort
     * "bye" um dann entsprechend die Verbindung zum Server zu trennen.
     */
    public void sendMessage() {
        try {

            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                if (messageToSend.equalsIgnoreCase("bye")) {
                    bufferedWriter.write(messageToSend);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    System.exit(0);
                }
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Diese Methode prüft eingehende Nachrichten und druckt sie.
     */

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        if (msgFromGroupChat != null)
                            System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    /**
     * Diese Methode schließt für ein ordnungsgemäßes Schließen der Verbindung zum Server alle zugehörigen Instanzen.
     * @param socket Der Socket, der geschlossen werden soll
     * @param bufferedReader Der BufferedReader, der geschlossen werden soll
     * @param bufferedWriter Der BufferedWriter, der geschlossen werden soll
     */
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
