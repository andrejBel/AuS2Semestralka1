package GUI.Controller;

import InformacnySystem.ISSpravyKatastra;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class CHome extends ControllerBase {

    @FXML
    private JFXListView<Tab> listViewTabs;

    public CHome(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();

    }

    public void init( JFXTabPane tabPane) {
        for (Tab tab: tabPane.getTabs()) {
            if (tab.getText().equals(this.getViewName())) {
                continue;
            }
            listViewTabs.getItems().add(tab);
        }

        listViewTabs.setCellFactory(param -> new JFXListCell<Tab>(){

            @Override
            protected void updateItem(Tab item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getText());
                }
            }
        });
        listViewTabs.setExpanded(true);

        listViewTabs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            tabPane.getSelectionModel().select(newValue);
        });
    }

    private void clearSelection() {
        listViewTabs.getSelectionModel().select(null);
    }

    @Override
    public Runnable getRunnableOnSelection() {
        return this::clearSelection;
    }

    @Override
    protected void initView() {
        loadView();
    }

    @Override
    protected String getViewFileName() {
        return "domov.fxml";
    }

    @Override
    public String getViewName() {
        return "Domov";
    }
}
