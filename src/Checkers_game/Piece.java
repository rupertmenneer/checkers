package Checkers_game;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.text.Text;

import java.io.File;
import java.util.ArrayList;

public class Piece extends StackPane {

        private int board_x, board_y;

        private Piece_player player;
        private MoveManager moveManager;
        private double scale = 0.366;
        private boolean king;
        private static final File skull_path = new File("Checkers_images/skull.png");


        public MoveManager getMoveManager() {
            return moveManager;
        }

         public Piece_player getPlayer(){
            return player;
        }

        public Color getColor(){
            if(player == Piece_player.Human){ return Color.INDIANRED; } else { return Color.CORNFLOWERBLUE; }
        }

        public Piece(Piece_player player, int x, int y, Tile[][] board){
            this.king = false;
            this.player = player;
            this.movePiece(x, y);
            this.board_x = x;
            this.board_y = y;
            this.moveManager = new MoveManager(board, this);
            // background
            Circle piece_bg = new Circle(Checkers.tile_size * scale);
            piece_bg.setFill(getColor().darker());
            piece_bg.setStroke(Color.BLACK);
            piece_bg.setStrokeWidth(Checkers.tile_size * 0.1 * scale);
            piece_bg.setTranslateX(Checkers.tile_size * 0.09);
            piece_bg.setTranslateY(Checkers.tile_size * 0.14);
            // foreground
            Circle piece_fg = new Circle(Checkers.tile_size * scale);
            piece_fg.setFill(getColor());
            piece_fg.setStroke(Color.BLACK);
            piece_fg.setStrokeWidth(Checkers.tile_size * 0.1 * scale);
            piece_fg.setTranslateX(Checkers.tile_size * 0.09);
            piece_fg.setTranslateY(Checkers.tile_size * 0.05);
            
            getChildren().addAll(piece_bg, piece_fg);

            this.setCursor(Cursor.OPEN_HAND);

            setOnMousePressed(e-> this.setCursor(Cursor.CLOSED_HAND));

           setOnMouseDragged(e-> {
               // calculate height offset so piece doesn't jump when pick it up
                double offsetX = e.getSceneX() - this.getWidth() / 2;
                double offsetY = e.getSceneY() - this.getHeight();
                this.setLayoutX(offsetX);
                this.setLayoutY(offsetY);
            });

           setOnMouseReleased(e->{

           });

        }

        public void movePiece(int x, int y){
            this.setLayoutX(x * Checkers.tile_size);
            this.setLayoutY(y * Checkers.tile_size);
        }

        public void animatePiece(Checkers game, int new_x, int new_y){

            // move piece to new valid move with animation and 0.5 sec delay
            TranslateTransition transition = new TranslateTransition();
            transition.setDuration(Duration.seconds(0.5));
            transition.setNode(this);
            // reset piece position before animation
            this.setLayoutX(this.getBoardX() * Checkers.tile_size);
            this.setLayoutY(this.getBoardY()  * Checkers.tile_size);
            // set animation values
            transition.setToX((new_x - board_x) * Checkers.tile_size);
            transition.setToY((new_y - board_y) * Checkers.tile_size);
            // check if kinged
            checkIfKinged(new_y);
            // don't change turn until animation is finished
            transition.setOnFinished(e -> {
                // set up audio clip
                String move_audio = new File("move_piece_sound.wav").toURI().toString();
                AudioClip audioClip = new AudioClip(move_audio);
                audioClip.setVolume(0.5);
                audioClip.play();
                // position piece in exact position
                this.setLayoutX(new_x * Checkers.tile_size);
                this.setLayoutY(new_y * Checkers.tile_size);
                // reset translate
                this.setTranslateX(0);
                this.setTranslateY(0);
                // update pieces board position reference
                board_x = new_x;
                board_y = new_y;
                game.changeTurn();
            });

            // play animation
            transition.play();
        }

        public void deathAnimation(Group group){
            group.getChildren().remove(this);
            Image skull = new Image(skull_path.toURI().toString());
            Rectangle s = new Rectangle(Checkers.tile_size*0.9, Checkers.tile_size*0.9);
            s.setFill(new ImagePattern(skull, 0, 0, 1, 1, true));
            s.relocate(board_x*Checkers.tile_size, board_y*Checkers.tile_size);
            s.setTranslateX(Checkers.tile_size * 0.05);
            s.setTranslateY(Checkers.tile_size * 0.05);
            group.getChildren().add(s);
            FadeTransition ft = new FadeTransition(Duration.millis(2000), s);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> {
                group.getChildren().remove(s);
            });
            ft.play();
        }

        public boolean isKing(){
            return king;
        }

        public void setKing(boolean isKing){
            this.king = isKing;
        }
        
        private void checkIfKinged(int new_y){
            if (player == Piece_player.Human && new_y == 0) {
                king = true;
            } if (player == Piece_player.AI && new_y == 7) {
                king = true;
            }
            if (king) {
                // Visually make king
                Circle piece_king = new Circle(Checkers.tile_size * scale);
                piece_king.setFill(Color.GOLD);
                piece_king.setStroke(Color.BLACK);
                piece_king.setStrokeWidth(Checkers.tile_size * 0.1 * scale);
                piece_king.setTranslateX(Checkers.tile_size * 0.09);
                piece_king.setTranslateY(Checkers.tile_size * -0.01);
                this.getChildren().add(piece_king);
            }

        }

        public int getBoardX(){
            return this.board_x;
        }

        public int getBoardY(){
            return this.board_y;
        }

        public void setBoardX(int x){
            this.board_x = x;
        }

        public void setBoardY(int y){
            this.board_y = y;
        }

}
