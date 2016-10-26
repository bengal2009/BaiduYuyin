/**
 * Created by bennylin on 2016/10/17.
 */
import javazoom.jl.player.*;
import java.io.FileInputStream;
public class playmp3 {
    public static void main(String[] args) {
        try{

            FileInputStream fis = new FileInputStream("1.MP3");
            Player playMP3 = new Player(fis);

            playMP3.play();

        }catch(Exception e){System.out.println(e);}
    }

}
