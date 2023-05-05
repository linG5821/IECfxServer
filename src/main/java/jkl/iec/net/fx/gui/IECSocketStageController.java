package jkl.iec.net.fx.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jkl.iec.net.sockets.IECSocketParameter;

public class IECSocketStageController  {

//    @FXML private URL location;
	@FXML protected Pane rootPane;
    @FXML private Label SockLabel;
    @FXML private TextField t0text;
    @FXML private TextField t1text;
    @FXML private TextField t2text;
    @FXML private TextField t3text;
    @FXML private TextField wtext;
    @FXML private TextField ktext;


    protected IECSocketParameter p;
	private String name;
    
	@FXML protected void handleOnKeyPressed(KeyEvent ke ) {
        System.out.println("Key Pressed: " + ke.getCode());
        if (ke.getCode() == KeyCode.ESCAPE)  {
        	closeStage();
        }
        if (ke.getCode() == KeyCode.ENTER)  {
            	saveStage();
            }
    }

	protected void closeStage() {
		Stage stage = (Stage) rootPane.getScene().getWindow();
		stage.close();	
	}

	
	void saveStage() {
		closeStage();
	}

	void loadStage() {
		SockLabel.setText(name);
		t0text.setText(String.valueOf(p.T0));
		t1text.setText(String.valueOf(p.T1));
		t2text.setText(String.valueOf(p.T2));
		t3text.setText(String.valueOf(p.T3));
		wtext.setText(String.valueOf(p.W));
		ktext.setText(String.valueOf(p.K));
	}
	
	public void init(String n,IECSocketParameter o) {
		this.p=o;
		this.name = n;
		loadStage();
		}
	
    public void initialize() {
    }
}
