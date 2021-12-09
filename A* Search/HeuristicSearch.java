import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class HeuristicSearch{

    Queue<State2D> queue = new PriorityQueue<>(State2D::compareTo);
    HashSet<State2D> closedSet = new HashSet<>();
    HashMap<State2D, Integer> openSet = new HashMap<>();
    State2D startState;
    int noExplored, noExpanded, non=0;
    public HeuristicSearch(@NotNull State2D startState) {
        this.startState = startState;
        noExpanded = 0;
        noExplored = 0;
    }

    public static void main(String[] args) throws IOException {
        File file = new File("log.txt");
        //System.setOut(new PrintStream(file));
        try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Grid size: ");
                short size = Short.parseShort(scanner.nextLine());
                //scanner.nex
                short[][] grid = new short[size][size];
                String s;
                System.out.println("Start configuration: ");
                for (int i = 0; i < size; ++i) {
                    for (int j = 0; j < size; ++j) {
                        s = scanner.next();
                        grid[i][j] = (s.equals("*")) ? 0 : Short.valueOf(s);
                    }
                    scanner.nextLine();
                }
                for(String c : new String[]{"linear conflict", "manhattan", "hamming"}) {
                    System.out.println(c);
                    State2D state2D = State2D.getState2D(grid, c.charAt(0));
                    if (state2D != null) {
                        System.out.println("The board is solvable.");
                        HeuristicSearch hs = new HeuristicSearch(state2D);
                        State2D goal = hs.run();
                        System.out.println("Required number of moves: " + goal.moves);
                        System.out.println("Number of explored nodes: " + hs.noExplored);
                        System.out.println("Number of expanded nodes: " + hs.noExpanded);

                        goal.printPath();
                    } else {
                        System.out.println("The board is not solvable.");
                    }
                    System.out.println();
                }
        } catch (OutOfMemoryError e) {
            System.out.println(Runtime.getRuntime().totalMemory());
        }
    }

    public void setStartState(@NotNull State2D startState) {
        this.startState = startState;
    }

    public State2D run() {
        queue.clear();
        closedSet.clear();
        //openSet.clear();
        queue.offer(startState);

        State2D u;
        int dist=0;

        while (!queue.isEmpty()) {
            u = queue.poll();
            if(u.isGoalState())
                return u;
            if(!closedSet.contains(u)) {
                ++noExpanded;
                closedSet.add(u);
                Collection<State2D> uN = u.getAdjacentStates();
                for(State2D v:uN){
                    if(closedSet.contains(v)) {
                        ++non;
                        continue;
                    }
                    int val = openSet.getOrDefault(v, -5);
                    if(val == -5){
                        // not in openSet
                        queue.add(v);
                        openSet.put(v, v.key);
                        ++noExplored;
                    } else {
                        // in OpenSet
                        if(val > v.key) {
                            queue.add(v);
                            openSet.put(v, v.key);
                        }
                    }
                }
            }
        }
        return null;
    }

    /*public static void main(String[] args) throws IOException {
        File file = new File("log.txt");
        long time_1 = System.currentTimeMillis();
        System.setOut(new PrintStream(file));
        try (Scanner scanner = new Scanner(System.in)) {
            //while (true) {
                //System.out.println("Size: ");
                short iCases = Short.parseShort(scanner.nextLine());
                short size = 3;
                scanner.nextLine();
                //scanner.nex
                short[][] grid = new short[size][size];
                String s;
                for(int cs=1; cs<=iCases; ++cs) {
                    for (int i = 0; i < size; ++i) {
                        for (int j = 0; j < size; ++j) {
                            s = scanner.next();
                            grid[i][j] = (s.equals("*")) ? 0 : Short.valueOf(s);
                        }
                        scanner.nextLine();
                    }
                    if(cs!=iCases)
                        scanner.nextLine();
                    //for(char c : new char[]{'l', 'm', 'h'}) {
                    State2D state2D = State2D.getState2D(grid, 'l');
                    // System.out.println("Done");
                    if (state2D != null) {
                        HeuristicSearch hs = new HeuristicSearch(state2D);
                        State2D goal = hs.run();
                        System.out.println("Case " + cs + ": " + goal.moves);
                    }
                    else
                        System.out.println("Case " + cs + ": impossible");
                    //System.out.println();
                    //}
                //}
            }
        } catch (OutOfMemoryError e) {
            System.out.println(Runtime.getRuntime().totalMemory());
        }
        long time_2 = System.currentTimeMillis();
        //System.setOut(null);
        System.out.println(time_2-time_1);
    }*/
}
