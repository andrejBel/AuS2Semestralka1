package GUI.Controller;

import GUI.AsyncTask;
import InformacnySystem.ISSpravyKatastra;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSpinner;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class CGenerator extends ControllerBase {

    @FXML
    private JFXSlider sliderKatastralneUzemie_;

    @FXML
    private JFXSlider sliderObcania_;

    @FXML
    private JFXSlider sliderPocetObyvatelovSTrvalymPobytom;

    @FXML
    private JFXSlider sliderListVlastnictva_;

    @FXML
    private JFXSlider sliderPocetObyvatelovVListeVl_;

    @FXML
    private JFXSlider sliderPocetNehnutelnostiVListeVl_;

    @FXML
    private JFXButton buttonGeneruj;

    public CGenerator(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        assert sliderKatastralneUzemie_ != null;
        assert sliderObcania_ != null;
        assert sliderListVlastnictva_ != null;
        assert sliderPocetObyvatelovVListeVl_ != null;
        assert sliderPocetNehnutelnostiVListeVl_ != null;
        assert buttonGeneruj != null;
        buttonGeneruj.setOnAction(event -> {
            new GeneratorLoader().execute();

        });
    }

    @Override
    protected void initView() {
        loadView();
    }

    @Override
    protected String getViewFileName() {
        return "generator.fxml";
    }

    @Override
    public String getViewName() {
        return "Generátor operácii";
    }

    private class GeneratorLoader extends AsyncTask {

        @Override
        public void onPreExecute() {
            showSpinner("Generovanie dát");
        }

        @Override
        public Object doInBackground(Object[] params) throws InterruptedException {
            int pocetKatastralnychUzemi = (int) sliderKatastralneUzemie_.getValue();
            int celkovyPocetObcanov = (int) sliderObcania_.getValue();
            int pocetObyvatelovSTravlymPobytom = (int) sliderPocetObyvatelovSTrvalymPobytom.getValue();
            int pocetListovVlastnictvaVKatastralnomUzemi = (int) sliderListVlastnictva_.getValue();
            int pocetVlastnikovNaListeVlastnictva = (int) sliderPocetObyvatelovVListeVl_.getValue();
            int pocetNehnutelnostiNaListeVlastnictva = (int) sliderPocetNehnutelnostiVListeVl_.getValue();
            System.out.println("pocetKatastralnychUzemi: " + pocetKatastralnychUzemi);
            System.out.println("celkovyPocetObcanov: " + celkovyPocetObcanov);
            System.out.println("pocetListovVlastnictvaVKatastralnomUzemi: " + pocetListovVlastnictvaVKatastralnomUzemi);
            System.out.println("pocetVlastnikovNaListeVlastnictva: " + pocetVlastnikovNaListeVlastnictva);
            System.out.println("pocetNehnutelnostiNaListeVlastnictva: " + pocetNehnutelnostiNaListeVlastnictva);
            return new Boolean(isSpravyKatastra_.generujData( pocetKatastralnychUzemi, celkovyPocetObcanov, pocetObyvatelovSTravlymPobytom, pocetListovVlastnictvaVKatastralnomUzemi, pocetVlastnikovNaListeVlastnictva, pocetNehnutelnostiNaListeVlastnictva));
        }

        @Override
        public void onPostExecute(Object params) {
            boolean generateResult = (Boolean) params;
            if (generateResult) {

                showSuccessDialog("Generovanie dát úspešné", false);
            } else {

                showWarningDialog("Generovanie dát neúspešné", false);
            }
        }

        @Override
        public void progressCallback(Object[] params) {

        }

        @Override
        public void onFail(Exception e) {
            showWarningDialog("Generovanie dát neúspešné - vážna chyba");
        }
    }


}
