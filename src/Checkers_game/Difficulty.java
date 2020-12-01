package Checkers_game;

public enum Difficulty {

    Easy(2), Normal(6), Hard(11);

    final int difficulty;

    Difficulty(int difficulty){
        this.difficulty = difficulty;
    }
}
