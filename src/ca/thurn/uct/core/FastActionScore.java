package ca.thurn.uct.core;

/**
 * Class which associates an action with a given heuristic score.
 * 
 * @param <A> Action type to use.
 */
public class FastActionScore {
  private final double score;
  private final long action;
  
  /**
   * An ActionScore with a score of 0.
   * 
   * @param action The action.
   */
  public FastActionScore(long action) {
    this(action, 0.0);
  }
  
  /**
   * @param score The score.
   * @param action The action.
   */
  public FastActionScore(long action, double score) {
    this.score = score;
    this.action = action;
  }

  /**
   * @return The score.
   */
  public double getScore() {
    return score;
  }

  /**
   * @return The action.
   */
  public long getAction() {
    return action;
  }
}