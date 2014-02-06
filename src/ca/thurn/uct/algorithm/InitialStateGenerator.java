package ca.thurn.uct.algorithm;

import ca.thurn.uct.core.Action;

public interface InitialStateGenerator<A extends Action> {
  public State<A> initialState();
}
