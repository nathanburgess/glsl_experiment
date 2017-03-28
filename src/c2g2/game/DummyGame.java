package c2g2.game;

import c2g2.engine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import c2g2.engine.GameItem;
import c2g2.engine.IGameLogic;
import c2g2.engine.MouseInput;
import c2g2.engine.Window;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.ArrayList;

public class DummyGame implements IGameLogic
{

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private static final float SCALE_STEP = 0.01f;

    private static final float TRANSLATE_STEP = 0.01f;

    private static final float ROTATION_STEP = 0.3f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private GameItem[] gameItems;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private DirectionalLight directionalLight;

    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;

    private int currentObj;

    private int currentShaderIndex;

    public DummyGame()
    {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
        currentObj = 0;
        currentShaderIndex = 0;
    }

    @Override
    public void init(Window window) throws Exception
    {
        renderer.init(window);
        float reflectance = 1f;
        ArrayList<GameItem> objects = new ArrayList<>();

        Texture cubeTex = new Texture("/resources/textures/grassblock.png");
        Mesh cubeMesh = OBJLoader.loadMesh("src/resources/models/cube.obj");
        cubeMesh.name = "cube";
        cubeMesh.setMaterial(new Material(new Vector3f(0.2f, 0.5f, 0.5f), reflectance, cubeTex));
        objects.add(new GameItem(cubeMesh));

        Texture capTex = new Texture("/resources/textures/cap.png");
        Mesh capMesh = OBJLoader.loadMesh("src/resources/models/cap.obj");
        capMesh.name = "capsule";
        capMesh.setMaterial(new Material(new Vector3f(0.2f, 0.5f, 0.5f), reflectance, capTex));
        objects.add(new GameItem(capMesh));

        Mesh sphereMesh = OBJLoader.loadMesh("src/resources/models/icosphere.obj");
        sphereMesh.shader = "flat";
        sphereMesh.name = "sphere";
        sphereMesh.setMaterial(new Material(new Vector3f(0.2f, 0.5f, 0.5f), reflectance));
        objects.add(new GameItem(sphereMesh));



        Mesh wolfMesh = OBJLoader.loadMesh("src/resources/models/wolf1.obj");
        wolfMesh.shader = "cel";
        wolfMesh.name = "wolf1";
        wolfMesh.setMaterial(new Material(new Vector3f(0.4f, 0.32f, 0.32f), reflectance));
        objects.add(new GameItem(wolfMesh));

        Mesh wolf2Mesh = OBJLoader.loadMesh("src/resources/models/wolf2.obj");
        wolf2Mesh.name = "wolf2";
        wolf2Mesh.setMaterial(new Material(new Vector3f(0.5f, 0.2f, 0.25f), reflectance));
        objects.add(new GameItem(wolf2Mesh));

        Mesh wolf3Mesh = OBJLoader.loadMesh("src/resources/models/wolf3.obj");
        wolf3Mesh.name = "wolf3";
        wolf3Mesh.setMaterial(new Material(new Vector3f(0.45f, 0.15f, 0.15f), reflectance));
        objects.add(new GameItem(wolf3Mesh));



        Texture sceneTex = new Texture("/resources/textures/scene.png");
        Mesh sceneMesh = OBJLoader.loadMesh("src/resources/models/scene.obj");
        sceneMesh.shader = "flat";
        sceneMesh.name = "scene";
        sceneMesh.setMaterial(new Material(new Vector3f(0.5f, 0.5f, 0.5f), 0, sceneTex));
        sceneMesh.setStatic(true);
        objects.add(new GameItem(sceneMesh));

        gameItems = new GameItem[objects.size()];
        for (int i = 0; i < objects.size(); i++)
        {
            objects.get(i).setPosition(0, -2, -10);
            gameItems[i] = objects.get(i);
        }
        gameItems[0].setPosition(8, 3, -18);
        gameItems[0].setRotation(45, 45, 45);
        gameItems[1].setPosition(-5, 3, -10);
        gameItems[2].setScale(2);
        gameItems[2].setPosition(0, 3, -10);
        gameItems[objects.size() - 1].setPosition(0, -2.5f, -10);

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 1.0f;

        pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);

