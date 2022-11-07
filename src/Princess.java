/**
 * description: Apply Effect fuer princess Karte
 * @author Yilu,Amir,Chen,Mateo
 **/
public class Princess extends Card {
    public Princess() {
        value = 8;
        name = "Princess";
        description = "Lose if discarded.";
    }

    @Override
    public void play(Game game, Gamer gamer) {
        game.server.messageForAllUsers(gamer.username + " play a Princess, so " + gamer.username + " is remove.");
        game.removeGamer(gamer);
        game.nextGamer();
    }

    @Override
    public void trigger(Game game, Gamer gamer, Gamer choseGamer) {
    }
}


