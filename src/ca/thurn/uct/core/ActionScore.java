package ca.thurn.uct.core;

/**
 * Class which associates an action with a given heuristic score.
 */
public class ActionScore {
  private final double score;
  private final long action;

  /**
   * Creates a new ActionScore.
   *
   * @param score The score.
   * @param action The action.
   */
  public ActionScore(long action, double score) {
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