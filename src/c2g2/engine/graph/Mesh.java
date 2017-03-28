package c2g2.engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class Mesh
{

    private int vaoId;

    private List<Integer> vboIdList;

    private int vertexCount;

    public String shader;
    public String name;

    private Material material;

    private float[] pos;
    private float[] textco;
    private float[] norms;
    private int[] inds;

    public static class Triangle
    {
        Triangle(Vector3f a, Vector3f b, Vector3f c)
        {
            v0 = a;
            v1 = b;
            v2 = c;
        }

        public Vector3f v0;
        public Vector3f v1;
        public Vector3f v2;
    }

    public ArrayList<Triangle> triangles = new ArrayList<>();

    public boolean isStatic;

    public Mesh()
    {
        this(new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}, new int[]{0, 6, 4, 0, 2, 6, 0, 3, 2, 0, 1, 3, 2, 7, 6, 2, 3, 7, 4, 6, 7, 4, 7, 5, 0, 4, 5, 0, 5, 1, 1, 5, 7, 1, 7, 3});
    }

    public void setMesh(float[] positions, float[] textCoords, float[] normals, int[] indices)
    {
        isStatic = false;
        pos = positions;
        textco = textCoords;
        norms = normals;
        inds = indices;

        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer trianglesBuffer = null;

        System.out.println("create mesh:");
        System.out.println("v: " + positions.length + " t: " + textCoords.length + " n: " + normals.length + " idx: " + indices.length);

        ArrayList<Vector3f> vectors = new ArrayList<>();
        for (int i = 0; i < positions.length / 3; i++)
            vectors.add(new Vector3f(positions[i * 3], positions[i * 3 + 1], positions[i * 3 + 2]));

        for (int i = 0; i < indices.length / 3; i++)
        {
            Vector3f v1 = vectors.get(indices[i * 3]);
            Vector3f v2 = vectors.get(indices[i * 3 + 1]);
            Vector3f v3 = vectors.get(indices[i * 3 + 2]);
            triangles.add(new Triangle(v1, v2, v3));
        }

        try
        {
            vertexCount = indices.length;
            vboIdList = new ArrayList<Integer>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Triangles VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            trianglesBuffer = MemoryUtil.memAllocFloat(triangles.size() * 9);
            for (int i = 0; i < triangles.size(); i++)
            {
                Triangle t = triangles.get(i);
                trianglesBuffer.put(t.v0.x).put(t.v0.y).put(t.v0.z);
                trianglesBuffer.put(t.v1.x).put(t.v1.y).put(t.v1.z);
                trianglesBuffer.put(t.v2.x).put(t.v2.y).put(t.v2.z);
            }
            trianglesBuffer.flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, trianglesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        finally
        {
            if (posBuffer != null) MemoryUtil.memFree(posBuffer);
            if (textCoordsBuffer != null) MemoryUtil.memFree(textCoordsBuffer);
            if (vecNormalsBuffer != null) MemoryUtil.memFree(vecNormalsBuffer);
            if (indicesBuffer != null) MemoryUtil.memFree(indicesBuffer);
            if (trianglesBuffer != null) MemoryUtil.memFree(trianglesBuffer);
        }
    }

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices)
    {
        this.name = "default";
        setMesh(positions, textCoords, normals, indices);
    }

    public Material getMaterial()
    {
        return material;
    }

    public void setMaterial(Material material)
    {
        this.material = material;
    }

    public void setStatic(boolean value)
    {
        this.isStatic = value;
    }

    public int getVaoId()
    {
        return vaoId;
    }

    public int getVertexCount()
    {
        return vertexCount;
    }

    public void render()
    {
        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glBindVertexArray(0);

        if (this.getMaterial().isTextured()) glBindTexture(GL_TEXTURE_2D, this.getMaterial().getTexture().getId());
        else glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanUp()
    {
        if (this.getMaterial().isTextured()) this.getMaterial().getTexture().cleanup();

        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList)
            glDeleteBuffers(vboId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public void scaleMesh(float sx, float sy, float sz)
    {
        cleanUp();
        for (int i = 0; i < pos.length / 3; i++)
        {
            pos[3 * i] = pos[3 * i] * sx;
            pos[3 * i + 1] = pos[3 * i + 1] * sy;
            pos[3 * i + 2] = pos[3 * i + 2] * sz;
        }

        setMesh(pos, textco, norms, inds);
    }

    public void translateMesh(Vector3f trans)
    {
        cleanUp();
        //reset position of each point
        //student code
        for (int i = 0; i < pos.length / 3; i++)
        {

        }
        setMesh(pos, textco, norms, inds);
    }

    public void rotateMesh(Vector3f axis, float angle)
    {
        cleanUp();
        //reset position of each point
        //student code
        for (int i = 0; i < pos.length / 3; i++)
        {

        }
        setMesh(pos, textco, norms, inds);
    }

    public void reflectMesh(Vector3f p, Vector3f n)
    {
        cleanUp();
        //reset position of each point
        //student code
        for (int i = 0; i < pos.length / 3; i++)
        {

        }
        setMesh(pos, textco, norms, inds);
    }
}
