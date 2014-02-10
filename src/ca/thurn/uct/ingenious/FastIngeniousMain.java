package ca.thurn.uct.ingenious;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.FastUctSearch;
import ca.thurn.uct.core.FastAgent;
import ca.thurn.uct.core.FastMain;


public class FastIngeniousMain {
  public static void main(String[] args) {
    List<FastAgent> agents = new ArrayList<FastAgent>();
//    agents.add(new FastIngeniousHumanAgent(new FastIngeniousState()));
    agents.add(FastUctSearch.builder(new FastIngeniousState())
        .setNumSimulations(10000)
        .setNumInitialVisits(5)
        .build());
    agents.add(FastUctSearch.builder(new FastIngeniousState())
        .setNumSimulations(10000)
        .setNumInitialVisits(5)
        .build());       
//    agents.add(FastMonteCarloSearch.builder(new FastIngeniousState()).setNumSimulations(50000).build());
//    agents.add(FastMonteCarloSearch.builder(new FastIngeniousState()).setNumSimulations(50000).build());
    FastMain main = new FastMain(agents, new FastIngeniousState().setToStartingConditions());
    main.runTournament(10);
  }
}
