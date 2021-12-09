import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.ToIntFunction;

public class State2D implements Comparable<State2D> {
    short[][] array;
    //short size;
    int key = Integer.MAX_VALUE, moves = 0;
    State2D parent;
    static ToIntFunction<State2D> function = (x) -> lc(x.array);

    State2D(short[][] grid) {
        array = grid;
    }

    State2D(short[][] grid, char heuristic) {
        array = grid;

        switch (heuristic) {
            case 'h':
                function = (x) -> hamming(x.array);
                break;
            case 'm':
                function = (x) -> manhattan(x.array);
                break;
            default:
                function = (x) -> lc(x.array);
        }
    }

    private State2D(short[][] grid, State2D par) {
        array = grid.clone();
        parent = par;
        //function = par.function;
        moves = par.moves + 1;
        /*if(moves%10 == 0){
            System.out.println("Total memory (bytes): " +
                    Runtime.getRuntime().totalMemory());
        }*/
        key = moves + function.applyAsInt(this);
        if(isGoalState()) {
            System.out.println("A goal state with moves = " + moves + " with key  = " + key);
        }
    }

    public static short[][] copyOf(short[][] array) {
        int k = array.length;
        short[][] p = new short[k][k];
        for (int i = 0; i < k; ++i) {
            for (int j = 0; j < k; ++j)
                p[i][j] = array[i][j];
        }
        return p;
    }

    public static State2D getState2D(short[][] grid, char heuristic) {
        State2D state2D = new State2D(grid, heuristic);
        if (!state2D.isSolvable()) {
            //System.out.println("This board is not solvable.");
            return null;
        }
        return state2D;
    }

    static int diff(int a, int b) {
        return (a > b) ? a - b : b - a;
    }

    static int hamming(short[][] array) {
        int hammingDistance = 0, k = array.length;
        for (int i = 0, pos = 1; i < k; ++i) {
            for (int j = 0; j < k && pos < k * k; ++j, ++pos) {
                if (array[i][j] != pos)
                    ++hammingDistance;
            }
        }
        return hammingDistance;
    }

    static int manhattan(short[][] array) {
        int manhattanDistance = 0, k = array.length;
        for (int i = 0, pos = 0; i < k; ++i) {
            for (int j = 0; j < k && pos < k * k; ++j, ++pos) {
                int there = array[i][j] - 1;
                if (there == -1)
                    continue;
                manhattanDistance += diff(i, there / k);
                manhattanDistance += diff(j, there % k);
            }
        }
        return manhattanDistance;
    }

    /*
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Size: ");
                short size = Short.parseShort(scanner.nextLine());
                //scanner.nex
                short[][] grid = new short[size][size];
                String s;
                for (int i = 0; i < size; ++i) {
                    for (int j = 0; j < size; ++j) {
                        s = scanner.next();
                        grid[i][j] = (s.equals("*")) ? 0 : Short.valueOf(s);
                    }
                    scanner.nextLine();
                }
                State2D state2D = getState2D(grid);
                System.out.println("Done");
            }
        }
    }
*/

    static int lc(short[][] array) {
        int linearConflict = 0, k = array.length;
        for (int i = 0; i < k; ++i) {
            for (int j = 0; j < k; ++j) {
                int ti = (array[i][j] - 1) / k;
                if (ti != i || array[i][j] == 0)
                    continue;
                for (int l = j + 1; l < k; ++l) {
                    int li = (array[i][l] - 1) / k;
                    if (li != i || array[i][l] == 0)
                        continue;
                    if (array[i][l] < array[i][j])
                        linearConflict++;
                }
            }
        }
        for (int j = 0; j < k; ++j) {
            for (int i = 0; i < k; ++i) {
                int tj = (array[i][j] - 1) % k;
                if (tj != j || array[i][j] == 0)
                    continue;
                for (int l = i + 1; l < k; ++l) {
                    int lj = (array[l][j] - 1) % k;
                    if (lj != j || array[l][j] == 0)
                        continue;
                    if (array[l][j] < array[i][j])
                        linearConflict++;
                }
            }
        }
        linearConflict = manhattan(array) + 2 * linearConflict;
        return linearConflict;
    }

    static boolean isSolvable(short[][] array) {
        int k = array.length, zx = 0, inv = 0;
        //System.out.println("It came here with k = " + k);
        for (int i = 0; i < k; ++i) {
            for (int j = 0; j < k; ++j) {
                if (array[i][j] == 0) {
                    zx = i;
                }
            }
        }
        for (int i = 0; i < k; ++i) {
            for (int j = 0; j < k; ++j) {
                if (array[i][j] == 0)
                    continue;
                for (int _i = i; _i < k; ++_i) {
                    for (int _j = (_i == i) ? (j + 1) : 0; _j < k; ++_j) {
                        if (array[_i][_j] != 0 && array[_i][_j] < array[i][j])
                            ++inv;
                    }
                }
            }
        }
        //System.out.println("Inv: " + inv);
        if (k % 2 == 1)
            return inv % 2 == 0;
        return (inv + (k - zx)) % 2 == 1;
    }

    public void printGrid() {
        System.out.println("Moves: " + moves);
        System.out.println("/************************/");
        for (int i = 0, k = array.length; i < k; ++i) {
            for (int j = 0; j < k; ++j) {
                System.out.printf("%4d ", array[i][j]);
            }
            System.out.println();
        }
        System.out.println("/************************/");
    }

    public void printPath() {
        if (parent != null)
            parent.printPath();
        printGrid();
    }

    boolean isGoalState() {
        return hamming() == 0;
    }

    public Collection<State2D> getAdjacentStates() {
        int k = array.length, zx = -1, zy = -1;
        short temp;
        for (int i = 0; i < k; ++i) {
            for (int j = 0; j < k; ++j) {
                if (array[i][j] == 0) {
                    zx = i;
                    zy = j;
                }
            }
        }
        List<State2D> list = new LinkedList<>();
        //blank square up
        if (zx != k - 1) {
            short[][] grid = copyOf(array);
            grid[zx + 1][zy] = 0;
            grid[zx][zy] = array[zx + 1][zy];
            list.add(new State2D(grid, this));
        }
        //blank square down
        if (zx != 0) {
            short[][] grid = copyOf(array);
            grid[zx - 1][zy] = 0;
            grid[zx][zy] = array[zx - 1][zy];
            list.add(new State2D(grid, this));
        }
        //blank square left
        if (zy != k - 1) {
            short[][] grid = copyOf(array);
            grid[zx][zy + 1] = 0;
            grid[zx][zy] = array[zx][zy + 1];
            list.add(new State2D(grid, this));
        }
        //blank square down
        if (zy != 0) {
            short[][] grid = copyOf(array);
            grid[zx][zy - 1] = 0;
            grid[zx][zy] = array[zx][zy - 1];
            list.add(new State2D(grid, this));
        }
        return list;
    }

    boolean isSolvable() {
        return isSolvable(array);
    }


    @Override
    public int hashCode() {

        //return array.hashCode();
        return Arrays.deepHashCode(array);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof State2D) {
            short[][] v = ((State2D) obj).array;
            for (int i = 0; i < array.length; ++i) {
                for (int j = 0; j < array.length; ++j) {
                    if (array[i][j] != v[i][j])
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(State2D o) {
        return Integer.compare(key, o.key);
    }

    int getSize() {
        return array.length;
    }

    int hamming() {
        return hamming(array);
    }

    int manhattan() {
        return manhattan(array);
    }

    int lc() {
        return lc(array);
    }

}
