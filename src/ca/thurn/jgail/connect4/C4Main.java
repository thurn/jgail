package ca.thurn.jgail.connect4;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.jgail.algorithm.MonteCarloSearch;
import ca.thurn.jgail.algorithm.NegamaxSearch;
import ca.thurn.jgail.core.Agent;
import ca.thurn.jgail.core.Main;

public class C4Main {
  public static void main(String[] args) throws InterruptedException {
    List<Agent> agents = new ArrayList<Agent>();
    agents.add(NegamaxSearch.builder(new C4State()).build());
    agents.add(MonteCarloSearch.builder(new C4State()).build());
    Main main = new Main(agents, new C4State().setToStartingConditions());
    main.runTournament(10, 1000L);
  }
}
