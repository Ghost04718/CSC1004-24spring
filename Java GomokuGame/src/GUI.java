import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GUI extends Application {

    private final int boardSize = 15;
    private final int squareSize = 40;
    private final int canvasWidth = boardSize * squareSize;
    private final int canvasHeight = boardSize * squareSize;

    private Stage stage;
    private BorderPane borderPane;
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private HBox hbox;
    private Pane pane;

    private MenuBar menuBar;
    private Menu menu;
    private MenuItem quit;
    private MenuItem newGame;

    private Board board = new Board(boardSize);
    private boolean isPlaying;

    private String player1Name;
    private String player2Name;
    private String winner;
    private Label turnLabel;
    private int player1 = 1;
    private int player2 = -1;
    private int currentPlayer = 1;


    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        newGame();
    }

    //Show whose turn
    private Label createTurnLabel() {
        turnLabel = new Label("Turn: " + player1Name);
        return turnLabel;
    }

    //Change current player and change turn
    private void updateTurnLabel() {
        String currentPlayerName = currentPlayer == player1 ? player1Name : player2Name;
        turnLabel.setText("Turn: " + currentPlayerName);
    }

    //Draw the board
    private void drawBoard(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.LIGHTGRAY);
        graphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);

        graphicsContext.setStroke(Color.BLACK);
        for (int i = 0; i < boardSize; i++) {
            graphicsContext.strokeLine(i * squareSize, 0, i * squareSize, canvasHeight);
            graphicsContext.strokeLine(0, i * squareSize, canvasWidth, i * squareSize);
        }
    }

    //Handle mouse click and place the move
    private void handleMouseClick(double x, double y, GraphicsContext graphicsContext) {
        int col = (int) (x / squareSize);
        int row = (int) (y / squareSize);

        if (playerMove(currentPlayer, col, row)) {
            if (currentPlayer == player1) {
                drawPiece(graphicsContext, row, col, Color.BLACK);
            } else {
                drawPiece(graphicsContext, row, col, Color.WHITE);
                }
            if (board.gameOver(col, row)) {
                endGame();
                return;
            }
            currentPlayer *= -1;
            updateTurnLabel();
        }
    }

    //End the game, pop out the window showing the winner, and ask if start a new game
    private void endGame() {
        winner = currentPlayer == player1 ? player1Name : player2Name;
        showAlert("Game Over", winner + " wins!");

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("New Game");
        confirmDialog.setHeaderText("Start a New Game?");
        confirmDialog.setContentText("Do you want to start a new game?");
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                newGame();
            } else {
                System.exit(0);
            }
        });
    }

    private void newGame() {
        isPlaying = true;
        board.clearBoard();
        
        //Set current player to player1 and show the turn
        currentPlayer = player1;

        enterName();
        
        setMenu();

        setStage();

        Scene scene = new Scene(borderPane, canvasWidth, canvasHeight + 40);
        stage.setScene(scene);
        stage.setTitle("Gomoku Game");
        stage.show();
    }

    private void setStage() {
        borderPane = new BorderPane();

        canvas = new Canvas(canvasWidth, canvasHeight);
        graphicsContext = canvas.getGraphicsContext2D();
        drawBoard(graphicsContext);

        canvas.setOnMouseClicked(e -> handleMouseClick(e.getX(), e.getY(), graphicsContext));

        hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(10));
        hbox.getChildren().addAll(menuBar, createTurnLabel());

        pane = new Pane(canvas);
        borderPane.setTop(hbox);
        borderPane.setCenter(pane);
    }

    //Pop out a window and ask for player name input
    private void enterName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Player Names");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Player 1 Name:");
        dialog.showAndWait().ifPresent(name -> player1Name = name);

        dialog.getEditor().clear();
        dialog.setContentText("Enter Player 2 Name:");
        dialog.showAndWait().ifPresent(name -> player2Name = name);
    }

    //Create the menubar and add menu "Quit", "New game"
    private void setMenu() {
        menuBar = new MenuBar();
        menu = new Menu("Menu");
        quit = new MenuItem("Quit");
        newGame = new MenuItem("NEW GAME");
        quit.setOnAction(actionEvent -> {
            System.exit(0);;
        });
        newGame.setOnAction(actionEvent -> newGame());
        menu.getItems().addAll(quit, newGame);
        menuBar.getMenus().add(menu);
    }

    //Pop out the window and show title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Place the piece
    private void drawPiece(GraphicsContext graphicsContext, int row, int col, Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillOval(col * squareSize, row * squareSize, squareSize, squareSize);
    }

    //Handle the move on board
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

    public static void main(String[] args) {
        launch(args);
    }
}

