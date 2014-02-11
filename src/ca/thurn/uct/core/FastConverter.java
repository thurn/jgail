package ca.thurn.uct.core;

public interface FastConverter<A extends Action> {
  public long toFastAction(A action);
  
  public A fromFastAction(long action);
  
  public int toFastPlayer(Player player);
  
  public Player fromFastPlayer(int player);
  
  public FastState toFastState(State<A> state);
}
