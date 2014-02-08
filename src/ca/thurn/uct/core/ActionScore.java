package ca.thurn.uct.core;

/**
 * Class which associates an action with a given heuristic score.
 * 
 * @param <A> Action type to use.
 */
public class ActionScore<A extends Action> {
  private final double score;
  private final A action;
  
  /**
   * An ActionScore with a score of 0.
   * 
   * @param action The action.
   */
  public ActionScore(A action) {
    this(0.0, action);
  }
  
  /**
   * @param score The score.
   * @param action The action.
   */
  public ActionScore(double score, A action) {
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
  public A getAction() {
    return action;
  }
}