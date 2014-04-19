package ca.thurn.jgail.ingenious;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.jgail.algorithm.NegamaxSearch;
import ca.thurn.jgail.algorithm.UctSearch;
import ca.thurn.jgail.core.Agent;
import ca.thurn.jgail.core.Main;

public class IngeniousMain {
  public static void main(String[] args) throws InterruptedException {
    List<Agent> agents = new ArrayList<Agent>();
    agents.add(UctSearch.builder(new IngeniousState())
        .setNumSimulations(10000)
        .setNumInitialVisits(5)
        .build());
    agents.add(NegamaxSearch.builder(new IngeniousState())
        .setEvaluator(new IngeniousState.LowestScoreEvaluator())
        .setSearchDepth(2)
        .build());    
    Main main = new Main(agents, new IngeniousState().setToStartingConditions());
    main.runTournament(5, 500L);
  }
}
