package org.astroEngine.Goodboise;

import org.astroEngine.comp.Animation;
import org.astroEngine.comp.ShapeComp;
import org.astroEngine.shapes.GameObject;
import org.astroEngine.graphics.ImageSprite;
import org.astroEngine.util.Builder.GameObjectBuilder;
import org.astroEngine.util.FileUtils;
import org.astroEngine.util.GameUtils;
import org.astroEngine.AEWindow;
import org.joml.Vector3d;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bros extends AEWindow {
    GameObject leo, manas;

    public Bros() {
        super(new Dimension(800, 600), "We are bros - Leo & Manas Manushya");

        try {
            leo = new GameObjectBuilder()
                    .setTranslate(new Vector3d(200, 200, 0))
                    .setScale(new Vector3d(1))
                    .addComponent(new ShapeComp(
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
                .addComponent(new ShapeComp(
                        new ImageSprite(FileUtils.internal("/Manas.png"))))
                .addComponent(new Animation(GameUtils.packTextureFromResource(
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
