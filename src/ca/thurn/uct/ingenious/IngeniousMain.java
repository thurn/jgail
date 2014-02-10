package ca.thurn.uct.ingenious;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.UctSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Main;


public class IngeniousMain {
  public static void main(String[] args) {
    List<Agent<IngeniousAction>> agents = new ArrayList<Agent<IngeniousAction>>();
//    agents.add(new IngeniousHumanAgent(new IngeniousState()));
    agents.add(UctSearch.builder(new IngeniousState())
        .setNumSimulations(10000)
        .setNumInitialVisits(5)
        .build());
    agents.add(UctSearch.builder(new IngeniousState())
        .setNumSimulations(10000)
        .setNumInitialVisits(5)
        .build());    
//    agents.add(MonteCarloSearch.builder(new IngeniousState()).setNumSimulations(50000).build());
//    agents.add(MonteCarloSearch.builder(new IngeniousState()).setNumSimulations(50000).build());
    Main<IngeniousAction> main = new Main<IngeniousAction>(agents,
        new IngeniousState().setToStartingConditions());
    main.runTournament(10);
  }
}
