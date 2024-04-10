import java.util.Scanner;

//Used for terminal play mode.
public class Main {

    public static int SIZE = 15;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Board board = new Board(SIZE);
        Game game = new Game(board, scanner);

        try {
            while (game.playing()) {
                game.playGame();
            }
        } catch (IllegalStateException e) {
            System.exit(1);
        }
    }
}