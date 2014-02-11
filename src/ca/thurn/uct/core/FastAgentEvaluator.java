package ca.thurn.uct.core;


/**
 * An Evaluator which relies on an underlying Agent to perform evaluation.
 *
 * @param <A>
 */
public class FastAgentEvaluator implements FastEvaluator {
  private final FastAgent agent;  

  public FastAgentEvaluator(FastAgent agent) {
    this.agent = agent;
  }
  
  @Override
  public double evaluate(int player, FastState state) {
    if (state.isTerminal()) {
      return state.getWinner() == player ? 1.0 : -1.0;
    } else {
      agent.pickAction(player, state);
      return agent.getScoreForLastAction();
    }
  }

}
