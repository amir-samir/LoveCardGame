
/**
 * In dieser Klasse werden die Spielregeln und die logischen Abläufe behandelt
 * Ein Game wird Gestartet und Ein Gamer wird eingefügt und die Karten werden verteilt
 *
 * @author Amir, Mateo, Chen, Yilu
 *
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Game {
    public List<Gamer> gamers = new LinkedList<Gamer>();
    public Library library = new Library();
    public boolean isStart = false;
    public boolean isOver = false;
    public int roundCount = 0;
    // the round gamer count
    public int currentCount = -1;
    public Gamer choseGamer = null;
    public String guessName = null;
    public final Server server;
    public String lastWinner = null;
    public int gameCount = 1;

    public Game(Server server) {
        this.server = server;
    }

    /**
     * @param gamer Der Spieler, der hinzugefügt werden soll
     * description This method adds a new Gamer.
     **/
    public void addGamer(Gamer gamer) {
        if (isStart) {
            server.singleMessage(gamer.username, "Game already started");
        } else if (gamers.size() >= 4) {
            server.singleMessage(gamer.username, "Game is full");
        } else if (gamers.contains(gamer)){
            server.singleMessage(gamer.username, "You are already part of the game");
        } else {
            gamers.add(gamer);
            server.singleMessage(gamer.username, "you have been added to the game");
            server.messageForAllUsers(gamer.username + " adds to the game");
        }
    }

    /**
     *
     * description: diese Methode zeigt für jeden Spieler die Anzahl seiner Token an
     **/
    public void broadcastToken() {
        server.messageForAllUsers("all gamer token rank");
        for (Gamer gamer : gamers) {
            server.messageForAllUsers(gamer.username + " has" + gamer.token + " token");
        }
    }
    /**
     *
     * description: diese Methode ist zur Initialisierung da.
     **/
    public void init() {
        roundCount = 0;
        currentCount = -1;
        choseGamer = null;
        guessName = null;

    }
    /**
     * @param gamer Der Spieler, der das Spiel startet
     * description: diese Methode startet ein neues Game
     **/
    public void startGame(Gamer gamer) {
        if (gamers.size() < 2) {
            server.singleMessage(gamer.username, "gamer number is not enough");
        } else if (!isStart) {
            // game beginnen
            isStart = true;
            String msg = "";
            msg += "The game started!!!";
            msg += "\n" + "The game has " + gamers.size() + " gamers";
            Collections.shuffle(gamers);

            msg += "\n" + "order is refresh.The new order is here";
            for (int i = 0; i < gamers.size(); i++) {
                msg += "\n" + (i + 1) + ":" + gamers.get(i).username;
            }
            msg += "\n" + "=========================================";
            msg += "\n" + "this is new game.";
            server.messageForAllUsers(msg);
            // initialisierung von Library
            library.init(gamers.size());
            if (gamers.size() == 2) {
                msg = "Because gamer number is 2, so remove 4 cards. The first on is covered, 3 removes is here:";
                server.messageForAllUsers("");
                for (int i = 0; i < 3; i++) {
                    msg += "\n" + (i + 1) + ":" + library.played.get(i).name;
                }
                server.messageForAllUsers(msg + "\n" + "---------------------------");

            } else {
                server.messageForAllUsers("Because gamer number is more than 2, so remove a card.\n---------------------------");
            }
            // initialisierung von handcards
            for (Gamer g : gamers) {
                g.handCard = library.draw();
                g.value = g.handCard.value;
                server.singleMessage(g.username, "your handCard is " + g.handCard.name);
            }
            // first round
            nextRound();
        } else {
            server.singleMessage(gamer.username, "only the one who created the game can start it");
        }
    }
    /**
     *
     * description: diese Methode startet ein next Game
     **/
    public void nextGame() {
        broadcastToken();
        String msg = "=========================================";
        msg += "\n" + "order is update.The new order is here";
        for (int i = 0; i < gamers.size(); i++) {
            if (gamers.get(i).username.equals(lastWinner)) {
                break;
            } else {
                gamers.add(gamers.remove(i));
            }
        }
        for (int i = 0; i < gamers.size(); i++) {
            msg += "\n" + (i + 1) + ":" + gamers.get(i).username;
        }
        msg += "\n" + "this is game" + gameCount;
        server.messageForAllUsers(msg);
        // initialisierung deck
        library.init(gamers.size());
        if (gamers.size() <= 2) {
            msg = "Because gamer number less than 2, so remove 3 cards.The removes is here";
            server.messageForAllUsers("");
            for (int i = 0; i < 3; i++) {
                msg += "\n" + (i + 1) + ":" + library.played.get(i).name;
            }
            server.messageForAllUsers(msg + "\n" + "---------------------------");
        } else {
            server.messageForAllUsers("Because gamer number is 4, so remove a card.\n---------------------------");
        }
        // initialisierung handcard
        for (Gamer g : gamers) {
            g.init();
            g.handCard = library.draw();
            g.value = g.handCard.value;
            server.singleMessage(g.username, "your handCard is " + g.handCard.name);
        }
        // inisialisierung
        init();
        // next round
        nextRound();
    }
    /**
     *
     * description: diese Methode ist für neue Runde zu starten(call nextGamer())
     **/
    public void nextRound() {
        roundCount++;
        server.messageForAllUsers("Round started!");
        currentCount = -1;
        nextGamer();
    }
    /**
     *
     * description: diese Methode zeigt welche Spieler dieser Runde überlebt haben und wenn die surviveCount = 1 ist dann (Call gameEnd())
     **/
    public void nextGamer() {
        int surviveCount = 0;
        String surviveName = "";
        for (Gamer g : gamers) {
            if (g.isSurvival) {
                surviveCount++;
                surviveName = g.username;
            }
        }
        if (surviveCount == 1) {
            lastWinner = surviveName;
            gameEnd();
        }

        currentCount++;
        if (currentCount == gamers.size()) {
            server.messageForAllUsers("The round is end!\n---------------------------");
            nextRound();
            return;
        }


        gamers.get(currentCount).inProtect = false;
        if (gamers.get(currentCount).isSurvival) {
            gamerAction();
        } else {
            nextGame();
        }
    }
    /**
     *
     * description: diese Methode beendet das Spiel,
     * wenn die maximale Zahl von Tokens je nach Anzahl der Spieler erreicht wird, wird das Spiel beendet.
     **/
    public void gameEnd() {
        int index = 0;
        for (int i = 0; i < gamers.size(); i++) {
            if (lastWinner.equals(gamers.get(i).username)) {
                index = i;
                break;
            }
        }
        gamers.get(index).token++;
        int flag = 0;
        switch (gamers.size()) {
            case 2:
                flag = 7;
                break;
            case 3:
                flag = 5;
                break;
            case 4:
                flag = 4;
                break;
        }
        if (gamers.get(index).token == flag) {
            isOver = true;
            server.messageForAllUsers("-------------------------------------------------");
            server.messageForAllUsers("   _____ ______ __  __ ______    ______      ________ _____  \n" +
                    "  / ____|  ____|  \\/  |  ____|  / __ \\ \\    / /  ____|  __ \\ \n" +
                    " | |  __| |__  | \\  / | |__    | |  | \\ \\  / /| |__  | |__) |\n" +
                    " | | |_ |  __| | |\\/| |  __|   | |  | |\\ \\/ / |  __| |  _  / \n" +
                    " | |__| | |____| |  | | |____  | |__| | \\  /  | |____| | \\ \\ \n" +
                    "  \\_____|______|_|  |_|______|  \\____/   \\/   |______|_|  \\_\\\n" +
                    "                                                             \n" +
                    "                                                             ");

            server.messageForAllUsers("-------------------------------------------------");
            server.messageForAllUsers("The Winner is " + lastWinner);
            server.closeGame();
        } else {
            nextGame();
        }
    }
    /**
     *
     * description: In dieser wird geprüft ob ein Deck leer ist, und wenn ja dann suche den Gewinner, sonst ziehe eine karte
     *
     **/
    public void gamerAction() {
        if (library.library.size() == 0) {
            lastWinner = biggestValueGamer().username;
            server.messageForAllUsers("library is empty,the game " + gameCount + " is over.");
            server.messageForAllUsers("The game " + gameCount + " winner is " + lastWinner);
            gameEnd();
        }
        server.messageForAllUsers("Turn to " + gamers.get(currentCount).username);
        if(gamers.get(currentCount).isSurvival) {
            letGamerDraw(gamers.get(currentCount));
        } else if (gamers.size() - 1 == currentCount){
            nextGame();
        } else if (gamers.get(currentCount + 1).isSurvival){
            letGamerDraw(gamers.get(currentCount + 1));
        } else if (gamers.size() - 1 == currentCount + 1){
            nextGame();
        } else {
            letGamerDraw(gamers.get(currentCount + 2));
        }
    }
    /**
     *
     * description: suche nach Gewinner,
     * wenn nur ein Spieler geblieben ist, dann dieser ist der Gewinner
     * wenn mehr als ein Spieler dann überprüfe welche Spieler hat die Karte mit der höheren Grade, er ist der Gewinnner
     * falls, mehr als ein Spieler geblieben sind, und mehr als ein Spieler die Gleiche Karte haben, dann überprüfe die Summe aller gespielten Karten von jedem Spieler, der Spieler mit der höheren Summe hat gewonnen
     * @return Der Spieler mit den meisten Punkten
     *
     **/
    public Gamer biggestValueGamer() {
        int biggestValue = 0;
        Gamer winner = null;
        List<Gamer> sames = new ArrayList<>();
        for (Gamer g : gamers) {
            if (g.value > biggestValue) {
                biggestValue = g.value;
                winner = g;
                sames.clear();
            } else if (g.value == biggestValue) {
                sames.add(g);
            }
        }
        if (sames.size() > 0) {
            winner.calPlayedValue();
            biggestValue = winner.playedValue;
            for (Gamer g : sames) {
                g.calPlayedValue();
                if (g.playedValue > biggestValue) {
                    biggestValue = g.playedValue;
                    winner = g;
                }
            }
        }
        return winner;
    }
    /**
     * @param gamer
     * description: diese Methode lässt ein Spieler eine Karte ziehen.
     * wenn der Spieler die Countess Karte und gleichzeitig King oder Prince hat, dann muss der Spieler die Countess spielen
     * sonst ziehe eine neue Karte
     **/
    public void letGamerDraw(Gamer gamer) {
        Card newCard = library.draw();
        server.singleMessage(gamer.username, "you draw a " + newCard.name + "");
        switch (newCard.name) {
            case "Countess":
                if (gamer.handCard.name.equals("King") || gamer.handCard.name.equals("Prince")) {
                    server.singleMessage(gamer.username, "because you have King or Prince in hand, Countess is discarded");
                    nextGamer();
                    return;
                }
            case "King":
                if (gamer.handCard.equals("Countess")) {
                    server.singleMessage(gamer.username, "because you dr Countess in hand, Countess is discarded");
                    nextGamer();
                    return;
                }
            case "Prince":
                if (gamer.handCard.equals("Countess")) {
                    server.singleMessage(gamer.username, "because you dr Countess in hand, Countess is discarded");
                    nextGamer();
                    return;
                }
        }
        gamer.newCard = newCard;
        server.singleMessage(gamer.username, "please chose a card to play");
    }

    /**
     * @param gamer
     * description: diese Methode wird gerufen, wenn der Spieler die HandKarte zum spielen auswählt
     *
     **/
    public void playHandCard(Gamer gamer) {
        if (!gamers.get(currentCount).username.equals(gamer.username)) {
            server.singleMessage(gamer.username, "It's not turn to you.you cant not play card.");
            return;
        }
        gamer.playedCards.add(gamer.handCard);
        gamer.handCard.play(this, gamer);
        gamer.handCard = gamer.newCard;
        gamer.value = gamer.handCard.value;
        gamer.newCard = null;
    }
    /**
     * @param gamer
     * description: diese Methode wird gerufen, wenn der Spieler die NewKarte zum spielen auswählt
     *
     **/
    public void playNewCard(Gamer gamer) {
        if (gamer.newCard != null) {

            if (!gamers.get(currentCount).username.equals(gamer.username)) {
                server.singleMessage(gamer.username, "It's not turn to you.you cant not play card.");
                return;
            }
            gamer.playedCards.add(gamer.newCard);
            gamer.newCard.play(this, gamer);
            gamer.newCard = null;
        }
    }
    /**
     * @param gamer Der ausgewählte Spieler
     * @param canSelf Boolean der angibt, ob ein Spieler sich selbst auswählen darf
     * description: Mit dieser Methode kann mann einen Spieler auswählen
     *
     **/
    public void letChoseGamer(Gamer gamer, Boolean canSelf) {
        String msg = "please chose a user:";
        int choseCount = 0;
        for (int i = 0; i < gamers.size(); i++) {
            if (gamers.get(i).isSurvival && !gamers.get(i).inProtect && (canSelf || !gamers.get(i).username.equals(gamer.username))) {
                msg += "\n" + gamers.get(i).username;
                choseCount++;
            }
        }
        if (choseCount == 0) {
            server.messageForAllUsers("no one can select,so useLess");
            nextGamer();
        } else {
            server.singleMessage(gamer.username, msg);
        }
    }
    /**
     * @param gamer Der ausgewählte Spieler
     * @param username Name des ausgewählten Spielers
     * description: überprüft ob ein Spieler dran ist
     * ob der ausgewählte Spieler protected ist
     * ob der ausgewählte Spieler noch verfügbar ist
     **/
    public void selectGamer(Gamer gamer, String username) {
        if (!gamers.get(currentCount).username.equals(gamer.username)) {
            server.singleMessage(gamer.username, "It's not turn to you.you cant not select gamer.");
            return;
        }
        Gamer choseGamer = null;
        for (Gamer g : gamers) {
            if (g.username.equals(username)) {
                if (!g.inProtect && g.isSurvival) {
                    choseGamer = g;
                }
            }
        }
        if (choseGamer == null) {
            server.singleMessage(gamer.username, "chosen is not exist or out or protected.please chose again.");
        } else {
            this.choseGamer = choseGamer;
            Card lastCard = gamer.playedCards.get(gamer.playedCards.size() - 1);
            switch (lastCard.name) {
                case "Guard":
                    letGuessCard(gamer);
                    break;
                default:
                    lastCard.trigger(this, gamer, choseGamer);
            }
        }
    }

    /**
     * @param gamer
     * description: schreibt den Spieler ,dass er eine Karte erraten soll.
     *
     **/
    public void letGuessCard(Gamer gamer) {
        server.singleMessage(gamer.username, "please guess his card:");
    }
    /**
     * @param gamer Der Ausgewählte Spieler
     * @param cardName Die Ausgewählte Karte
     * description: diese Methode wird gerufen, um die Karte von einem anderen Spieler zu erraten
     *
     **/
    public void guessCard(Gamer gamer, String cardName) {
        if (!gamers.get(currentCount).username.equals(gamer.username)) {
            server.singleMessage(gamer.username, "It's not turn to you.you cant not guess.");
            return;
        }
        guessName = cardName;
        gamer.playedCards.get(gamer.playedCards.size() - 1).trigger(this, gamer, choseGamer);
    }
    /**
     * @param gamer
     * description: diese Methode entfernt einen Spieler
     *
     **/
    public void removeGamer(Gamer gamer) {
        gamer.isSurvival = false;
    }

    public void gamerList(Gamer request) {
        String msg = "This is gamer list:";
        for (int i = 0; i < gamers.size(); i++) {
            msg += "\n" + (i + 1) + ":" + gamers.get(i).username;
        }
        server.singleMessage(request.username, msg);
    }
}

