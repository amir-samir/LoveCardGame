
/**
 * description: Apply Effect fuer Baron Karte
 * @author Yilu,Amir,Chen,Mateo
 **/
public class Baron extends Card {
    public Baron() {
        value = 3;
        name = "Baron";
        description = "Compare hands with another player, the one with the lower value is out.";
    }


    @Override
    public void play(Game game, Gamer gamer) {
        game.server.messageForAllUsers(gamer.username + " play a Baron.");
        // Er darf ihn selber nicht waehlen
        game.letChoseGamer(gamer, false);
    }

    @Override
    public void trigger(Game game, Gamer gamer, Gamer choseGamer) {
        // unterschiedliche Situationen bei verschiedenem raten Ergebniss
        if (gamer.value == choseGamer.value) {
            game.server.messageForAllUsers(gamer.username + " and " + choseGamer.username + " value is same,so need not out.");
        } else if (gamer.value > choseGamer.value) {
            game.server.messageForAllUsers(gamer.username + " bigger than " + choseGamer.username + " value ,so " + choseGamer.username + " is out.");
            game.removeGamer(choseGamer);
        } else {
            game.server.messageForAllUsers(choseGamer.username + " bigger than " + gamer.username + " value ,so " + gamer.username + " is out.");
            game.removeGamer(gamer);
        }
        game.nextGamer();
    }


}
