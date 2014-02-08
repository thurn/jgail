package ca.thurn.uct.ingenious;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.UctSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Main;


public class CopyOfIngeniousMain {
  public static void main(String[] args) {
    List<Agent<FastIngeniousAction>> agents = new ArrayList<Agent<FastIngeniousAction>>();
    agents.add(new CopyOfIngeniousHumanAgent(new CopyOfIngeniousState()));
//    agents.add(new CopyOfIngeniousHumanAgent(new CopyOfIngeniousState()));
    agents.add(UctSearch.builder(new CopyOfIngeniousState()).build());
    Main<FastIngeniousAction> main = new Main<FastIngeniousAction>(agents,
        new CopyOfIngeniousState().setToStartingConditions());
    main.runMatch();
  }
}
