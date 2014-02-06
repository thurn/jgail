package ca.thurn.uct.algorithm;

import ca.thurn.uct.core.Action;
import ca.thurn.uct.core.Player;
import ca.thurn.uct.core.State;
import ca.thurn.uct.core.Evaluator;

public class UctEvaluator<A extends Action> implements Evaluator<A> {
  private final UctSearch<A> uctSearch;  

  public UctEvaluator(State<A> stateRepresentation, int numSimulations) {
    this.uctSearch = UctSearch.builder(stateRepresentation)
        .setNumSimulations(numSimulations)
        .build();
  }
  
  @Override
  public double evaluate(Player player, State<A> state) {
    if (state.isTerminal()) {
      if (state.getWinner() == null) return 0.0;
      return state.getWinner() == player ? 1.0 : -1.0;
    }
    return uctSearch.getBestAction(player, state).getScore();
  }

}
