package Checkers_game;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RulesDisplay {

    private static final List<String> list = new ArrayList<>();
    static int img_index = 0;

    public static void displayRules(String title){
        Stage window = new Stage();
        window.setTitle(title);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(250);

        // images in src folder.
        try {
            list.add("Checkers_images/part1.png");
            list.add("Checkers_images/part2.png");
            list.add("Checkers_images/part3.png");
            list.add("Checkers_images/part4.png");
            list.add("Checkers_images/part5.png");
            list.add("Checkers_images/part6.png");
            list.add("Checkers_images/part7.png");

            GridPane root = new GridPane();
            root.setAlignment(Pos.CENTER);

            Button lbutton = new Button("<");
            lbutton.setScaleX(2);
            lbutton.setScaleY(2);
            Button rbutton = new Button(">");
            rbutton.setScaleX(2);
            rbutton.setScaleY(2);

            Image[] images = new Image[list.size()];
            for (int i = 0; i < list.size(); i++) {
                File img_path = new File(list.get(i));
                images[i] = new Image(img_path.toURI().toString());
            }

            ImageView imageView = new ImageView(images[img_index]);
            imageView.setCursor(Cursor.CLOSED_HAND);

            rbutton.setOnAction(e -> {
                img_index = img_index + 1;
                if (img_index == list.size()) {
                    img_index = 0;
                }
                imageView.setImage(images[img_index]);

            });
            lbutton.setOnAction(e -> {
                img_index = img_index - 1;
                if (img_index == 0 || img_index > list.size() + 1 || img_index == -1) {
                    img_index = list.size() - 1;
                }
                imageView.setImage(images[img_index]);
            });

            imageView.setFitHeight(800*0.8);
            imageView.setFitWidth(800*0.8);

            HBox hBox = new HBox();
            hBox.setSpacing(15);
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().addAll(lbutton, imageView, rbutton);

            root.add(hBox, 1, 1);
            Scene scene = new Scene(root, 800, 800);
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
