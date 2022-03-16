import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Casper {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_L3 = "\u001B[101m";
    public static final String ANSI_L2 = "\u001B[103m";
    public static final String ANSI_L1 = "\u001B[107m";
    public static final String ANSI_BLACK = "\u001B[40m";
    public static final String ANSI_PURPLE = "\u001B[35m";


    private final double edgeProbability = 0.9;
    private final double sensorAccuracy = 0.85;

    private final int m;
    private final int n;
    HashSet<Integer> obstacles = new HashSet<>();
    private int k;
    private double[] prob;
    private double[] sensor;
    private double[][] trans;

    Casper(int _n, int _m) {
        n = _n; 
        m = _m; 
        k = 0;
        prob = new double[m * n];
        sensor = new double[prob.length];
        trans = new double[prob.length][prob.length];
    }

    public static double sum(double[] arr) {
        double res = 0;
        for(int p = 0; p < arr.length; ++p)
            res += arr[p];
        return res;
    }

    void checkPrint() {
        System.out.println("Total prob = " + sum(prob) + " " + sum(trans[0]) + " " + sum(trans[n])
                + " " + sum(trans[m]) + " " + sum(trans[prob.length - 1]));
        for(int i = 0; i < prob.length; ++i) {
            double p = Math.abs(1 - sum(trans[i]));
            if(p > 0.01)
                System.out.println("ERROR at " + i/m + " " + i%m + " " + p);
        }
    }

    public static void main(String[] args) throws Exception{

        System.setOut(new PrintStream("out02.txt"));
        try (Scanner scanner = new Scanner(System.in)) {
            int n, m, k, c = 0;
            String[] prompt;
            n = scanner.nextInt();
            m = scanner.nextInt();
            k = scanner.nextInt();

            Casper casper = new Casper(n, m);

            for (int j = 0; j < k; ++ j) {
                n = scanner.nextInt();
                m = scanner.nextInt();
                casper.addObstacle(n, m);
            }

            casper.setInitialProbability();
            System.out.println("Initial probability:");
            casper.printProbabilities();

            while (true) {
                prompt = scanner.nextLine().split("[ \t]+");
                if(prompt.length == 0)
                    continue;
                switch (prompt[0]) {
                    case "Q":
                        System.out.println("Goodbye Casper!");
                        return;
                    case "C":
                        casper.printProbableLocation();
                        break;
                    case "R":
                        n = Integer.parseInt(prompt[1]);
                        m = Integer.parseInt(prompt[2]);
                        k = Integer.parseInt(prompt[3]);
                        casper.forwardPass(n, m, k != 0);
                        ++c;
                        System.out.println("Probability Update (Reading " + c + "):");
                        casper.printProbabilities();
                        break;
                    case "D":
                        casper.checkPrint();
                }
            }
        }
    }

    void printProbabilities() {

        double[] probClone = Arrays.copyOf(prob, prob.length);
        Arrays.sort(probClone);
        double q0 = probClone[prob.length / 4];
        double q1 = probClone[prob.length / 2];
        double q2 = probClone[(prob.length / 4) * 3];

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                if (prob[i * m + j] == 0)
                    System.out.print(ANSI_BLACK + ANSI_PURPLE + String.format("%.5f", prob[i * m + j]) + ANSI_RESET + " ");
                else if (prob[i * m + j] <= q0)
                    System.out.print(String.format("%.5f", prob[i * m + j]) + " ");
                else if (prob[i * m + j] <= q1)
                    System.out.print(ANSI_L1 + String.format("%.5f", prob[i * m + j]) + ANSI_RESET + " ");
                else if (prob[i * m + j] <= q2)
                    System.out.print(ANSI_L2 + String.format("%.5f", prob[i * m + j]) + ANSI_RESET + " ");
                else
                    System.out.print(ANSI_L3 + String.format("%.5f", prob[i * m + j]) + ANSI_RESET + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    void printProbableLocation() {

        int optP = 0;
        double opt = 0;

        for (int i = 0; i < prob.length; ++i) {
            if (prob[i] >= opt) {
                opt = prob[i];
                optP = i;
            }
        }

        System.out.println("Casper is most probably at (" + optP / m + ", " + optP % m + ")\n");
    }


    void addObstacle(int x, int y) {
        obstacles.add(m * x + y);
        ++k;
    }

    void setInitialProbability() {
        double x = 1.0 / (m * n - k);
        for (int i = 0; i < prob.length; ++i) {
            prob[i] = obstacles.contains(i) ? 0 : x;
        }
    }

    boolean isValidCell(int x, int y) {
        return (x >= 0) && (x < n) && (y >= 0) && (y < m) && !obstacles.contains(m * x + y);
    }

    void setTransitionModel() {
        int[] validEdgeMoves = new int[prob.length];
        Arrays.fill(validEdgeMoves, 0);
        int[] validCornerMoves = new int[prob.length];
        Arrays.fill(validCornerMoves, 0);

        int[] incsEX = {1, -1, 0, 0}, incsEY = {0, 0, 1, -1};
        int[] incsCX = {0, 1, 1, -1, -1}, incsCY = {0, 1, -1, 1, -1};
        int x, y, i;
        
        for(int _x = 0; _x < n; ++ _x) {
            for (int _y = 0; _y < m; ++_y) {
                i = _x * m + _y;
                
                for (int c = 0; c < incsEY.length; ++c) {
                    x = _x + incsEX[c];
                    y = _y + incsEY[c];

                    if (isValidCell(x, y))
                        ++validEdgeMoves[i];
                }
                for (int c = 0; c < incsCY.length; ++c) {
                    x = _x + incsCX[c];
                    y = _y + incsCY[c];

                    if (isValidCell(x, y))
                        ++validCornerMoves[i];
                }
            }
        }

        for(int _x = 0; _x < n; ++ _x) {
            for (int _y = 0; _y < m; ++_y) {
                i = _x * m + _y;
                Arrays.fill(trans[i], 0);
                
                double edgeProb = edgeProbability;
                
                if(validEdgeMoves[i] == 0) {
                    edgeProb = 0; 
                } else {
                    updateTransitionModel(validEdgeMoves[i], incsEX, incsEY, i, _x, _y, edgeProb);
                }
                
                double cornerProb = 1 - edgeProb;

                updateTransitionModel(validCornerMoves[i], incsCX, incsCY, i, _x, _y, cornerProb);
            }
        }
    }

    private void updateTransitionModel(int validMoves, int[] incsEX, int[] incsEY, int curState, int _x, int _y, double cumProb) {
        int x;
        int y;
        for (int c = 0; c < incsEY.length; ++c) {
            x = _x + incsEX[c];
            y = _y + incsEY[c];

            if (isValidCell(x, y)) {
                trans[curState][x * m + y] = cumProb / validMoves;
            }
        }
    }

    void setSensorModel(int x, int y, boolean isPresent) {

        double f1, f2;
        if(!isPresent) {
            f1 = sensorAccuracy;
            f2 = 1 - f1;
        } else {
            f2 = sensorAccuracy;
            f1 = 1 - f2;
        }

        Arrays.fill(sensor, f1);
        int no_f2 = 0;

        for(int _x = x-1, i; _x <= x+1; ++_x) {
            for(int _y = y-1; _y <= y+1; ++_y) {
                if(_x < 0 || _x >= n || _y < 0 || _y >= m)
                    continue;

                i = _x * m + _y;
                sensor[i] = f2;
                no_f2++;
            }
        }

        /*Arrays.fill(sensor, f1/(n*m - no_f2));
        f2 /= no_f2;

        for(int _x = x-1, i; _x <= x+1; ++_x) {
            for(int _y = y-1; _y <= y+1; ++_y) {
                if(_x < 0 || _x >= n || _y < 0 || _y >= m)
                    continue;

                i = _x * m + _y;
                sensor[i] = f2;
            }
        }

        System.out.println(sum(sensor));*/
    }

    void forwardPass(int x, int y, boolean isPresent) {
        setTransitionModel();
        setSensorModel(x, y, isPresent);

        double[] f = new double[prob.length];
        double tot_f = 0;
        Arrays.fill(f, 0);

        for(int i = 0; i < prob.length; ++ i) {
            for (int j = 0; j < prob.length; ++j) {
                f[i] += prob[j] * trans[j][i];
            }
            f[i] *= sensor[i];
            tot_f += f[i];
        }

        for(int i = 0; i < prob.length; ++ i) {
            prob[i] = f[i] / tot_f;
        }
    }
}
