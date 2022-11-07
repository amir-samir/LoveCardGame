
/**
 * @extends Card
 * description: die Klasse initialisiert alle Gamer Eigenschaften
 * @author Yilu,Amir,Chen,Mateo
 **/
import java.util.LinkedList;
import java.util.List;

public class Gamer {
    public List<Card> playedCards = new LinkedList<>();
    // card in hand
    public Card handCard;
    // the drew card
    public Card newCard;
    public int value;
    public String username;
    public int token = 0;
    public boolean inProtect = false;
    public boolean isSurvival = true;
    public int playedValue = 0;

    public Gamer(String username) {
        this.username = username;
    }
    /**
     *
     * description: diese Methode initialisiert die Variablen(inProtect, isSurvival, PlayedValue, Value)
     *
     **/
    public void init() {
        inProtect = false;
        isSurvival = true;
        playedValue = 0;
        value = 0;
    }
    /**
     *
     * description: diese Methode summiert die Grades aller gespielten Karten von jedem Spieler
     *
     **/
    public void calPlayedValue() {
        for (Card card : playedCards) {
            playedValue += card.value;
        }
    }

}