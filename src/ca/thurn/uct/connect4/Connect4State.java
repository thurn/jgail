package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.thurn.uct.algorithm.State;
import ca.thurn.uct.connect4.Connect4Action.Player;

public class Connect4State extends State<Connect4Action, Connect4State> {

  private static List<Connect4Action> redActions;
  private static List<Connect4Action> blackActions;
  static {
    redActions = new ArrayList<Connect4Action>();
    for (int i = 0; i < 7; ++i) {
      redActions.add(new Connect4Action(Player.RED, i));			
    }

    blackActions = new ArrayList<Connect4Action>();
    for (int i = 0; i < 7; ++i) {
      blackActions.add(new Connect4Action(Player.BLACK, i));
    }
  }
  
  private static enum Direction {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW
  }

  // Indexed as board[column][row] with the origin being in the bottom left.
  private final Player[][] board;
  
  // The player whose turn it is
  private final Player player;
  
  // The player who has won the game at this state, or null if the game is
  // still ongoing.
  private final Player winner;

  public Connect4State() {
    super(blackActions);
    this.board = new Player[7][6];
    this.player = Player.BLACK;
    this.winner = null;
  }
  
  private Connect4State(Player player, Player[][] board, Player winner) {
    super(actionsForPlayer(board, player));
    this.board = board;
    this.player = player;
    this.winner = winner;
  }

  private static List<Connect4Action> actionsForPlayer(Player[][] board, Player player) {
    List<Connect4Action> actions = player == Player.RED ? redActions : blackActions;
    List<Connect4Action> result = new ArrayList<Connect4Action>();
    for (Connect4Action action : actions) {
      if (board[action.getColumnNumber()][5] == null) {
        result.add(action);
      }
    }
    return result;
  }

  @Override
  public double evaluate() {
    return 0.0;
  }

  @Override
  public ActionResult perform(Connect4Action action) {
    Player[][] newBoard = copyBoard();
    int freeCell = 0;
    while (newBoard[action.getColumnNumber()][freeCell] != null) {
      freeCell++;
    }
    newBoard[action.getColumnNumber()][freeCell] = player;
    Player nextStateWinner = computeWinner(player, action.getColumnNumber(), freeCell);
    Connect4State nextState = new Connect4State(player == Player.RED ? Player.BLACK : Player.RED,
        newBoard, nextStateWinner);
    double reward = (nextStateWinner == Player.RED) ? 1.0 : 0.0;
    return new ActionResult(nextState, reward);
  }

  @Override
  public boolean isTerminal() {
    if (actions.size() == 0) return true; // Draw
    return winner != null;
  }
  
  public void printState() {
    for (int row = 5; row >= 0; --row) {
      for (int column = 0; column < 7; ++column) {
        Player p = board[column][row];
        if (p == null) {
          System.out.print("-");
        } else {
          System.out.print(p == Player.RED ? "X" : "O");
        }
      }
      System.out.print("\n");
    }
  }
  
  private Player[][] copyBoard() {
    Player[][] result = new Player[7][6];
    for (int i = 0; i < 7; ++i) {
      result[i] = Arrays.copyOf(board[i], 7);
    }
    return result;
  }
  
  public Player getWinner() {
    return winner;
  }
  
  public Player getCurrentPlayer() {
    return player;
  }
  
  /**
   * Checks whether the provided player has won by making the provided move.
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

  private int countGroupSize(int col, int row, Direction dir,
      Player player) {
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

}