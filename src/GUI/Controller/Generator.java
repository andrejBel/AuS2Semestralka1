package GUI.Controller;

import InformacnySystem.ISSpravyKatastra;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.fxml.FXML;

public class Generator extends ControllerBase {

    @FXML
    private JFXSlider sliderKatastralneUzemie_;
    @FXML
    private JFXSlider sliderObcania_;

    @FXML
    private JFXSlider sliderListVlastnictva_;

    @FXML
    private JFXSlider sliderPocetObyvatelovVListeVl_;

    @FXML
    private JFXSlider sliderPocetNehnutelnostiVListeVl_;

    @FXML
    private JFXButton buttonGeneruj;

    public Generator(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        assert sliderKatastralneUzemie_ != null;
        assert sliderObcania_ != null;
        assert sliderListVlastnictva_ != null;
        assert sliderPocetObyvatelovVListeVl_ != null;
        assert sliderPocetNehnutelnostiVListeVl_ != null;
        assert buttonGeneruj != null;
        buttonGeneruj.setOnAction(event -> {
            System.out.println("Generujem");
            System.out.println(sliderKatastralneUzemie_.getValue());
        });
    }

    @Override
    protected void initView() {
        view_ = loadView();
    }

    @Override
    protected String getViewFileName() {
        return "generator.fxml";
    }

    @Override
    public String getViewName() {
        return "Generátor operácii";
    }

}
