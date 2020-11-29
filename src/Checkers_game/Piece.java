package Checkers_game;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Piece extends StackPane {

        private int board_x, board_y;

        private Piece_player player;
        private MoveManager availableMoves;
        private double scale = 0.366;


        public MoveManager getAvailableMoves() {
            return availableMoves;
        }

        public void setAvailableMoves(MoveManager availableMoves) {
            this.availableMoves = availableMoves;
        }

        public void clearAvailableMoves(){
            availableMoves = null;
        }

         public Piece_player getPlayer(){
            return player;
        }

        public Color getColor(){
            if(player == Piece_player.Human){ return Color.INDIANRED; } else { return Color.CORNFLOWERBLUE; }
        }

        public Piece(Piece copy){
            this.player = copy.getPlayer();
            this.board_x = copy.getBoardX();
            this.board_y = copy.getBoardY();
        }

        public Piece(Piece_player player, int x, int y){
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

            setOnMousePressed(e-> this.setCursor(Cursor.CLOSED_HAND));

           setOnMouseDragged(e-> {
               // calculate height offset so piece doesn't jump when pick it up
                double offsetX = e.getSceneX() - this.getWidth() / 2;
                double offsetY = e.getSceneY() - this.getHeight();
                this.relocate(offsetX, offsetY);
            });

           setOnMouseReleased(e->{

           });

        }

        public void movePiece(int x, int y){
            relocate(x * Checkers.tile_size, y * Checkers.tile_size);
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
