package ca.thurn.uct.algorithm;

public interface InitialStateGenerator<A extends Action> {
  public State<A> initialState();
}
