package ca.thurn.uct.algorithm;

import java.util.HashMap;
import java.util.Map;

import ca.thurn.uct.core.ActionScore;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.AsynchronousAgent;
import ca.thurn.uct.core.Evaluator;
import ca.thurn.uct.core.State;
import ca.thurn.uct.core.WinLossEvaluator;

/**
 * An agent which picks actions by running repeated random simulations from
 * the current state and returning the one that had the best average outcome.
 */
public class MonteCarloSearch implements Agent, AsynchronousAgent {
  
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
  private volatile ActionScore asyncResult;
  private Thread workerThread;
  
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
    return stateRepresentation.copy();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ActionScore pickActionBlocking(int player, State root) {
    Map<Long, Double> actionRewards = new HashMap<Long, Double>(); 
    return runSimulations(player, root, actionRewards, numSimulations);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void beginAsynchronousSearch(final int player, final State root) {
    workerThread = (new Thread() {
      @Override
      public void run() {
        Map<Long, Double> actionRewards = new HashMap<Long, Double>();
        while (!isInterrupted()) {
          asyncResult = runSimulations(player, root, actionRewards, 1000);         
        }
      }
    });
    workerThread.start();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized ActionScore getAsynchronousSearchResult() {
    workerThread.interrupt();
    workerThread = null;
    return asyncResult;
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
   * Run random simulations, updating actionRewards with the results.
   *
   * @param player Player to optimize for.
   * @param root Starting state to simulate from.
   * @param number Number of simulations to run.
   * @return An ActionScore representing the best-scoring action to take from
   *     this state and its score.
   */
  private ActionScore runSimulations(int player, State root, Map<Long, Double> actionRewards,
      int number) {
    for (int i = 0; i < number; ++i) {
      runSimulation(player, root.copy(), actionRewards, 0);
    }
    double bestReward = Double.NEGATIVE_INFINITY;
    long bestAction = -1;
    for (Map.Entry<Long, Double> entry : actionRewards.entrySet()) {
      if (entry.getValue() > bestReward) {
        bestReward = entry.getValue();
        bestAction = entry.getKey();
      }
    }
    return new ActionScore(bestAction, bestReward);
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
  private double runSimulation(int player, State state, Map<Long, Double> actionRewards,
      int depth) {
    if (depth > maxDepth || state.isTerminal()) {
      return evaluator.evaluate(player, state);
    }
    long action = state.getRandomAction();
    state.perform(action);
    double reward = discountRate * runSimulation(player, state, actionRewards, depth + 1);
    if (depth == 0) {
      Double current = actionRewards.get(action);
      actionRewards.put(action, current == null ? reward : current + reward);
    }
    return reward;
  }

}
