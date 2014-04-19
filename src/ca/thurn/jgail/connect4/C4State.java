package ca.thurn.jgail.connect4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ca.thurn.jgail.core.Copyable;
import ca.thurn.jgail.core.Player;
import ca.thurn.jgail.core.State;

/**
 * State class for a game of Connect4.
 */
public class C4State implements State {
  
  private static final int BOARD_HEIGHT = 6;
  private static final int BOARD_WIDTH = 7;  
  private static final List<Long> p1Actions;
  private static final List<Long> p2Actions;  
  static {
    p1Actions = new ArrayList<Long>();
    for (int i = 0; i < BOARD_WIDTH; ++i) {
      p1Actions.add(C4Action.create(Player.PLAYER_ONE, i));            
    }

    p2Actions = new ArrayList<Long>();
    for (int i = 0; i < BOARD_WIDTH; ++i) {
      p2Actions.add(C4Action.create(Player.PLAYER_TWO, i));   
    }
  }
  
  /**
   * Direction on the game board.
   */
  private static enum Direction {
    N, NE, E, SE, S, SW, W, NW
  }
  
  // Indexed as board[column][row] with the origin being in the bottom left,
  // null represents an empty space.
  private int[][] board;
  private List<Long> actions;
  private int currentPlayer;
  private int winner;
  private Random random = new Random();
  
  /**
   * Null-initializes this state. The state will not be usable until one of
   * initialize() or setToStartingConditions() is called on the result; 
   */
  public C4State() {
  }

  private C4State(int[][] board, List<Long> actions, int currentPlayer, int winner) {
    this.board = board;
    this.actions = actions;
    this.currentPlayer = currentPlayer;
    this.winner = winner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State.ActionIterator getActionIterator() {
    return new State.ActionIteratorFromIterable(actions);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public long getRandomAction() {
    return actions.get(random.nextInt(actions.size()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long perform(long action) {
    int freeSpace = 0;
    while (board[C4Action.getColumnNumber(action)][freeSpace] != 0) {
      freeSpace++;
    }
    board[C4Action.getColumnNumber(action)][freeSpace] = currentPlayer;
    winner = computeWinner(currentPlayer, C4Action.getColumnNumber(action), freeSpace);
    currentPlayer = playerAfter(currentPlayer);
    actions = actionsForCurrentPlayer();
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void undo(long action, long undoToken) {
    int freeCell = BOARD_HEIGHT - 1;
    while (board[C4Action.getColumnNumber(action)][freeCell] == 0) {
      freeCell--;
    }
    board[C4Action.getColumnNumber(action)][freeCell] = 0;
    winner = 0;
    currentPlayer = playerBefore(currentPlayer);
    actions = actionsForCurrentPlayer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public C4State setToStartingConditions() {
    board = new int[BOARD_WIDTH][BOARD_HEIGHT];
    winner = 0;
    currentPlayer = Player.PLAYER_ONE;
    actions = actionsForCurrentPlayer();
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State copy() {
    if (actions == null && board == null) {
      return new C4State();
    }
    return new C4State(copyBoard(), new ArrayList<Long>(actions), currentPlayer, winner);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public C4State initializeFrom(Copyable state) {
    C4State temp = (C4State)state.copy();
    this.board = temp.board;
    this.winner = temp.winner;
    this.currentPlayer = temp.currentPlayer;
    this.actions = temp.actions;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isTerminal() {
    if (actions.size() == 0) return true; // Draw
    return winner != 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getWinner() {
    return winner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int playerAfter(int player) {
    return player == Player.PLAYER_ONE ? Player.PLAYER_TWO :
        Player.PLAYER_ONE;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int playerBefore(int player) {
    return playerAfter(player);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String actionToString(long action) {
    return "[" + C4Action.getColumnNumber(action) + "]";
  }
  
  @Override  
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (int row = 5; row >= 0; --row) {
      for (int column = 0; column < 7; ++column) {
        int p = board[column][row];
        if (p == 0) {
          result.append("-");
        } else {
          result.append(p ==  Player.PLAYER_TWO ? "X" : "O");
        }
      }
      result.append("\n");
    }
    return result.toString();
  }
  
  /**
   * @return A list of actions the current player could legally take from 
   *     the current state.
   */
  private List<Long> actionsForCurrentPlayer() {
    List<Long> actions = currentPlayer == Player.PLAYER_TWO ? p2Actions : p1Actions;
    List<Long> result = new ArrayList<Long>();
    for (int i = 0; i < actions.size(); ++i) {
      long action = actions.get(i);
      if (board[C4Action.getColumnNumber(action)][BOARD_HEIGHT - 1] == 0) {
        result.add(action);
      }
    }
    return result;
  }
  
  /**
   * Checks whether the provided player has won by making the provided move.
   *
   * @param player Player to check.
   * @param moveColumn Column number of player's move.
   * @param moveRow Row number of player's move.
   * @return The provided player if this move was a winning move for that
   *     player, otherwise null.
   */
  private int computeWinner(int player, int moveColumn, int moveRow) {
    // Vertical win?
    if (countGroupSize(moveColumn, moveRow - 1, Direction.S, player) >= 3) {
      return player;
    }
    
    // Horizontal win?
    if (countGroupSize(moveColumn + 1, moveRow, Direction.E, player) +
        countGroupSize(moveColumn - 1, moveRow, Direction.W, player) >= 3) {
      return player;
    }
    
    // Diagonal win?
    if (countGroupSize(moveColumn + 1, moveRow + 1, Direction.NE, player) +
        countGroupSize(moveColumn - 1, moveRow - 1, Direction.SW, player) >= 3) {
      return player;
    }
    if (countGroupSize(moveColumn - 1, moveRow + 1, Direction.NW, player) +
        countGroupSize(moveColumn + 1, moveRow - 1, Direction.SE, player) >=3) {
      return player;
    }
    
    // No win
    return 0;
  }

  /**
   * Counts consecutive pieces from the same player.
   *
   * @param col Column number to start counting from.
   * @param row Row number to start counting from.
   * @param dir Direction in which to count.
   * @param player Player whose pieces we are counting.
   * @return The number of pieces belonging to this player, not counting the
   *     provided column and row, which can be found in a line in the provided
   *     direction.
   */
  private int countGroupSize(int col, int row, Direction dir, int player) {
    if (row < 6 && row >= 0 && col < 7 && col >= 0
        && board[col][row] == player) {
      switch (dir) {
        case N:
          return 1 + countGroupSize(col, row + 1, dir, player);
        case S:
          return 1 + countGroupSize(col, row - 1, dir, player);
        case E:
          return 1 + countGroupSize(col + 1, row, dir, player);
        case W:
          return 1 + countGroupSize(col - 1, row, dir, player);
        case NE:
          return 1 + countGroupSize(col + 1, row + 1, dir, player);
        case NW:
          return 1 + countGroupSize(col - 1, row + 1, dir, player);
        case SE:
          return 1 + countGroupSize(col + 1, row - 1, dir, player);
        case SW:
          return 1 + countGroupSize(col - 1, row - 1, dir, player);
        default:
          return 0;
      }
    } else {
      return 0;
    }
  }
  
  /**
   * @return A copy of the game's current board.
   */
  private int[][] copyBoard() {
    int[][] result = new int[BOARD_WIDTH][BOARD_HEIGHT];
    for (int i = 0; i < BOARD_WIDTH; ++i) {
      result[i] = Arrays.copyOf(board[i], BOARD_HEIGHT);
    }
    return result;
  }

}
