/**
 * description: abstrakte klasse Card
 * respäsentieren die allgemeiner Card function
 */
public abstract class Card {
    protected String name;
    protected int value;
    protected String description;

    /**
     * @param game Das Spiel
     * @param gamer Der Spieler
     * description: zwei abstrakte Methode fuer Game und Gamer
     */
    public abstract void play(Game game, Gamer gamer);

    public abstract void trigger(Game game, Gamer gamer, Gamer choseGamer);
}
