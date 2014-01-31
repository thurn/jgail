package ca.thurn.uct.algorithm;

import ca.thurn.uct.algorithm.State.ActionResult;
import ca.thurn.uct.connect4.Connect4Player;

public class NegamaxSearch<A extends Action> implements ActionPicker<A> {
  
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
  
  public NegamaxSearch() {
    this(4);
  }
  
  public NegamaxSearch(int searchDepth) {
    this(searchDepth, new UctEvaluator<A>(200));
  }
  
  public NegamaxSearch(int searchDepth, Evaluator<A> evaluator) {
    this.searchDepth = searchDepth;
    this.evaluator = evaluator;
  }
  
  
  public ActionScore<A> search(Player player, State<A> state, int maxDepth) {
    if (state.isTerminal() || maxDepth == 0) {
      ActionScore<A> as = new ActionScore<A>(evaluator.evaluate(player, state), null);
      return as;
    }
    double bestValue = Double.NEGATIVE_INFINITY;
    A bestAction = null;
    for (A action : state.getActions()) {
      ActionResult<A> next = state.perform(player, action);
      Player newPlayer = player == Connect4Player.RED ? Connect4Player.BLACK : Connect4Player.RED;
      double value = -1 * search(newPlayer, next.getNextState(), maxDepth - 1).score;
//      String s = "" + maxDepth;
//      for (int i = 0; i < maxDepth; ++i) {
//        s += ">>>>";
//      }
//      System.out.println(player + " " + maxDepth + ") " + action + s + value);      
      if (value > bestValue) {
        bestValue = value;
        bestAction = action;
      }
    }
    return new ActionScore<A>(bestValue, bestAction);
  }

  @Override
  public A pickAction(Player player, State<A> rootNode) {
    ActionScore<A> as = search(player, rootNode, searchDepth);
    return as.action;
  }
}
