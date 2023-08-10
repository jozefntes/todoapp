module com.example {
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires javafx.controls;
    requires javafx.fxml;
    

    opens com.example to javafx.fxml;
    exports com.example;
}
