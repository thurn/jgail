package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.MonteCarloSearch;
import ca.thurn.uct.algorithm.UctSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.AgentEvaluator;
import ca.thurn.uct.core.Main;

public class C4Main {
  public static void main(String[] args) {
    List<Agent<C4Action>> agents = new ArrayList<Agent<C4Action>>();
//    agents.add(new C4HumanAgent(new C4State()));
    AgentEvaluator<C4Action> monteCarloEvaluator =
        new AgentEvaluator<C4Action>(
            MonteCarloSearch.builder(new C4State()).setNumSimulations(500).build());
    agents.add(UctSearch.builder(new C4State())
        .setNumSimulations(1000)
        .setMaxDepth(5)
        .setEvaluator(monteCarloEvaluator)
        .build());
    agents.add(UctSearch.builder(new C4State()).build());
    Main<C4Action> main = new Main<C4Action>(agents, new C4State().setToStartingConditions());
//    main.runMatch();
    main.runTournament(10);
  }
}
