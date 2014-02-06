package ca.thurn.uct.algorithm;

import ca.thurn.uct.core.Action;
import ca.thurn.uct.core.Player;

public class UctEvaluator<A extends Action> implements Evaluator<A> {
  private final UctSearch<A> uctSearch;  

  public UctEvaluator(int numSimulations) {
    this.uctSearch = UctSearch
        .<A>builder()
        .setNumSimulations(numSimulations)
        .build();
  }
  
  @Override
  public double evaluate(Player player, State<A> state) {
    if (state.isTerminal()) {
      if (state.getWinner() == null) return 0.0;
      return state.getWinner() == player ? 1.0 : -1.0;
    }
    A bestAction = uctSearch.pickAction(player, state);
    return state.averagePayoff(bestAction);
  }

}
