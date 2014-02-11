package ca.thurn.uct.connect4;

import ca.thurn.uct.core.Action;
import ca.thurn.uct.core.Player;

/**
 * An Action in a game of Connect 4
 */
public class C4Action implements Action {
    /**
     * The column in which you will drop your piece.
     */
    private final int columnNumber;
    
    /**
     * The player performing this action
     */
    private final Player player;
    
    public C4Action(Player player, int columnNumber) {
        this.player = player;
        this.columnNumber = columnNumber;
    }

  int getColumnNumber() {
    return columnNumber;
  }

  Player getPlayer() {
    return player;
  }
  
  public String toString() {
    return "[" + columnNumber + "]";
  }
}
