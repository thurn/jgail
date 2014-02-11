package ca.thurn.uct.algorithm;

import gnu.trove.list.TLongList;
import ca.thurn.uct.core.AgentEvaluator;
import ca.thurn.uct.core.FastActionScore;
import ca.thurn.uct.core.FastAgent;
import ca.thurn.uct.core.FastAgentEvaluator;
import ca.thurn.uct.core.FastEvaluator;
import ca.thurn.uct.core.FastState;

/**
 * An agent which selects an action via the Negamax search algorithm.
 * 
 * @param <A> The action type to use.
 */
public class FastNegamaxSearch implements FastAgent {
  
  /**
   * Builder for NegamaxSearch.
   *
   * @param <A> The action type to use.
   */
  public static class Builder {
    private final FastState stateRepresentation;
    private int searchDepth = 4;
    private FastEvaluator evaluator;
    
    /**
     * Constructor.
     * 
     * @param stateRepresentation State representation to employ.
     */
    public Builder(FastState stateRepresentation) {
      this.stateRepresentation = stateRepresentation;
      this.evaluator = new FastAgentEvaluator(
          FastMonteCarloSearch.builder(stateRepresentation).setNumSimulations(1000).build());
    }

    /**
     * @return A new NegamaxSearch agent based on this builder.
     */
    public FastNegamaxSearch build() {
      return new FastNegamaxSearch(stateRepresentation, searchDepth, evaluator);
    }
    
    /**
     * @param searchDepth Depth to search to before evaluating nodes in the
     *     search tree. Default value: 4.
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
    public Builder setEvaluator(FastEvaluator evaluator) {
      this.evaluator = evaluator;
      return this;
    }    
  }
  
  /**
   * @param stateRepresentation State representation to employ.
   * @return A new builder for NegamaxSearch agents.
   */
  public static Builder builder(FastState stateRepresentation) {
    return new Builder(stateRepresentation);
  }
  
  private final FastState stateRepresentation;
  private final int searchDepth;
  private final FastEvaluator evaluator;
  private double lastActionScore;
  
  private FastNegamaxSearch(FastState stateRepresentation, int searchDepth, FastEvaluator evaluator) {
    this.stateRepresentation = stateRepresentation;
    this.searchDepth = searchDepth;
    this.evaluator = evaluator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long pickAction(int player, FastState rootNode) {
    FastActionScore result = search(player, rootNode, searchDepth, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY);
    lastActionScore = result.getScore();
    return result.getAction();
  }
  
  @Override
  public double getScoreForLastAction() {
   return lastActionScore; 
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FastState getStateRepresentation() {
    return stateRepresentation;
  }
  
  private FastActionScore search(int player, FastState state, int maxDepth, double alpha,
      double beta) {
    if (state.isTerminal() || maxDepth == 0) {
      return new FastActionScore(-1, evaluator.evaluate(player, state.copy()));
    }
    double bestValue = Double.NEGATIVE_INFINITY;
    long bestAction = -1;
    TLongList actions = state.getActions();
    for (int i = 0; i < actions.size(); ++i) {
      long action = actions.get(i);
      state.perform(action);
      double value = -1 *
          search(state.getCurrentPlayer(), state, maxDepth - 1, -beta, -alpha).getScore();
      state.undo(action);
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
    return new FastActionScore(bestAction, bestValue);
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("NegamaxSearch [searchDepth=");
    builder2.append(searchDepth);
    builder2.append("]");
    return builder2.toString();
  }

}
