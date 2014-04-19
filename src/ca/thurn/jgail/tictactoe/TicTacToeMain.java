package ca.thurn.jgail.tictactoe;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.jgail.algorithm.MonteCarloSearch;
import ca.thurn.jgail.algorithm.NegamaxSearch;
import ca.thurn.jgail.core.Agent;
import ca.thurn.jgail.core.Main;
import ca.thurn.jgail.core.WinLossEvaluator;

public class TicTacToeMain {
  public static void main(String[] args) throws InterruptedException {
    List<Agent> agents = new ArrayList<Agent>();
    agents.add(NegamaxSearch.builder(new TicTacToeState())
        .setEvaluator(new WinLossEvaluator())
        .build());
    agents.add(MonteCarloSearch.builder(new TicTacToeState()).setNumSimulations(10).build());
    Main main = new Main(agents, new TicTacToeState().setToStartingConditions());
    main.runTournament(100, 25L);
  }
}