        lightPosition = new Vector3f(-1, 0, 0);
        lightColour = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);
    }

    @Override
    public void input(Window window, MouseInput mouseInput)
    {
        glfwSetKeyCallback(window.getWindowHandle(), new GLFWKeyCallback()
        {
            public void invoke(long window, int key, int scancode, int action, int mods)
            {
                if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE)
                {
                    // select current shader
                    currentShaderIndex = currentShaderIndex + 1;
                    currentShaderIndex = currentShaderIndex % renderer.getNumShaders();
                    System.out.println("selected shader: " + renderer.getShaderName(currentShaderIndex));
                }
                else if (key == GLFW_KEY_4 && action == GLFW_RELEASE)
                {
                    currentShaderIndex++;
                    if (currentShaderIndex > renderer.getNumShaders() - 1) currentShaderIndex = 0;
                    System.out.println("selected shader: " + renderer.getShaderName(currentShaderIndex));
                }
                else if (key == GLFW_KEY_3 && action == GLFW_RELEASE)
                {
                    currentShaderIndex--;
                    if (currentShaderIndex < 0) currentShaderIndex = renderer.getNumShaders() - 1;
                    System.out.println("selected shader: " + renderer.getShaderName(currentShaderIndex));
                }
                else if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                {
                    glfwSetWindowShouldClose(window, true);
                }
                else if (key == GLFW_KEY_Q && action == GLFW_RELEASE)
                {
                    //select current object
                    currentObj++;
                    if (gameItems[currentObj].getMesh().name.equals("scene")) currentObj++;
                    if (currentObj > gameItems.length - 1) currentObj = 0;
                }
                else if ((key == GLFW_KEY_W && action == GLFW_RELEASE))
                {
                    //select current object
                    currentObj--;
                    if (currentObj < 0) currentObj = gameItems.length - 1;
                    if (gameItems[currentObj].getMesh().name.equals("scene")) currentObj--;
                    if (currentObj < 0) currentObj = gameItems.length - 1;
                }
            }
        });

        if (window.isKeyPressed(GLFW_KEY_E))
        {
            //scale object
            float curr = gameItems[currentObj].getScale();
            gameItems[currentObj].setScale(curr + SCALE_STEP);
        }
        else if (window.isKeyPressed(GLFW_KEY_R))
        {
            //scale object
            float curr = gameItems[currentObj].getScale();
            gameItems[currentObj].setScale(curr - SCALE_STEP);
        }
        else if (window.isKeyPressed(GLFW_KEY_T))
        {
            for (GameItem item : gameItems)
                item.setPosition(item.getPosition().x + TRANSLATE_STEP, item.getPosition().y, item.getPosition().z);
            //move object x by step
            //Vector3f curr = gameItems[currentObj].getPosition();
            //gameItems[currentObj].setPosition(curr.x + TRANSLATE_STEP, curr.y, curr.z);
        }
        else if (window.isKeyPressed(GLFW_KEY_Y))
        {
            for (GameItem item : gameItems)
                item.setPosition(item.getPosition().x - TRANSLATE_STEP, item.getPosition().y, item.getPosition().z);
            //move object x by step
            //Vector3f curr = gameItems[currentObj].getPosition();
            //gameItems[currentObj].setPosition(curr.x - TRANSLATE_STEP, curr.y, curr.z);
        }
        else if (window.isKeyPressed(GLFW_KEY_U))
        {
            //move object y by step
            Vector3f curr = gameItems[currentObj].getPosition();
            gameItems[currentObj].setPosition(curr.x, curr.y + TRANSLATE_STEP, curr.z);
        }
        else if (window.isKeyPressed(GLFW_KEY_I))
        {
            //move object y by step
            Vector3f curr = gameItems[currentObj].getPosition();
            gameItems[currentObj].setPosition(curr.x, curr.y - TRANSLATE_STEP, curr.z);
        }
        else if (window.isKeyPressed(GLFW_KEY_O))
        {
            //move object z by step
            Vector3f curr = gameItems[currentObj].getPosition();
            gameItems[currentObj].setPosition(curr.x, curr.y, curr.z + TRANSLATE_STEP);
        }
        else if (window.isKeyPressed(GLFW_KEY_P))
        {
            //move object z by step
            Vector3f curr = gameItems[currentObj].getPosition();
            gameItems[currentObj].setPosition(curr.x, curr.y, curr.z - TRANSLATE_STEP);
        }
        else if (window.isKeyPressed(GLFW_KEY_A))
        {
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x + ROTATION_STEP, curr.y, curr.z);
        }
        else if (window.isKeyPressed(GLFW_KEY_S))
        {
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x - ROTATION_STEP, curr.y, curr.z);
        }
        else if (window.isKeyPressed(GLFW_KEY_D))
        {
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x, curr.y + ROTATION_STEP, curr.z);
        }
        else if (window.isKeyPressed(GLFW_KEY_F))
        {
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x, curr.y - ROTATION_STEP, curr.z);
        }
        else if (window.isKeyPressed(GLFW_KEY_G))
        {
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x, curr.y, curr.z + ROTATION_STEP);
        }
        else if (window.isKeyPressed(GLFW_KEY_H))
        {
            //rotate object at x axis
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x, curr.y, curr.z - ROTATION_STEP);
        }
        else if (window.isKeyPressed(GLFW_KEY_0))
        {
            //rotation by manipulating mesh
            gameItems[currentObj].getMesh().translateMesh(new Vector3f(0f, 0.05f, 1f));
        }
        else if (window.isKeyPressed(GLFW_KEY_9))
        {
            //rotation by manipulating mesh
            gameItems[currentObj].getMesh().rotateMesh(new Vector3f(1, 1, 1), 30);
        }
        else if (window.isKeyPressed(GLFW_KEY_8))
        {
            //rotation by manipulating mesh
            gameItems[currentObj].getMesh().scaleMesh(1.001f, 1.0f, 1.0f);
        }
        else if (window.isKeyPressed(GLFW_KEY_7))
        {
            //rotation by manipulating mesh
            gameItems[currentObj].getMesh().reflectMesh(new Vector3f(0f, 1f, 0f), new Vector3f(0f, 1f, 0f));
        }
        else if (window.isKeyPressed(GLFW_KEY_1))
        {
            //get screenshot
            renderer.writePNG(window);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput)
    {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse            
        if (mouseInput.isLeftButtonPressed())
        {
            Vector2f rotVec = mouseInput.getDisplVec();
            System.out.println("rotation: " + rotVec);
            Vector3f curr = gameItems[currentObj].getRotation();
            gameItems[currentObj].setRotation(curr.x + rotVec.x * MOUSE_SENSITIVITY, curr.y + rotVec.y * MOUSE_SENSITIVITY, curr.z * MOUSE_SENSITIVITY);
        }
        else if (mouseInput.isRightButtonPressed())
        {
            gameItems[currentObj].setRotation(-20, 45, 0);
            gameItems[currentObj].setPosition(0, 0, -5);
        }

        Vector3f vRot = gameItems[1].getRotation();
        gameItems[1].setRotation(vRot.x + ROTATION_STEP, vRot.y + ROTATION_STEP, vRot.z + ROTATION_STEP * 2);
        vRot = gameItems[2].getRotation();
        gameItems[2].setRotation(vRot.x, vRot.y + ROTATION_STEP*10, vRot.z);

        // Update directional light direction, intensity and colour
        lightAngle += 1.1f;

        if (lightAngle > 90)
        {
            directionalLight.setIntensity(0);
            if (lightAngle >= 90)
            {
                lightAngle = -90;
            }
        }
        else if (lightAngle <= -80 || lightAngle >= 80)
        {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        }
        else
        {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window)
    {
        renderer.render(window, camera, gameItems, ambientLight, pointLight, directionalLight, renderer.getShaderName(currentShaderIndex));
    }

    @Override
    public void cleanup()
    {
        renderer.cleanup();
        for (GameItem gameItem : gameItems)
        {
            gameItem.getMesh().cleanUp();
        }
    }
}
