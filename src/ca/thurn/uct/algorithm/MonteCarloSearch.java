package ca.thurn.uct.algorithm;

import java.util.HashMap;
import java.util.Map;

import ca.thurn.uct.core.ActionScore;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Evaluator;
import ca.thurn.uct.core.State;
import ca.thurn.uct.core.WinLossEvaluator;

/**
 * An agent which picks actions by running repeated random simulations from
 * the current state and returning the one that had the best average outcome.
 */
public class MonteCarloSearch implements Agent {
  
  /**
   * Builder for MonteCarloSearch.
   */
  public static class Builder {
    private final State stateRepresentation;
    
    private int numSimulations = 100000;

    private int maxDepth = 500;
    
    private double discountRate = 1.0; 
    
    private Builder(State stateRepresentation) {
      this.stateRepresentation = stateRepresentation;
    }
    
    private Evaluator evaluator = new WinLossEvaluator();
    
    /**
     * @return A new MonteCarloSearch instance.
     */
    public MonteCarloSearch build() {
      return new MonteCarloSearch(stateRepresentation, numSimulations, discountRate, maxDepth,
          evaluator);
    }

    /**
     * @param numSimulations Number of simulations to run before picking the
     *     best action from the root node. Default value: 100000.
     * @return this.
     */
    public Builder setNumSimulations(int numSimulations) {
      this.numSimulations = numSimulations;
      return this;
    }
    
    /**
     * @param discountRate The rate at which rewards should be discounted in
     *     the future, used to compute the present value of future rewards.
     *     This way, rewards further in the future are worth less. This
     *     captures our uncertainty about the future, as well as helping avoid
     *     infinite reward cycles, etc. Default value: 1.0 (no discounting).
     * @return this.
     */
    public Builder setDiscountRate(double discountRate) {
      this.discountRate = discountRate;
      return this;
    }

    /**
     * @param maxDepth The maximum depth the search to in the simulation.
     *     Default value: 500.
     * @return this.
     */
    public Builder setMaxDepth(int maxDepth) {
      this.maxDepth = maxDepth;
      return this;
    }

    /**
     * @param evaluator Function to use to evaluate the heuristic value of a
     *     terminal search node. Default value: {@link WinLossEvaluator}.
     * @return this.
     */
    public Builder setEvaluator(Evaluator evaluator) {
      this.evaluator = evaluator;
      return this;
    }
  }
  
  /**
   * @param stateRepresentation State representation to use.
   * @return A new Builder for a MonteCarloSearch agent.
   */
  public static Builder builder(State stateRepresentation) {
    return new Builder(stateRepresentation);
  }

  private final State stateRepresentation;  
  private final int numSimulations;
  private final double discountRate;
  private final int maxDepth;
  private final Evaluator evaluator;  

  private Map<Long, Double> actionRewards = new HashMap<Long, Double>(); 
  
  /**
   * Field-initializing constructor.
   * 
   * @param stateRepresentation
   * @param numSimulations
   * @param maxDepth
   * @param evaluator
   */
  private MonteCarloSearch(State stateRepresentation, int numSimulations, double discountRate,
      int maxDepth, Evaluator evaluator) {
    this.stateRepresentation = stateRepresentation;
    this.numSimulations = numSimulations;
    this.discountRate = discountRate;
    this.maxDepth = maxDepth;
    this.evaluator = evaluator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State getStateRepresentation() {
    return stateRepresentation;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ActionScore pickAction(int player, State root, long timeBudget) {
    actionRewards = new HashMap<Long, Double>(); 
    for (int i = 0; i < numSimulations; ++i) {
      runSimulation(player, root.copy(), 0);
    }
    double bestReward = Double.NEGATIVE_INFINITY;
    long bestAction = -1;
    for (Map.Entry<Long, Double> entry : actionRewards.entrySet()) {
//      System.out.println(stateRepresentation.actionToString(entry.getKey()) + " " + entry.getValue());
      if (entry.getValue() > bestReward) {
        bestReward = entry.getValue();
        bestAction = entry.getKey();
      }
    }
    return new ActionScore(bestAction, bestReward);
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("MonteCarloSearch [numSimulations=");
    builder.append(numSimulations);
    builder.append(", discountRate=");
    builder.append(discountRate);
    builder.append(", maxDepth=");
    builder.append(maxDepth);
    builder.append("]");
    return builder.toString();
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
  private double runSimulation(int player, State state, int depth) {
    if (depth > maxDepth || state.isTerminal()) {
      return evaluator.evaluate(player, state);
    }
    long action = state.getRandomAction();
    state.perform(action);
    double reward = discountRate * runSimulation(player, state, depth + 1);
    if (depth == 0) {
      Double current = actionRewards.get(action);
      actionRewards.put(action, current == null ? reward : current + reward);
    }
    return reward;
  }

}
