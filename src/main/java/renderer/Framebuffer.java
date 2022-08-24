package renderer;

import util.AssetPool;

import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {
    private int fboID = 0;
    private Texture texture = null;

    public Framebuffer(int width , int height){
        //Generate framebuffer
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER ,fboID);


        //Create a texture to attach the render the data to and attach your buffer to
        this.texture = new Texture(width , height);
//        this.texture = AssetPool.getTexture("assets/images/decorationsAndBlocks.png");
        glFramebufferTexture2D(GL_FRAMEBUFFER , GL_COLOR_ATTACHMENT0 , GL_TEXTURE_2D, this.texture.getId() , 0 );

        //Create renderbuffer store the depth info
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER , GL_DEPTH_COMPONENT32, width , height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER , GL_DEPTH_ATTACHMENT , GL_RENDERBUFFER , rboID);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false: "Error : Buffer is not complete";
        }

        glBindFramebuffer(GL_FRAMEBUFFER ,0);

    }

    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER , fboID);
    }

    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER , 0);
    }

    public int getFboID(){
        return this.fboID;
    }

    public int getTextureId(){
        return texture.getId();
    }
}
