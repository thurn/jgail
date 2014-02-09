package ca.thurn.uct.ingenious;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.MonteCarloSearch;
import ca.thurn.uct.algorithm.UctSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.AgentEvaluator;
import ca.thurn.uct.core.Main;


public class IngeniousMain {
  public static void main(String[] args) {
    List<Agent<IngeniousAction>> agents = new ArrayList<Agent<IngeniousAction>>();
    agents.add(new IngeniousHumanAgent(new IngeniousState()));
    AgentEvaluator<IngeniousAction> monteCarloEvaluator =
        new AgentEvaluator<IngeniousAction>(
            MonteCarloSearch.builder(new IngeniousState()).setNumSimulations(1).build());
    agents.add(UctSearch.builder(new IngeniousState())
        .setNumSimulations(100000)
        .setMaxDepth(2)
        .setEvaluator(monteCarloEvaluator)
        .build());
    Main<IngeniousAction> main = new Main<IngeniousAction>(agents,
        new IngeniousState().setToStartingConditions());
    main.runMatch();
  }
}
