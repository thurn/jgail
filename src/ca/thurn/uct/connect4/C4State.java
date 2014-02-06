package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.thurn.uct.core.Player;
import ca.thurn.uct.core.State;

/**
 * State class for a game of Connect4.
 */
public class C4State implements State<C4Action> {
  
  private static final int BOARD_HEIGHT = 6;
  private static final int BOARD_WIDTH = 7;  
  private static final List<C4Action> p1Actions;
  private static final List<C4Action> p2Actions;  
  static {
    p1Actions = new ArrayList<C4Action>();
    for (int i = 0; i < BOARD_WIDTH; ++i) {
      p1Actions.add(new C4Action(Player.PLAYER_ONE, i));            
    }

    p2Actions = new ArrayList<C4Action>();
    for (int i = 0; i < BOARD_WIDTH; ++i) {
      p2Actions.add(new C4Action(Player.PLAYER_TWO, i));
    }
  }
  
  private static enum Direction {
    N, NE, E, SE, S, SW, W, NW
  }
  
  // Indexed as board[column][row] with the origin being in the bottom left,
  // null represents an empty space.
  private Player[][] board;
  private List<C4Action> actions;
  private Player currentPlayer;
  private Player winner;
  
  /**
   * Null-initializes this state. The state will not be usable until one of
   * initialize() or setToStartingConditions() is called on the result; 
   */
  public C4State() {
  }

  /**
   * Private field-initializing constructor. 
   * 
   * @param board
   * @param actions
   * @param currentPlayer
   * @param winner
   */
  private C4State(Player[][] board, List<C4Action> actions, Player currentPlayer, Player winner) {
    this.board = board;
    this.actions = actions;
    this.currentPlayer = currentPlayer;
    this.winner = winner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<C4Action> getActions() {
    return actions;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void perform(C4Action action) {
    int freeSpace = 0;
    while (board[action.getColumnNumber()][freeSpace] != null) {
      freeSpace++;
    }
    board[action.getColumnNumber()][freeSpace] = currentPlayer;
    winner = computeWinner(currentPlayer, action.getColumnNumber(), freeSpace);
    currentPlayer = playerAfter(currentPlayer);
    actions = actionsForCurrentPlayer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void undo(C4Action action) {
    int freeCell = BOARD_HEIGHT - 1;
    while (board[action.getColumnNumber()][freeCell] == null) {
      freeCell--;
    }
    board[action.getColumnNumber()][freeCell] = null;
    winner = null;
    currentPlayer = playerBefore(currentPlayer);
    actions = actionsForCurrentPlayer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public C4State setToStartingConditions() {
    board = new Player[BOARD_WIDTH][BOARD_HEIGHT];
    winner = null;
    currentPlayer = Player.PLAYER_ONE;
    actions = actionsForCurrentPlayer();
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State<C4Action> copy() {
    return new C4State(copyBoard(), new ArrayList<C4Action>(actions), currentPlayer, winner);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public C4State initialize(State<C4Action> state) {
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
    return winner != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Player getWinner() {
    return winner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Player playerAfter(Player player) {
    return player == Player.PLAYER_ONE ? Player.PLAYER_TWO :
        Player.PLAYER_ONE;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Player playerBefore(Player player) {
    return playerAfter(player);
  }
  
  @Override  
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (int row = 5; row >= 0; --row) {
      for (int column = 0; column < 7; ++column) {
        Player p = board[column][row];
        if (p == null) {
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
  private List<C4Action> actionsForCurrentPlayer() {
    List<C4Action> actions = currentPlayer == Player.PLAYER_TWO ? p2Actions : p1Actions;
    List<C4Action> result = new ArrayList<C4Action>();
    for (C4Action action : actions) {
      if (board[action.getColumnNumber()][BOARD_HEIGHT - 1] == null) {
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
  private Player computeWinner(Player player, int moveColumn, int moveRow) {
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
    return null;
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
  private int countGroupSize(int col, int row, Direction dir, Player player) {
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
  private Player[][] copyBoard() {
    Player[][] result = new Player[BOARD_WIDTH][BOARD_HEIGHT];
    for (int i = 0; i < BOARD_WIDTH; ++i) {
      result[i] = Arrays.copyOf(board[i], BOARD_HEIGHT);
    }
    return result;
  } 

}
