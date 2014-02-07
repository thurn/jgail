package ca.thurn.uct.algorithm;

import java.util.Random;

import ca.thurn.uct.algorithm.State.PerformMode;
import ca.thurn.uct.core.Action;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Player;

public class UctSearch<A extends Action> implements Agent<A> {
  
  public static class Builder<A extends Action> {
    private int numSimulations = 100000;
    
    // The bias value here, 1/sqrt(2), was shown by Kocsis and Szepesvari to
    // work well if rewards are in the range [0,1].    
    private double explorationBias = 0.70710678;

    private double discountRate = 0.999;
    
    private int maxDepth = 50;
    
    private boolean multiLevelSearch = false;
    
    private Evaluator<A> evaluator = new Evaluator<A>() {
      @Override
      public double evaluate(Player player, State<A> state) {
        return state.getWinner() == player ? 1.0 : -1.0;
      }
    };
    
    public UctSearch<A> build() {
      return new UctSearch<A>(numSimulations, explorationBias, discountRate, maxDepth,
          multiLevelSearch, evaluator);
    }

    /**
     * Number of simulations to run before picking the best action from the
     * root node.
     */
    public Builder<A> setNumSimulations(int numSimulations) {
      this.numSimulations = numSimulations;
      return this;
    }

    /**
     * The multiplier applied to the uct bias, named C_p in the original UCT
     * paper. A higher number places more emphasis on exploration, a lower
     * number places more emphasis on exploitation.
     */
    public Builder<A> setExplorationBias(double explorationBias) {
      this.explorationBias = explorationBias;
      return this;
    }

    /**
     * The rate at which rewards should be discounted in the future, used to
     * compute the present value of future rewards. This way, rewards further
     * in the future are worth less. This captures our uncertainty about the
     * future, as well as helping avoid infinite reward cycles, etc.
     */
    public Builder<A> setDiscountRate(double discountRate) {
      this.discountRate = discountRate;
      return this;
    }

    /**
     * The maximum depth the search to in the simulation.
     */
    public Builder<A> setMaxDepth(int maxDepth) {
      this.maxDepth = maxDepth;
      return this;
    }

    /**
     * If false, only random moves will be selected at search levels other
     * than the root. 
     */
    public Builder<A> setMultiLevelSearch(boolean multiLevelSearch) {
      this.multiLevelSearch = multiLevelSearch;
      return this;
    }

    /**
     * Function to use to evaluate the heuristic value of a terminal search
     * node.
     */
    public Builder<A> setEvaluator(Evaluator<A> evaluator) {
      this.evaluator = evaluator;
      return this;
    }
  }
  
  public static <A extends Action> Builder<A> builder() {
    return new Builder<A>();
  }

  private final int numSimulations;  
  private final double explorationBias;
  private final double discountRate;
  private final int maxDepth;
  private final boolean multiLevelSearch;
  private final Evaluator<A> evaluator;  
  private final Random random = new Random();
  
  private UctSearch(int numSimulations, double explorationBias, double discountRate, int maxDepth,
      boolean multiLevelSearch, Evaluator<A> evaluator) {
    this.numSimulations = numSimulations;
    this.explorationBias = explorationBias;
    this.discountRate = discountRate;
    this.maxDepth = maxDepth;
    this.multiLevelSearch = multiLevelSearch;
    this.evaluator = evaluator;
  }

  /**
   * Picks an action to take from the provided root node. 
   */
  public A pickAction(Player player, State<A> root) {
    root.prepareForSimulation();
    for (int i = 0; i < numSimulations; ++i) {
      runSimulation(player, root, 0);
      root.reset();
    }
    double bestPayoff = Double.NEGATIVE_INFINITY;
    A bestAction = null;
    for (A action : root.getActions()) {
      double estimatedPayoff = root.averagePayoff(action);
      if (estimatedPayoff > bestPayoff) {
        bestPayoff = estimatedPayoff;
        bestAction = action;
      }
    }
    return bestAction;
  }
  
  /**
   * Runs a simulation to determine the total payoff associated with being at
   * the provided state, tracking the depth in the search tree in "depth".  
   */
  double runSimulation(Player player, State<A> state, int depth) {
    if (depth > maxDepth || state.isTerminal()) {
      return evaluator.evaluate(player, state);
    }
    A action = uctSelectAction(state, depth);
    State<A> nextState = multiLevelSearch ? state.perform(action, PerformMode.TRANSFER_STATE) :
        state.perform(action, PerformMode.IGNORE_STATE);
    double reward = discountRate * runSimulation(player, nextState, depth + 1);
    state.addReward(action, reward);
    return reward;
  }

  /**
   * Selects an action to take from the provided state via the UCT
   * algorithm.
   */
  A uctSelectAction(State<A> state, int d) {
    // We iterate through each action and return the one that maximizes
    // uctValue.
    double maximum = Double.NEGATIVE_INFINITY;
    A result = null;
    for (A action : state.getActions()) {
      double uctValue = state.averagePayoff(action) +
          uctBias(state.numberOfTimesVisited(),
              state.numberOfTimesActionSelected(action));
      // We multiply the result by 1000000 and then add a random double from
      // the interval [0,1] in order to break ties.
      uctValue = (uctValue * 1000000) + random.nextDouble();
      if (uctValue > maximum) {
        maximum = uctValue;
        result = action;
      }
    }
    return result;
  }

  /**
   * The bias to add to the estimated payoff of a given action when deciding
   * which action to take. This is called c_{t,s} in the original UCT paper.
   * This is the "exploration term" of the UCT equation, designed to
   * encourage exploration instead of always just picking the best node.
   */
  double uctBias(int visitsToState, int visitsToAction) {
    // We return 1000000000 if we've never visited this state or action before
    // in order to prioritize nodes we have not yet explored.
    if (visitsToState == 0 || visitsToAction == 0) {
      return 1000000000.0;
    }
    return 2.0 * explorationBias *
        Math.sqrt(Math.log(visitsToState) / visitsToAction);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UctSearch [biasMultiplier=");
    builder.append(explorationBias);
    builder.append(", numSimulations=");
    builder.append(numSimulations);
    builder.append(", discountRate=");
    builder.append(discountRate);
    builder.append(", maxDepth=");
    builder.append(maxDepth);
    builder.append("]");
    return builder.toString();
  }

  @Override
  public A pickAction(Player player, ca.thurn.uct.core.State<A> rootNode) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ca.thurn.uct.core.State<A> getStateRepresentation() {
    // TODO Auto-generated method stub
    return null;
  }
}
