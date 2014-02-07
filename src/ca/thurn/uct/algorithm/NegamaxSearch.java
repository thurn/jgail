package ca.thurn.uct.algorithm;

import ca.thurn.uct.core.Action;
import ca.thurn.uct.core.ActionScore;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.AgentEvaluator;
import ca.thurn.uct.core.Evaluator;
import ca.thurn.uct.core.Player;
import ca.thurn.uct.core.State;

/**
 * An agent which selects an action via the Negamax search algorithm.
 * 
 * @param <A> The action type to use.
 */
public class NegamaxSearch<A extends Action> implements Agent<A> {
  
  /**
   * Builder for NegamaxSearch.
   *
   * @param <A> The action type to use.
   */
  public static class Builder<A extends Action> {
    private final State<A> stateRepresentation;
    private int searchDepth = 4;
    private Evaluator<A> evaluator;
    
    /**
     * Constructor.
     * 
     * @param stateRepresentation State representation to employ.
     */
    public Builder(State<A> stateRepresentation) {
      this.stateRepresentation = stateRepresentation;
      this.evaluator = new AgentEvaluator<A>(
          MonteCarloSearch.builder(stateRepresentation).setNumSimulations(500).build());
    }

    /**
     * @return A new NegamaxSearch agent based on this builder.
     */
    public NegamaxSearch<A> build() {
      return new NegamaxSearch<A>(stateRepresentation, searchDepth, evaluator);
    }
    
    /**
     * @param searchDepth Depth to search to before evaluating nodes in the
     *     search tree. Default value: 4.
     * @return this.
     */
    public Builder<A> setSearchDepth(int searchDepth) {
      this.searchDepth = searchDepth;
      return this;
    }
    
    /**
     * @param evaluator Function to use to evaluate the quality of nodes in
     *     the search tree once the depth limit is hit. Default value is an
     *     {@link AgentEvaluator} based on a {@link MonteCarloSearch} agent.
     * @return
     */
    public Builder<A> setEvaluator(Evaluator<A> evaluator) {
      this.evaluator = evaluator;
      return this;
    }    
  }
  
  /**
   * @param stateRepresentation State representation to employ.
   * @return A new builder for NegamaxSearch agents.
   */
  public static <A extends Action> Builder<A> builder(State<A> stateRepresentation) {
    return new Builder<A>(stateRepresentation);
  }
  
  private final State<A> stateRepresentation;
  private final int searchDepth;
  private final Evaluator<A> evaluator;
  
  private NegamaxSearch(State<A> stateRepresentation, int searchDepth, Evaluator<A> evaluator) {
    this.stateRepresentation = stateRepresentation;
    this.searchDepth = searchDepth;
    this.evaluator = evaluator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ActionScore<A> pickAction(Player player, State<A> rootNode) {
    ActionScore<A> result = search(player, rootNode, searchDepth, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY);
    if (result.getAction() == null) {
      throw new RuntimeException();
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State<A> getStateRepresentation() {
    return stateRepresentation;
  }
  
  private ActionScore<A> search(Player player, State<A> state, int maxDepth, double alpha,
      double beta) {
    if (state.isTerminal() || maxDepth == 0) {
      return new ActionScore<A>(evaluator.evaluate(player, state.copy()), null);
    }
    double bestValue = Double.NEGATIVE_INFINITY;
    A bestAction = null;
    for (A action : state.getActions()) {     
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
    return new ActionScore<A>(bestValue, bestAction);
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
