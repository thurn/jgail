package ca.thurn.uct.tictactoe;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.MonteCarloSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.AsyncMain;

public class TicTacToeMain {
  public static void main(String[] args) throws InterruptedException {
    List<Agent> agents = new ArrayList<Agent>();
    agents.add(MonteCarloSearch.builder(new TicTacToeState())
        .setDiscountRate(0.5).build());
    agents.add(MonteCarloSearch.builder(new TicTacToeState())
        .setDiscountRate(0.5).build());    
    AsyncMain main = new AsyncMain(agents, new TicTacToeState().setToStartingConditions());
    main.runTournament(100, 30);
  }
}
