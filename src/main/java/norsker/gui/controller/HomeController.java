package norsker.gui.controller;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import norsker.gui.ActionButtonCell;
import norsker.soundboard.Settings;
import norsker.soundboard.SoundBoard;
import norsker.soundboard.SoundClip;

import javax.sound.sampled.LineUnavailableException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class HomeController implements Initializable
{
    public GridPane soundList;
    public Button homeButton;
    public TableView<SoundClip> table;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        fillTableFromData();


    }


//    public void fillGridPaneFromSettings(GridPane gridPane)
//    {
////        HashMap<String, String> soundClips = Settings.fakeLoad();
//
//        int count = 0;
//        //for each sound clip entry
////        for (Map.Entry<String,String> entry: soundClips.entrySet())
//        {
//            //add new row
//            gridPane.addRow(count+1);
//
//            //create play button
//            Button play = new Button("Play");
//            play.setId(entry.getKey());
//            play.setOnAction(actionEvent ->
//            {
//                System.out.println("sound");
//                try {
//                    SoundBoard.getInstance().play(entry.getKey());
//                } catch (LineUnavailableException lineUnavailableException) {
//                    lineUnavailableException.printStackTrace();
//                }
//            });
//
//
//            //design layout
//            gridPane.add(new Text(entry.getKey()), 0, count);
//            gridPane.add(new Text(entry.getValue()),1, count);
//            gridPane.add(new Button(), 2, count);
//            count++;
//
//        }
//
//    }

    public void fillTableFromData()
    {

        //get fake members
        ObservableList<SoundClip> list = Settings.fakeLoad();

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
        c2.setCellValueFactory(new PropertyValueFactory<>("keyBindings"));
//        c4.setCellValueFactory(new PropertyValueFactory<>("dummy"));
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

        /*
        column.setCellFactory(ActionButtonTableCell.<Person>forTableColumn("Remove", (Person p) -> {
    table.getItems().remove(p);
    return p;
}));

         */



        table.setItems(list);
        table.getColumns().addAll(c1,c2,c3,c4);



    }
}
