module com.tareq.chatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;

    requires org.controlsfx.controls;
    requires java.persistence;

    exports com.tareq.chatapp.server;
    opens com.tareq.chatapp.server to javafx.fxml;
    exports com.tareq.chatapp.client;
    opens com.tareq.chatapp.client to javafx.fxml;
}