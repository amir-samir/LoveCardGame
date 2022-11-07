
/**
 * description: Apply Effect fuer Prince Karte
 * @author Yilu,Amir,Chen,Mateo
 **/
public class Prince extends Card {
    public Prince() {
        value = 5;
        name = "Prince";
        description = "Player can choose any player to discard their hand and draw a new one.";
    }

    @Override
    public void play(Game game, Gamer gamer) {
        game.server.messageForAllUsers(gamer.username + " play a Prince.");
        game.letChoseGamer(gamer, true);
    }

    @Override
    public void trigger(Game game, Gamer gamer, Gamer choseGamer) {
        game.server.messageForAllUsers(gamer.username + " select " + choseGamer.username);
        if (game.library.library.size() > 0) {
            game.library.intoPlayed(choseGamer.handCard);
            choseGamer.handCard = game.library.draw();
        } else {
            Card temp = choseGamer.handCard;
            choseGamer.handCard = game.library.drawFromPlayed();
            game.library.intoPlayed(temp);
        }
        game.server.singleMessage(choseGamer.username, "your handCard is refresh, now is " + choseGamer.handCard.name);
        game.nextGamer();
    }
}

