package com.actividades.gestortareas;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.Properties;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.*;
import static org.bson.codecs.configuration.CodecRegistries.*;

public class ListaTareas {

    private Properties propiedades;
    private static MongoClient clienteMongo;
    private MongoDatabase databaseMongo;
    private static MongoCollection<Tarea> coleccionTareas;

    private static ObservableList<Tarea> listaTareas;

    public ListaTareas(Properties propiedades) {

        this.propiedades = propiedades;
        clienteMongo= MongoClients.create(propiedades.getProperty("MONGODB_URI"));
        databaseMongo = clienteMongo.getDatabase(propiedades.getProperty("MONGODB_DATABASE")).
                withCodecRegistry(fromRegistries(getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build())));
        coleccionTareas = databaseMongo.getCollection(propiedades.getProperty("MONGODB_COLLECTION"), Tarea.class);
        listaTareas = coleccionTareas.find().limit(MainController.tableRows).into(FXCollections.observableArrayList());

        if (listaTareas.size() == 0) {
            cargarDatosEjemplo(true);
        }
    }

    /**
     * Carga datos de ejemplo en la tabla de la aplicación. Estos no se guardarán en la base de datos
     * a menos que se especifique.
     * @param upload> 'true' si los datos deben subirse a la base de datos, 'false' de otro modo
     */
    public void cargarDatosEjemplo(boolean upload) {
        Tarea t1 = new Tarea(LocalDate.now().format(Tarea.dateFormatter),"Hoy", false);
        Tarea t2 = new Tarea(LocalDate.EPOCH.format(Tarea.dateFormatter),"Epoch",true);
        listaTareas.addAll(t1,t2);
        if (upload) {
            uploadTaskToDB(t1);
            uploadTaskToDB(t2);
        }
    }

    /**
     * Sube una tarea a la base de datos MongoDB
     * @param t> Tarea a subir
     */
    public static void uploadTaskToDB(Tarea t) {

        Document query = new Document().append("descripcion", t.getDescripcion());
        Bson updates = Updates.combine(
                Updates.set("fecha", t.getFecha()),
                Updates.set("descripcion", t.getDescripcion()),
                Updates.set("finalizada", t.getFinalizada()));
        UpdateOptions options = new UpdateOptions().upsert(true);

        UpdateResult result = coleccionTareas.updateOne(query, updates, options);

        try {
            BsonValue bv = result.getUpsertedId();
            if (!bv.isNull()) {

                System.out.println("Nuevo elemento " + bv + " insertado correctamente.");
            } else {

                System.out.println("Elemento " + t.getId() + " actualizado.");
            }

        } catch (NullPointerException ignored) {}
    }

    /**
     * Modifica una tarea de la base de datos MongoDB
     * @param v1> Tarea a editar
     * @param v2> Tarea nueva
     */
    public static void uploadTaskToDB(Tarea v1, Tarea v2) {

        Document query = new Document().append("descripcion", v1.getDescripcion()).append("fecha", v1.getFecha());
        Bson updates = Updates.combine(
                Updates.set("fecha", v2.getFecha()),
                Updates.set("descripcion", v2.getDescripcion()),
                Updates.set("finalizada", v2.getFinalizada()));

        UpdateResult result = coleccionTareas.updateOne(query, updates);

        System.out.println("Elemento " + v1.getId() + " actualizado.");
    }

    /**
     * Elimina una tarea de la base de datos
     * @param t> Tarea que se quiere eliminar
     */
    public static void dropTaskFromDB(Tarea t) {

        Document query = new Document().append("descripcion", t.getDescripcion());
        Tarea deletedTask = coleccionTareas.findOneAndDelete(query);
        System.out.println("Tarea eliminada de la base de datos correctamente: \n"+deletedTask);
    }

    public static void stopDB() {

        clienteMongo.close();
    }

    public static MongoCollection<Tarea> getColeccionTareas() {
        return coleccionTareas;
    }

    public static ObservableList<Tarea> getListaTareas() {

        return listaTareas;
    }

    @Override
    public String toString() {

        StringBuilder x = new StringBuilder("Lista de Tareas: ");

        for (Tarea t : listaTareas) x.append(t.toString()).append(",");
        x.deleteCharAt(x.length());

        return x.toString();
    }
}
