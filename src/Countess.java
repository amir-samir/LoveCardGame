/**
 * description: Apply Effect fuer Countess Karte
 * @author Yilu,Amir,Chen,Mateo
 **/
public class Countess extends Card {
    public Countess() {
        value = 7;
        name = "Countess";
        description = "Must be played if you have King or Prince in hand.";
    }

    @Override
    public void play(Game game, Gamer gamer) {
        game.server.messageForAllUsers(gamer.username + " play a Countess.");
        game.nextGamer();
    }

    @Override
    public void trigger(Game game, Gamer gamer, Gamer choseGamer) {
        return;
    }
}
