package Checkers_game;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Text;

import java.io.File;

public class Tile extends Rectangle {

    private Piece piece;
    // wood texture 1
    private static final File wood_1_path = new File("/Users/rm594/Documents/Checkers_images/wood1.png");
    Image wood_1 = new Image(wood_1_path.toURI().toString());
    // wood texture 2
    private static final File wood_2_path = new File("/Users/rm594/Documents/Checkers_images/wood2.png");
    Image wood_2 = new Image(wood_2_path.toURI().toString());

    public boolean has_piece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Tile(boolean even, int x, int y) {
        super(Checkers.tile_size, Checkers.tile_size);
        this.relocate(x * Checkers.tile_size, y * Checkers.tile_size);
        this.setVisible(true);
        if(even){ this.setFill(new ImagePattern(wood_1, 0, 0, 1, 1, true));}
        else{this.setFill(new ImagePattern(wood_2, 0, 0, 1, 1, true));}

    }
}
