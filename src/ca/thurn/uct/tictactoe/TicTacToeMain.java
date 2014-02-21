package ca.thurn.uct.tictactoe;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.NegamaxSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Main;
import ca.thurn.uct.core.WinLossEvaluator;

public class TicTacToeMain {
  public static void main(String[] args) throws InterruptedException {
    List<Agent> agents = new ArrayList<Agent>();
    agents.add(NegamaxSearch.builder(new TicTacToeState())
        .setEvaluator(new WinLossEvaluator())
        .build());
    agents.add(new TicTacToeHumanAgent(new TicTacToeState()));
    Main main = new Main(agents, new TicTacToeState().setToStartingConditions());
    main.runMatch(5000L);
  }
}
