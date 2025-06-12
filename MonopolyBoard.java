import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Random;
public class MonopolyBoard extends Application {
    private static final int SIZE = 11;
    private static final int TILE_SIZE = 80;
    private Circle pawn;
    private int currentPosition = 0;
    private Label diceResultLabel;
    private ImageView diceImage;
    private GridPane grid;
    private StackPane[][] tiles = new StackPane[SIZE][SIZE]; // Track tiles for easy access

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        grid = new GridPane();
        grid.setGridLinesVisible(true);

        // Initialize all tiles first
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                StackPane tile = new StackPane();
                tile.setPrefSize(TILE_SIZE, TILE_SIZE);
                tile.setStyle("-fx-border-color: black; -fx-background-color: white;");

                if (row == 0 || row == SIZE - 1 || col == 0 || col == SIZE - 1) {
                    Label label = new Label(getLabelForPosition(row, col));
                    label.setFont(Font.font("Arial", 10));
                    label.setWrapText(true);
                    label.setAlignment(Pos.CENTER);

                    if (col == 0) label.setRotate(90);
                    if (col == SIZE - 1) label.setRotate(-90);

                    tile.getChildren().add(label);

                    if ((row == 0 && col == 0) || (row == 0 && col == SIZE-1) ||
                            (row == SIZE-1 && col == 0) || (row == SIZE-1 && col == SIZE-1)) {
                        tile.setStyle("-fx-background-color: lightyellow;");
                    }
                } else {
                    tile.setStyle("-fx-background-color: lightgray;");
                }

                grid.add(tile, col, row);
                tiles[row][col] = tile; // Store reference to the tile
            }
        }

        // Create pawn after tiles exist
        pawn = new Circle(15, Color.RED);
        pawn.setStroke(Color.BLACK);
        placePawnAtPosition(0);

        // Dice components
        diceImage = new ImageView();
        diceImage.setFitWidth(50);
        diceImage.setFitHeight(50);

        diceResultLabel = new Label("Click Roll to start!");
        diceResultLabel.setFont(Font.font(14));

        Button rollButton = new Button("Roll Dice");
        rollButton.setOnAction(e -> rollDiceAndMovePawn());

        VBox diceBox = new VBox(10, diceImage, diceResultLabel, rollButton);
        diceBox.setAlignment(Pos.CENTER);
        grid.add(diceBox, 5, 5);

        Scene scene = new Scene(grid, TILE_SIZE * SIZE, TILE_SIZE * SIZE);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Monopoly with Dice");
        primaryStage.show();
    }

    private void rollDiceAndMovePawn() {
        Random rand = new Random();
        int diceRoll = rand.nextInt(6) + 1;

        diceResultLabel.setText("You rolled: " + diceRoll);
        try {
            // For actual images, use: new Image(getClass().getResourceAsStream("dice_" + diceRoll + ".png"))
            // Here we'll just use a placeholder
            diceImage.setImage(null);
        } catch (Exception e) {
            diceImage.setImage(null);
        }

        movePawn(diceRoll);
    }

    private void movePawn(int steps) {
        removePawnFromCurrentPosition();
        currentPosition = (currentPosition + steps) % 40;
        placePawnAtPosition(currentPosition);
    }

    private void placePawnAtPosition(int pos) {
        int[] coords = getCoordinatesForPosition(pos);
        tiles[coords[0]][coords[1]].getChildren().add(pawn);
    }

    private void removePawnFromCurrentPosition() {
        int[] coords = getCoordinatesForPosition(currentPosition);
        tiles[coords[0]][coords[1]].getChildren().remove(pawn);
    }

    private int[] getCoordinatesForPosition(int pos) {
        int row, col;
        if (pos < 10) {         // Bottom row (right to left)
            row = SIZE - 1;
            col = 10 - pos;
        } else if (pos < 20) {  // Top row (left to right)
            row = 0;
            col = pos - 10;
        } else if (pos < 30) {  // Left side (bottom to top)
            row = SIZE - 1 - (pos - 20);
            col = 0;
        } else {                // Right side (top to bottom)
            row = pos - 30;
            col = SIZE - 1;
        }
        return new int[]{row, col};
    }

    private String getLabelForPosition(int row, int col) {
        // Bottom row (positions 0-9: GO to Jail)
        if (row == SIZE - 1) {
            String[] properties = {"GO", "Mediterranean\nAvenue", "Community\nChest", "Baltic\nAvenue",
                    "Income\nTax", "Reading\nRailroad", "Oriental\nAvenue",
                    "Chance", "Vermont\nAvenue", "Connecticut\nAvenue", "Jail"};
            return properties[col];
        }
        // Top row (positions 10-19)
        else if (row == 0) {
            String[] properties = {"Free\nParking", "New York\nAvenue", "Tennessee\nAvenue",
                    "Community\nChest", "St. James\nPlace", "Pennsylvania\nRailroad",
                    "Virginia\nAvenue", "States\nAvenue", "Electric\nCompany",
                    "Park\nPlace", "Go To\nJail"};
            return properties[col];
        }
        // Left side (positions 20-29)
        else if (col == 0) {
            String[] properties = {"", "Kentucky\nAvenue", "Indiana\nAvenue", "Illinois\nAvenue",
                    "B. & O.\nRailroad", "Atlantic\nAvenue", "Ventnor\nAvenue",
                    "Water\nWorks", "Marvin\nGardens", "Pacific\nAvenue", ""};
            return properties[row];
        }
        // Right side (positions 30-39)
        else if (col == SIZE - 1) {
            String[] properties = {"", "Short Line\nRailroad", "Community\nChest",
                    "North Carolina\nAvenue", "Pennsylvania\nAvenue", "Chance",
                    "Pacific\nAvenue", "Northwest\nAvenue", "Boardwalk",
                    "Luxury\nTax", ""};
            return properties[row];
        }
        return "";
    }
}