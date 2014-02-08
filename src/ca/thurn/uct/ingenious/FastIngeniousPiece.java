package ca.thurn.uct.ingenious;

public class FastIngeniousPiece {
  private final byte piece;
  
  FastIngeniousPiece(byte piece) {
    this.piece = piece;
  }
  
  public static FastIngeniousPiece create(IngeniousHex h1, IngeniousHex h2) {
    return new FastIngeniousPiece((byte) ((hexToBits(h1) << 4) | hexToBits(h2)));
  }
  
  byte getBits() {
    return piece;
  }
  
  public IngeniousHex getHex1() {
    return bitsToHex((byte) ((piece >> 4) & 0xf)); 
  }
  
  public IngeniousHex getHex2() {
    return bitsToHex((byte) (piece & 0xf)); 
  }
  
  public static byte hexToBits(IngeniousHex hex) {
    switch (hex) {
      case BLUE:
        return 0x1;
      case GREEN:
        return 0x2;
      case ORANGE:
        return 0x3;
      case PURPLE:
        return 0x4;
      case RED:
        return 0x5;
      case YELLOW:
        return 0x6;
      case OFF_BOARD:
        return 0x7;
      default:
        throw new RuntimeException("Unknown hex type");
    }
  }
  
  public static IngeniousHex bitsToHex(byte bits) {
    switch (bits) {
      case 0x1:
        return IngeniousHex.BLUE;
      case 0x2:
        return IngeniousHex.GREEN;
      case 0x3:
        return IngeniousHex.ORANGE;
      case 0x4:
        return IngeniousHex.PURPLE;
      case 0x5:
        return IngeniousHex.RED;
      case 0x6:
        return IngeniousHex.YELLOW;
      case 0x7:
        return IngeniousHex.OFF_BOARD;
      default:
        throw new RuntimeException("Unknown hex type");
    }
  }
  
  public String toString() {
    return "[" + getHex1() + " " + getHex2() + "]";
  }
  
}
