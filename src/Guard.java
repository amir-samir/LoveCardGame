/**
 * description: Apply Effect fuer Guard Karte
 * @author Yilu,Amir,Chen,Mateo
 **/
public class Guard extends Card {
    public Guard() {
        value = 1;
        name = "Guard";
        description = "Guess a player's hand, if correct the player is out.";
    }

    @Override
    public void play(Game game, Gamer gamer) {
        game.server.messageForAllUsers(gamer.username + " play a Guard.");
        // er darf ihn selbser nicht auswaehlen
        game.letChoseGamer(gamer, false);
    }

    @Override
    public void trigger(Game game, Gamer gamer, Gamer choseGamer) {
        if (choseGamer.handCard.name.toUpperCase().equals(game.guessName.toUpperCase())) {
            game.removeGamer(choseGamer);
            game.server.messageForAllUsers(gamer.username + " guess true," + choseGamer.username + " is out.");
            game.choseGamer = null;
            game.guessName = null;
        } else {
            game.server.messageForAllUsers(gamer.username + " guess false");
        }
        game.nextGamer();
    }
}
