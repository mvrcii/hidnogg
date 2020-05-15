package sample.animation;

import com.sun.prism.Graphics;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.enums.Direction;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

public class DrawFlippedTest extends Application {

    private Canvas canvas;
    private GraphicsContext gc;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("2D Stickfight");
        Group root = new Group();

        canvas = new Canvas(500, 500);
        gc = canvas.getGraphicsContext2D();

        canvas.requestFocus();
        canvas.setFocusTraversable(true);

        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        root.getChildren().add(canvas);
        stage.setScene(new Scene(root));
        stage.show();

        BufferedImage spriteSheet = ImageIO.read(new File("src/unbenannt.png"));
        BufferedImage sourceSprite = spriteSheet.getSubimage(0, 0, 64, 64);
        Image spriteImg = FrameData.convertToFxImage(sourceSprite);


        int x = 200;
        int y = 200;

        gc.drawImage(spriteImg, x, y);

        double startTime = System.currentTimeMillis();
        int i = 50;
        while(i > 0){
            drawHorizontallyFlipped(gc, sourceSprite, x, y);
            i--;
        }
        double time1 = System.currentTimeMillis();
        i = 50;
        while(i > 0){
            drawHorizontallyFlippedAffineTrans(gc,sourceSprite,x,y);
            i--;
        }
        double time2 = System.currentTimeMillis();



        System.out.println("Flip draw: "+(time1-startTime));
        System.out.println("Flip affine: "+(time2-time1));
        System.out.println((time2-time1)/(time1-startTime));
    }

    private static void drawHorizontallyFlippedAffineTrans(GraphicsContext gc, BufferedImage bufferedImage, int x, int y){
        AffineTransform tx = AffineTransform.getScaleInstance(-1,1);
        tx.translate(-bufferedImage.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bufferedImage = op.filter(bufferedImage, null);
        gc.drawImage(FrameData.convertToFxImage(bufferedImage), x-(bufferedImage.getWidth()/2), y);
    }


    // WORKS
    public static void drawHorizontallyFlipped(GraphicsContext gc, BufferedImage bufferedImage, int x, int y){
        double width = bufferedImage.getWidth();
        double height = bufferedImage.getHeight();
        gc.drawImage(FrameData.convertToFxImage(bufferedImage), x+(width/2),y, -width, height);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
