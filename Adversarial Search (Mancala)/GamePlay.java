import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

public class GamePlay {

    static int successor = -1, heuristicTop = 1, heuristicBottom = 2, depth = 7;
    static List<Integer> bottomMoves = Arrays.asList(1, 2, 3, 4, 5, 6);
    static List<Integer> topMoves = Arrays.asList(8, 9, 10, 11, 12, 13);
    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random(System.currentTimeMillis());
    static boolean toShuffleMoves = false;


    public static void main(String[] args) throws IOException {

        MancalaBoard board = new MancalaBoard();
        int moves = 0;
        PrintWriter writer = new PrintWriter(new File("output.csv"));

        if (args.length == 3) {
            heuristicTop = Integer.parseInt(args[0]);
            heuristicBottom = Integer.parseInt(args[1]);
            depth = Integer.parseInt(args[2]);
        } else {
            System.out.print("Top [human (-1)/heuristic (1-6)]: ");
            heuristicTop = scanner.nextInt();
            System.out.print("Bottom [human (-1)/heuristic (1-6)]: ");
            heuristicBottom = scanner.nextInt();
        }
        if (random.nextBoolean()) {
            System.out.println("Top moves first.");
            board.swapPlayer();
        } else {
            System.out.println("Bottom moves first.");
        }


        while (true) {
            board.swapPlayer();
            ++moves;
            System.out.println(board);
            switch (board.status()) {
                case Constants.TOP:
                    System.out.println("Top wins with heuristic = " + heuristicTop + "!");
                    System.out.println("Total moves: " + moves);
                    System.out.println(board);
                    writer.println(heuristicTop + ", " + heuristicBottom + ", " + heuristicTop + ", " + moves);
                    writer.flush();
                    return;
                case Constants.BOTTOM:
                    System.out.println("Bottom wins with heuristic = " + heuristicBottom + "!");
                    System.out.println("Total moves: " + moves);
                    System.out.println(board);
                    writer.println(heuristicTop + ", " + heuristicBottom + ", " + heuristicBottom + ", " + moves);
                    writer.flush();
                    return;
            }
            selectMove(board);
            board.resetAdditionalPoints();
        }
    }

    /*
    public static void main(String[] args) throws IOException {

        MancalaBoard board = new MancalaBoard();
        int moves = 0;
        PrintWriter writer = new PrintWriter(new File("output.csv"));
        PrintWriter writer2 = new PrintWriter(new File("output2.csv"));

        for (heuristicTop = 1; heuristicTop < 6; ++heuristicTop) {
            System.setOut(new PrintStream("output"));
            for (heuristicBottom = heuristicTop + 1; heuristicBottom <= 6; ++heuristicBottom) {
                int topWin = 0, bottomWin = 0;
                for (int depth = 3; depth < 9; ++depth) {
                    for (int l = 0; l < 17; ++l) {

                        board = new MancalaBoard();
                        moves = 0;
                        if (random.nextBoolean()) {
                            System.out.println("Top moves first.");
                            board.swapPlayer();
                        } else {
                            System.out.println("Bottom moves first.");
                        }

                        boolean flag = true;
                        while (flag) {
                            board.swapPlayer();
                            ++moves;
                            System.out.println(board);
                            switch (board.status()) {
                                case Constants.TOP:
                                    ++topWin;
                                    System.out.println("Top wins with heuristic = " + heuristicTop + "!");
                                    System.out.println("Total moves: " + moves);
                                    System.out.println(board);
                                    writer.println(heuristicTop + ", " + heuristicBottom + ", " + heuristicTop + ", " + moves);
                                    //writer.flush();
                                    flag = false;
                                    break;
                                    //return;
                                case Constants.BOTTOM:
                                    ++bottomWin;
                                    System.out.println("Bottom wins with heuristic = " + heuristicBottom + "!");
                                    System.out.println("Total moves: " + moves);
                                    System.out.println(board);
                                    writer.println(heuristicTop + ", " + heuristicBottom + ", " + heuristicBottom + ", " + moves);
                                    writer.flush();
                                    flag = false;
                                    break;
                                    //return;
                            }
                            selectMove(board);
                            board.resetAdditionalPoints();
                        }
                    }
                }
                writer2.println(heuristicTop + ", " + heuristicBottom + ", " + topWin + ", " + bottomWin);
                writer2.flush();
                writer.flush();
            }
        }
    }
    */
    static int evaluate(MancalaBoard board, int choice) {
        switch (choice) {
            case 1:
                return board.heuristic1();
            case 2:
                return board.heuristic2();
            case 3:
                return board.heuristic3();
            case 4:
                return board.heuristic4();
            case 5:
                return board.heuristic5();
            default:
                return board.heuristic6();
        }
    }

