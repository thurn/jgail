package ca.thurn.jgail.ingenious;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ca.thurn.jgail.core.Copyable;
import ca.thurn.jgail.core.Evaluator;
import ca.thurn.jgail.core.Player;
import ca.thurn.jgail.core.State;

/**
 * State class for the game Ingenious.
 */
public class IngeniousState implements State {
  
  private static final int BOARD_SIZE = 11;
  private static final int HAND_SIZE = 6;
  
  /**
   * Direction on the game board.
   */
  private static enum Direction {
    NE, E, SE, SW, W, NW
  }
  
  /**
   * Evaluator which evaluates Ingenious game states by comparing the player's
   * lowest scoring color in the state, breaking ties by looking at the second
   * lowest score, and so on.
   */
  public static class LowestScoreEvaluator implements Evaluator {
    @Override
    public double evaluate(int player, State state) {
      IngeniousState ingeniousState = (IngeniousState)state;
      List<Integer> scores = 
          new ArrayList<Integer>(ingeniousState.scoresForPlayer(player).values());
      Collections.sort(scores);
      return (scores.get(0) * 1000000) + (scores.get(1) * 100000) + (scores.get(2) * 10000) +
          (scores.get(3) * 1000) + (scores.get(4) * 100) + (scores.get(5) * 10);
    }
    
    public String toString() {
      return "LowestScoreEvaluator";
    }
  }
  
  /**
   * Evaluator which evaluates Ingenious game states by comparing the player's
   * cumulative scores.
   */
  public static class CumulativeScoreEvaluator implements Evaluator {
    @Override
    public double evaluate(int player, State state) {
      IngeniousState ingeniousState = (IngeniousState)state;
      int result = 0;
      for (int score : ingeniousState.scoresForPlayer(player).values()) {
        result += score;
      }
      return result;
    }
    
    public String toString() {
      return "CumulativeScoreEvaluator";
    }
  }
  
  private List<Long> actions;
  // 11x11 board using the axial coordinate system
  private int[][] board;
  private int currentPlayer;
  private List<Integer> p1Hand;
  private List<Integer> p2Hand;
  private Map<Integer, Integer> p1Score;
  private Map<Integer, Integer> p2Score;
  private final Random random = new Random();
  
  /**
   * Null-initializing constructor.
   */
  public IngeniousState() {
  }

