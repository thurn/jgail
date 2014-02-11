package ca.thurn.uct.ingenious;


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

  public static long create(int piece, int x1, int y1, int x2, int y2) {
    return piece | (y2 << 8) | (x2 << 13) | (y1 << 18) | (x1 << 23); 
  }

  public static int getX1(long action) {
    return (int) ((action >> 23) & 0x1F);
  }

  public static int getY1(long action) {
    return (int) ((action >> 18) & 0x1F);
  }

  public static int getX2(long action) {
    return (int) ((action >> 13) & 0x1F);
  }

  public static int getY2(long action) {
    return (int) ((action >> 8) & 0x1F);
  }

  public static int getPiece(long action) {
    return (int) (action & 0xFF);
  }
  
}
