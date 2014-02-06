package ca.thurn.uct.algorithm;

import ca.thurn.uct.core.Action;

public interface StateInitializer<A extends Action> {
  public State<A> initializeFromState(State<A> state);
}
