package c2g2.engine.graph;

import org.joml.Vector3f;

public class Material
{
    private static final Vector3f DEFAULT_COLOUR = new Vector3f(1.0f, 1.0f, 1.0f);

    private Vector3f colour;

    private float reflectance;

    private Texture texture;

    public Material()
    {
        colour = DEFAULT_COLOUR;
        reflectance = 0;
    }

    public Material(Vector3f colour, float reflectance)
    {
        this();
        this.colour = colour;
        this.reflectance = reflectance;
        this.texture = null;
    }

    public Material(Vector3f colour, float reflectance, Texture tex)
    {
        this();
        this.colour = colour;
        this.reflectance = reflectance;
        this.texture = tex;
    }


    public Vector3f getColour()
    {
        return colour;
    }

    public void setColour(Vector3f colour)
    {
        this.colour = colour;
    }

    public float getReflectance()
    {
        return reflectance;
    }

    public void setReflectance(float reflectance)
    {
        this.reflectance = reflectance;
    }

    public Texture getTexture()
    {
        return this.texture;
    }

    public void setTexture(Texture tex)
    {
        this.texture = tex;
    }

    public boolean isTextured()
    {
        return this.texture != null;
    }

}