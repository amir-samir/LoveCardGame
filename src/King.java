/**
 * description: Apply Effect fuer King Karte
 * @author Yilu,Amir,Chen,Mateo
 **/
public class King extends Card {
    public King() {
        value = 6;
        name = "King";
        description = "Player trades hands with any other player.";
    }

    @Override
    public void play(Game game, Gamer gamer) {
        game.server.messageForAllUsers(gamer.username + " play a King.");
        game.letChoseGamer(gamer, false);
    }

    @Override
    public void trigger(Game game, Gamer gamer, Gamer choseGamer) {
        Card temp = gamer.handCard;
        gamer.handCard = choseGamer.handCard;
        choseGamer.handCard = temp;
        game.server.messageForAllUsers("because of King," + gamer.username + " and " + choseGamer.username + "  is exchanged");
        game.nextGamer();
    }
}