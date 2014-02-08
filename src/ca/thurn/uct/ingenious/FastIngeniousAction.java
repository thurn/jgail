package ca.thurn.uct.ingenious;

import ca.thurn.uct.core.Action;

public class FastIngeniousAction implements Action {
  private final int value;
  
  private FastIngeniousAction(int value) {
    this.value = value;
  }
  
  public static FastIngeniousAction create(FastIngeniousPiece piece, byte x1, byte y1, byte x2, byte y2) {
    return new FastIngeniousAction((x1 << 23) | (y1 << 18) | (x2 << 13) | (y2 << 8) | piece.getBits());
  }
  
  public byte getX1() {
    return (byte) ((value >> 23) & 0x1f);
  }
  
  public byte getY1() {
    return (byte) ((value >> 18) & 0x1f);
  }
  
  public byte getX2() {
    return (byte) ((value >> 13) & 0x1f);
  }
  
  public byte getY2() {
    return (byte) ((value >> 8) & 0x1f);
  }
  
  public FastIngeniousPiece getPiece() {
    return new FastIngeniousPiece((byte) (value & 0xff));
  }
}
