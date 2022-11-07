/**
 * description: Apply Effect fuer Handmaid Karte
 * @author Yilu,Amir,Chen,Mateo
 **/
public class Handmaid extends Card {
    public Handmaid() {
        value = 4;
        name = "Handmaid";
        description = "You cannot be chosen until your next turn.";
    }

    @Override
    public void play(Game game, Gamer gamer) {
        gamer.inProtect = true;
        game.server.messageForAllUsers(gamer.username + " is in protect.");
        game.nextGamer();
    }

    @Override
    public void trigger(Game game, Gamer gamer, Gamer choseGamer) {
        return;
    }
}