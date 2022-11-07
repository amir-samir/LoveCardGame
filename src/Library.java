import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * description: diese Klasse addiert alle Karten zur Deck
 * @author Chen,Amir,Yilu,Mateo
 **/

public class Library {
    public LinkedList<Card> library = new LinkedList<Card>();
    public LinkedList<Card> played = new LinkedList<Card>();

    public Library() {
        addAllCard();
    }

    private void addAllCard() {
        library.add(new Guard());
        library.add(new Guard());
        library.add(new Guard());
        library.add(new Guard());
        library.add(new Guard());
        library.add(new Priest());
        library.add(new Priest());
        library.add(new Princess());
        library.add(new King());
        library.add(new Handmaid());
        library.add(new Handmaid());
        library.add(new Baron());
        library.add(new Baron());
        library.add(new Prince());
        library.add(new Prince());
        library.add(new Countess());
    }

    public void init(int gamerCount) {
        library.clear();
        played.clear();
        addAllCard();
        mix();
        if (gamerCount > 2 && gamerCount <= 4) {
            played.add(library.pop());
        } else {
            List<Card> res = new ArrayList<Card>();
            res.add(library.pop());
            res.add(library.pop());
            res.add(library.pop());
            res.add(library.pop());
            played.addAll(res);
        }
    }

    public Card draw() {
        return library.pop();
    }

    public Card drawFromPlayed() {
        return played.pop();
    }

    public void intoPlayed(Card card) {
        played.add(card);
    }

    public void mix() {
        Collections.shuffle(library);
    }
}