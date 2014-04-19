package ca.thurn.jgail.ingenious;


/**
 * An Action in the game Ingenious.
 */
public class IngeniousAction {
  // Bit structure:
  // 0000 AAAA ABBB BBCC CCCD DDDD EEEE FFFF
  // AAAAA -> x1
  // BBBBB -> y1
  // CCCCC -> x2
  // DDDDD -> y2
  // EEEE -> Piece hex 1
  // FFFF -> Piece hex 2

  /**
   * Create a new Ingenious action represented as a long.
   *
   * @param piece Piece to place.
   * @param x1 First action x coordinate. 
   * @param y1 First action y coordinate.
   * @param x2 Second action x coordinate.
   * @param y2 Second action y coordinate.
   * @return Action represented as a long.
   */
  public static long create(int piece, int x1, int y1, int x2, int y2) {
    return piece | (y2 << 8) | (x2 << 13) | (y1 << 18) | (x1 << 23); 
  }

  /**
   * @param action Ingenious action.
   * @return First x coordinate of action.
   */
  public static int getX1(long action) {
    return (int) ((action >> 23) & 0x1F);
  }

  /**
   * @param action Ingenious action.
   * @return First y coordinate of action.
   */
  public static int getY1(long action) {
    return (int) ((action >> 18) & 0x1F);
  }

  /**
   * @param action Ingenious action.
   * @return Second x coordinate of action.
   */
  public static int getX2(long action) {
    return (int) ((action >> 13) & 0x1F);
  }

  /**
   * @param action Ingenious action.
   * @return Second y coordinate of action.
   */
  public static int getY2(long action) {
    return (int) ((action >> 8) & 0x1F);
  }

  /**
   * @param action Ingenious action.
   * @return Piece action would place.
   */
  public static int getPiece(long action) {
    return (int) (action & 0xFF);
  }
  
}
