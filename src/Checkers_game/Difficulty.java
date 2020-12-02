package Checkers_game;

public enum Difficulty {

    Easy(1), Normal(3), Hard(7);

    final int difficulty;

    Difficulty(int difficulty){
        this.difficulty = difficulty;
    }
}
