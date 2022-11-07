
/**
 * description: Apply Effect fuer Priest Karte
 * @author Yilu,Amir,Chen,Mateo
 **/
public class Priest extends Card {
    public Priest() {
        value = 2;
        name = "Priest";
        description = "Look at a player's hand.";
    }

    @Override
    public void play(Game game, Gamer gamer) {
        game.server.messageForAllUsers(gamer.username + " play a Priest.");
        game.letChoseGamer(gamer, false);
    }

    @Override
    public void trigger(Game game, Gamer gamer, Gamer choseGamer) {
        game.server.singleMessage(gamer.username, "his handCard is " + choseGamer.handCard.name);
        game.nextGamer();
    }
}
