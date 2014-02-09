package ca.thurn.uct.ingenious;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.thurn.uct.core.Player;
import ca.thurn.uct.core.State;
import ca.thurn.uct.core.Util;

/**
 * State class for the game Ingenious.
 */
public class IngeniousState implements State<IngeniousAction> {
  
  private static final int BOARD_SIZE = 11;
  private static final int HAND_SIZE = 6;
  
  private static enum Direction {
    NE, E, SE, SW, W, NW
  }
  
  // 11x11 board using the axial coordinate system
  private List<IngeniousAction> actions;
  private IngeniousHex[][] board;
  private Player currentPlayer;
  private Map<Player, List<IngeniousPiece>> hands;
  private Map<Player, Map<IngeniousHex, Integer>> scores;
  
  /**
   * Null-initializing constructor.
   */
  public IngeniousState() {
  }

  private IngeniousState(List<IngeniousAction> actions, IngeniousHex[][] board,
      Player currentPlayer, Map<Player, List<IngeniousPiece>> hands,
      Map<Player, Map<IngeniousHex, Integer>> scores) {
    this.actions = actions;
    this.board = board;
    this.currentPlayer = currentPlayer;
    this.hands = hands;
    this.scores = scores;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<IngeniousAction> getActions() {
    return actions;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void perform(IngeniousAction action) {
    // Perform move
    board[action.getX1()][action.getY1()] = action.getPiece().getHex1();
    board[action.getX2()][action.getY2()] = action.getPiece().getHex2();
    
    // Score points
    Map<IngeniousHex, Integer> myScores = scores.get(currentPlayer);
    IngeniousHex hex1 = action.getPiece().getHex1();
    IngeniousHex hex2 = action.getPiece().getHex2();
    Direction exclude1 = 
        hexDirection(action.getX1(), action.getY1(), action.getX2(), action.getY2());
    Direction exclude2 =
        hexDirection(action.getX2(), action.getY2(), action.getX1(), action.getY1());
    myScores.put(hex1, Util.getWithDefault(myScores, hex1, 0) + 
        scoreForHex(action.getX1(), action.getY1(), hex1, exclude1));
    myScores.put(hex2, Util.getWithDefault(myScores, hex2, 0) + 
        scoreForHex(action.getX2(), action.getY2(), hex2, exclude2));    
    
    // Update hand
    List<IngeniousPiece> hand = hands.get(currentPlayer);
    hand.remove(action.getPiece());
    hand.add(randomPiece());

    currentPlayer = playerAfter(currentPlayer);
    actions = allActions(hands.get(currentPlayer));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void undo(IngeniousAction action) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State<IngeniousAction> setToStartingConditions() {
    this.board = new IngeniousHex[BOARD_SIZE][BOARD_SIZE];
    addInitialHexes(board);
    this.currentPlayer = Player.PLAYER_ONE;
    this.hands = new HashMap<Player, List<IngeniousPiece>>();
    List<IngeniousPiece> currentHand = randomHand();
    this.hands.put(Player.PLAYER_ONE, currentHand);
    this.hands.put(Player.PLAYER_TWO, randomHand());
    HashMap<IngeniousHex, Integer> p1 = new HashMap<IngeniousHex, Integer>();
    for (IngeniousHex hex : IngeniousHex.allColors()) {
      p1.put(hex, 0);
    }
    HashMap<IngeniousHex, Integer> p2 = new HashMap<IngeniousHex, Integer>();
    for (IngeniousHex hex : IngeniousHex.allColors()) {
      p2.put(hex, 0);
    }    
    this.scores = new HashMap<Player, Map<IngeniousHex, Integer>>();
    this.scores.put(Player.PLAYER_ONE, p1);
    this.scores.put(Player.PLAYER_TWO, p2);
    actions = allActions(currentHand);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State<IngeniousAction> copy() {
    Map<Player, List<IngeniousPiece>> newHands = new HashMap<Player, List<IngeniousPiece>>();
    for (Map.Entry<Player, List<IngeniousPiece>> entry : hands.entrySet()) {
      newHands.put(entry.getKey(), new ArrayList<IngeniousPiece>(entry.getValue()));
    }
    Map<Player, Map<IngeniousHex, Integer>> newScores =
        new HashMap<Player, Map<IngeniousHex, Integer>>();
    for (Map.Entry<Player, Map<IngeniousHex, Integer>> entry : scores.entrySet()) {
      newScores.put(entry.getKey(), new HashMap<IngeniousHex, Integer>(entry.getValue()));
    }
    return new IngeniousState(new ArrayList<IngeniousAction>(actions), copyBoard(),
        currentPlayer, newHands, newScores);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State<IngeniousAction> initialize(State<IngeniousAction> state) {
    IngeniousState copy = (IngeniousState)state.copy();
    this.actions = copy.actions;
    this.board = copy.board;
    this.currentPlayer = copy.currentPlayer;
    this.hands = copy.hands;
    this.scores = copy.scores;
    return this;
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

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isTerminal() {
    return actions.size() == 0;
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
  public Player getWinner() {
    ArrayList<Integer> p1 = new ArrayList<Integer>(scores.get(Player.PLAYER_ONE).values());
    Collections.sort(p1);
    ArrayList<Integer> p2 = new ArrayList<Integer>(scores.get(Player.PLAYER_TWO).values());
    Collections.sort(p2);
    
    for (int i = 0; i < 6; ++i) {
      if (p1.get(i) > p2.get(i)) {
        return Player.PLAYER_ONE;
      } else if (p2.get(i) > p1.get(i)) {
        return Player.PLAYER_TWO;
      }
    }
    
    return null;
  }
  
  /**
   * @param player Player whose hand to look in.
   * @param index Index of desired piece.
   * @return The piece at the indicating index in this player's hand.
   */
  IngeniousPiece getPiece(Player player, int index) {
    return hands.get(player).get(index);
  }
  
  /**
   * @param x X coordinate.
   * @param y Y coordinate.
   * @return True if there is no hex currently placed on the specified board
   *     location.
   */
  boolean isOpen(int x, int y) {
    return board[x][y] == null;
  }

  /**
   * @param hand The current player's hand.
   * @return A list of all actions possible from the current game state with
   *     the provided hand.
   */
  private List<IngeniousAction> allActions(List<IngeniousPiece> hand) {
    List<IngeniousAction> result = new ArrayList<IngeniousAction>();
    for (int x = 0; x < BOARD_SIZE; ++x) {
      for (int y = 0; y < BOARD_SIZE; ++y) {
        if (board[x][y] == null) {
          addActionsForPosition(hand, result, x, y);
        }
      }
    }
    return result;
  }
  
  /**
   * Adds all possible actions at the provided board position to the action list.
   *
   * @param hand The current player's hand.
   * @param list The action list.
   * @param x X coordinate.
   * @param y Y coordinate.
   */
  private void addActionsForPosition(List<IngeniousPiece> hand,  List<IngeniousAction> list,
      int x, int y) {
    if (y - 1 > 0 && y - 1 < BOARD_SIZE && board[x][y - 1] == null) {
      addActionsForMove(hand, list, x, y, x, y - 1);
    } else if (x + 1 > 0 && x + 1 < BOARD_SIZE && y - 1 > 0 && y - 1 < BOARD_SIZE &&
        board[x + 1][y - 1] == null) {
      addActionsForMove(hand, list, x, y, x + 1, y - 1);
    } else if (x + 1 > 0 && x + 1 < BOARD_SIZE && board[x + 1][y] == null) {
      addActionsForMove(hand, list, x, y, x + 1, y);
    } else if (y + 1 > 0 && y + 1 < BOARD_SIZE && board[x][y + 1] == null) {
      addActionsForMove(hand, list, x, y, x, y + 1);
    } else if (x - 1 > 0 && x - 1 < BOARD_SIZE && y + 1 > 0 && y + 1 < BOARD_SIZE &&
        board[x - 1][y + 1] == null) {
      addActionsForMove(hand, list, x, y, x - 1 , y + 1);
    } else if (x - 1 > 0 && x - 1 < BOARD_SIZE && board[x - 1][y] == null) {
      addActionsForMove(hand, list, x, y, x - 1, y);
    }
  }
  
  /**
   * Add all possible actions involving providing a piece on the specified two
   * hexes onto the board.
   *
   * @param hand The current player's hand.
   * @param list The action list.
   * @param x1 First hex X coordinate.
   * @param y1 First hex Y coordinate.
   * @param x2 Second hex X coordinate.
   * @param y2 Second hex Y coordinate.
   */
  private void addActionsForMove(List<IngeniousPiece> hand, List<IngeniousAction> list,
      int x1, int y1, int x2, int y2) {
    for (IngeniousPiece piece : hand) {
      list.add(new IngeniousAction(piece, x1, y1, x2, y2));
    }
  }
  
  /**
   * @return A randomly generated hand of pieces.
   */
  private List<IngeniousPiece> randomHand() {
    List<IngeniousPiece> hand = new ArrayList<IngeniousPiece>(HAND_SIZE);
    for (int i = 0; i < HAND_SIZE; ++i) {
      hand.add(randomPiece());
    }
    return hand;
  }
  
  /**
   * @return A randomly generated piece.
   */
  private IngeniousPiece randomPiece() {
    return new IngeniousPiece(IngeniousHex.randomHex(), IngeniousHex.randomHex());
  }
  
  /**
   * Add OFF_BOARD dummy hexes to the parts of the board that are off-limits.
   * This is necessary because you can't store a hexagonal board in a square
   * array. Also add the starting colored hexes.
   *
   * @param board The board.
   */
  private void addInitialHexes(IngeniousHex[][] board) {
    board[0][0] = IngeniousHex.OFF_BOARD;
    board[0][1] = IngeniousHex.OFF_BOARD;
    board[0][2] = IngeniousHex.OFF_BOARD;
    board[0][3] = IngeniousHex.OFF_BOARD;
    board[0][4] = IngeniousHex.OFF_BOARD;
    board[1][0] = IngeniousHex.OFF_BOARD;
    board[1][1] = IngeniousHex.OFF_BOARD;
    board[1][2] = IngeniousHex.OFF_BOARD;
    board[1][3] = IngeniousHex.OFF_BOARD;
    board[2][0] = IngeniousHex.OFF_BOARD;
    board[2][1] = IngeniousHex.OFF_BOARD;
    board[2][2] = IngeniousHex.OFF_BOARD;
    board[3][0] = IngeniousHex.OFF_BOARD;
    board[3][1] = IngeniousHex.OFF_BOARD;
    board[4][0] = IngeniousHex.OFF_BOARD;
    
    board[10][6] = IngeniousHex.OFF_BOARD;
    board[10][7] = IngeniousHex.OFF_BOARD;
    board[10][8] = IngeniousHex.OFF_BOARD;
    board[10][9] = IngeniousHex.OFF_BOARD;
    board[10][10] = IngeniousHex.OFF_BOARD;
    board[9][7] = IngeniousHex.OFF_BOARD;
    board[9][8] = IngeniousHex.OFF_BOARD;
    board[9][9] = IngeniousHex.OFF_BOARD;
    board[9][10] = IngeniousHex.OFF_BOARD;
    board[8][8] = IngeniousHex.OFF_BOARD;
    board[8][9] = IngeniousHex.OFF_BOARD;
    board[8][10] = IngeniousHex.OFF_BOARD;
    board[7][9] = IngeniousHex.OFF_BOARD;
    board[7][10] = IngeniousHex.OFF_BOARD;
    board[6][10] = IngeniousHex.OFF_BOARD;
    
    board[0][10] = IngeniousHex.BLUE;
    board[5][10] = IngeniousHex.GREEN;
    board[10][5] = IngeniousHex.ORANGE;
    board[10][0] = IngeniousHex.PURPLE;
    board[5][0] = IngeniousHex.RED;
    board[0][5] = IngeniousHex.YELLOW;
  }
  
  /**
   * @return A copy of the current board.
   */
  private IngeniousHex[][] copyBoard() {
    IngeniousHex[][] result = new IngeniousHex[BOARD_SIZE][BOARD_SIZE];
    for (int i = 0; i < BOARD_SIZE; ++i) {
      result[i] = Arrays.copyOf(board[i], BOARD_SIZE);
    }
    return result;
  }
  
  /**
   * Calculates the score for placing a hex.
   * 
   * @param x X coordinate of hex.
   * @param y Y coordinate of hex.
   * @param hex The hex being placed.
   * @param excludeDirection A Direction in which score should NOT be counted.
   *     This is used to enforce the rule that you can't score points in the
   *     direction of a piece's sibling hex. 
   * @return Total score for placing this hex at these coordinates.
   */
  private int scoreForHex(int x, int y, IngeniousHex hex, Direction excludeDirection) {
    int total = 0;
    if (Direction.NE != excludeDirection) {
      total += countInDirection(x + 1, y - 1, hex, Direction.NE);
    }
    if (Direction.E != excludeDirection) {
      total += countInDirection(x + 1, y, hex, Direction.E);
    }
    if (Direction.SE != excludeDirection) {
      total += countInDirection(x, y + 1, hex, Direction.SE);
    }
    if (Direction.SW != excludeDirection) {
      total += countInDirection(x - 1, y + 1, hex, Direction.SW);
    }
    if (Direction.W != excludeDirection) {
      total += countInDirection(x - 1, y, hex, Direction.W);
    }
    if (Direction.NW != excludeDirection) {
      total += countInDirection(x, y - 1, hex, Direction.NW);
    }   
    return total;
  }
  
  /**
   * Counts hexes of the same color as the provided hex in the provided
   * direction from the given start coordinates.
   *
   * @param x Starting X coordinate.
   * @param y Starting Y coordinate.
   * @param hex Hex whose color we are counting.
   * @param direction Direction in which to count.
   * @return The number of hexes in a line in the provided direction from the
   *     given coordiantes.
   */
  private int countInDirection(int x, int y, IngeniousHex hex, Direction direction) {
    if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE || board[x][y] == null ||
        board[x][y] != hex) {
      return 0;
    }
    switch (direction) {
      case NE:
        return 1 + countInDirection(x + 1, y - 1, hex, direction);
      case E:
        return 1 + countInDirection(x + 1, y, hex, direction);
      case SE:
        return 1 + countInDirection(x, y + 1, hex, direction);
      case SW:
        return 1 + countInDirection(x - 1, y + 1, hex, direction);
      case W:
        return 1 + countInDirection(x - 1, y, hex, direction);
      case NW:
        return 1 + countInDirection(x, y - 1, hex, direction);
    }
    return 0;
  }
  
  /**
   * @param x1 First X coordinate.
   * @param y1 First Y coordinate.
   * @param x2 Second X coordinate.
   * @param y2 Second Y coordinate.
   * @return The direction you will travel if you go from (x1, y1) to
   *     (x2, y2), assuming they are one step apart. 
   */
  Direction hexDirection(int x1, int y1, int x2, int y2) {
    if (x2 - x1 == 0 && y2 - y1 == -1) {
      return Direction.NW;
    } else if (x2 - x1 == 1 && y2 - y1 == -1) {
      return Direction.NE;
    } else if (x2 - x1 == 1 && y2 - y1 == 0) {
      return Direction.E;
    } else if (x2 - x1 == 0 && y2 - y1 == 1) {
      return Direction.SE;
    } else if (x2 - x1 == -1 && y2 - y1 == 1) {
      return Direction.SW;
    } else if (x2 - x1 == -1 && y2 - y1 == 0) {
      return Direction.W;
    } else {
      throw new RuntimeException("Invalid arguments to hexDirection");
    }
  }
  
  /**
   * Append the given number of space characters to this StringBuilder.
   *
   * @param builder The StringBuilder.
   * @param count Number of spaces to append.
   */
  private void addSpaces(StringBuilder builder, int count) {
    for (int i = 0; i < count; ++i) {
      builder.append(" ");
    }
  }
  
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("\nNum Actions: " + getActions().size() + "\n");
    scoresToString(result);
    boardToString(result);
    handsToString(result);
    return result.toString();
  }

  /**
   * Adds the current game scores to the provided StringBuilder.
   *
   * @param result The StringBuilder.
   */
  private void scoresToString(StringBuilder result) {
    result.append("Scores:\n");
    for (Player player : scores.keySet()) {
      result.append(player + ":");
      for (IngeniousHex hex : IngeniousHex.allColors()) {
        result.append(" " + hex + "=" + Util.getWithDefault(scores.get(player), hex, 0));
      }
      result.append("\n");
    }
  }

  /**
   * Adds a representation of the board to the provided StringBuilder.
   *
   * @param result The StringBuilder.
   */
  private void boardToString(StringBuilder result) {
    result.append("\nBoard:\n");
    for (int y = 0; y < BOARD_SIZE; ++y) {
      addSpaces(result, 2*Math.abs(5 - y));
      for (int x = 0; x < BOARD_SIZE; ++x) {
        if (board[x][y] == null) {
          if (x == 10) {
            result.append("[T" + y % 10 + "]");            
          } else if (y == 10) {
            result.append("[" + x + "T]");            
          } else {
            result.append("[" + x + "" + y + "]");
          }
          
        } else {
          if (board[x][y] != IngeniousHex.OFF_BOARD) {
            result.append("[" + board[x][y].toString() + "]");
          }
        }
      }
      result.append("\n");
    }
  }

  /**
   * Adds the current player's hand to the provided StringBuilder.  
   *
   * @param result The StringBuilder.
   */
  private void handsToString(StringBuilder result) {
    result.append("\nHand:\n");
    int pieceNumber = 0;
    for (IngeniousPiece piece : hands.get(currentPlayer)) {
      result.append(" " + pieceNumber + ") " + piece);
      pieceNumber++;
    }
  }
}
