package Checkers_game;

import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public class Piece extends StackPane {

        private int board_x, board_y;

        private Piece_player player;
        private MoveManager moveManager;
        private double scale = 0.366;

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

            this.player = player;
            this.movePiece(x, y);
            this.board_x = x;
            this.board_y = y;
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
            this.moveManager = new MoveManager(board, this);
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
            // remove dragged movement
//            this.relocate(new_x * Checkers.tile_size, new_y * Checkers.tile_size);
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
            // don't change turn until animation is finished
            transition.setOnFinished(e -> {
                // position piece in exact position
                this.setLayoutX(new_x * Checkers.tile_size);
                this.setLayoutY(new_y * Checkers.tile_size);
                // reset translate
                this.setTranslateX(0);
                this.setTranslateY(0);
                // update pieces board position reference
                this.setBoardX(new_x);
                this.setBoardY(new_y);
                // update piece's ref to board position
                game.changeTurn();
            });
            // play animation
            transition.play();
            System.out.println("expected " + new_x + " " + new_y);
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
