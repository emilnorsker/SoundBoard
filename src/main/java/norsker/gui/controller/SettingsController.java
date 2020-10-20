package norsker.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import norsker.Utility;
import norsker.soundboard.Settings;
import norsker.soundboard.SoundBoard;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SettingsController implements Initializable
{
    @FXML private ComboBox defaultSpeakerBox;
    @FXML private ComboBox VACSpeakerBox;
    @FXML private CheckBox enableVAC;
    @FXML private Button saveBtn;
    @FXML private Button exitBtn;
    @FXML private AnchorPane anchorPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        ObservableList<String> list = FXCollections.observableList(Arrays.asList(Utility.getMixerNames(SoundBoard.getInstance().standardDataLineInfo)));

        defaultSpeakerBox.setItems(list);
        defaultSpeakerBox.setValue(list.get(findSelected(Settings.getInstance().defaultSpeakerName)));

        VACSpeakerBox.setItems(list);
        VACSpeakerBox.setValue(list.get(findSelected(Settings.getInstance().VACSpeakerName)));

        enableVAC.setSelected(Settings.getInstance().isVACEnabled);

        exitBtn.setOnAction(Event -> close());
        saveBtn.setOnAction(Event ->
        {
            save();
            close();
        });

    }

    private int findSelected(String name)
    {
        int result = 0;
        int count = 0;
        for (String s: Utility.getMixerNames(SoundBoard.getInstance().standardDataLineInfo))
        {
            if (s.equals(name))
                result=count;
            count++;
        }
        return result;
    }

    private void save()
    {
        Settings settings = Settings.getInstance();

        if (defaultSpeakerBox.getValue()!= null)
            settings.defaultSpeakerName= (String) defaultSpeakerBox.getValue();

        if (VACSpeakerBox.getValue()!=null)
            settings.VACSpeakerName = (String) VACSpeakerBox.getValue();

        Settings.getInstance().isVACEnabled= enableVAC.isSelected();


        settings.saveSettings();

    }

    private void close()
    {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }
}
