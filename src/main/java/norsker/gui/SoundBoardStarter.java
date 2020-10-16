package norsker.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import norsker.soundboard.SoundBoard;

public class SoundBoardStarter extends Application
{

    public void run(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        SoundBoard soundBoard = SoundBoard.getInstance();
        soundBoard.setMixerOutput("Primary Sound Driver", false);
        soundBoard.setMixerOutput("CABLE Input (VB-Audio Virtual Cable)", true);



        FXMLLoader loader = new FXMLLoader();



        loader.setLocation(getClass().getClassLoader().getResource("view/home.fxml"));
        Parent content = loader.load();


        primaryStage.setTitle("PC Dragon Sounds");
        primaryStage.setScene(new Scene(content));
        primaryStage.show();
    }


}
