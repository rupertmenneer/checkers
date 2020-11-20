package Checkers_game;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;

public class Checkers extends Application {

    public final static int tile_size = 72;
    public final static int grid_size = 8;
    public static boolean turn = true;

    private final Group tiles = new Group();
    private final Group pieces = new Group();
    private final Tile[][] board = new Tile[grid_size][grid_size];

    private Pane generateBoard(){
        Pane root = new Pane();
        root.setPrefSize(grid_size*tile_size, grid_size*tile_size);

        for(int x = 0; x < grid_size; x++){
            for(int y = 0; y < grid_size; y++){
                Tile tile = new Tile((x+y)%2 == 0,x ,y);
                tile.setVisible(true);
                tiles.getChildren().add(tile);
                board[x][y] = tile;
                // generate pieces
                if ((x+y)%2==0){
                    if(y<=2){
                        // for AI
                        Piece p_ai = generatePiece(Piece_player.AI, x, y);
                        board[x][y].setPiece(p_ai);
                        pieces.getChildren().add(p_ai);
                    }
                    else if (y>= grid_size - 3){
                        // for human
                        Piece p_h = generatePiece(Piece_player.Human, x, y);
                        board[x][y].setPiece(p_h);
                        pieces.getChildren().add(p_h);
                    }
                }
            }
        }

        tiles.setVisible(true);
        root.getChildren().addAll(tiles,pieces);
        return root;
    }


    private Piece generatePiece(Piece_player player, int x, int y){
        Piece p = new Piece(player, x, y);

        // handle selecting a piece
        p.setOnMousePressed(e-> {
            p.setCursor(Cursor.CLOSED_HAND);
            p.setAvailableMoves(new MoveManager(this, p));
            p.getAvailableMoves().printValidMoves();
        });
        // handle piece being moved
        p.setOnMouseReleased(e->{
            p.setCursor(Cursor.OPEN_HAND);
            // snap to grid space
            int new_x = roundToTile(p.getLayoutX())/tile_size;
            int new_y = roundToTile(p.getLayoutY())/tile_size;
            Move attempted_move = new Move(new_x, new_y);
            // if not your turn or not valid move
            if(p.getAvailableMoves().getMove(attempted_move) == null |
                    (p.getPlayer() == Piece_player.Human && !turn) |
                    (p.getPlayer() == Piece_player.AI && turn)){
                // move piece back to previous position
                p.movePiece(p.getBoardX(), p.getBoardY());
                p.clearAvailableMoves();
            }
            else {
                // if pieces were taken, remove them and set board ref to null
                ArrayList<Piece > taken_pieces = p.getAvailableMoves().getMove(attempted_move).getPiecesTaken();
                for (Piece taken_piece : taken_pieces) {
                    board[taken_piece.getBoardX()][taken_piece.getBoardY()].setPiece(null);
                    pieces.getChildren().remove(taken_piece);
                }
                // change turn
                turn = !turn;
                // set old board ref to null
                board[p.getBoardX()][p.getBoardY()].setPiece(null);
                p.clearAvailableMoves();
                // update new board ref
                board[new_x][new_y].setPiece(p);
                // update piece's ref to board position
                p.setBoardX(new_x);
                p.setBoardY(new_y);
                // move piece to new valid move
                p.movePiece(new_x, new_y);
            }
        });
        return p;
    }

    private int roundToTile(double i){
        return (int) (Math.round(i/tile_size) * tile_size);
    }

    public Tile[][] getBoard(){
        return board;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public MenuBar generateMenu(){
        // menu
        // create file menu item
        final Menu menu1 = new Menu("File");
        MenuItem menuItem1 = new MenuItem("Help...");
        MenuItem menuItem2 = new MenuItem("Exit Game");
        menu1.getItems().add(menuItem1);
        menu1.getItems().add(menuItem2);
        // create game options menu item
        final Menu menu2 = new Menu("Options");
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: cornsilk");
        menuBar.getMenus().addAll(menu1, menu2);
        return menuBar;
    }

    @Override
    public void start(Stage primaryStage) {
        // game window
        BorderPane root = new BorderPane();
        // menu
        MenuBar menu = generateMenu();
        // generate game board
        BorderPane game_window = new BorderPane();
        Pane board_pane = generateBoard();
        game_window.setCenter(board_pane);
        // create game window with menu and board
        root.setTop(menu);
        root.setCenter(game_window);
        // create scene with above layout
        Scene scene = new Scene(root);

        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.show();
    }

}
