package ca.thurn.uct.algorithm;

import gnu.trove.list.TLongList;

import java.util.Random;

import ca.thurn.uct.core.FastActionTree;
import ca.thurn.uct.core.FastAgent;
import ca.thurn.uct.core.FastEvaluator;
import ca.thurn.uct.core.FastState;

/**
 * An agent which selects actions based on the UCT algorithm described in the
 * 2006 paper "Bandit based Monte-Carlo Planning" by Kocsis and Szepesvari.
 */
public class FastUctSearch implements FastAgent {
  
  /**
    * This exploration bias value, 1/sqrt(2), was shown by Kocsis and
    * Szepesvari to work well if rewards are in the range [0,1]. 
   */
  public static double UNIT_EXPLORATION_BIAS = 0.70710678f;
  
  /**
   * Builder for UctSearch agents.
   *
   * @param <A> Action type to use.
   */
  public static class Builder {
    private final FastState stateRepresentation;
    
    private int numSimulations = 100000;
  
    private double explorationBias = UNIT_EXPLORATION_BIAS;

    private double discountRate = 1.0f;
    
    private int maxDepth = 50;
    
    private int numInitialVisits = 0;
    
    private FastEvaluator evaluator = new FastEvaluator() {
      @Override
      public double evaluate(int player, FastState state) {
        return state.getWinner() == player ? 1.0f : -1.0f;
      }
    };
    
    /**
     * Constructor for UctSearch Builders.
     * 
     * @param stateRepresentation State representation to employ for this agent.
     */
    public Builder(FastState stateRepresentation) {
      this.stateRepresentation = stateRepresentation;
    }
    
    /**
     * @return A new UctSearch agent based on this builder.
     */
    public FastUctSearch build() {
      return new FastUctSearch(stateRepresentation, numSimulations, explorationBias,
          discountRate, maxDepth, numInitialVisits, evaluator);
    }

    /**
     * @param numSimluations Number of simulations to run before picking the
     *     best action from the root node. Default value: 100000.
     * @return this.
     */
    public Builder setNumSimulations(int numSimulations) {
      this.numSimulations = numSimulations;
      return this;
    }

    /**
     * @param explorationBias The multiplier named C_p in the original UCT
     *     paper. A higher number places more emphasis on exploration, a lower
     *     number places more emphasis on exploitation. Default value:
     *     {@link FastUctSearch#UNIT_EXPLORATION_BIAS}.
     * @return this.
     */
    public Builder setExplorationBias(double explorationBias) {
      this.explorationBias = explorationBias;
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
     *     Default value: 50.
     * @return this.
     */
    public Builder setMaxDepth(int maxDepth) {
      this.maxDepth = maxDepth;
      return this;
    }
    
    /**
     * @param numInitialVisits For the first numInitialVisits to a given game
     *     tree position, play random games instead of expanding the tree. This
     *     saves memory. Default value: 0.
     * @return this.
     */
    public Builder setNumInitialVisits(int numInitialVisits) {
      this.numInitialVisits = numInitialVisits;
      return this;
    }

    /**
     * @param evaluator Function to use to evaluate the heuristic value of a
     *     terminal search node. Default value returns -1 for losses, 1 for
     *     wins, and 0 for all other states.
     * @return this.
     */
    public Builder setEvaluator(FastEvaluator evaluator) {
      this.evaluator = evaluator;
      return this;
    }
  }
  
  /**
   * @param stateRepresentation State representation to employ.
   * @return A new builder for UctSearch agents.
   */
  public static Builder builder(FastState stateRepresentation) {
    return new Builder(stateRepresentation);
  }

  private final FastState stateRepresentation;
  private final int numSimulations;
  private final double explorationBias;
  private final double discountRate;
  private final int maxDepth;
  private final int numInitialVisits;
  private final FastEvaluator evaluator;  
  private double lastActionReward;
  private final Random random = new Random();
  
  private FastUctSearch(FastState stateRepresentation, int numSimulations, double explorationBias,
      double discountRate, int maxDepth, int numInitialVisits, FastEvaluator evaluator) {
    this.stateRepresentation = stateRepresentation;
    this.numSimulations = numSimulations;
    this.explorationBias = explorationBias;
    this.discountRate = discountRate;
    this.maxDepth = maxDepth;
    this.evaluator = evaluator;
    this.numInitialVisits = numInitialVisits;
  }

  /**
   * {@inheritDoc} 
   */
  @Override
  public long pickAction(int player, FastState root) {
    FastActionTree actionTree = new FastActionTree();
    for (int i = 0; i < numSimulations; ++i) {
      runSimulation(actionTree, player, root.copy(), 0);
    }
    double bestPayoff = Double.NEGATIVE_INFINITY;
    long bestAction = -1;
    TLongList actions = root.getActions();
    for (int i = 0; i < actions.size(); ++i) {
      FastActionTree child = actionTree.child(actions.get(i));
      double estimatedPayoff = averageReward(child);
      if (estimatedPayoff > bestPayoff) {
        bestPayoff = estimatedPayoff;
        bestAction = actions.get(i);
      }
    }
    lastActionReward = bestPayoff;
    return bestAction;
  }
  
