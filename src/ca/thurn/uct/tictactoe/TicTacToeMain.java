package ca.thurn.uct.tictactoe;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.MonteCarloSearch;
import ca.thurn.uct.algorithm.NegamaxSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Main;
import ca.thurn.uct.core.WinLossEvaluator;

public class TicTacToeMain {
  public static void main(String[] args) {
    List<Agent> agents = new ArrayList<Agent>();
    agents.add(NegamaxSearch.builder(new TicTacToeState())
        .setSearchDepth(10)
        .setEvaluator(new WinLossEvaluator())
        .build());
    agents.add(MonteCarloSearch.builder(new TicTacToeState())
        .setDiscountRate(0.5)
        .build());
    Main main = new Main(agents, new TicTacToeState().setToStartingConditions());
    main.runTournament(100);
  }
}
