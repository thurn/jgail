package ca.thurn.uct.core;


/**
 * An Evaluator which relies on an underlying Agent to perform evaluation.
 */
public class AgentEvaluator implements Evaluator {
  private final Agent agent;  
  private final long timeBudget;

  /**
   * Constructs a new AgentEvaluator.
   * 
   * @param agent Underlying agent to perform evaluations.
   * @param timeBudget Amount of time that should be allowed to perform the
   *     evaluation.
   */
  public AgentEvaluator(Agent agent, long timeBudget) {
    this.agent = agent;
    this.timeBudget = timeBudget;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public double evaluate(int player, State state) {
    if (state.isTerminal()) {
      return state.getWinner() == player ? 1.0 : -1.0;
    } else {
      return agent.pickAction(player, state, timeBudget).getScore();
    }
  }
  
  public String toString() {
    return "AgentEvaluator [agent=" + agent + "]";
  }

}
