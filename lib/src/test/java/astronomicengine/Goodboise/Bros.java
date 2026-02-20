package astronomicengine.Goodboise;

import astronomicengine.comp.anim.AnimationComponent;
import astronomicengine.comp.std.DrawableComponent;
import astronomicengine.shapes.GameObject;
import astronomicengine.graphics.img.ImageSprite;
import astronomicengine.util.Builder.GameObjectBuilder;
import astronomicengine.util.FileUtils;
import astronomicengine.util.GameUtils;
import astronomicengine.window.AEWindow;
import org.joml.Vector3d;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bros extends AEWindow {
    GameObject leo, manas;

    public Bros() {
        super(new Dimension(800, 600), "We are bros - Leo & Manas Manushya");
        setBounds(0, 800, 0, 600, -1, 1);

        try {
            leo = new GameObjectBuilder()
                    .setTranslate(new Vector3d(200, 200, 0))
                    .setScale(new Vector3d(1))
                    .addComponent(new DrawableComponent(
                            new ImageSprite(ImageIO.read(getClass().getResource("/Leo.png")))))
                    .build();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        objects.add(leo);

        manas = new GameObjectBuilder()
                .setTranslate(new Vector3d(100, 100, 0))
                .setScale(new Vector3d(1))
                .addComponent(new DrawableComponent(
                        new ImageSprite(FileUtils.internal("/Manas.png"))))
                .addComponent(new AnimationComponent(GameUtils.packTextureFromResource(
                        FileUtils.internal("/pack/testPack/pack.txt")), .25f){{
                            start();
                        }})
                .build();
        objects.add(manas);
    }

    public static void main(String[] args) {
        new Bros().initialStart();
    }
}
