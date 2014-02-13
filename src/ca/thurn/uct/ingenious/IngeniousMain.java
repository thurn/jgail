package ca.thurn.uct.ingenious;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.NegamaxSearch;
import ca.thurn.uct.algorithm.UctSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Main;

public class IngeniousMain {
  public static void main(String[] args) {
    List<Agent> agents = new ArrayList<Agent>();
    agents.add(UctSearch.builder(new IngeniousState())
        .setNumSimulations(10000)
        .setNumInitialVisits(5)
        .build());
    agents.add(NegamaxSearch.builder(new IngeniousState())
        .setEvaluator(new IngeniousState.LowestScoreEvaluator())
        .setSearchDepth(3)
        .build());    
    Main main = new Main(agents, new IngeniousState().setToStartingConditions());
    main.runMatch();
  }
}
