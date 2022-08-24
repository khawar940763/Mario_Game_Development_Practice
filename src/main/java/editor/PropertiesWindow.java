package editor;

import components.NonPickable;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import jade.Window;
import renderer.PickingTexture;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private  GameObject activeGameObject = null;
    private PickingTexture pickingTexture;
    private float deboundTime = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene){
        deboundTime -= dt;
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && deboundTime <= 0){
            int x = (int) MouseListener.getScreenX((float) Window.aspectX);
            int y = (int) MouseListener.getScreenY((float) Window.aspectY);
            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject pickObject = currentScene.getGameObject(gameObjectId);
            if(pickObject != null && pickObject.getComponent(NonPickable.class) == null){
                activeGameObject = pickObject;
            }else if(pickObject == null && !MouseListener.isDragging()){
                activeGameObject = null;
            }
            this.deboundTime = 0.2f;
        }
    }

    public void imgui(){
        if(activeGameObject != null){
            ImGui.begin("Properties");
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject(){
        return this.activeGameObject;
    }
}
