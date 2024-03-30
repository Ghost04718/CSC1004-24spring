import java.util.Scanner;

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
        } catch (IllegalStateException excp) {
            System.exit(1);
        }
    }
}