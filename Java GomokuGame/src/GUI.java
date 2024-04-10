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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;

public class GUI extends Application{

    //Set the size og the board and the gui.
    private final int boardSize = 15;
    private final int squareSize = 40;
    private final int canvasWidth = boardSize * squareSize;
    private final int canvasHeight = boardSize * squareSize;

    //Gui components.
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
    private MenuItem save;
    private MenuItem load;

    private Board board = new Board(boardSize);

    private String player1Name = "player1";
    private String player2Name = "player2";
    private String winner;
    private Label turnLabel;
    private final int player1 = 1;
    private final int player2 = -1;
    private int currentPlayer = 1;

    //Override the start function.
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        setMenu();
        setStage();
        Scene scene = new Scene(borderPane, canvasWidth, canvasHeight + 40);
        stage.setScene(scene);
        stage.setTitle("Gomoku Game");
        stage.show();

        showAlert("Welcome", "Please click on the menu and choose one action.");
    }

    //Show whose turn.
    private Label createTurnLabel() {
        turnLabel = new Label("Turn: " + player1Name);
        return turnLabel;
    }

    //Change current player and change turn
    private void updateTurnLabel() {
        String currentPlayerName = currentPlayer == player1 ? player1Name : player2Name;
        turnLabel.setText("Turn: " + currentPlayerName);
    }

    //Draw the board.
    private void drawBoard(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.LIGHTGRAY);
        graphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);

        graphicsContext.setStroke(Color.BLACK);
        for (int i = 0; i < boardSize; i++) {
            graphicsContext.strokeLine(i * squareSize, 0, i * squareSize, canvasHeight);
            graphicsContext.strokeLine(0, i * squareSize, canvasWidth, i * squareSize);
        }
    }

    //Handle mouse click and place the piece, also change the current player and update label
    private void handleMouseClick(double x, double y, GraphicsContext graphicsContext) {
        int col = (int) Math.round(x / squareSize);
        int row = (int) Math.round(y / squareSize);

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

    //End the game, pop out the window showing the winner, and ask if start a new game.
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

    //Reset the game and start.
    private void newGame() {
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

    //Set the GUI.
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

    //Pop out a window and ask for player names input, and the default.
    private void enterName() {
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Enter Player Names");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Player 1 Name:");
        dialog.showAndWait().ifPresent(name -> player1Name = name);
        if (player1Name.isEmpty()) {
            player1Name = "player1";
        }

        dialog.getEditor().clear();
        dialog.setContentText("Enter Player 2 Name:");
        dialog.showAndWait().ifPresent(name -> player2Name = name);
        if (player2Name.isEmpty()) {
            player2Name = "player2";
        }
    }

    //Create the menubar and add menu "Quit", "New game", "Save", "Load".
    private void setMenu() {
        menuBar = new MenuBar();
        menu = new Menu("Menu");
        quit = new MenuItem("Quit");
        newGame = new MenuItem("New Game");
        save = new MenuItem("Save");
        load = new MenuItem("Load");

        quit.setOnAction(actionEvent -> {
            System.exit(0);;
        });

        newGame.setOnAction(actionEvent -> newGame());

        save.setOnAction(actionEvent -> {
            saveGame();
        });

        load.setOnAction(actionEvent -> {
            loadGame();
        });

        menu.getItems().addAll(quit, newGame, save, load);
        menuBar.getMenus().add(menu);
    }

    //Pop out the window and show title as well as message.
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Place the piece at the intersection.
    private void drawPiece(GraphicsContext graphicsContext, int row, int col, Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillOval(col * squareSize - squareSize / 4, row * squareSize - squareSize / 4, squareSize / 2, squareSize / 2);
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

    //Let the user choose the location to save the file and pop out the result (Only save the board).
    public void saveGame() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game State");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game State Files", "*.dat"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
                outputStream.writeObject(this.board);
                showAlert("Save", "Game state saved successfully.");
            } catch (IOException e) {
                showAlert("Save", "Error saving game state: " + e.getMessage());
            }
        }
    }

    //The user can choose a file and load it, players' name needed.
    public void loadGame() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Game State");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game State Files", "*.dat"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                this.board = (Board) inputStream.readObject();

                //Reset the gui
                enterName();
                setMenu();
                setStage();

                //Based on the loaded board and draw it.
                int player = 0;
                for (int c = 0; c < boardSize; c++) {
                    for (int r = 0; r < boardSize; r++) {
                        int i = board.getValue(c, r);
                        if (i == player1) {
                            drawPiece(graphicsContext, r, c, Color.BLACK);
                            player++;
                        } else if (i == player2) {
                            drawPiece(graphicsContext, r, c, Color.WHITE);
                            player--;
                        }
                    }
                }

                //Judge whose turn.
                if (player == 0) {
                    currentPlayer = player1;
                } else {
                    currentPlayer = player2;
                }
                updateTurnLabel();

                Scene scene = new Scene(borderPane, canvasWidth, canvasHeight + 40);
                stage.setScene(scene);
                stage.setTitle("Gomoku Game");
                stage.show();
                showAlert("Load", "Game state loaded successfully.");

                //Pop out the result.
            } catch (Exception e) {
                showAlert("Load", "Error loading game state: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

