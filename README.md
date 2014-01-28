# UCT in Java
I was interested in learning more about Monte Carlo Tree Search, especially as it applies to 2 player games, so I implemented the well known UCT algorithm in Java based on the description in the classic paper that introduced the algorithm, "[Bandit based Monte-Carlo Planning](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.102.1296)" by Kocsis and Szepesvari (2006). My implementation is quite simple, I make no attempt to optimize the code for performance, as this is simply a learning experience.

## Connect 4 Example
As an example of how to use the algorithm, I've implemented a simple version of the game [Connect 4](http://en.wikipedia.org/wiki/Connect_Four), which will work if you import the code as an Eclipse project and run it. The AI plays a reasonably good game, but I've managed to beat it before, so there's clearly room for improvement here.

## Code Structure
A problem domain consists of States and Actions. At any given state, you can perform one of a number of different actions, potentially giving some reward, and be transitioned to a new state. An ActionPicker interface is also defined, this is intended to represent any class that's capable of picking an action from a given state. The default configuration in the main method uses a human ActionPicker for user-inputted moves and an ActionPicker based on UCT for AI moves.

## License
Code is dedicated to the public domain under the Creative Commons Zero (CC0) license unless otherwise noted in a source file. Refer to the [license text](http://creativecommons.org/publicdomain/zero/1.0/legalcode) for a description of the dedication.