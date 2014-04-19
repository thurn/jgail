package ca.thurn.jgail.core;


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
   *     evaluation. 0 indicates no time budget.
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
      State represented = agent.getStateRepresentation().initializeFrom(state);
      if (timeBudget != 0 && agent instanceof AsynchronousAgent) {
        ((AsynchronousAgent)agent).beginAsynchronousSearch(player, represented);
        try {
          Thread.sleep(timeBudget);
        } catch (InterruptedException e) {
          // Ran out of time and have no useful information.
          return 0.0;
        }
        return ((AsynchronousAgent)agent).getAsynchronousSearchResult().getScore();
      } else {
        return agent.pickActionBlocking(player, represented).getScore();
      }
    }
  }
  
  public String toString() {
    return "AgentEvaluator [agent=" + agent + "]";
  }

}
