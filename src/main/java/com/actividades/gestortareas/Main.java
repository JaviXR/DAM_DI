package com.actividades.gestortareas;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main extends Application {

    private static final String ARCHIVO_PROPIEDADES = "settings.properties";
    private static final Properties PROPIEDADES = new Properties();
    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {

        InputStream config = this.getClass().getClassLoader().getResourceAsStream(ARCHIVO_PROPIEDADES);
        try {
            PROPIEDADES.load(config);
        } catch (IOException ioe) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText("Error al leer el fichero de configuración");
            alerta.setTitle("Error al leer el fichero de configuración");
            alerta.setContentText("ERROR: No se ha podido leer el contenido del fichero " + ARCHIVO_PROPIEDADES);
            alerta.showAndWait();
            Platform.exit();
        }

        new ListaTareas(PROPIEDADES);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("vistaMain.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Gestor de Tareas");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        mainStage = stage;
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {

        ListaTareas.stopDB();
        super.stop();
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static Properties getPropiedades() { return PROPIEDADES; }

    /** Carga un resource contenedor de una scene para obtenerla.
     * @param s> URI del resource a convertir en Scene.
     * @return Scene caragada desde FXML
     * @throws IOException
     */
    public static Scene loadScene(String s) throws IOException {

        return new Scene (FXMLLoader.load(Main.class.getResource(s)));
    }

}