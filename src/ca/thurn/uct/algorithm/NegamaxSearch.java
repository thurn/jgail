package ca.thurn.uct.algorithm;

import ca.thurn.uct.core.ActionScore;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.AgentEvaluator;
import ca.thurn.uct.core.Evaluator;
import ca.thurn.uct.core.State;

/**
 * An agent which selects an action via the Negamax search algorithm.
 */
public class NegamaxSearch implements Agent {
  
  /**
   * Builder for NegamaxSearch.
   */
  public static class Builder {
    private final State stateRepresentation;
    private int searchDepth = 5;
    private Evaluator evaluator;
    
    /**
     * Constructor.
     * 
     * @param stateRepresentation State representation to employ.
     */
    public Builder(State stateRepresentation) {
      this.stateRepresentation = stateRepresentation;
      this.evaluator = new AgentEvaluator(
          MonteCarloSearch.builder(stateRepresentation).setNumSimulations(200).build(),
          0);
    }

    /**
     * @return A new NegamaxSearch agent based on this builder.
     */
    public NegamaxSearch build() {
      return new NegamaxSearch(stateRepresentation, searchDepth, evaluator);
    }
    
    /**
     * @param searchDepth Depth to search to before evaluating nodes in the
     *     search tree. Default value: 5.
     * @return this.
     */
    public Builder setSearchDepth(int searchDepth) {
      this.searchDepth = searchDepth;
      return this;
    }
    
    /**
     * @param evaluator Function to use to evaluate the quality of nodes in
     *     the search tree once the depth limit is hit. Default value is an
     *     {@link AgentEvaluator} based on a {@link MonteCarloSearch} agent.
     * @return
     */
    public Builder setEvaluator(Evaluator evaluator) {
      this.evaluator = evaluator;
      return this;
    }    
  }
  
  /**
   * @param stateRepresentation State representation to employ.
   * @return A new builder for NegamaxSearch agents.
   */
  public static Builder builder(State stateRepresentation) {
    return new Builder(stateRepresentation);
  }
  
  private final State stateRepresentation;
  private final int searchDepth;
  private final Evaluator evaluator;
  
  private NegamaxSearch(State stateRepresentation, int searchDepth, Evaluator evaluator) {
    this.stateRepresentation = stateRepresentation;
    this.searchDepth = searchDepth;
    this.evaluator = evaluator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ActionScore pickAction(int player, State rootNode, long timeBudget) {
    return search(player, rootNode, searchDepth, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State getStateRepresentation() {
    return stateRepresentation;
  }
  
  /**
   * Search for the best action to take for the provided player.
   * 
   * @param player The player to find an action for.
   * @param state The root state for the search.
   * @param maxDepth The maximum depth to search to in the game tree.
   * @param alpha The minimum known score that the maximizing player can get.
   * @param beta The maximum known score that the minimizing player can get.
   * @return An ActionScore pair consisting of the best action for the player
   *     to take and the heuristic score associated with this action.
   */
  private ActionScore search(int player, State state, int maxDepth, double alpha,
      double beta) {
    if (state.isTerminal() || maxDepth == 0) {
      return new ActionScore(-1, evaluator.evaluate(player, state.copy()));
    }
    double bestValue = Double.NEGATIVE_INFINITY;
    long bestAction = -1;
    State.ActionIterator actionIterator = state.getActionIterator();
    while (actionIterator.hasNextAction()) {
      long action = actionIterator.nextAction();
      long undoToken = state.perform(action);
      double value = -1 *
          search(state.getCurrentPlayer(), state, maxDepth - 1, -beta, -alpha).getScore();
      state.undo(action, undoToken);
      if (value > bestValue) {
        bestValue = value;
        bestAction = action;
      }
      if (value > alpha) {
        alpha = value;
      }
      if (alpha >= beta) {        
        break;
      }
    }
    return new ActionScore(bestAction, bestValue);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("NegamaxSearch [searchDepth=");
    builder.append(searchDepth);
    builder.append(", evaluator=");
    builder.append(evaluator);
    builder.append("]");
    return builder.toString();
  }

}
