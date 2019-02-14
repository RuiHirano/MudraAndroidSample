package il.co.wearabledevices.mudrasdkreference;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cube {

    private FloatBuffer vertexBuffer;
    private ByteBuffer drawListBuffer;
    private FloatBuffer colorBuffer;

    private final int NUM_OF_VERTICES_PER_FACE = 6;
    private final int NUM_OF_FACES = 6;

    // Coordinates for vertices
    static float cubeCoords[] = {
            -1.0f,-1.0f,-1.0f, // triangle 1 : begin
            -1.0f,-1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f, // triangle 1 : end
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,

            1.0f,-1.0f,-1.0f,
            -1.0f,1.0f,-1.0f,
            -1.0f,-1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f,1.0f,-1.0f,
            -1.0f,1.0f,-1.0f,

            1.0f,1.0f,-1.0f,
            1.0f,1.0f,1.0f,
            -1.0f,1.0f,-1.0f,
            1.0f,1.0f,1.0f,
            -1.0f,1.0f,1.0f,
            -1.0f,1.0f,-1.0f,

            1.0f,-1.0f,1.0f,
            1.0f,1.0f,1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f,1.0f,1.0f,
            1.0f,1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,

            1.0f,-1.0f,1.0f,
            1.0f,1.0f,1.0f,
            -1.0f,1.0f,1.0f,
            1.0f,-1.0f,1.0f,
            -1.0f,1.0f,1.0f,
            -1.0f,-1.0f,1.0f,

            1.0f,-1.0f,-1.0f,
            1.0f,-1.0f,1.0f,
            -1.0f,-1.0f,-1.0f,
            1.0f,-1.0f,1.0f,
            -1.0f,-1.0f,1.0f,
            -1.0f,-1.0f,-1.0f


    };

    // Color definition
    private float faceColors[] = {
            0.4f,   0.765f, 0.894f, 1.0f,
            0.149f, 0.663f, 0.843f, 1.0f,
            0.106f, 0.463f, 0.588f, 1.0f,
            0.4f,   0.765f, 0.894f, 1.0f,
            0.149f, 0.663f, 0.843f, 1.0f,
            0.106f, 0.463f, 0.588f, 1.0f
    };

    /*
    private byte drawOrder[] = {
            0, 1, 3, 1, 3, 2,
            1, 2, 6, 1, 6, 5,
            0, 3, 7, 0, 7, 4,
            4, 7, 6, 4, 6, 5,
            3, 7, 2, 7, 2, 6,
            0, 4, 1, 4, 1, 5
    };
    */
    private byte drawOrder[] = {
            0, 1, 2, 3, 4, 5,
            6, 7, 8, 9, 10, 11,
            12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23,
            24, 25, 26, 27, 28, 29,
            30, 31, 32, 33, 34, 35
    };

    public Cube() {

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(cubeCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);

        float[] colors = new float[NUM_OF_VERTICES_PER_FACE * NUM_OF_FACES * 4];

        for (int face = 0; face < NUM_OF_FACES; face++) {
            for (int j = 0; j < NUM_OF_VERTICES_PER_FACE; j++) {
                int start = (face*NUM_OF_VERTICES_PER_FACE + j) * 4;
                colors[start] =     faceColors[face*4];
                colors[start + 1] = faceColors[face*4 + 1];
                colors[start + 2] = faceColors[face*4 + 2];
                colors[start + 3] = faceColors[face*4 + 3];
            }
        }

        byteBuffer = ByteBuffer.allocateDirect(colors.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        colorBuffer = byteBuffer.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        drawListBuffer = ByteBuffer.allocateDirect(drawOrder.length);
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

    }

    public void draw(GL10 gl) {

        // Enable vertex array buffer to be used during rendering
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // Tell openGL where the vertex array buffer is
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);


        // Enable color array buffer to be used during rendering
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        // Tell openGL where the color array buffer is
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);

        // Draw each plane as a pair of triangles, based on the drawListBuffer information
        gl.glDrawElements(GL10.GL_TRIANGLES, drawOrder.length, GL10.GL_UNSIGNED_BYTE, drawListBuffer);

    }

}
