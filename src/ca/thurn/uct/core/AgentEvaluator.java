package ca.thurn.uct.core;


/**
 * An Evaluator which relies on an underlying Agent to perform evaluation.
 *
 * @param <A>
 */
public class AgentEvaluator implements Evaluator {
  private final Agent agent;  

  public AgentEvaluator(Agent agent) {
    this.agent = agent;
  }
  
  @Override
  public double evaluate(int player, State state) {
    if (state.isTerminal()) {
      return state.getWinner() == player ? 1.0 : -1.0;
    } else {
      agent.pickAction(player, state);
      return agent.getScoreForLastAction();
    }
  }

}
