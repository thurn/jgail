package ca.thurn.uct.ingenious;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.NegamaxSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Main;


public class IngeniousMain {
  public static void main(String[] args) {
    List<Agent<IngeniousAction>> agents = new ArrayList<Agent<IngeniousAction>>();
    agents.add(new IngeniousHumanAgent(new IngeniousState()));
//    agents.add(MonteCarloSearch.builder(new IngeniousState()).build());
//    agents.add(UctSearch.builder(new IngeniousState()).build());
    agents.add(NegamaxSearch.builder(new IngeniousState()).build());
    Main<IngeniousAction> main = new Main<IngeniousAction>(agents,
        new IngeniousState().setToStartingConditions());
    main.runMatch();
  }
}
