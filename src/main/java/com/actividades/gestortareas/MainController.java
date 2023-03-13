package com.actividades.gestortareas;

import de.jensd.fx.glyphs.icons525.Icons525;
import de.jensd.fx.glyphs.icons525.Icons525View;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.types.ObjectId;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane parent;
    @FXML
    private Button buttonClose;

    @FXML
    private Button buttonGotoTaskView;

    @FXML
    private Button buttonDeleteTask;

    @FXML
    private TableColumn<Tarea, String> columnaFecha;

    @FXML
    private TableColumn<Tarea, Icons525View> columnaFinalizada;

    @FXML
    private TableColumn<Tarea, String> columnaTarea;

    @FXML
    private TableView<Tarea> tablaTareas;

    @FXML
    private Pagination pagination;

    public static final Integer tableRows = 7;

    public static Integer pageIndex;

    public static Tarea selectedTask;
    private double xOffset = 0;
    private double yOffset = 0;

    public void initialize(){

        makeUndecoratedWindowDragable(parent);
        buttonGotoTaskView.setTooltip(new Tooltip("Abre la ventana del editor de tareas"));
        buttonClose.setTooltip(new Tooltip("Salir de la aplicación"));
        buttonDeleteTask.setTooltip(new Tooltip("Borrar tarea seleccionada"));

        tablaTareas.setItems(ListaTareas.getListaTareas());
        columnaFecha.setCellValueFactory(cellData  -> new SimpleStringProperty(cellData.getValue().getFecha()));
        columnaTarea.setCellValueFactory(cellData  -> new SimpleStringProperty(cellData.getValue().getDescripcion()));
        columnaFinalizada.setCellValueFactory(cellData  -> {
            if (cellData.getValue().getFinalizada()){

                return new SimpleObjectProperty<>(new Icons525View(Icons525.OK));
            } else {

                return new SimpleObjectProperty<>(new Icons525View(Icons525.CANCEL));
            }
        });

        tablaTareas.setOnMouseClicked(this::showEdit);

        pageIndex = 1;
        pagination.setPageCount((int)ListaTareas.getColeccionTareas().countDocuments() / tableRows +1);
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory(this::createPage);
    }

    @FXML
    void actionClose(ActionEvent event) {

        Platform.exit();
    }

    /**
     * Acción que lanza una ventana modal para crear o editar tareas
     * @param event> Evento que lanza la acción
     */
    @FXML
    void actionGotoTaskView(ActionEvent event) throws IOException {

        Stage dialog = new Stage();
        FXMLLoader dialogFxml = new FXMLLoader(Main.class.getResource("vistaTarea.fxml"));
        dialog.setScene(new Scene(dialogFxml.load()));
        dialog.initOwner(Main.getMainStage());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.showAndWait();
        createPage(pageIndex);
    }

    @FXML
    void actionDeleteTask(ActionEvent event) {

        Tarea selectedTask = tablaTareas.getSelectionModel().getSelectedItem();
        ListaTareas.dropTaskFromDB(selectedTask);
        ListaTareas.getListaTareas().remove(selectedTask);
        createPage(pageIndex);
    }

    @FXML
    void mouseActionUnselect(MouseEvent event) {

        unselectTask();
    }

    /**
     * Método que cambia la interfaz para poder editar una tarea correctamente en lugar de crearla
     */
    void showEdit(MouseEvent event) {

        if (tablaTareas.getSelectionModel().getSelectedItem() != null) {

            selectedTask = tablaTareas.getSelectionModel().getSelectedItem();
            buttonGotoTaskView.setText("Editar Tarea");
            buttonDeleteTask.setDisable(false);
        }
    }

    private Node createPage(int pageIndex) {

        MainController.pageIndex = pageIndex;
        tablaTareas.setItems(ListaTareas.getColeccionTareas().find().
                skip(pageIndex * tableRows).limit(tableRows).into(FXCollections.observableArrayList()));
        unselectTask();

        return new Label("");
    }

    /**
     * Método que cambia la interfaz para volver a poder crear una tarea correctamente en lugar de editarla
     */
    private void unselectTask() {

        tablaTareas.getSelectionModel().clearSelection();
        selectedTask = null;
        buttonGotoTaskView.setText("Crear Tarea");
        buttonDeleteTask.setDisable(true);
    }

    private void makeUndecoratedWindowDragable(BorderPane parent) {

        parent.getTop().setOnMousePressed(event -> {

            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        parent.getTop().setOnMouseDragged(event -> {

            ((Node)event.getSource()).getScene().getWindow().setX(event.getScreenX() - xOffset);
            ((Node)event.getSource()).getScene().getWindow().setY(event.getScreenY() - yOffset);
        });
    }
}
