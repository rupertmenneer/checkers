package Checkers_game;

import javafx.animation.TranslateTransition;
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
import javafx.util.Duration;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Checkers extends Application {

    public final static int tile_size = 72;
    public final static int grid_size = 8;
    public static Piece_player turn = Piece_player.Human;

    private final Group tiles = new Group();
    private final Group pieces = new Group();
    private Group display_available_moves = new Group();
    private final ArrayList<Piece> human_pieces = new ArrayList<>();
    private final ArrayList<Piece> ai_pieces = new ArrayList<>();
    private final Tile[][] board = new Tile[grid_size][grid_size];

    private Pane generateBoard(){
        Pane root = new Pane();
        root.setPrefSize(grid_size*tile_size, grid_size*tile_size);

        for(int x = 0; x < grid_size; x++) {
            for (int y = 0; y < grid_size; y++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                tile.setVisible(true);
                tiles.getChildren().add(tile);
                board[x][y] = tile;
                // generate pieces
            }
        }
            for(int x = 0; x < grid_size; x++){
                for(int y = 0; y < grid_size; y++) {
                    if ((x + y) % 2 == 0) {
                        if (y <= 2) {
                            // for AI
                            Piece p_ai = generatePiece(Piece_player.AI, x, y);
                            board[x][y].setPiece(p_ai);
                            p_ai.setDisable(true);
                            ai_pieces.add(p_ai);
                            pieces.getChildren().add(p_ai);
                        } else if (y >= grid_size - 3) {
                            // for human
                            Piece p_h = generatePiece(Piece_player.Human, x, y);
                            board[x][y].setPiece(p_h);
                            human_pieces.add(p_h);
                            pieces.getChildren().add(p_h);
                        }
                    }
                }
            }
        tiles.setVisible(true);
        root.getChildren().addAll(tiles,display_available_moves,pieces);
        return root;
    }


    private Piece generatePiece(Piece_player player, int x, int y){
        Piece p = new Piece(player, x, y, board);

        // handle selecting a piece
        p.setOnMousePressed(e-> {
            p.setCursor(Cursor.CLOSED_HAND);
            System.out.println("Piece pos " + p.getBoardX() + " " + p.getBoardY());
            showValidMoves(p);
        });
        // handle piece being moved
        p.setOnMouseReleased(e->{
            p.setCursor(Cursor.OPEN_HAND);
            // snap to grid space
            int new_x = roundToTile(p.getLayoutX())/tile_size;
            int new_y = roundToTile(p.getLayoutY())/tile_size;
            Move attempted_move = new Move(p, new_x, new_y, p.getBoardX(), p.getBoardY());
           if(p.getMoveManager().getMove(attempted_move) == null) {
                // if attempted move was not valid move piece back to previous position
                p.movePiece(p.getBoardX(), p.getBoardY());
            }
            else {
                // make move with Move from Move Manager if it was a valid attempt
                makeMove(p.getMoveManager().getMove(attempted_move));
            }
            display_available_moves.getChildren().clear();
        });
        return p;
    }

    private void showValidMoves(Piece p){
        ArrayList<Move> available_moves = p.getMoveManager().getValidMoves();
        for(int i = 0; i<available_moves.size(); i++){
            Rectangle move = new Rectangle(Checkers.tile_size, Checkers.tile_size);
            move.setVisible(true);
            move.setDisable(true);
            move.setFill(Color.GREEN);
            move.setOpacity(0.2);
            move.setTranslateX(available_moves.get(i).getX()*Checkers.tile_size);
            move.setTranslateY(available_moves.get(i).getY()*Checkers.tile_size);
            display_available_moves.getChildren().add(move);
        }
    }


    private void makeMove(Move m){
        // get piece and new x and y co-ords
        Piece p = m.getPiece();
        int new_x = m.getX();
        int new_y = m.getY();
        // are any pieces taken in this move? if so remove them
        ArrayList<Piece > taken_pieces = m.getPiecesTaken();
        System.out.println("Number of pieces taken: " + taken_pieces.size());
        for (Piece taken_piece : taken_pieces) {
            board[taken_piece.getBoardX()][taken_piece.getBoardY()].setPiece(null);
            pieces.getChildren().remove(taken_piece);
            System.out.println("Piece taken");
        }
        // set old board ref to null
        board[p.getBoardX()][p.getBoardY()].setPiece(null);
        // update new board ref
        board[new_x][new_y].setPiece(p);
        // animate piece
        p.animatePiece(this, new_x, new_y);

        m.printMove();

    }

    public void changeTurn(){
        if (turn == Piece_player.Human){
            turn = Piece_player.AI;
            Play();
        } else {
            turn = Piece_player.Human;
        }
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

    private void Play(){
        if (turn == Piece_player.AI && !gameOver()){
            OpponentAI AI = new OpponentAI(board, 2);
            makeMove(AI.getBestMove());
        }
    }

    private boolean gameOver(){
        return pieces.getChildren().isEmpty();
    }

    public void printBoardState(){
        for(int y = 0; y < grid_size; y++){
            for(int x = 0; x < grid_size; x++) {
                if (board[x][y].has_piece()) {
                    System.out.print(" | " + board[x][y].getPiece().getPlayer());
                } else{
                    System.out.print(" | empty");
                }
                }
            System.out.println(" ");
            }
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
