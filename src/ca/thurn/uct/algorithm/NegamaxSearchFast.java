package ca.thurn.uct.algorithm;

import ca.thurn.uct.algorithm.State.ActionResult;
import ca.thurn.uct.connect4.Connect4Player;

public class NegamaxSearchFast<A extends Action> implements ActionPicker<A> {
  
  private static class ActionScore<A> {
    final double score;
    final A action;
    
    ActionScore(double score, A action) {
      this.score = score;
      this.action = action;
    }
  }  
  
  private final int searchDepth;
  
  private final Evaluator<A> evaluator;
  
  public NegamaxSearchFast() {
    this(4);
  }
  
  public NegamaxSearchFast(int searchDepth) {
    this(searchDepth, new UctEvaluator<A>(200));
  }
  
  public NegamaxSearchFast(int searchDepth, Evaluator<A> evaluator) {
    this.searchDepth = searchDepth;
    this.evaluator = evaluator;
  }
  
  
  public ActionScore<A> search(Player player, State<A> state, int maxDepth, double alpha,
      double beta) {
    if (state.isTerminal() || maxDepth == 0) {
      ActionScore<A> as = new ActionScore<A>(evaluator.evaluate(player, state), null);
      return as;
    }
    double bestValue = Double.NEGATIVE_INFINITY;
    A bestAction = null;
    for (A action : state.getActions()) {
      ActionResult<A> next = state.perform(player, action);
      Player newPlayer = player == Connect4Player.RED ? Connect4Player.BLACK : Connect4Player.RED;
      double value = -1 * search(newPlayer, next.getNextState(), maxDepth - 1, -beta, -alpha).score;   
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
  public A pickAction(Player player, State<A> rootNode) {
    ActionScore<A> as = search(player, rootNode, searchDepth, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY);
    return as.action;
  }
}
