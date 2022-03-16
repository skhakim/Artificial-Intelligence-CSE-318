import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class MancalaBoard {

    int additionalMoveEarned = 0, stonesCaptured = 0;
    private byte[] board = new byte[14];
    private byte player;
    MancalaBoard() {
        Arrays.fill(board, (byte) 4);
        board[0] = 0;
        board[7] = 0;
        player = Constants.TOP;
    }


    private MancalaBoard(byte player) {
        Arrays.fill(board, (byte) 4);
        board[0] = 0;
        board[7] = 0;
        this.player = player;
    }

    public static MancalaBoard createMancalaBoard(byte player) {
        return new MancalaBoard(player);
    }

    public byte getPlayer() {
        return player;
    }

    void resetAdditionalPoints() {
        additionalMoveEarned = 0;
        stonesCaptured = 0;
    }

    @Override
    protected MancalaBoard clone() {
        MancalaBoard newBoard = new MancalaBoard(player);
        for (int i = 0; i < 14; ++i)
            newBoard.board[i] = board[i];
        newBoard.additionalMoveEarned = additionalMoveEarned;
        newBoard.stonesCaptured = stonesCaptured;
        return newBoard;
    }

    void swapPlayer() {
        player = (byte) -player;
    }

    byte at(int i) {
        return board[i];
    }

    byte move(int j) {
        //System.out.println("Player: " + ((player == Constants.TOP) ? "TOP" : "BOTTOM") + " with j = " + j);
        if ((player == Constants.TOP && j > 7 && j < 14) || (player == Constants.BOTTOM && j > 0 && j < 7)) {
            int k = board[j];
            board[j] = 0;
            for (; k > 0; --k) {
                j = (j < 13) ? (j + 1) : 0;
                if (j == 7) {
                    if (player == Constants.BOTTOM) {
                        board[7]++;
                        if (k == 1) { // last stone in bottom player's pot
                            additionalMoveEarned |= 1; // treating it as boolean
                            return Constants.ANOTHER_MOVE;
                        }
                    } else {
                        k++;
                    }
                } else if (j == 0) {
                    if (player == Constants.TOP) {
                        board[0]++;
                        if (k == 1) { // last stone in top player's pot
                            additionalMoveEarned++;
                            return Constants.ANOTHER_MOVE;
                        }
                    } else {
                        k++;
                    }
                } else {
                    board[j]++;
                    if (board[j] == 1 && k == 1 && board[14 - j] != 0) { // capture
                        if (player == Constants.TOP) {
                            if (j > 7) {
                                board[0] += (byte) (board[j] + board[14 - j]);
                                stonesCaptured += (byte) (board[j] + board[14 - j]);
                                board[j] = 0;
                                board[14 - j] = 0;
                            }
                        } else {
                            if (j < 7) {
                                board[7] += (byte) (board[j] + board[14 - j]);
                                stonesCaptured += (byte) (board[j] + board[14 - j]);
                                board[j] = 0;
                                board[14 - j] = 0;
                            }
                        }
                    }
                }
            }
            return Constants.MOVE_COMPLETED;
        }
        assert (stonesInTopSide() + stonesInBottomSide() + board[0] + board[7] == 48);
        System.out.println("FAILURE:::: Player: " + ((player == Constants.TOP) ? "TOP" : "BOTTOM") + " with j = " + j);
        return Constants.FAILURE;
    }

    int stonesInTopSide() {
        int count = 0;
        for (int j = 1; j <= 6; ++j)
            count += board[j];
        return count;
    }

    int stonesInBottomSide() {
        int count = 0;
        for (int j = 8; j <= 13; ++j)
            count += board[j];
        return count;
    }

    int stonesInMySide() {
        return (player == Constants.TOP) ? stonesInTopSide() : stonesInBottomSide();
    }

    int stonesInOpponentSide() {
        return (player == Constants.BOTTOM) ? stonesInTopSide() : stonesInBottomSide();
    }

    short status() {
        if (stonesInTopSide() == 0 || stonesInBottomSide() == 0) {
            for (int j = 8; j <= 13; ++j) {
                board[7] += board[j];
                board[j] = 0;
            }
            for (int j = 1; j <= 6; ++j) {
                board[0] += board[j];
                board[j] = 0;
            }
            return (board[7] >= board[0]) ? Constants.BOTTOM : Constants.TOP;
        }
        return Constants.NO_RESULT_YET;
    }

    int maxOfTopSide() {
        int count = -0x3f3f3f3f;
        for (int j = 1; j <= 6; ++j)
            count = Math.max(count, board[j]);
        return count;
    }

    int maxOfBottomSide() {
        int count = -0x3f3f3f3f;
        for (int j = 8; j <= 13; ++j)
            count = Math.max(count, board[j]);
        return count;
    }

    int stonesCloseToTopStorage() {
        int count = 0;
        for (int j = 1; j <= 6; ++j)
            count += Math.min(board[j], 7 - j);
        return count;
    }

    int stonesCloseToBottomStorage() {
        int count = 0;
        for (int j = 8; j <= 13; ++j)
            count += Math.min(board[j], 14 - j);
        return count;
    }

    int heuristic1() {
        // assumption: top is max, regardless of who plays first
        return (board[0] - board[7]);
    }

    int heuristic2() {
        //check if it can be configured
        return 10 * (board[0] - board[7]) + (stonesInTopSide() - stonesInBottomSide());
    }

    int heuristic3() {
        return 10 * (board[0] - board[7]) + (stonesInTopSide() - stonesInBottomSide()) + 2 * additionalMoveEarned;
    }

    int heuristic4() {
        if (board[0] > 24)
            return 0x3f3f3f3f;
        else if (board[7] > 24)
            return -0x3f3f3f3f;
        else
            return board[0] - board[7];
    }

    int heuristic5() {
        return 5 * (board[0] - board[7]) + 3 * stonesCaptured + 2 * additionalMoveEarned
                                + 4 * (stonesCloseToTopStorage() - stonesCloseToBottomStorage());
    }

    int heuristic6() {
        return 3 * (board[0] - board[7]) + 3 * stonesCaptured + (maxOfTopSide() - maxOfBottomSide());
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write("Current Player: " + ((player == 10) ? "TOP\n" : "BOTTOM\n"));
        writer.write("/****************************************************************/\n");
        writer.write(String.format("%2d[%2d]\t", 14, board[0]));
        for (int j = 13; j > 7; --j)
            writer.write(String.format("%2d[%2d]\t", j, board[j]));
        writer.write("\n  \t  \t");
        for (int j = 1; j <= 7; ++j)
            writer.write(String.format("%2d[%2d]\t", j, board[j]));
        writer.write("\n/****************************************************************/\n");
        return writer.toString();
    }
}
