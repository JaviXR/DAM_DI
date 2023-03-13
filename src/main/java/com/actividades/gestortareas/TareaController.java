package com.actividades.gestortareas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

public class TareaController {

    @FXML
    private Button buttonCancelTask;

    @FXML
    private Button buttonCreateTask;

    @FXML
    private Button buttonDoneNo;

    @FXML
    private Button buttonDoneYes;

    @FXML
    private DatePicker date;

    @FXML
    private TextArea description;

    boolean done;

    public void initialize() {

        buttonDoneYes.setTooltip(new Tooltip("La tarea ya está finalizada"));
        buttonDoneNo.setTooltip(new Tooltip("La tarea necesita ser completada"));

        if (MainController.selectedTask != null) {

            date.setValue(LocalDate.parse(MainController.selectedTask.getFecha(), Tarea.dateFormatter));
            description.setText(MainController.selectedTask.getDescripcion());
            if (MainController.selectedTask.getFinalizada()) {

                buttonDoneYes.fire();
            } else  {

                buttonDoneNo.fire();
            }

            buttonCreateTask.setText("Editar");
        } else {

            buttonDoneNo.fire();
            buttonCreateTask.setText("Crear");
        }
    }

    @FXML
    void actionCancelTask(ActionEvent event) {

        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    /**
     * Acción que, según si MainController.selectedTask es o no null, inserta una nueva tarea en la base de datos
     * o actualiza una ya existente
     * @param event> Evento que lanza esta acción
     */
    @FXML
    void actionCreateTask(ActionEvent event) {
        if (date.getValue() != null && !description.getText().isEmpty()) {

            Tarea nuevaTarea = new Tarea(date.getValue().format(Tarea.dateFormatter), description.getText(), done);
            if (MainController.selectedTask != null) {

                ListaTareas.uploadTaskToDB(MainController.selectedTask, nuevaTarea);
            } else {

                ListaTareas.uploadTaskToDB(nuevaTarea);
            }
            actionCancelTask(event);
        } else {

            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setContentText("Ningún campo puede estar vacío.");
            error.showAndWait();
        }
    }

    /**
     * Comprobante del botón selector de estado de la aplicación. Según éste, done = true | done = false
     * @param event> Evento que lanza esta acción
     */
    @FXML
    void isDone(ActionEvent event) {
        if (((Node)event.getSource()).getId().equals(buttonDoneYes.getId())){

            done = true;
            buttonDoneYes.setStyle("-fx-background-color: green");
            buttonDoneNo.setStyle("-fx-background-color: black");
        } else if (((Node)event.getSource()).getId().equals(buttonDoneNo.getId())){

            done = false;
            buttonDoneYes.setStyle("-fx-background-color: black");
            buttonDoneNo.setStyle("-fx-background-color: green");
        }
    }

}
