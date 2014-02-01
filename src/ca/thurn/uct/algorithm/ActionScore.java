package ca.thurn.uct.algorithm;

class ActionScore<A> {
  private final double score;
  private final A action;
  
  ActionScore(double score, A action) {
    this.score = score;
    this.action = action;
  }

  public double getScore() {
    return score;
  }

  public A getAction() {
    return action;
  }
}