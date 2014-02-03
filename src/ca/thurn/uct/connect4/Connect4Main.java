package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.thurn.uct.algorithm.ActionPicker;
import ca.thurn.uct.algorithm.InitialStateGenerator;
import ca.thurn.uct.algorithm.Main;
import ca.thurn.uct.algorithm.Main.RunMode;
import ca.thurn.uct.algorithm.State;
import ca.thurn.uct.algorithm.StateInitializer;
import ca.thurn.uct.algorithm.UctSearch;

public class Connect4Main {
  private static class Connect4InitialStateGenerator
      implements InitialStateGenerator<Connect4Action> {
    @Override
    public State<Connect4Action> initialState() {
      return new Connect4State();
    }
  }
  
  private static class Connect4StateInitializer implements StateInitializer<Connect4Action> {
    @Override
    public State<Connect4Action> initializeFromState(State<Connect4Action> state) {
      // TODO Auto-generated method stub
      return new Connect4State((Connect4State) state);
    }
  }
  
  private static class Connect4StateCopyInitializer implements StateInitializer<Connect4Action> {
    @Override
    public State<Connect4Action> initializeFromState(State<Connect4Action> state) {
      // TODO Auto-generated method stub
      return new CopyOfConnect4State((Connect4State) state);
    }
  }
  
  public static void main(String[] args) {
    List<ActionPicker<Connect4Action>> actionPickers =
        new ArrayList<ActionPicker<Connect4Action>>();
//    Connect4HumanActionPicker hap = new Connect4HumanActionPicker();
    UctSearch<Connect4Action> search1 = new UctSearch<Connect4Action>(10000, 0.70710678);
    UctSearch<Connect4Action> search2 = new UctSearch<Connect4Action>(10000, 0.70710678);
    actionPickers.add(search1);
    actionPickers.add(search2);
    Map<ActionPicker<Connect4Action>, StateInitializer<Connect4Action>> initializers =
        new HashMap<ActionPicker<Connect4Action>, StateInitializer<Connect4Action>>();
    initializers.put(search1, new Connect4StateInitializer());
    initializers.put(search2, new Connect4StateCopyInitializer());
    new Main<Connect4Action>(RunMode.TOURNAMENT,
        new Connect4InitialStateGenerator(),
        actionPickers,
        initializers);
  }
        
}
