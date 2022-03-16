import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class Futoshiki {

    public static void main(String[] args) {


        Model model = new Model("My Sudoku!");
        IntVar[] list = new IntVar[9];

        IntVar[][] board_rm = model.intVarMatrix(9, 9, 1, 9, false);


        for (int i = 0; i < 9; ++i) {
            model.allDifferent(board_rm[i]).post();
        }

        for (int j = 0; j < 9; ++j) {
            list = new IntVar[9];
            for (int i = 0; i < 9; ++i) {
                list[i] = board_rm[i][j];
            }
            model.allDifferent(list).post();
        }

        model.post(
                model.arithm(board_rm[0][1], "=", 3),
                model.arithm(board_rm[0][8], "=", 5),
                model.arithm(board_rm[1][3], "=", 4),
                model.arithm(board_rm[1][5], "=", 1),
                model.arithm(board_rm[2][7], "=", 6),
                model.arithm(board_rm[3][2], "=", 6),
                model.arithm(board_rm[4][2], "=", 3),
                model.arithm(board_rm[4][5], "=", 4),
                model.arithm(board_rm[4][7], "=", 7),
                model.arithm(board_rm[6][4], "=", 2),
                model.arithm(board_rm[6][5], "=", 3),
                model.arithm(board_rm[6][8], "=", 8),
                model.arithm(board_rm[8][6], "=", 4)
        );



        model.post(
                model.arithm(board_rm[0][3], ">", board_rm[0][4]),

                model.arithm(board_rm[1][7], ">", board_rm[1][6]),
                model.arithm(board_rm[1][7], ">", board_rm[1][8]),

                model.arithm(board_rm[2][1], ">", board_rm[2][0]),

                model.arithm(board_rm[3][2], ">", board_rm[3][1]),
                model.arithm(board_rm[3][3], ">", board_rm[3][2]),
                model.arithm(board_rm[3][3], ">", board_rm[3][4]),
                model.arithm(board_rm[3][7], ">", board_rm[3][8]),

                model.arithm(board_rm[4][2], ">", board_rm[4][3]),
                model.arithm(board_rm[4][7], ">", board_rm[4][8]),

                model.arithm(board_rm[5][0], ">", board_rm[5][1]),
                model.arithm(board_rm[5][3], ">", board_rm[5][2]),

                model.arithm(board_rm[6][8], ">", board_rm[6][7]),

                model.arithm(board_rm[7][1], ">", board_rm[7][0]),
                model.arithm(board_rm[7][7], ">", board_rm[7][6]),

                model.arithm(board_rm[8][6], ">", board_rm[8][5])

                //model.arithm(board_rm[][],">", board_rm[][]),
        );

        model.post(
                model.arithm(board_rm[0][1], ">", board_rm[1][1]),

                model.arithm(board_rm[0][2], ">", board_rm[1][2]),
                model.arithm(board_rm[5][2], ">", board_rm[6][2]),
                model.arithm(board_rm[8][2], ">", board_rm[7][2]),

                model.arithm(board_rm[7][3], ">", board_rm[6][3]),

                model.arithm(board_rm[1][6], ">", board_rm[2][6]),
                model.arithm(board_rm[2][6], ">", board_rm[3][6]),
                model.arithm(board_rm[4][6], ">", board_rm[5][6]),
                model.arithm(board_rm[7][6], ">", board_rm[6][6]),

                model.arithm(board_rm[6][7], ">", board_rm[7][7]),
                model.arithm(board_rm[8][7], ">", board_rm[7][7]),

                model.arithm(board_rm[6][8], ">", board_rm[7][8]),
                model.arithm(board_rm[7][8], ">", board_rm[8][8])
        );




        Solver solver = model.getSolver();

        solver.showStatistics();
        solver.showSolutions();
        solver.findSolution();


// 5. Print the solution

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                System.out.print(" ");
                /* get the value for the board position [i][j] for the solved board */
                int k = board_rm[i][j].getValue();
                System.out.print(k);

                if (j % 3 == 2) {
                    System.out.print(" |");
                }
            }
            System.out.println();
            if (i % 3 == 2) {
                System.out.println("========================");
            }
        }
    }

}