    static void selectMove(MancalaBoard board) {
        /***
         * choice: -1 -> player, [1,6] -> heuristic
         */
        int choice = (board.getPlayer() == Constants.TOP) ? heuristicTop : heuristicBottom;
        switch (choice) {
            case -1:
                System.out.print("Bin no: ");
                while (true) {
                    if (scanner.hasNextInt()) {
                        if (board.move(scanner.nextInt()) != Constants.ANOTHER_MOVE)
                            break;
                        else
                            System.out.println(board);
                    }
                }
                break;
            default:
                while (true) {
                    Pair p = alphaBetaSearch(board, depth, -0x3f3f3f3f
                            , 0x3f3f3f3f, choice);
                    System.out.println("AI's move: " + p.successor + " with player: " + ((board.getPlayer() == 10) ? "TOP\n" : "BOTTOM\n"));
                    if (board.move(p.successor) != Constants.ANOTHER_MOVE)
                        break;
                    else
                        System.out.println(board);
                }
        }

    }

    static Pair alphaBetaSearch(MancalaBoard board, int depth, int alpha, int beta, int heuristic) {

        Pair p = new Pair();
        // cut-off test
        if (depth < 0 || board.status() != Constants.NO_RESULT_YET) {
            return new Pair(evaluate(board, heuristic), -2);
        }

        int current = 0;

        /***
         * an assumption is that MAX is the top player and MIN is the bottom player
         */

        if (board.getPlayer() == Constants.TOP) {
            p.optimal = -0x3f3f3f3f;
            Collections.shuffle(bottomMoves);
            for (int i : topMoves) {
                if (board.at(i) != 0) { // no move from empty
                    MancalaBoard resBoard = board.clone();
                    switch (resBoard.move(i)) {
                        case Constants.ANOTHER_MOVE:
                            current = alphaBetaSearch(resBoard, depth, alpha, beta, heuristic).optimal;
                            break;
                        case Constants.MOVE_COMPLETED:
                            resBoard.swapPlayer();
                            current = alphaBetaSearch(resBoard, depth - 1, alpha, beta, heuristic).optimal;
                            resBoard.swapPlayer(); //TODO: reversing the player to previous stage
                    }
                    if (current >= p.optimal) {
                        p.optimal = current;
                        alpha = Math.max(alpha, p.optimal);
                        p.successor = i;
                    }
                }
                if (alpha >= beta)
                    break;
            }
        } else {
            p.optimal = 0x3f3f3f3f;
            Collections.shuffle(bottomMoves);
            for (int i : bottomMoves) {
                if (board.at(i) != 0) { // no move from empty
                    MancalaBoard resBoard = board.clone();
                    switch (resBoard.move(i)) {
                        case Constants.ANOTHER_MOVE:
                            current = alphaBetaSearch(resBoard, depth, alpha, beta, heuristic).optimal;
                            break;
                        case Constants.MOVE_COMPLETED:
                            resBoard.swapPlayer();
                            current = alphaBetaSearch(resBoard, depth - 1, alpha, beta, heuristic).optimal;
                            resBoard.swapPlayer(); //TODO: reversing the player to previous stage
                    }
                    if (current <= p.optimal) {
                        p.optimal = current;
                        beta = Math.min(p.optimal, beta);
                        p.successor = i;
                    }
                }
                if (alpha >= beta)
                    break;
            }
        }
        return p;
    }
}

class Pair {
    int optimal;
    int successor;

    public Pair() {
        successor = -701;
    }

    public Pair(int optimal, int successor) {
        this.optimal = optimal;
        this.successor = successor;
    }
}