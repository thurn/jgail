package ca.thurn.uct.connect4;


/**
 * An Action in a game of Connect 4
 */
public class C4Action {	
  // Representation: [columnNumber][player]

  /**
   * @param player Player taking the action.
   * @param columnNumber Column number to drop a piece into.
   * @return A long representing a Connect4 Action.
   */
  public static long create(int player, int columnNumber) {
    return ((long)columnNumber << 32) | (long)player;
  }

  /**
   * @param action A Connect4 Action.
   * @return Column number of this action.
   */
  public static int getColumnNumber(long action) {
    return (int)(action >> 32);
  }

  /**
   * @param action A Connect4 Action.
   * @return Player performing this action.
   */
  public static int getPlayer(long action) {
    return (int)action;
  }

  /**
   * @param action A Connect4 Action.
   * @return String representation of this action.
   */
  public static String toString(long action) {
    return "[" + getColumnNumber(action) + "]";
  }
}


