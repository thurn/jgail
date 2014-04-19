package ca.thurn.jgail.ingenious;

import java.util.Random;

/**
 * A specific colored hex on the board of Ingenious (or a special value called
 * OFF_BOARD).
 */
public class IngeniousHex {
  public static final int ORANGE = 1;
  public static final int YELLOW = 2;
  public static final int PURPLE = 3;
  public static final int RED = 4;
  public static final int GREEN = 5;
  public static final int BLUE = 6;
  public static final int OFF_BOARD = 7;
  
  private static final boolean USE_COLOR = true;
  
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_BLUE = "\u001B[34m";
  private static final String ANSI_PURPLE = "\u001B[35m";
  private static final String ANSI_CYAN = "\u001B[36m";
  
  private static final int[] values = {ORANGE, YELLOW, PURPLE, RED, GREEN, BLUE};
  private static final Random random = new Random();
  
  /**
   * @return A random hex color.
   */
  public static int randomHex() {
    return values[random.nextInt(values.length)];
  }
  
  /**
   * @return An array consisting of all possible colors (but not OFF_BOARD).
   */
  public static int[] allColors() {
    return values;
  }
  
  /**
   * @param hex An ingenious hex.
   * @return A string representation of the hex.
   */
  public static String toString(int hex) {
    if (USE_COLOR) {
      switch(hex) {
        case BLUE:
          return ANSI_BLUE + "BB" + ANSI_RESET;
        case GREEN:
          return ANSI_GREEN + "GG" + ANSI_RESET;
        case ORANGE:
          return ANSI_CYAN + "OO" + ANSI_RESET;
        case PURPLE:
          return ANSI_PURPLE + "PP" + ANSI_RESET;
        case RED:
          return ANSI_RED + "RR" + ANSI_RESET;
        case YELLOW:
          return ANSI_YELLOW + "YY" + ANSI_RESET;
        case OFF_BOARD:
          return "";
        default:
          throw new RuntimeException();
      }
    } else {
      switch(hex) {
        case BLUE:
          return "BB";
        case GREEN:
          return "GG";
        case ORANGE:
          return "OO";
        case PURPLE:
          return "PP";
        case RED:
          return "RR";
        case YELLOW:
          return "YY";
        case OFF_BOARD:
          return "";
        default:
          throw new RuntimeException();
      }      
    }
  }
}
