package ca.thurn.uct.algorithm;


public class NegamaxSearch<A extends Action> implements ActionPicker<A> {
  
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

  public ActionScore<A> search(Player player, State<A> state, int maxDepth, double alpha,
      double beta) {
    if (state.isTerminal() || maxDepth == 0) {
      ActionScore<A> as = new ActionScore<A>(evaluator.evaluate(player, state.copy()), null);
//      System.out.println(state.getWinner() + ">" + player + " " + as.getScore());
      return as;
    }
    double bestValue = Double.NEGATIVE_INFINITY;
    A bestAction = null;
    for (A action : state.getActions()) {
//      if (maxDepth == 4 && action.getActionNumber() == 2) {
//        System.out.println();
//      }      
      state = state.perform(action);
      Player newPlayer = state.playerAfter(player);
      double value = -1 * search(newPlayer, state, maxDepth - 1, -beta, -alpha).getScore();
//      String s = "";
//      for (int i = 0; i < maxDepth; ++i) {
//        s += ">>>";
//      }
//      System.out.println(s + action + " " + player + " " + value);
      state = state.unperform(action);
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
//    System.out.println("pick action for " + player);
    ActionScore<A> as = search(player, rootNode, searchDepth, Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY);
//    System.out.println("picking action " + as.getAction() + " with score " + as.getScore());
    return as.getAction();
  }
}
