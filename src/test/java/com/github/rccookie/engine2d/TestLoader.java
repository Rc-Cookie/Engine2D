package com.github.rccookie.engine2d;

import java.io.IOException;
import java.net.Inet4Address;

import com.github.rccookie.engine2d.components.Follower;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Font;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.online.HTTPRequest;
import com.github.rccookie.engine2d.physics.BoxCollider;
import com.github.rccookie.engine2d.ui.ColorPanel;
import com.github.rccookie.engine2d.ui.Dimension;
import com.github.rccookie.engine2d.ui.Fade;
import com.github.rccookie.engine2d.ui.FloatInputField;
import com.github.rccookie.engine2d.ui.IconPanel;
import com.github.rccookie.engine2d.ui.RadioButton;
import com.github.rccookie.engine2d.ui.SimpleList;
import com.github.rccookie.engine2d.ui.StackView;
import com.github.rccookie.engine2d.ui.Structure;
import com.github.rccookie.engine2d.ui.TextButton;
import com.github.rccookie.engine2d.ui.TextCheckbox;
import com.github.rccookie.engine2d.ui.TextInputField;
import com.github.rccookie.engine2d.image.Theme;
import com.github.rccookie.engine2d.ui.Toggle;
import com.github.rccookie.engine2d.ui.ValueInputField;
import com.github.rccookie.engine2d.ui.debug.DebugPanel;
import com.github.rccookie.engine2d.ui.util.ToggleGroup;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.util.Console;

import org.jetbrains.annotations.TestOnly;

@TestOnly
class TestLoader implements ILoader {

