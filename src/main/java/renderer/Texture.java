package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String filePath;
    private transient int texID;
    private int height;
    private int  width;

//    public Texture(String filePath){
//
//
//    }

    public Texture(){
        texID = -1;
        width = -1;
        height = -1;
    }
    public Texture(int width , int height){
        this.filePath  = "Generated";

        //Generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,  texID);

        glTexParameteri(GL_TEXTURE_2D , GL_TEXTURE_MIN_FILTER , GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D , GL_TEXTURE_MAG_FILTER , GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0 , GL_RGB, width , height, 0 , GL_RGB, GL_UNSIGNED_BYTE , 0 );
    }

    public void init(String filePath){
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
        stbi_set_flip_vertically_on_load(true);
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

    public String getFilePath(){
        return this.filePath;
    }

    public int getHeight(){
        return this.height;
    }

    public int getId(){
        return texID;
    }

    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof Texture)) return false;
        Texture oTex = (Texture) o;
        return oTex.getWidth() == this.width && oTex.getHeight() == this.height && oTex.getId() == this.texID && oTex.getFilePath().equals(this.filePath);
    }
}