  @Override
  public double getScoreForLastAction() {
    return lastActionReward;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FastState getStateRepresentation() {
    return stateRepresentation;
  }
  
  /**
   * Runs a simulation to determine the total payoff associated with being at
   * the provided state.
   *
   * @param actionTree An ActionTree tracking the rewards at each game tree
   *     position.
   * @param player The player we are trying to optimize for.
   * @param state The current state.
   * @param depth The current depth in the search tree.
   * @return The heuristic value of being in this state. 
   */
  private double runSimulation(FastActionTree actionTree, int player, FastState state,
      int depth) {
    if (depth > maxDepth || state.isTerminal()) {
      double reward = -evaluator.evaluate(player, state);
      updateTree(actionTree, reward);
      return reward;
    }
    if (actionTree.getNumVisits() < numInitialVisits) {
      double reward = -playRandomGame(player, state, depth + 1);
      updateTree(actionTree, reward);
      return reward;
    } else {
      long action = uctSelectAction(actionTree, state);
      state.perform(action);
      final double reward = discountRate *
          -runSimulation(actionTree.child(action), state.getCurrentPlayer(), state, depth + 1);
      updateTree(actionTree, reward);
      return reward;      
    }
  }
  
  private double playRandomGame(int player, FastState state, int depth) {
    if (depth > maxDepth || state.isTerminal()) {
      return evaluator.evaluate(player, state);
    }    
    long action = randomAction(state);
    state.perform(action);
    return playRandomGame(player, state, depth + 1);   
  }

  /**
   * @param state The current game state.
   * @return A random action possible from this state.
   */
  private long randomAction(FastState state) {
    return state.getActions().get(random.nextInt(state.getActions().size()));
  }
  
  /**
   * Updates the tree at the given position, adding the given reward and
   * marking this node as visited
   *
   * @param actionTree An ActionTree tracking the rewards at each game tree
   *     position.
   * @param reward The reward associated with this position.
   */
  private void updateTree(FastActionTree actionTree, final double reward) {;
    actionTree.incrementNumVisits();
    actionTree.addReward(reward);
  }

  /**
   * Selects an action to take from the provided state via the UCT algorithm.
   *
   * @param actionTree An ActionTree tracking the rewards at each game tree
   *     position.
   * @param state The current state.
   * @return The action to take.
   */
  private long uctSelectAction(FastActionTree actionTree, FastState state) {
    // We iterate through each action and return the one that maximizes
    // uctValue.
    double maximum = Double.NEGATIVE_INFINITY;
    long result = -1;
    TLongList actions = state.getActions();
    for (int i = 0 ; i < actions.size(); ++i) {
      FastActionTree child = actionTree.child(actions.get(i));
      double uctValue = averageReward(child) +
          explorationBias(actionTree.getNumVisits(), child.getNumVisits());
      // We multiply the result by 1000000 and then add a random double from
      // the interval [0,1] in order to break ties.
      uctValue = (uctValue * 1000000) + random.nextDouble();
      if (uctValue > maximum) {
        maximum = uctValue;
        result = actions.get(i);
      }
    }
    return result;
  }
  
  /**
   * @param actionTree An ActionTree tracking the rewards at each game tree
   *     position.
   * @return The average reward of visiting this state, or 0 if this state has
   *     never been visited before;
   */
  private double averageReward(FastActionTree actionTree) {
    int numVisits = actionTree.getNumVisits();
    if (numVisits == 0) {
      return 0;
    } else {
      return actionTree.getTotalReward() / numVisits;
    }
  }

  /**
   * The bias to add to the estimated payoff of a given action when deciding
   * which action to take. This is called c_{t,s} in the original UCT paper.
   * This is the "exploration term" of the UCT equation, designed to
   * encourage exploration instead of always just picking the best node.
   *
   * @param visitsToState How many times the current state has been visited.
   * @param visitsToAction How many times the state resulting from the proposed
   *     action has been visited.
   * @return The exploration bias.
   */
  private double explorationBias(int visitsToState, int visitsToAction) {
    // We return 1000000000 if we've never visited this state or action before
    // in order to prioritize nodes we have not yet explored.
    if (visitsToState == 0 || visitsToAction == 0) {
      return 1000000000.0f;
    }
    return (2.0f * explorationBias *
        Math.sqrt(Math.log(visitsToState) / visitsToAction));
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UctSearch [numSimulations=");
    builder.append(numSimulations);
    builder.append(", explorationBias=");
    builder.append(explorationBias);
    builder.append(", discountRate=");
    builder.append(discountRate);
    builder.append(", maxDepth=");
    builder.append(maxDepth);
    builder.append("]");
    return builder.toString();
  }
}
