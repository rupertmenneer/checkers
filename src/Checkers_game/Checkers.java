package Checkers_game;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Checkers extends Application {

    public final static int tile_size = 72;
    public final static int grid_size = 8;
    public static Piece_player turn = Piece_player.Human;
    public static boolean forceCapture = false;
    private int difficulty = Difficulty.Normal.difficulty;

    private final Group tiles = new Group();
    private final Group pieces = new Group();
    private Group display_available_moves = new Group();
    private Group display_hint = new Group();
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
        root.getChildren().addAll(tiles,display_available_moves, display_hint, pieces);
        return root;
    }


    private Piece generatePiece(Piece_player player, int x, int y){
        Piece p = new Piece(player, x, y, board);

        // handle selecting a piece
        p.setOnMousePressed(e-> {
            p.setCursor(Cursor.CLOSED_HAND);
//            System.out.println("Piece pos " + p.getBoardX() + " " + p.getBoardY());
            checkForceCapture(Piece_player.Human);
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
            display_hint.getChildren().clear();
        });
        return p;
    }

    private void checkForceCapture(Piece_player player){
        ArrayList<Move> all_available_moves = getPlayersAvailableMoves(player);
        for (Move move : all_available_moves){
            if(move.getPiecesTaken().size() > 0){
                forceCapture = true;
            }
        }
    }

    private ArrayList<Move> getPlayersAvailableMoves(Piece_player player) {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j].has_piece() && board[i][j].getPiece().getPlayer() == player) {
                    pieces.add(board[i][j].getPiece());
                }
            }
        }
        ArrayList<Move> all_moves = new ArrayList<>();
        for(int i = 0; i < pieces.size(); i++){
            all_moves.addAll(pieces.get(i).getMoveManager().getValidMoves());
        }
        return all_moves;
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
        for (Piece taken_piece : taken_pieces) {
            board[taken_piece.getBoardX()][taken_piece.getBoardY()].setPiece(null);
            taken_piece.deathAnimation(pieces);
        }
        // set old board ref to null
        board[m.getOldX()][m.getOldY()].setPiece(null);
        // update new board ref
        board[new_x][new_y].setPiece(p);
        if(!p.isKing()) {
            p.setKing(m.capturesKing());
        }
        // animate piece
        p.animatePiece(this, new_x, new_y);

    }

    public void changeTurn(){
        forceCapture = false;
        if(gameOver(turn)){
            System.out.println("GAME OVER");
            PopUp gameover_window = new PopUp();
            gameover_window.display("Game Over", turn + " wins!", "close window");
        }
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

    public MenuBar generateMenu(Stage p){
        // GAME MENU
        final Menu menuGame = new Menu("Game");
        MenuItem menuItem1 = new MenuItem("New Game");
        menuItem1.setOnAction( e ->
        {
            System.out.println( "Restarting app!" );
            p.close();
            Platform.runLater( () -> new Checkers().start( new Stage()));
        } );
        MenuItem menuItem2 = new MenuItem("Exit Game");
        menuItem2.setOnAction(e->{closeProgram(p);});
        menuGame.getItems().addAll(menuItem1, menuItem2);

        // DIFFICULTY
        final Menu menuDifficulty = new Menu("Change Difficulty...");
        RadioMenuItem easy = new RadioMenuItem("Too young to die! (easy)");
        easy.setOnAction(e->{difficulty = Difficulty.Easy.difficulty;});
        RadioMenuItem normal = new RadioMenuItem("Normal");
        normal.setOnAction(e->{difficulty = Difficulty.Normal.difficulty;});
        RadioMenuItem hard = new RadioMenuItem("Bring it on! (hard)");
        hard.setOnAction(e->{difficulty = Difficulty.Hard.difficulty;});
        normal.setSelected(true);
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(easy, normal, hard);
        menuDifficulty.getItems().addAll(easy, normal, hard);

        // HELP
        final Menu menuHelp = new Menu("Help...");
        MenuItem menuItem4 = new MenuItem("Rules");
        menuItem4.setOnAction(e->{displayRules();});
        MenuItem menuItem5 = new MenuItem("Get Move Hint");
        menuItem5.setOnAction(e->{displayMoveHint();});
        menuHelp.getItems().addAll(menuItem4, menuItem5);

        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: cornsilk");
        // add menus to menu bar
        menuBar.getMenus().addAll(menuGame, menuDifficulty, menuHelp);
        return menuBar;
    }

    private void displayMoveHint() {
        checkForceCapture(Piece_player.Human);
        OpponentAI hintFromAI = new OpponentAI(board, 7, Piece_player.Human);
        Move hint = hintFromAI.getBestMove();
        Rectangle move = new Rectangle(Checkers.tile_size, Checkers.tile_size);
        Rectangle piece = new Rectangle(Checkers.tile_size, Checkers.tile_size);
        move.setVisible(true);
        move.setDisable(true);
        piece.setVisible(true);
        piece.setDisable(true);
        move.setFill(Color.CADETBLUE);
        piece.setFill(Color.CADETBLUE);
        move.setOpacity(0.5);
        piece.setOpacity(0.5);
        move.setTranslateX(hint.getX()*Checkers.tile_size);
        move.setTranslateY(hint.getY()*Checkers.tile_size);
        piece.setTranslateX(hint.getOldX()*Checkers.tile_size);
        piece.setTranslateY(hint.getOldY()*Checkers.tile_size);
        display_hint.getChildren().addAll(move, piece);

    }

    private void Play(){
        if (turn == Piece_player.AI && !gameOver(Piece_player.AI)){
            checkForceCapture(Piece_player.AI);
            OpponentAI AI = new OpponentAI(board, difficulty, Piece_player.AI);
            makeMove(AI.getBestMove());
        }
    }

    private boolean gameOver(Piece_player p){
        if (getPlayersAvailableMoves(Piece_player.AI).size() == 0 | getPlayersAvailableMoves(Piece_player.Human).size() == 0){
            return true;
        }
        return false;
    }

    @Override
    public void start(Stage primaryStage) {

        // game window
        BorderPane root = new BorderPane();

        // generate game board
        BorderPane game_window = new BorderPane();
        Pane board_pane = generateBoard();
        game_window.setCenter(board_pane);

        // menu
        MenuBar menu = generateMenu(primaryStage);

        // create game window with menu and board
        root.setTop(menu);
        root.setCenter(game_window);
        // create scene with above layout
        Scene scene = new Scene(root);

        // set up music
        String music_path = new File("cherryBlossom.mp3").toURI().toString();
        Media music = new Media (music_path);
        MediaPlayer mediaPlayer = new MediaPlayer(music);
        mediaPlayer.setVolume(0.05);
        mediaPlayer.play();


        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.show();
    }

    private void closeProgram(Stage window){
        PopUp.display("Exit Confirmation", "Are you sure you want to exit?", "Exit game");
        window.close();
    }

    private void displayRules(){
        RulesDisplay display = new RulesDisplay();
        display.displayRules("Rules");
    }


}
