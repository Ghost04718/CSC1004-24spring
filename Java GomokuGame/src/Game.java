import java.util.Scanner;

public class Game {

    private Board board;

    private Scanner scanner;

    private boolean isPlaying;

    private String player1Name;
    private String player2Name;
    private String winner;

    private int player1 = 1;
    private int player2 = -1;
    private int currentPlayer = 1;



    public Game(Board board, Scanner scanner) {
        this.board = board;
        this.scanner = scanner;
        isPlaying = true;
    }

    public boolean playing() {
        return isPlaying;
    }

    private boolean playerMove(int player, int c, int r) {
        if (board.validPoistion(c, r)) {
            if (player == player1) {
                board.move(player1, c, r);
            } else if (player == player2){
                board.move(player2, c, r);
            }
            return true;
        }
        return false;
    }

    public void playGame() {

        System.out.println("Please enter player1: ");
        player1Name = scanner.nextLine();
        System.out.println("please enter player2: ");
        player2Name = scanner.nextLine();

        board.clearBoard();
        board.printBoard();

        while (isPlaying) {

            System.out.println("Player " + currentPlayerName(currentPlayer) + "'s turn, enter your move 'c, r': ");
            String input = scanner.nextLine();
            String[] ints = input.split(", ");
            int c = Integer.parseInt(ints[0]);
            int r = Integer.parseInt(ints[1]);

            boolean moved = false;
            while (!moved) {
                if (playerMove(currentPlayer, c, r)) {
                    moved = true;
                } else {
                    System.out.println("Invalid input, Player " + currentPlayerName(currentPlayer) + " please change your move 'c, r': ");
                    input = scanner.nextLine();
                    ints = input.split(", ");
                    c = Integer.parseInt(ints[0]);
                    r = Integer.parseInt(ints[1]);
                }
            }

            board.printBoard();
            if (board.gameOver(c, r)) {
                isPlaying = false;
                winner = currentPlayerName(currentPlayer);
                System.out.println("Player " + winner + " is the winner!");
            }

            if (isPlaying) {
                currentPlayer *= -1;
            }
        }
    }

    private String currentPlayerName(int player) {
        if (player == player1) {
            return player1Name;
        }
        return player2Name;
    }
}
