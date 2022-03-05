package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.impl.awt.AWTApplicationLoader;
import com.github.rccookie.engine2d.impl.awt.AWTStartupPrefs;
import com.github.rccookie.engine2d.physics.SimplePlayerController;
import com.github.rccookie.engine2d.ui.IconPanel;
import com.github.rccookie.geometry.performance.Coll3;
import com.github.rccookie.geometry.performance.Ray3;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.geometry.performance.float3;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.geometry.performance.raycast.BVH3;
import com.github.rccookie.geometry.performance.raycast.GPURaycast3;

public class TestLoader3D implements ILoader {

    private static final int ITS = 2;
    private static float z = 0;
    private static int it = 0;
    private static int i=0;
    private static Image image;

    @Override
    public void load() {
        BVH3 bvh = new BVH3();
        bvh.add(new float3(50, -10, -10), new float3(10, 20, 0), new float3(0, 0, 20), true);
        bvh.add(new float3(70, 10, 10), new float3(0, -20, 0), new float3(0, 0, -20), true);

        for(int i=0; i<20000; i++)
            bvh.add(float3.random(-2000, 4000), float3.random(-20, 40), float3.random(-20, 40), false);

        Camera camera = new Camera(new int2(600, 399));
        Map map = new Map();
        GameObject player = new GameObject();

        player.setMap(map);
        camera.setGameObject(player);

        camera.setBackgroundColor(Color.DARK_GRAY);
        camera.setUI(new UI());

//        player.setImage(new Image(20, 20, Color.RED));
        SimplePlayerController controller = new SimplePlayerController(player);
        controller.setBoostFactor(5);
        controller.setTurningSpeed(90);
        player.update.add(() -> {
            if(Input.getKeyState("r")) z += 100 * Time.delta();
            if(Input.getKeyState("f")) z -= 100 * Time.delta();
        });

        GameObject reference = new GameObject();
        reference.location.y = 100;
//        reference.setImage(new Image(20, 20, Color.LIGHT_GRAY));
        reference.setMap(map);

        IconPanel display = new IconPanel(camera.getUI(), new Image(camera.getResolution()));
        display.update.add(() -> {
            image = display.getImage();
            if(!image.size.equals(camera.getResolution())) {
                image = new Image(camera.getResolution());
                display.setIcon(image);
            }

            Ray3[] rays = new Ray3[(image.size.x * image.size.y + ITS - 1) / ITS];
            i = 0;
            for(int w=0; w<image.size.x; w++)
//            IntStream.range(0, image.size.x).parallel().forEach(w -> {
                for(int h = 0; h<image.size.y; h++) {
                    if((w*image.size.y+h) % ITS != it) continue;
                    float2 xy = new float2(1, w * 1.2f / image.size.x - 0.6f).rotate(player.angle - 90);
                    rays[i++] = new Ray3(new float3(player.location.x, player.location.y, z), new float3(xy.x, xy.y, 0.4f - h * 0.8f / image.size.y));
                }
//            });

            Coll3[] colls = GPURaycast3.raycast(bvh, rays);

            i = 0;
//            IntStream.range(0, image.size.x).parallel().forEach(w -> {
            for(int w=0; w<image.size.x; w++) {
                for (int h = 0; h < image.size.y; h++) {
                    if((w * image.size.y + h) % ITS != it) continue;
                    if(colls[i] != null) {
                        image.setPixel(new int2(w, h), new Color(Math.min(1, 1 / (colls[i].rI / 200)), 0, 0));
                    } else image.setPixel(new int2(w, h), Color.CLEAR);
                    i++;
                }
            }//);

            if(++it >= ITS) it = 0;
        });

        Debug.bindOverlayToggle();
    }


    public static void main(String[] args) {
        new AWTApplicationLoader(new TestLoader3D(), new AWTStartupPrefs()) { };
    }
}
