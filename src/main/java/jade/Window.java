package jade;

import renderer.*;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;
import util.AssetPool;
import util.Time;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private static int height , width;
    private String title;
    private long glfwWindow;
    private static Scene currentScene = null;
    public static float r , g , b ,a ;
    private static Window window = null;
    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;

    public static int aspectX = 900 , aspectY = 506;

    private Window(){
        this.height = 600;
        this.width = 720;
        this.title = "Mario";
        this.r = 1.0f;
        this.g = 1.0f;
        this.b = 1.0f;
        this.a = 1.0f;
    }

    public static void changeScene(int newScene){
        switch (newScene){
            case 0:
                currentScene = new LevelEditorScene();

                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false: "Unknown Scene '" + newScene + "'";
                break;
        }
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Window get(){
        if(Window.window == null){
            Window.window = new Window();
        }

        return Window.window;
    }
    public  static Scene  getScene(){
        return get().currentScene;
    }


    public void run() {
    System.out.println("Hello LWJGL " + Version.getVersion() + "!");

    init();
    loop();

    // Free the window callbacks and destroy the window
    glfwFreeCallbacks(glfwWindow);
    glfwDestroyWindow(glfwWindow);

    // Terminate GLFW and free the error callback
    glfwTerminate();
    glfwSetErrorCallback(null).free();
}

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
//        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
         glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE); // the window will be maximized

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title , NULL, NULL);
        if ( glfwWindow == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(glfwWindow, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    glfwWindow,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        glfwSetCursorPosCallback(glfwWindow , MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow , MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow , MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow , KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow , (w , newWidth , newHeight)->{
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });


        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE  , GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(aspectX , aspectY);
        this.pickingTexture = new PickingTexture(aspectX , aspectY);
        glViewport(0 , 0 , aspectX , aspectY);

        this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imGuiLayer.initImGui();

        Window.changeScene(0);
    }

    private void loop() {
        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");



        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        while ( !glfwWindowShouldClose(glfwWindow) ) {

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            //Render pass 1: Render to picking frame
            glDisable(GL_BLEND);
            pickingTexture.enableWritnig();
            glViewport(0 , 0 , aspectX , aspectY);
            glClearColor(0.0f , 0.0f , 0.f , 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();



            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            //Render pass 2: Render the actual game

            DebugDraw.beginFrame();

            // Set the clear color
            this.framebuffer.bind();
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer


            if(dt >= 0){
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);
                currentScene.update(dt);
                currentScene.render();
            }
            this.framebuffer.unbind();

            this.imGuiLayer.update(dt , currentScene);
            glfwSwapBuffers(glfwWindow); // swap the color buffers ( It should be at the end and after rendering of any graphics otherwise graphic won't be visible)

            MouseListener.endFrame();

            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = Time.getTime();

        }
        currentScene.saveExit();
    }
    public static int getWidth(){
        return get().width;
    }

    public static int getHeight(){
        return get().height;
    }

    public static void setWidth(int width){
        get().width = width;
    }
    public static void setHeight(int height){
        get().height = height;
    }

    public static Framebuffer getFramebuffer(){
        return get().framebuffer;
    }

    public static float getTargetAspectRatio(){
        return 16.0f / 9.0f;
    }

    public static ImGuiLayer getImGuiLayer(){
        return get().imGuiLayer;
    }
}
