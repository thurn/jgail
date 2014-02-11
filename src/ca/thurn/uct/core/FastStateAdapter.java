package ca.thurn.uct.core;

import java.util.ArrayList;
import java.util.List;

public class FastStateAdapter<A extends Action> implements State<A> {

  private final FastState state;
  private final FastConverter<A> converter;
  
  public FastStateAdapter(FastState state, FastConverter<A> converter) {
    this.state = state;
    this.converter = converter;
  }
  
  public FastState getUnderlyingState() {
    return state;
  }
  
  @Override
  public List<A> getActions() {
    List<A> result = new ArrayList<A>();
    for (int i = 0; i < state.getActions().size(); ++i) {
      result.add(converter.fromFastAction(state.getActions().get(i)));
    }
    return result;
  }

  @Override
  public void perform(A action) {
    state.perform(converter.toFastAction(action));
  }

  @Override
  public void undo(A action) {
    state.undo(converter.toFastAction(action));
  }

  @Override
  public State<A> setToStartingConditions() {
    state.setToStartingConditions();
    return this;
  }

  @Override
  public State<A> copy() {
    return new FastStateAdapter<A>(state.copy(), converter);
  }

  @Override
  public State<A> initialize(State<A> state) {
    this.state.initialize(converter.toFastState(state));
    return this;
  }

  @Override
  public boolean isTerminal() {
    return state.isTerminal();
  }

  @Override
  public Player getWinner() {
    return converter.fromFastPlayer(state.getWinner());
  }

  @Override
  public Player getCurrentPlayer() {
    return converter.fromFastPlayer(state.getCurrentPlayer());
  }

  @Override
  public Player playerAfter(Player player) {
    return converter.fromFastPlayer(state.playerAfter(converter.toFastPlayer(player)));
  }

  @Override
  public Player playerBefore(Player player) {
    return converter.fromFastPlayer(state.playerBefore(converter.toFastPlayer(player)));
  }

}
