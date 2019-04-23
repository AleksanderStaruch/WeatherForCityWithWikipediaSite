import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.*;
import java.awt.*;

public class Main extends JFrame{

  public Main(){
    setSize(900,700);
    setLayout(new BorderLayout());

    JTextPane country = new JTextPane();country.setText("Great Britain");
    JTextPane city = new JTextPane();city.setText("London");
    JButton refresh = new JButton("Refresh");

    Service service = new Service(country.getText());

    JPanel myPanel = new JPanel();this.add(myPanel,BorderLayout.PAGE_START);
    myPanel.add(new JLabel("Country:"));
    myPanel.add(country);
    myPanel.add(Box.createHorizontalStrut(40));
    myPanel.add(new JLabel("City:"));
    myPanel.add(city);
    myPanel.add(refresh);

    JFXPanel web= new JFXPanel();this.add(web,BorderLayout.CENTER);
    Platform.runLater(new Runnable() {
      public void run() {
        initFX(web,"https://en.wikipedia.org/wiki/"+city.getText());
      }
    });//"https://en.wikipedia.org/wiki/"+city.getText()

    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(3,1));
    this.add(panel,BorderLayout.PAGE_END);
    JPanel panel1 = new JPanel();panel.add(panel1);
    JPanel panel2 = new JPanel();panel.add(panel2);
    JPanel panel3 = new JPanel();panel.add(panel3);

    JLabel w=new JLabel(service.getWeather(city.getText()));panel1.add(w);

    JLabel text= new JLabel("1 "+service.currency+" is ");
    JTextPane rate=new JTextPane();rate.setText("USD");
    JLabel r=new JLabel(service.getRateFor(rate.getText())+"");
    panel2.add(text);panel2.add(r);panel2.add(rate);

    JLabel n=new JLabel(service.getNBPRate()+"");panel3.add(n);

    refresh.addActionListener((e)->{
      Service tmp=new Service(country.getText());
      text.setText("1 "+tmp.currency+" is ");
      w.setText(tmp.getWeather(city.getText()));
      r.setText(tmp.getRateFor(rate.getText())+"");
      n.setText(tmp.getNBPRate()+"");

      Platform.runLater(new Runnable() {
        public void run() {
          initFX(web,"https://en.wikipedia.org/wiki/"+city.getText());
        }
      });
    });

    this.setTitle("TPO2");
    this.setVisible(true);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    SwingUtilities.updateComponentTreeUI(this);
  }

  private static void initFX(JFXPanel fxPanel,String s) {
    Scene scene = createScene(s);
    fxPanel.setScene(scene);
  }

  private static Scene createScene(String s) {
    WebView webView = new WebView();
    WebEngine webEngine = webView.getEngine();
    webEngine.load(s);

    VBox root = new VBox();
    root.getChildren().add(webView);
    Scene scene = new Scene(root);

    return scene;
  }

  public static void main(String[] args) {
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();
    System.out.println(weatherJson);
    System.out.println(rate1);
    System.out.println(rate2);

    EventQueue.invokeLater(()->new Main());
  }
}