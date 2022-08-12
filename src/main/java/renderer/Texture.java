package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Texture {
    private String filePath;
    private int texID;
    private int height;
    private int  width;

    public Texture(String filePath){
        this.filePath  = filePath;

        //Generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,  texID);

        //Bind parameters to the texture

        //Repeat image in both sides for x-axis
        glTexParameteri(GL_TEXTURE_2D , GL_TEXTURE_WRAP_S , GL_REPEAT);
        //Repeat image in both sides for y-axis
        glTexParameteri(GL_TEXTURE_2D , GL_TEXTURE_WRAP_T , GL_REPEAT);
        //When stretch the image, pixelate
        glTexParameteri(GL_TEXTURE_2D , GL_TEXTURE_MIN_FILTER , GL_NEAREST);
        //When stretch the image, pixelate
        glTexParameteri(GL_TEXTURE_2D , GL_TEXTURE_MAG_FILTER , GL_NEAREST);

        //Load Image using stb 'loading library'
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(filePath , width ,height , channels , 0);

        if(image != null){

            this.width = width.get(0);
            this.height = height.get(0);

            //Uploading texture to GPU
            if(channels.get(0) == 4){
                glTexImage2D(GL_TEXTURE_2D, 0 , GL_RGBA, width.get(0) ,
                        height.get(0), 0 , GL_RGBA, GL_UNSIGNED_BYTE , image );
            }else if(channels.get(0) == 3){
                glTexImage2D(GL_TEXTURE_2D, 0 , GL_RGB, width.get(0) ,
                        height.get(0), 0 , GL_RGB, GL_UNSIGNED_BYTE , image );
            }else{
                assert false: "Unhandled no of channels "+channels.get(0)+" for "+filePath+"in texture)!";
            }

            }else{
            assert false: "Unable to load the image "+filePath+"in texture!";
        }

        stbi_image_free(image);

    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D , texID);
    }

    public void unBind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }
}
