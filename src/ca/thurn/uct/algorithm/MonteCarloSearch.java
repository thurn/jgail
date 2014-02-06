package ca.thurn.uct.algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ca.thurn.uct.core.Action;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Evaluator;
import ca.thurn.uct.core.Player;
import ca.thurn.uct.core.State;

/**
 * An agent which picks actions by running repeated random simulations from
 * the current state and returning the one that had the best outcome.
 * 
 * @param <A> Action type to use.
 */
public class MonteCarloSearch<A extends Action> implements Agent<A> {
  
  /**
   * Builder for Monte Carlo Search.
   *
   * @param <A> Action type to use.
   */
  public static class Builder<A extends Action> {
    private final State<A> stateRepresentation;
    
    private int numSimulations = 100000;

    private int maxDepth = 50;
    
    private Builder(State<A> stateRepresentation) {
      this.stateRepresentation = stateRepresentation;
    }
    
    private Evaluator<A> evaluator = new Evaluator<A>() {
      @Override
      public double evaluate(Player player, State<A> state) {
        return state.getWinner() == player ? 1.0 : -1.0;
      }
    };
    
    /**
     * @return A new MonteCarloSearch instance.
     */
    public MonteCarloSearch<A> build() {
      return new MonteCarloSearch<A>(stateRepresentation, numSimulations, maxDepth, evaluator);
    }

    /**
     * Number of simulations to run before picking the best action from the
     * root node. Default value: 100000.
     */
    public Builder<A> setNumSimulations(int numSimulations) {
      this.numSimulations = numSimulations;
      return this;
    }

    /**
     * The maximum depth the search to in the simulation. Default value: 50.
     */
    public Builder<A> setMaxDepth(int maxDepth) {
      this.maxDepth = maxDepth;
      return this;
    }

    /**
     * Function to use to evaluate the heuristic value of a terminal search
     * node. Default value returns -1 for losses, 1 for wins, and 0 for all
     * other states.
     */
    public Builder<A> setEvaluator(Evaluator<A> evaluator) {
      this.evaluator = evaluator;
      return this;
    }
  }
  
  /**
   * @param stateRepresentation State representation to use.
   * @return A new Builder for a MonteCarloSearch agent.
   */
  public static <A extends Action> Builder<A> builder(State<A> stateRepresentation) {
    return new Builder<A>(stateRepresentation);
  }

  private final State<A> stateRepresentation;  
  private final int numSimulations;  
  private final int maxDepth;
  private final Evaluator<A> evaluator;  
  private final Random random = new Random();
  private Map<A, Double> actionRewards = new HashMap<A, Double>(); 
  
  /**
   * Field-initializing constructor.
   * 
   * @param stateRepresentation
   * @param numSimulations
   * @param maxDepth
   * @param evaluator
   */
  private MonteCarloSearch(State<A> stateRepresentation, int numSimulations, int maxDepth,
      Evaluator<A> evaluator) {
    this.stateRepresentation = stateRepresentation;
    this.numSimulations = numSimulations;
    this.maxDepth = maxDepth;
    this.evaluator = evaluator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State<A> getStateRepresentation() {
    return stateRepresentation;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A pickAction(Player player, State<A> root) {
    actionRewards = new HashMap<A, Double>(); 
    for (int i = 0; i < numSimulations; ++i) {
      runSimulation(player, root.copy(), 0);
    }
    double bestReward = Double.NEGATIVE_INFINITY;
    A bestAction = null;
    for (Map.Entry<A, Double> entry : actionRewards.entrySet()) {
      if (entry.getValue() > bestReward) {
        bestReward = entry.getValue();
        bestAction = entry.getKey();
      }
    }
    return bestAction;
  }
  
  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("MonteCarloSearch [numSimulations=");
    builder2.append(numSimulations);
    builder2.append(", maxDepth=");
    builder2.append(maxDepth);
    builder2.append("]");
    return builder2.toString();
  }

  /**
   * Runs a simulation to determine the total reward associated with being at
   * the provided state.
   *
   * @param player The player who we are optimizing for.
   * @param state The current game state.
   * @param depth The depth in the search.
   * @return The reward associated with being at this state.
   */
  private double runSimulation(Player player, State<A> state, int depth) {
    if (depth > maxDepth || state.isTerminal()) {
      return evaluator.evaluate(player, state);
    }
    A action = randomAction(state);
    state.perform(action);
    double reward = runSimulation(player, state, depth + 1);
    if (depth == 0) {
      Double current = actionRewards.get(action);
      actionRewards.put(action, current == null ? 0 : current + reward);
    }
    return reward;
  }

  /**
   * @param state The current game state.
   * @return A random action possible from this state.
   */
  private A randomAction(State<A> state) {
    return state.getActions().get(random.nextInt(state.getActions().size()));
  }
}
