package c2g2.game;

import c2g2.engine.graph.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.awt.HeadlessException;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import ar.com.hjg.pngj.*;

import c2g2.engine.GameItem;
import c2g2.engine.Window;

public class Renderer
{

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;

    private LinkedHashMap<String, ShaderProgram> shaderProgramList;

    private final float specularPower;

    public Renderer()
    {
        transformation = new Transformation();
        specularPower = 10f;
        shaderProgramList = new LinkedHashMap<>();
    }

    // Example shader
    public ShaderProgram createPhongShader() throws Exception
    {
        ShaderProgram shaderProgram = new ShaderProgram();

        shaderProgram.createVertexShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/phong.vert"))));
        shaderProgram.createFragmentShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/phong.frag"))));
        shaderProgram.link();

        // Create uniforms for modelView and projection matrices and texture
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");

        // Create uniform for material
        shaderProgram.createMaterialUniform("material");

        // Create lighting related uniforms
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirectionalLightUniform("directionalLight");

        return shaderProgram;
    }

    public ShaderProgram createGouraudShader() throws Exception
    {
        ShaderProgram shaderProgram = new ShaderProgram();

        shaderProgram.createVertexShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/gouraud.vert"))));
        shaderProgram.createFragmentShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/gouraud.frag"))));
        shaderProgram.link();

        // Create uniforms for modelView and projection matrices
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");

        // Create uniform for material
        shaderProgram.createMaterialUniform("material");

        // Create lighting related uniforms
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirectionalLightUniform("directionalLight");

        return shaderProgram;
    }

    public ShaderProgram createTMSShader() throws Exception
    {
        ShaderProgram shaderProgram = new ShaderProgram();

        shaderProgram.createVertexShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/gouraud.vert"))));
        shaderProgram.createFragmentShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/tms.frag"))));
        shaderProgram.link();

        // Create uniforms for modelView and projection matrices
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");

        // Create uniform for material
        shaderProgram.createMaterialUniform("material");

        // Create lighting related uniforms
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirectionalLightUniform("directionalLight");

        return shaderProgram;
    }

    public ShaderProgram createCelShader() throws Exception
    {
        ShaderProgram shaderProgram = new ShaderProgram();

        shaderProgram.createVertexShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/cel.vert"))));
        shaderProgram.createFragmentShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/cel.frag"))));
        shaderProgram.link();

        // Create uniforms for modelView and projection matrices
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");

        // Create uniform for material
        shaderProgram.createMaterialUniform("material");

        // Create lighting related uniforms
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirectionalLightUniform("directionalLight");

        return shaderProgram;
    }

    public ShaderProgram createFlatShader() throws Exception
    {
        ShaderProgram shaderProgram = new ShaderProgram();

        shaderProgram.createVertexShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/flat.vert"))));
        shaderProgram.createFragmentShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/flat.frag"))));
        shaderProgram.link();
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");
        shaderProgram.createMaterialUniform("material");
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirectionalLightUniform("directionalLight");
        return shaderProgram;
    }

    public ShaderProgram createCheckerShader() throws Exception
    {
        ShaderProgram shaderProgram = new ShaderProgram();

        shaderProgram.createVertexShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/checker.vert"))));
        shaderProgram.createFragmentShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/checker.frag"))));
        shaderProgram.link();
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirectionalLightUniform("directionalLight");
        return shaderProgram;
    }

    public ShaderProgram createWireframeShader() throws Exception
    {
        ShaderProgram shaderProgram = new ShaderProgram();

        shaderProgram.createVertexShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/wireframe.vert"))));
        shaderProgram.createFragmentShader(new String(Files.readAllBytes(Paths.get("src/resources/shaders/wireframe.frag"))));
        shaderProgram.link();
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createUniform("texture_sampler");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirectionalLightUniform("directionalLight");
        shaderProgram.createMaterialUniform("material");
        return shaderProgram;
    }

    public void init(Window window) throws Exception
    {
        // Create our example shader
        shaderProgramList.put("phong", createPhongShader());

        // Student code
        shaderProgramList.put("gouraud", createGouraudShader());
        shaderProgramList.put("checker", createCheckerShader());
        shaderProgramList.put("cel", createCelShader());
        shaderProgramList.put("tms", createTMSShader());
        shaderProgramList.put("flat", createFlatShader());

    }

    public int getNumShaders()
    {
        return shaderProgramList.size();
    }

    public String getShaderName(int ind)
    {
        return new ArrayList<String>(shaderProgramList.keySet()).get(ind);
    }

    public void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameItem[] gameItems, Vector3f ambientLight, PointLight pointLight, DirectionalLight directionalLight, String currentShader)
    {
        clear();
        if (window.isResized())
        {
            System.out.println("resizing");
            window.setResized(false);
        }
        //glViewport(0, 0, window.getWidth() * 2, window.getHeight() * 2);
        glViewport(0, 0, window.getWidth(), window.getHeight());

        ArrayList<ShaderProgram> shaders = new ArrayList<>();

        //ShaderProgram shaderProgram = shaderProgramList.get(currentShader);
        //shaderProgram.bind();

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);

        // Get a copy of the point light object and transform its position to view coordinates
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));

        // Render the gameItem(s)
        for (GameItem gameItem : gameItems)
        {
            Mesh mesh = gameItem.getMesh();
            String shaderName = mesh.shader;
            if (shaderName == null) shaderName = currentShader;
            ShaderProgram shader = shaderProgramList.get(shaderName);
            shader.bind();

            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shader.setUniform("projectionMatrix", projectionMatrix);
            shader.setUniform("modelViewMatrix", modelViewMatrix);
            shader.setUniform("ambientLight", ambientLight);
            shader.setUniform("specularPower", specularPower);
            shader.setUniform("pointLight", currPointLight);
            shader.setUniform("directionalLight", currDirLight);

            if (!shaderName.equals("checker"))
            {
                shader.setUniform("material", mesh.getMaterial());
                establishTexture(mesh, shader);
            }

            mesh.render();
            shader.unbind();
        }
    }

    private void establishTexture(Mesh mesh, ShaderProgram shader)
    {
        if (!mesh.getMaterial().isTextured())
        {
            shader.setUniform("texture_sampler", -1);
            return;
        }

        Material m = mesh.getMaterial();
        Texture t = m.getTexture();
        t.bind();
        glActiveTexture(GL_TEXTURE_2D);
        shader.setUniform("texture_sampler", GL_TEXTURE_2D);
    }

    public void cleanup()
    {
        for (ShaderProgram shaderProgram : shaderProgramList.values())
            if (shaderProgram != null) shaderProgram.cleanup();
    }

    private static int imgcount = 0;

    public void writePNG(Window window) throws HeadlessException
    {
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        glReadBuffer(GL_FRONT);

        int width = window.getWidth();
        int height = window.getHeight();
        //NOTE: if your display *is* a retina display, please uncomment the following two lines.
        //width = window.getWidth()*2;
        //height= window.getHeight()*2;

        int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        ImageInfo imi = new ImageInfo(width, height, 8, false);
        PngWriter png = new PngWriter(new File("screenshot" + imgcount + ".png"), imi, true);

        ImageLineInt iline = new ImageLineInt(imi);
        for (int row = 0; row < imi.rows; row++)
        {
            for (int col = 0; col < imi.cols; col++)
            { // this line will be written to all rows
                int i = (col + (width * (imi.rows - row - 1))) * bpp;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                ImageLineHelper.setPixelRGB8(iline, col, r, g, b);
            }
            png.writeRow(iline);
        }
        png.end();
        imgcount = imgcount + 1;
    }
}
