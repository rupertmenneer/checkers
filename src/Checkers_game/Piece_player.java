package Checkers_game;


public enum Piece_player {
    Human(-1), AI(1);
    final int direction;

    Piece_player(int direction){
        this.direction = direction;
    }

}
