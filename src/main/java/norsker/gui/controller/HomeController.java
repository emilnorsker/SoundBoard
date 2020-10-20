package norsker.gui.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.*;
import norsker.gui.ActionButtonCell;
import norsker.soundboard.Settings;
import norsker.soundboard.SoundBoard;
import norsker.soundboard.SoundClip;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable
{
    public Button homeButton;
    public TableView<SoundClip> table;
    public Button addFileButton;
    public ImageView reload;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        fillTableFromData();
        addFileButton.setOnAction(Event -> openAddSoundClip(Event));
        reload.setOnMouseClicked(Event -> fillTableFromData());
    }

    public void fillTableFromData()
    {
        System.out.println("filling");
        //get fake members
        ObservableList<SoundClip> list = Settings.getInstance().load();

        table.setEditable(false);
        //create table
        TableColumn<SoundClip, String> c1 = new TableColumn("Name");
        c1.setMinWidth(250);
        TableColumn<SoundClip, int[]> c2 = new TableColumn("Binding");
        c2.setMinWidth(250);
        TableColumn<SoundClip, String> c3 = new TableColumn("Location");
        c3.setMinWidth(250);
        TableColumn<SoundClip, Button> c4 = new TableColumn("Play");
        c4.setMinWidth(130);


        c1.setCellValueFactory(new PropertyValueFactory<>("name"));
        c3.setCellValueFactory(new PropertyValueFactory<>("filepath"));


        //todo make interactive
        c2.setCellValueFactory(new PropertyValueFactory<>("keyBindings"));//todo
        c4.setCellFactory(ActionButtonCell.<SoundClip>forTableColumn("Play", (SoundClip sound) ->
        {
            try {
                SoundBoard.getInstance().play(sound.filepath);
                System.out.println("hello");
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
            return sound;
        }));

        table.setItems(list);
        table.getColumns().addAll(c1,c2,c3,c4);
    }


    public void openAddSoundClip(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        try {
            Pane pane = loader.load(getClass().getClassLoader().getResource("view/addSoundClip.fxml"));
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);

            stage.setScene(new Scene(pane));
            stage.show();

            stage.setOnCloseRequest(Event -> fillTableFromData());

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

}
