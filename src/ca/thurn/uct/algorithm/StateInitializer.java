package ca.thurn.uct.algorithm;

public interface StateInitializer<A extends Action> {
  public State<A> initializeFromState(State<A> state);
}
