package ca.thurn.uct.connect4;

import ca.thurn.uct.algorithm.Action;
import ca.thurn.uct.algorithm.Player;

public class Connect4Action implements Action {
	/**
	 * The column in which you will drop your piece.
	 */
	private final int columnNumber;
	
	/**
	 * The player performing this action
	 */
	private final Player player;
	
	public Connect4Action(Player player, int columnNumber) {
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

  @Override
  public int getActionNumber() {
    return columnNumber;
  }
}
