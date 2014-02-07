package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.UctSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Main;

public class C4Main {
  public static void main(String[] args) {
    List<Agent<C4Action>> agents = new ArrayList<Agent<C4Action>>();
    agents.add(UctSearch.builder(new C4State()).setExplorationBias(0.1).build());
    agents.add(UctSearch.builder(new C4State()).setExplorationBias(0.7).build());
    agents.add(UctSearch.builder(new C4State()).setExplorationBias(1.4).build());
    agents.add(UctSearch.builder(new C4State()).setExplorationBias(2.0).build());
    agents.add(UctSearch.builder(new C4State()).setExplorationBias(5.0).build());
    Main<C4Action> main = new Main<C4Action>(agents, new C4State().setToStartingConditions());
    main.runTournament(1500);
  }
}
