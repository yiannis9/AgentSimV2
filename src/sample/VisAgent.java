package sample;

import jade.wrapper.StaleProxyException;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class VisAgent extends SimAgent{
    public Text text;
    public Circle circle;
    public Integer infectionRate;
    public String name;
    public String state;

    public VisAgent (String name) throws StaleProxyException {
        this.name = name;
        //creating agent label

            text = new Text();
            text.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
            //splitting at @ so that it looks cleaner in simulation gui
            text.setText(this.name);

        //creating agent circle
        circle = new Circle();
        circle.setCenterX(10.0f);
        circle.setCenterY(10.0f);
        circle.setRadius(10.0f);
        circle.setFill(Color.GREEN);

    }


}
