package ca.thurn.uct.core;


/**
 * An Evaluator which relies on an underlying Agent to perform evaluation.
 *
 * @param <A>
 */
public class AgentEvaluator<A extends Action> implements Evaluator<A> {
  private final Agent<A> agent;  

  public AgentEvaluator(Agent<A> agent) {
    this.agent = agent;
  }
  
  @Override
  public double evaluate(Player player, State<A> state) {
    return agent.pickAction(player, state).getScore();
  }

}
