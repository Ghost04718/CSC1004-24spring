public class Board {

    private int[][] board;

    private int size;

    private int player1 = 1;
    private int player2 = -1;

    public Board(int size) {
        board = new int[size][size];
        this.size = size;
        clearBoard();
    }

    public void clearBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 0;
            }
        }
    }

    private boolean onBoard(int col, int row) {
        return 0 <= col && col < size && 0 <= row && row < size;
    }

    public boolean validPoistion(int col, int row) {
        if (!onBoard(col, row)) {
            return false;
        }
        return board[col][row] == 0;
    }

    public boolean move(int player, int col, int row) {
        if (player == player1) {
            board[col][row] = 1;
        } else if (player == player2) {
            board[col][row] = -1;
        }
        return true;
    }

    public boolean gameOver(int col, int row) {
        if (gameOverHelper(col, row, 1, 0) || gameOverHelper(col, row, -1, 0) || gameOverHelper(col, row, 0, 1) || gameOverHelper(col, row, 0, -1) || gameOverHelper(col, row, 1, 1) || gameOverHelper(col, row, 1, -1) || gameOverHelper(col, row, -1, 1) || gameOverHelper(col, row, -1, -1)) {
            return true;
        }
        boolean over = true;
        for (int c = 0; c < size; c++) {
            for (int r = 0; r < size; r++) {
                if (board[c][r] == 0) {
                    over = false;
                }
                if (!over) {
                    break;
                }
            }
        }
        return over;
    }

    private boolean gameOverHelper(int col, int row, int c, int r) {
        if (!onBoard(col + 4 * c, row + 4 * r)) {
            return false;
        }
        int i = board[col][row];
        if (board[col + 1 * c][row + 1 * r] == i && board[col + 2 * c][row + 2 * r] == i && board[col + 3 * c][row + 3 * r] == i && board[col + 4 * c][row + 4 * r] == i) {
            return true;
        }
        return false;
    }

    public void printBoard() {
        System.out.print("   ");
        for (int i = 0; i < size; i++) {
            System.out.print(" " + i);
            if (i < 10) {
                System.out.print(" ");
            }
        }
        System.out.println();
        for (int c = 0; c < size; c++) {
            System.out.print(" " + c);
            if (c < 10) {
                System.out.print(" ");
            }
            for (int r = 0; r < size; r++) {
                int i = board[c][r];
                if (i == 0) {
                    System.out.print(" + ");
                } else if (i == 1) {
                    System.out.print(" X ");
                } else if (i == -1) {
                    System.out.print(" O ");
                }
            }
            System.out.println();
        }
    }
}