  private IngeniousState(List<Long> actions, int[][] board,
      int currentPlayer, List<Integer> p1Hand, List<Integer> p2Hand,
      Map<Integer, Integer> p1Score, Map<Integer, Integer> p2Score) {
    this.actions = actions;
    this.board = board;
    this.currentPlayer = currentPlayer;
    this.p1Hand = p1Hand;
    this.p2Hand = p2Hand;
    this.p1Score = p1Score;
    this.p2Score = p2Score;
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
    // Perform move
    board[IngeniousAction.getX1(action)][IngeniousAction.getY1(action)] = 
        IngeniousPiece.getHex1(IngeniousAction.getPiece(action));
    board[IngeniousAction.getX2(action)][IngeniousAction.getY2(action)] = 
        IngeniousPiece.getHex2(IngeniousAction.getPiece(action));
    
    // Score points
    modifyScoresForAction(action, true /* addToScore */);    
    
    // Update hand
    List<Integer> hand = handForPlayer(currentPlayer);
    hand.remove(new Integer(IngeniousAction.getPiece(action)));
    int newPiece = randomPiece();
    hand.add(newPiece);

    currentPlayer = playerAfter(currentPlayer);
    actions = allActions(handForPlayer(currentPlayer));
    return newPiece;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void undo(long action, long undoToken) {
    // Undo move
    board[IngeniousAction.getX1(action)][IngeniousAction.getY1(action)] = 0;
    board[IngeniousAction.getX2(action)][IngeniousAction.getY2(action)] = 0;
    
    // Un-score points
    modifyScoresForAction(action, false /* addToScore */);
    
    // Update hand
    List<Integer> hand = handForPlayer(currentPlayer);
    hand.remove(new Integer((int) undoToken));
    hand.add(IngeniousAction.getPiece(action));
    
    currentPlayer = playerBefore(currentPlayer);
    actions = allActions(handForPlayer(currentPlayer));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State setToStartingConditions() {
    this.board = new int[BOARD_SIZE][BOARD_SIZE];
    addInitialHexes(board);
    this.currentPlayer = Player.PLAYER_ONE;
    List<Integer> currentHand = randomHand();
    this.p1Hand = currentHand;
    this.p2Hand = randomHand();
    p1Score = new HashMap<Integer, Integer>();
    p2Score = new HashMap<Integer, Integer>();    
    for (int hex : IngeniousHex.allColors()) {
      p1Score.put(hex, 0);
      p2Score.put(hex, 0);
    }
    actions = allActions(currentHand);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State copy() {
    if (actions == null && board == null) {
      return new IngeniousState();
    }
    return new IngeniousState(new ArrayList<Long>(actions), copyBoard(),
        currentPlayer, new ArrayList<Integer>(p1Hand), new ArrayList<Integer>(p2Hand),
        new HashMap<Integer, Integer>(p1Score), new HashMap<Integer, Integer>(p2Score));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State initializeFrom(Copyable state) {
    IngeniousState copy = (IngeniousState)state.copy();
    this.actions = copy.actions;
    this.board = copy.board;
    this.currentPlayer = copy.currentPlayer;
    this.p1Hand = copy.p1Hand;
    this.p2Hand = copy.p2Hand;
    this.p1Score = copy.p1Score;
    this.p2Score = copy.p2Score;
    if (p1Hand.size() > 6 || p2Hand.size() > 6) {
      throw new RuntimeException();
    }    
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int playerAfter(int player) {
    return player == Player.PLAYER_ONE ? Player.PLAYER_TWO : Player.PLAYER_ONE;
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
  public boolean isTerminal() {
    return actions.size() == 0;
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
  public int getWinner() {
    List<Integer> p1 = new ArrayList<Integer>(p1Score.values());
    Collections.sort(p1);
    List<Integer> p2 = new ArrayList<Integer>(p2Score.values());
    Collections.sort(p2);
    
    for (int i = 0; i < 6; ++i) {
      if (p1.get(i) > p2.get(i)) {
        return Player.PLAYER_ONE;
      } else if (p2.get(i) > p1.get(i)) {
        return Player.PLAYER_TWO;
      }
    }
    
    return 0;
  }
  
  @Override
  public String actionToString(long action) {
    int piece = IngeniousAction.getPiece(action);
    StringBuilder result = new StringBuilder();
    result.append("[");
    result.append(IngeniousHex.toString(IngeniousPiece.getHex1(piece)));
    result.append(IngeniousHex.toString(IngeniousPiece.getHex2(piece)));
    result.append(" ");
    result.append(IngeniousAction.getX1(action));
    result.append(",");
    result.append(IngeniousAction.getY1(action));
    result.append(" ");
    result.append(IngeniousAction.getX2(action));
    result.append(",");
    result.append(IngeniousAction.getY2(action));
    result.append("]");
    return result.toString();
  }
  
  /**
   * @param player A player.
   * @return Score map for this player.
   */
  private Map<Integer, Integer> scoresForPlayer(int player) {
    return player == Player.PLAYER_ONE ? p1Score : p2Score;
  }
  
  /**
   * @param player A player.
   * @return Hand list for this player.
   */  
  private List<Integer> handForPlayer(int player) {
    return player == Player.PLAYER_ONE ? p1Hand : p2Hand;
  }
  
  /**
   * @param player Player whose hand to look in.
   * @param index Index of desired piece.
   * @return The piece at the indicating index in this player's hand.
   */
  int getPiece(int player, int index) {
    return handForPlayer(player).get(index);
  }
  
  /**
   * @param x X coordinate.
   * @param y Y coordinate.
   * @return True if there is no hex currently placed on the specified board
   *     location.
   */
  boolean isOpen(int x, int y) {
    return board[x][y] == 0;
  }

  /**
   * @param hand The current player's hand.
   * @return A list of all actions possible from the current game state with
   *     the provided hand.
   */
  private List<Long> allActions(List<Integer> hand) {
    List<Long> result = new ArrayList<Long>(300);
    for (int x = 0; x < BOARD_SIZE; ++x) {
      for (int y = 0; y < BOARD_SIZE; ++y) {
        if (board[x][y] == 0) {
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
  private void addActionsForPosition(List<Integer> hand, List<Long> list,
      int x, int y) {
    if (y - 1 > 0 && y - 1 < BOARD_SIZE && board[x][y - 1] == 0) {
      addActionsForMove(hand, list, x, y, x, y - 1);
    } else if (x + 1 > 0 && x + 1 < BOARD_SIZE && y - 1 > 0 && y - 1 < BOARD_SIZE &&
        board[x + 1][y - 1] == 0) {
      addActionsForMove(hand, list, x, y, x + 1, y - 1);
    } else if (x + 1 > 0 && x + 1 < BOARD_SIZE && board[x + 1][y] == 0) {
      addActionsForMove(hand, list, x, y, x + 1, y);
    } else if (y + 1 > 0 && y + 1 < BOARD_SIZE && board[x][y + 1] == 0) {
      addActionsForMove(hand, list, x, y, x, y + 1);
    } else if (x - 1 > 0 && x - 1 < BOARD_SIZE && y + 1 > 0 && y + 1 < BOARD_SIZE &&
        board[x - 1][y + 1] == 0) {
      addActionsForMove(hand, list, x, y, x - 1 , y + 1);
    } else if (x - 1 > 0 && x - 1 < BOARD_SIZE && board[x - 1][y] == 0) {
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
  private void addActionsForMove(List<Integer> hand, List<Long> list,
      int x1, int y1, int x2, int y2) {
    for (int i = 0; i < hand.size(); ++i) {
      list.add(IngeniousAction.create(hand.get(i), x1, y1, x2, y2));
    }
  }
  
  /**
   * @return A randomly generated hand of pieces.
   */
  private List<Integer> randomHand() {
    List<Integer> hand = new ArrayList<Integer>(HAND_SIZE);
    for (int i = 0; i < HAND_SIZE; ++i) {
      hand.add(randomPiece());
    }
    return hand;
  }
  
  /**
   * @return A randomly generated piece.
   */
  private int randomPiece() {
    return IngeniousPiece.create(IngeniousHex.randomHex(), IngeniousHex.randomHex());
  }
  
  /**
   * Add OFF_BOARD dummy hexes to the parts of the board that are off-limits.
   * This is necessary because you can't store a hexagonal board in a square
   * array. Also add the starting colored hexes.
   *
   * @param board The board.
   */
  private void addInitialHexes(int[][] board) {
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
  private int[][] copyBoard() {
    int[][] result = new int[BOARD_SIZE][BOARD_SIZE];
    for (int i = 0; i < BOARD_SIZE; ++i) {
      result[i] = Arrays.copyOf(board[i], BOARD_SIZE);
    }
    return result;
  }

  /**
   * Either scores points for the provided action or un-scores points for the
   * provided action.
   * 
   * @param action Action to score/un-score.
   * @param addToScore If true, add the points for this action to the player's
   *     score. If false, subtract the points from their score.
   */
  private void modifyScoresForAction(long action, boolean addToScore) {
    Map<Integer, Integer> myScores = scoresForPlayer(currentPlayer);
    int hex1 = IngeniousPiece.getHex1(IngeniousAction.getPiece(action));
    int hex2 = IngeniousPiece.getHex2(IngeniousAction.getPiece(action));
    Direction exclude1 = 
        hexDirection(IngeniousAction.getX1(action), IngeniousAction.getY1(action),
            IngeniousAction.getX2(action), IngeniousAction.getY2(action));
    Direction exclude2 =
        hexDirection(IngeniousAction.getX2(action), IngeniousAction.getY2(action),
            IngeniousAction.getX1(action), IngeniousAction.getY1(action));
    int scoreForHex1 = scoreForHex(IngeniousAction.getX1(action),
        IngeniousAction.getY1(action), hex1, exclude1);
    int scoreForHex2 = scoreForHex(IngeniousAction.getX2(action),
        IngeniousAction.getY2(action), hex2, exclude2);
    
    if (addToScore) {
      myScores.put(hex1, myScores.get(hex1) + scoreForHex1);
      myScores.put(hex2, myScores.get(hex2) + scoreForHex2);
    } else {
      myScores.put(hex1, myScores.get(hex1) - scoreForHex1);
      myScores.put(hex2, myScores.get(hex2) - scoreForHex2);      
    }
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
  private int scoreForHex(int x, int y, int hex, Direction excludeDirection) {
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
  private int countInDirection(int x, int y, int hex, Direction direction) {
    if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE || board[x][y] == 0 ||
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
    result.append("\nNum Actions: " + actions.size() + "\n");
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
      result.append("Player 1:");
      for (int hex : IngeniousHex.allColors()) {
        result.append(" " + IngeniousHex.toString(hex) + "=" + p1Score.get(hex));
      }
      result.append("\n");
      result.append("Player 2:");
      for (int hex : IngeniousHex.allColors()) {
        result.append(" " + IngeniousHex.toString(hex) + "=" + p2Score.get(hex));
      }
      result.append("\n");
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
        if (board[x][y] == 0) {
          if (x == 10) {
            result.append("[T" + y % 10 + "]");            
          } else if (y == 10) {
            result.append("[" + x + "T]");            
          } else {
            result.append("[" + x + "" + y + "]");
          }
          
        } else {
          if (board[x][y] != IngeniousHex.OFF_BOARD) {
            result.append("[" + IngeniousHex.toString(board[x][y]) + "]");
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
    List<Integer> hand = handForPlayer(currentPlayer);
    for (int i = 0; i < hand.size(); ++i) {
      result.append(" " + pieceNumber + ") " + IngeniousPiece.toString(hand.get(i)));
      pieceNumber++;
    }
  }
}
