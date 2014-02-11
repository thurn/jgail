package ca.thurn.uct.connect4;


/**
 * An Action in a game of Connect 4
 */
public class C4Action {	
  public static long create(int player, int columnNumber) {
	  return ((long)columnNumber << 32) | (long)player;
	}

  public static int getColumnNumber(long action) {
    return (int)(action >> 32);
  }

  public static int getPlayer(long action) {
    return (int)action;
  }
  
  public static String toString(long action) {
    return "[" + getColumnNumber(action) + "]";
  }
}


