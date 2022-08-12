package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class Shader {

    private int shaderProgramID;
    private String vertexSource;
    private String fragmentSource;
    private String filePath;
    private boolean beingUsed = false;

    public Shader(String filePath){
        this.filePath = filePath;

        try{
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )+([a-zA-z]+)");

            //First pattern after '#type' 'pattern'
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index , eol).trim();


            //Second  pattern after '#type' 'pattern'
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n" , index);
            String secondPattern = source.substring(index , eol).trim();

            if(firstPattern.equals("vertex")){
                vertexSource = splitString[1];
            }else if(firstPattern.equals("fragment")){
                fragmentSource = splitString[1];
            }else{
//                System.out.println("Fist pattern:"+firstPattern);
//                System.out.println("Second pattern:"+secondPattern);
                throw new IOException("Unexpected token :"+firstPattern+" in "+filePath+"!");
            }

            if(secondPattern.equals("vertex")){
                vertexSource = splitString[2];
            }else if(secondPattern.equals("fragment")){
                fragmentSource = splitString[2];
            }else {
//                System.out.println("Vertex Shader:"+firstPattern);
//                System.out.println("Fragment Shader:"+secondPattern);
              throw new IOException("Unexpected token :"+firstPattern+" in "+filePath+"!");
            }

         }catch(IOException e){
            e.printStackTrace();
            System.out.println("Error: Unable to load source file:"+filePath+"!");
        }
    }
    public void compile(){
        int vertexID , fragmentID;

        /*============================================================================
           Compile and Link the shaders
          ============================================================================*/

        // First Load and Compile vertex shader

        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //Pass the vertex shader source to GPU
        glShaderSource(vertexID , vertexSource);
        glCompileShader(vertexID);

        //Check for errors in compilation
        int success = glGetShaderi(vertexID , GL_COMPILE_STATUS);

        if(success == GL_FALSE){
            int len = glGetShaderi(vertexID ,  GL_INFO_LOG_LENGTH);
            System.out.println("Some error occured while compiling vertex shader in file:"+filePath);
            System.out.println(glGetShaderInfoLog(vertexID , len));
            assert false : "" ;
        }

        // Then Load and Compile fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        //Pass the fragment shader source to GPU
        glShaderSource(fragmentID , fragmentSource);
        glCompileShader(fragmentID);

        //Check for errors in compilation
        success = glGetShaderi(fragmentID , GL_COMPILE_STATUS);

        if(success == GL_FALSE){
            int len = glGetShaderi(fragmentID ,  GL_INFO_LOG_LENGTH);
            System.out.println("Some error occured while compiling fragment shader in file:"+filePath);
            System.out.println(glGetShaderInfoLog(fragmentID , len));
            assert false : "" ;
        }

        //Then link vertex shader and fragment shader
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID , vertexID);
        glAttachShader(shaderProgramID , fragmentID);
        glLinkProgram(shaderProgramID);

        //Check for linking errors
        success = glGetProgrami(shaderProgramID , GL_LINK_STATUS);

        if(success == GL_FALSE){
            int len = glGetProgrami(shaderProgramID ,  GL_INFO_LOG_LENGTH);
            System.out.println("Some error occured while linking the shaders in file:"+filePath);
            System.out.println(glGetProgramInfoLog(shaderProgramID , len));
            assert false : "" ;
        }

    }
    public void use(){
        if(!beingUsed) {
            //Bind shader Program
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }
    public void detach(){
        glUseProgram(0);
        beingUsed=false;
    }

    public void uploadMatrix4f(String varName , Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use(); //To confirm that the shader is running before we send
           // the uniform variable to the gpu glsl file
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation , false, matBuffer);
    }

    public void uploadMatrix3f(String varName , Matrix3f mat3){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use(); //To confirm that the shader is running before we send
        // the uniform variable to the gpu glsl file
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation , false, matBuffer);
    }

    public void uploadVec4f(String varName , Vector4f vec){
        int varLocation = glGetUniformLocation(shaderProgramID , varName);
        use();
        glUniform4f(varLocation , vec.x , vec.y , vec.z , vec.w);
    }

    public void uploadVec3f(String varName , Vector3f vec){
        int varLocation = glGetUniformLocation(shaderProgramID , varName);
        use();
        glUniform3f(varLocation , vec.x , vec.y , vec.z);
    }

    public void uploadVec2f(String varName , Vector2f vec){
        int varLocation = glGetUniformLocation(shaderProgramID , varName);
        use();
        glUniform2f(varLocation , vec.x , vec.y);
    }

    public void uploadFloat(String varName , float val){
        int varLocation = glGetUniformLocation(shaderProgramID , varName);
        use();
        glUniform1f(varLocation , val);
    }

    public void uploadInt(String varName , int val ){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation , val);
    }

    public void uploadIntArray(String varName , int[] array ){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation , array);
    }

    public void uploadTexture(String varName , int slot){
        int varLocation = glGetUniformLocation(shaderProgramID , varName);
        use();
        glUniform1i(varLocation , slot);
    }

    public static void LeftMe(){
        System.out.println("Just leave me alone!");
    }

}
