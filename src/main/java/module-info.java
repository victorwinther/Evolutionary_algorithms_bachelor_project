module group.ea {
    requires javafx.controls;
    requires javafx.fxml;


    opens group.ea to javafx.fxml;
    exports group.ea;
    exports group.ea.controllers;
    opens group.ea.controllers to javafx.fxml;
}