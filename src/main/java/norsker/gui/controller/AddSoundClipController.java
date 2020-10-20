package norsker.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import norsker.soundboard.Settings;
import norsker.soundboard.SoundClip;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class AddSoundClipController implements Initializable
{

    @FXML private TextField nameInput;
    @FXML private TextField filepath;
    @FXML private TextField keyBindings;
    @FXML private Button saveBtn;
    @FXML private Button exitBtn;
    @FXML private AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        filepath.setOnMouseClicked(Event -> chooseFile());
        saveBtn.setOnAction(Event ->
        {
            if (save())
                close();
        });
        exitBtn.setOnAction(Event -> close());
    }

    private void close()
    {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }


    public void chooseFile()
    {

        Stage stage = new Stage();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null)
        {filepath.setText(selectedFile.getAbsolutePath()); }

    }


    private boolean save()
    {
        if (nameInput.getText().isEmpty() || filepath.getText().isEmpty())
            return false;


        SoundClip clip = new SoundClip(nameInput.getText(), filepath.getText());

        if (!keyBindings.getText().isEmpty())
            clip.setKeyBindings(new int[2]); // todo create utility function

        //if ends with mp3, save the file as a wav in sound resources

        Settings.getInstance().soundMappings.add(clip);
        Settings.getInstance().saveSettings();

        return true;

    }

}
