# JGAIL - a Java Game AI Libary
This is the start of a library to implement some common algorithms from AI in Java for use in games, primarily games which are 2-player, turn-based, and have perfect information. I wrote this library primarily for my own education & amusement, but it might be helpful to you too. So far, the library includes 3 different action-search algorithms and 3 different example games on which the algorithms are used.

![Example Screenshot](images/ingenious.png?raw=true =700x322)

What follows is a high-level overview of the structure of JGAIL. For access to the full Javadoc for the library, go [here](https://thurn.github.io/jgail/doc/).

## Agents
A participant in a game is called an Agent, and should implement the [Agent interface](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/Agent.html). The core property of Agents is that when they are provided with a given game state, they are able to pick an action to take from that state... ideally one which maximizes their own rewards. Agents also have the ability to supply their own **state representation**. In my original design, all Agents in a game would use the same State object, but this ended up proving cumbersome. Different Agents may have different ways of representing the same game state in a way which is efficient for them.

If an Agent is able to selection Actions asynchronously on a background thread, it should instead implement the [Asynchronous Agent](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/AsynchronousAgent.html) interface, which provideds methods for starting and stopping the action search. Usually such an Agent will be given a fixed time budget in which to find an action, after which they are asked for a result and should return their "current best" action.

Agents can also be human players in the game, in which case they selection an action by prompting for user input. An example of such an Agent for the game Connect 4 is [C4HumanAgent](https://github.com/thurn/jgail/blob/master/src/ca/thurn/jgail/connect4/C4HumanAgent.java). Other Agents may implement algorithms, such as is the case with the [MonteCarloSearch](https://github.com/thurn/jgail/blob/master/src/ca/thurn/jgail/algorithm/MonteCarloSearch.java) agent.

## Evaluators

In order for an Agent to make good decisions, it must be able to figure out if a given state is better or worse for it that its current state. This is the role of the evaluation function, captured by the [Evaluator](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/Evaluator.html) interface. This can in many cases be the most complex part of designing a good AI... finding an evaluation function which *quickly* assigns a heuristic value to a given state. One simple Evaluator is the [Win-Loss Evaluator](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/WinLossEvaluator.html) which simply evaluates states based on whether or not the given player has won the game at that state.

Another class of Evaluators are based on the [AgentEvaluator](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/AgentEvaluator.html) class. When Agents are asked to select an Action, they actually return an instance of the [ActionScore](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/ActionScore.html) class, which gives an action to take along with an associated *score* for that action. An AgentEvaluator, then, asks an underlying Agent to evaluate the provided state. This enables an easy way of chaining Agents together. For example, a very strong AI for the game Connect4 can be made by performing a shallow search of the game tree with a traditional Negamax algorithm and then evaluating leaf states via a Monte Carlo search to give a heuristic score indicating how good that state is.

## Players and Actions

These two parts of AI implementaition are largely left up to you. Players are represented by integers, with distinct integers corresponding to distinct players. Actions from a given state are represented by java longs. This is done to maximize performance and cut down on the need to allocate a lot of objects during an action search. The standard technique to build actions (demonstrated in the examples) is to use bit-packing to store all of the information needed to perform the action. You are not limited to this approach, however. If you want to have a full Java Object to represent each possible action from a state, you can simply use the long value as an index into the list of possible actions to perform from that state.

## States

The core of JGAIL is the [State](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/State.html) interface. This is what defines any possible game state. States have a number of related responsibilities:

### Supplying Possible Actions
States are responsible for supplying clients with the *possible actions* that can be taken. For example, a State which represented pieces on a chess board would be responsible for providing clients with the legal moves for the current player. This is primarily implemented through the [getActionIterator()](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/State.html#getActionIterator(\)) method, which returns an [ActionIterator](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/State.ActionIterator.html) over the possible actions from the current state.

The State can return actions in any order. Some states choose to pre-compute all possible actions in advance, other states will dynamically produce them during iteration... both choices are valid depending on your specific problem space. States also need to be able to produce random actions for use by stochastic agents. These agents will perform better if the random actions are uniformly distributed.

### Performing Actions
States need to be able to *perform* actions. For example, a State which represented a chess baord would need to be able to take the action "e2-e4" and update itself so that the pawn in the e2 position is now in the e4 position. In the original version of JGAIL, States were immutable and returned a *new* State on perform, but this provided unacceptable performance, so the current version is based on mutating the State.

The key method here is [perform()](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/State.html#perform(long\)), which takes an Action (java long) and mutates the state. The supplied action will be one of the ones previously returned as a possible action by the State.

States also need to support undoing actions. Several search algorithms are most efficiently implemented by performing a series of actions, evaluating the resulting State, and then undoing the actions, thus saving on memory allocations. In order to help implement undo(), the State can return an *undo token* from perform which clients need to supply when they try and undo that action. This can, for example, encapsulate any random choices the State made when performing the action (such as drawing new cards from a deck).

### Providing standard State information
States also need to be able to answer some general questions about themselves. Most importantly, they need to have a *current player*, whose turn it currently is. They also need to be able to figure out if the game is currently over or not (representing a terminal game state). Finally, they need to implement the plumbing for turn management... "whose turn is it after the current player's turn?", etc.

### Initialization
As mentioned in the Agent section, different State representations may be employed during the same game. To help with this, States can be copied around, and a new State can be "initialized from" another state (via the [initializeFrom()](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/State.html#initializeFrom(ca.thurn.jgail.core.Copyable\)) method). Typically, when you first create a State class, its fields will simply be initialized with nulls. Then, the State will be given another State to initialize itself to. In this way, the game can maintain a **canonical game state** and then tell each Agent's state representaiton to initialize itself to match that canonical state.

States have a separate method called [setToStartingConditions()](https://thurn.github.io/jgail/doc/ca/thurn/jgail/core/State.html#setToStartingConditions(\)) which puts the state into the initial state of the game. In the case of a chess board, for example, this would be the state will all of the pieces in their starting positions.

## Algorithms
There are three different AI algorithms currently included with JGAIL:

* **Monte Carlo Search**. A simple algorithm which selects the best move by playing random games and picking the action which had the best results across those games. Despite its simplicity, it can be a strong algorithm because of its performance, and it can also be used to evaluate terminal states of another algorithm via the AgentEvaluator pattern.
* **Negamax Search**. Probably the canonical solution for solving problems in Game AI. When used with an efficient state representation and a good evaluation function, it can perform very well on games with a low branching factor (e.g. Chess). It breaks down quickly on games with a higher branching factor or ones which are not amenable to efficient heuristic evaluation of intermediate states.
* **UCT**. A very well-known algorithm described in the paper "[Bandit based Monte-Carlo Planning](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.102.1296)" by Kocsis and Szepesvari (2006). UCT differs from traditional Monte Carlo search in that it keeps track of actions at each level of the search which have been successful before and repeats them with some probability. Often a good choice for large state-space games such as Go.

## Example Domains
Three different examples are provided to show how to use the tools included in JGAIL:

* **Tic Tac Toe**. An extremely simple game with a very small state-space. It's useful for testing algorithms because any algorithm should be able to play a perfect game, and it's easy to manually follow the flow of the algorithm to debug problems.
* **Connect 4**. A medium-sized game with a smaller branching factor. All of the algorithms here are competitive at it and can outperform a human player, but it's still too large of a game for them to play a perfect game.
* **[Ingenious](https://en.wikipedia.org/wiki/Ingenious_(board_game\))**. A tile placement game designed by Reiner Knizia, shown in the screenshot above. Has a very large branching factor (there are on the order of 500 possible opening moves), and intermediate states are very difficult to evaluate heuristically, making it similar to games like Go. Algorithms like Negamax are effectively useless at playing this game.


## License
Code is dedicated to the public domain under the Creative Commons Zero (CC0) license unless otherwise noted in a source file. Refer to the [license text](http://creativecommons.org/publicdomain/zero/1.0/legalcode) for a description of the dedication.