    @Override
    public void load() {

//        Camera camera = new Camera(new IVec2(600, 400));
//
//        //camera.setGameObject(new GameObject());
//        camera.setBackgroundColor(Color.RED);

        Map map = new Map();
        map.setGravity(new float2(0, -9.81f));
        map.setGravity(float2.zero);

//        Online.connect("localhost");

        GameObject cameraObject = new GameObject();
        cameraObject.setMap(map);
        cameraObject.setImage(new Image(new int2(20, 40), Color.RED));
        cameraObject.location.x = 250;
        cameraObject.location.y = 100;
        cameraObject.velocity.x = -100;
        cameraObject.usePhysics(true);
        new BoxCollider(cameraObject, float2.one.scaled(10));
        cameraObject.rotation = 90;

        GameObject gameObject = new GameObject();
        gameObject.setImage(new Image(int2.one.scaled(32), Color.BLUE));
        gameObject.setImage(Font.defaultF(32).render("Hello\nWorld!\n★★", Color.BLUE));
        gameObject.usePhysics(true);
        gameObject.getComponent(Collider.class).setRestitution(1);
        gameObject.setMap(map);

//        Settings.maxTranslation = 200f;
//        Settings.maxTranslationSquared = 200f * 200f;

        Debug.bindOverlayToggle();

        UI ui = new UI();
        ui.setThemedCameraBackground(true);

        UIObject uiObject = new ColorPanel(ui, new int2(400, 100), t -> t.second);
        uiObject.relativeLoc.y = 1;
        UIObject uiObject1 = new ColorPanel(uiObject, int2.one.scaled(16), Color.PINK);
        uiObject1.relativeLoc.set(-1, -1);
        TextButton button = new TextButton(ui, "A button with a very long title string");
        button.onClick.add(() -> button.text.setText(button.text.getText().startsWith("A") ? "Button" : "A button with a very long title string"));
        button.onClick.add(Debug::printUI);
        button.relativeLoc.y = -.5f;

        TextCheckbox checkbox = new TextCheckbox(uiObject, "Dark mode");
        checkbox.relativeLoc.x = -1;
        checkbox.offset.x = 5;
        checkbox.onToggle.add(on -> ui.setTheme(on ? Theme.DARK : Theme.BRIGHT));
        checkbox.onToggle.add(on -> Console.map("On", on));
        checkbox.setOn(ui.getTheme() == Theme.DARK);

        TextCheckbox pixelPerfectBox = new TextCheckbox(uiObject, "Pixel perfect ui");
        pixelPerfectBox.relativeLoc.x = 1;
        pixelPerfectBox.offset.x = -5;
        pixelPerfectBox.onToggle.add(ui::setPixelPerfectMouse);

        TextCheckbox debugToggle = new TextCheckbox(uiObject, "Debug overlay");
        debugToggle.relativeLoc.x = -1;
        debugToggle.relativeLoc.y = 0.5f;
        debugToggle.offset.x = 5;
        debugToggle.onToggle.add(Debug::setOverlay);

        Fade fade = new Fade(ui);
        fade.onComplete.add(s -> {
            if(s) fade.fadeOut();
        });
//        fade.input.addKeyPressListener(fade::fadeIn, "f");

        StackView stack = new StackView(new Dimension(uiObject, 160, 0), StackView.Orientation.TOP_TO_BOTTOM);//new SimpleList(uiObject, true);
        stack.setStartGapSize(10);
        Structure list1 = new SimpleList(stack, false);//, list2 = new SimpleList(stack, false);
        Toggle t1 = new RadioButton(list1), t2 = new RadioButton(list1),
               t3 = new RadioButton(stack);
//        t1.relativeLoc.y = -0.5f;
//        t3.relativeLoc.y = 0.5f;
        new ToggleGroup<>(t1, t2, t3).onToggle.add(t -> Console.map("Selected", t));
        Input.addKeyPressListener(() -> ((Dimension) stack.getParent()).setSize(new int2(80, 0)), "+");

        ColorPanel listSize = new ColorPanel(uiObject, int2.one, Color.RED);
        listSize.moveToBack();
        Input.addKeyPressListener(() -> listSize.setSize(list1.getSize()), "0");
        Input.addKeyPressListener(list1::modified, "#");

        ColorPanel maxListSize = new ColorPanel(uiObject, stack.getParent().getSize(), Color.DARK_GRAY.brighter());
        stack.onParentSizeChange.add(() -> maxListSize.setSize(stack.getParent().getSize()));
        list1.onParentSizeChange.add(s -> Console.map("List parent size changed", s));
        maxListSize.moveToBack();

        Image icon = new Image(20, 20, Color.RED);
        icon.fillRect(int2.one, icon.size.added(-2, -2), Color.BLUE);
        IconPanel panel = new IconPanel(ui, icon);
        panel.relativeLoc.x = 1;
        panel.onClick.add(() -> stack.getChildren().forEach(o -> o.relativeLoc.setZero()));
        panel.onClick.add(() -> stack.getChildren().forEach(o -> o.offset.setZero()));
        panel.onClick.add(() -> stack.setOrientation(StackView.Orientation.values()[(stack.getOrientation().ordinal() + 1) & 3]));
        panel.onClick.add(() -> Console.map("Orientation", stack.getOrientation()));

        Input.addKeyPressListener(() -> Console.map("List", list1.toString()), "0");

        Camera camera = new Camera(new int2(600, 400));

        GameObject cameraManager = new GameObject();
        cameraManager.setImage(new Image(int2.one.scaled(30), Color.BLACK));
        cameraManager.getImage().setAlpha(127);
        cameraManager.getComponent(Collider.class).setRestitution(0);
//        cameraManager.getComponent(Collider.class).setDensity(1);
        cameraManager.setBackgroundFriction(1);
        cameraManager.usePhysics(true);
        cameraManager.setMap(map);
        Input.addKeyListener(() -> camera.setGameObject(cameraObject), "1");
        Input.addKeyListener(() -> camera.setGameObject(gameObject), "2");
        cameraManager.input.addKeyListener(() -> Console.map("Velocity", cameraObject.velocity, cameraObject.rotation), "t");
        cameraManager.input.addKeyPressListener(() -> cameraManager.velocity.y += 300, "s", "down");
        cameraManager.input.addKeyPressListener(() -> cameraManager.velocity.y -= 300, "w", "up");
        cameraManager.input.addKeyPressListener(() -> cameraManager.velocity.x += 300, "d", "right");
        cameraManager.input.addKeyPressListener(() -> cameraManager.velocity.x -= 300, "a", "left");
        cameraManager.input.addKeyReleaseListener(() -> cameraManager.velocity.y = 0, "s", "down");
        cameraManager.input.addKeyReleaseListener(() -> cameraManager.velocity.y = 0, "w", "up");
        cameraManager.input.addKeyReleaseListener(() -> cameraManager.velocity.x = 0, "d", "right");
        cameraManager.input.addKeyReleaseListener(() -> cameraManager.velocity.x = 0, "a", "left");
        cameraManager.input.addKeyListener(() -> cameraManager.angle += 90 * Time.delta(), "e");
        cameraManager.input.addKeyListener(() -> cameraManager.angle -= 90 * Time.delta(), "q");

        cameraManager.input.addKeyPressListener(() -> cameraManager.setFixedRotation(!cameraManager.isFixedRotation()), "f");

        GameObject cameraHolder = new GameObject();
        Follower follower = new Follower(cameraHolder, cameraManager);
        follower.setFollowAngle(false);

        camera.update.add(() -> {
            Mouse m = camera.input.getMouse();
            if(!m.pressed) return;
            GameObject o = new GameObject();
            o.setImage(new Image(int2.one.scaled(10), Color.GREEN));
            o.location.set(camera.pixelToPoint(m.pixel));
            o.setMap(map);
        });

        camera.setGameObject(cameraHolder);
        camera.setUI(ui);

        Execute.when(() -> uiObject1.update.add(() -> uiObject1.relativeLoc.x = Math.min(1, uiObject1.relativeLoc.x + Time.delta())), () -> Input.getKeyState(" "));
        Execute.when(cameraObject::remove, () -> Input.getKeyState("r"));
        Execute.when(() -> camera.setBackgroundColor(Color.DARK_GRAY.setBlue(1f)), () -> Input.getKeyState("c"));
        Execute.when(() -> camera.setGameObject(null), () -> Input.getKeyState("n"));

        Application.lateUpdate.add(() -> {
            if(Input.getKeyState("p"))
                throw new RuntimeException();
        });

        ui.onChildChange.add((o,t) -> {
            if(t == UIObject.ChangeType.ADDED && o instanceof DebugPanel)
                o.onEnable.add(debugToggle::silentSetOn);
        });

        TextInputField inputField = new TextInputField(ui, "Chat here");
        inputField.relativeLoc.y = -1;
//        inputField.onSubmit.add(s -> Online.share("messageReceived", new JsonObject("message", s, "source", getIpAddress())));
//        inputField.onSubmit.add(() -> Online.send("messageReport", new JsonObject("action", "messageSent")));
//        Online.registerShareProcessor("messageReceived", d -> {
//            Console.map("Message received from {}", d.json.get("message"), d.json.get("source"));
//            Online.send("messageReport", new JsonObject("action", "messageReceived"));
//        });
//        Online.registerSendProcessor("messageReport", d -> Console.map("Received report", d.json));

        ValueInputField<Double> numberField = new FloatInputField(ui, -1, x -> Math.max(-10, Math.min(10, x)));
        Console.map("Start value", numberField.getValue());
        numberField.onValue.add(s -> Console.map("Typed", s));
        numberField.offset.y = 30;
        numberField.setGrayDefault(false);



        fade.moveToTop();

        Execute.when(() -> Console.log(ui.stream().count()), () -> Input.getKeyState("ü"));
        Execute.when(() -> Console.log(ui.getAllChildren().size()), () -> Input.getKeyState("ü"));
        Input.addKeyPressListener(() -> Console.log(new JsonObject("key", "value")), "j");

        wideObject = new GameObject();
        wideObject.setImage(new Image(new int2(10, 100), Color.ORANGE));
        wideObject.location.x = -100;
        wideObject.setMap(map);

        GameObject circle = new GameObject();
        circle.setImage(new Image(new int2(102, 102), Color.ORANGE.setAlpha(.2f)));
        circle.removeComponent(Collider.class);
        circle.location.x = -100;
        circle.setMap(map);
        circle.update.add(() -> circle.angle = float2.between(circle.location, cameraManager.location).angle());

        Input.addKeyPressListener(() -> {
            Image text = new Font("Fira Code", 20).render("This text is toooooooooooo long for a single line.", Color.BLACK, 100);
            Image textImage = new Image(text.size, Color.RED);
            textImage.drawImage(text, int2.zero);
            Image image = new Image(100, textImage.size.y, Color.GREEN);
            image.drawImage(textImage, int2.zero);
            gameObject.setImage(image);
            Console.log(text);
        }, "i");

//        ColorPanel markdownBack = new ColorPanel(ui, new int2(520, 320), Color.LIGHT_GRAY);
//        markdownBack.onParentSizeChange.add(s -> markdownBack.setSize(s.added(-80, -80)));
//        Dimension markdownArea = new Dimension(markdownBack, 500, 300);
//        markdownArea.onParentSizeChange.add(s -> markdownArea.setSize(s.added(-20, -20)));
//        try {
//            new MarkdownPanel(markdownArea, Markdown.load("src/test/resources/resources/blatt5.md"));
//        } catch(Exception e) { Console.error(e); }
//        new TextEditor(markdownArea).setFont(new Font("Fira Code", 20, Font.UNDERLINED));

        Input.keyPressed.add(k -> Console.map("Key pressed", k));
        Input.addKeyListener(() -> {try{Thread.sleep(100);}catch(Exception e){Console.error(e);}}, "ä");

//        new LocationSyncHost(cameraManager);
//        GameObject otherCamera = new GameObject();
//        otherCamera.setImage(new Image(int2.one.scaled(30), Color.ORANGE));
//        otherCamera.getComponent(Collider.class).setRestitution(0);
//        otherCamera.setBackgroundFriction(1);
//        otherCamera.usePhysics(true);
//        otherCamera.setMap(map);
//        new LocationSyncApplier(otherCamera, 0);

//        ui.update.add(() -> Console.log(ui.getObjectsAt(Input.getMouse().pixel, true)));

        Application.setMaxFps(144);

//        Application.startCoroutine(() -> {
//            while(true) {
//                Console.map("Frame", Time.frame());
//                Coroutine.waitForNextFrame();
//            }
//        });

        Input.addKeyPressListener(() -> {
            new HTTPRequest("https://www.greenfoot.org/users/56824/follow")
                    .setMethod(HTTPRequest.Method.POST)
                    .setContentType("application/x-www-form-urlencoded")
                    .setCookies("__utma=208407620.2050265042.1643226985.1643226985.1643226985.1; __utmz=208407620.1643226985.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); user_id=52320; user_code=91b3a7c6bbce1639cbbfc0dfa06d1e702126c941d27ca0afa196b0288ac1bf00; _myothergame_session=cjV6Uk9vMFNvNFBCQWhoMXFrS0dBeWZyc3dJOGl0eE1sb0hKQmtFL0IwN1BSYVgxeDRjWE5XayttS0NGWUJreVBsNVhmRHhtbTA4Rnh1eXhYUFI3RjV0WFByYTlJNUFLVjZzWC9jd0UwT1hmNFd4M243am5jWnF4R28wZSszVUdEY1pkMlRnNy85UzBtMGxuYXdRSmhBPT0tLWs3S1hDODNtcVVaTmJXc0V3bVlrUnc9PQ%3D%3D--a044f3a2cc7d1df2095484bce97d384a0266d523")
                    .send("_method=put&authenticity_token=a6m5O6n05BY2q1osmCDFgXfw9ncN1DNIgYBL9tdcu6s%3D")
                    .then(r -> System.out.println(r));
        }, "8");
        Input.addKeyPressListener(() -> {
            new HTTPRequest("https://www.greenfoot.org/users/56824/unfollow")
                    .setMethod(HTTPRequest.Method.POST)
                    .setContentType("application/x-www-form-urlencoded")
                    .setCookies("__utma=208407620.2050265042.1643226985.1643226985.1643226985.1; __utmz=208407620.1643226985.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); user_id=52320; user_code=91b3a7c6bbce1639cbbfc0dfa06d1e702126c941d27ca0afa196b0288ac1bf00; _myothergame_session=cjV6Uk9vMFNvNFBCQWhoMXFrS0dBeWZyc3dJOGl0eE1sb0hKQmtFL0IwN1BSYVgxeDRjWE5XayttS0NGWUJreVBsNVhmRHhtbTA4Rnh1eXhYUFI3RjV0WFByYTlJNUFLVjZzWC9jd0UwT1hmNFd4M243am5jWnF4R28wZSszVUdEY1pkMlRnNy85UzBtMGxuYXdRSmhBPT0tLWs3S1hDODNtcVVaTmJXc0V3bVlrUnc9PQ%3D%3D--a044f3a2cc7d1df2095484bce97d384a0266d523")
                    .send("_method=put&authenticity_token=a6m5O6n05BY2q1osmCDFgXfw9ncN1DNIgYBL9tdcu6s%3D")
                    .then(r -> System.out.println(r));
        }, "9");

        Application.setMaxFps(144);

//        Console.log(Application.readFile("test.txt"));
    }

    public static GameObject wideObject;

    private static String getIpAddress() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch(IOException e) {
            return "unknown";
        }
    }

    public static void main(String[] args) {
        System.out.println(TestLoader.class.getClassLoader().getResource("/resources/blatt5.md"));
    }
}
