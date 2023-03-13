module com.actividades.gestortareas {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires de.jensd.fx.glyphs.iconsfivetwofive;
    requires de.jensd.fx.glyphs.commons;
    requires org.kordamp.ikonli.javafx;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;

    exports com.actividades.gestortareas;
    opens com.actividades.gestortareas to javafx.fxml;

}