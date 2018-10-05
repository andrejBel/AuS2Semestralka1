package GUI.Controller;

import InformacnySystem.ISSpravyKatastra;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;

public class C18PridanieNehnutelnosti extends ControllerBase {

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldCisloListuVlastnictva;

    @FXML
    private JFXTextField textFieldSupisneCisloNehnutelnosti;

    @FXML
    private JFXTextField textFieldAdresaNehnutelnosti;

    @FXML
    private JFXTextField textFieldPopisNehnutelnosti;

    @FXML
    private JFXButton buttonPridajNehnutelnost;

    private SimpleBooleanProperty isCisloKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isCisloListuVlastnictvaOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isSupisneCisloOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isAdsresaNehnutelnostiOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isPopisNehnutelnostiOk = new SimpleBooleanProperty(false);

    public C18PridanieNehnutelnosti(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();

        Helper.decorateNumberTextFieldWithValidator(textFieldCisloKatastralnehoUzemia);
        Helper.decorateNumberTextFieldWithValidator(textFieldCisloListuVlastnictva);
        Helper.decorateNumberTextFieldWithValidator(textFieldSupisneCisloNehnutelnosti);
        Helper.decorateTextFieldWithValidator(textFieldAdresaNehnutelnosti);
        Helper.decorateTextFieldWithValidator(textFieldPopisNehnutelnosti);



    }

    @Override
    protected void initView() {
        loadView();
    }

    @Override
    protected String getViewFileName() {
        return "18pridanieNehnutelnosti.fxml";
    }

    @Override
    public String getViewName() {
        return "18. Pridanie nehnuteľnosti na list vlastníctva";
    }
}
