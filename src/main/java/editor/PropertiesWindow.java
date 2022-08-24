package editor;

import Physics2d.components.Box2DCollider;
import Physics2d.components.CircleCollider;
import Physics2d.components.Rigidbody2D;
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

            if(ImGui.beginPopupContextWindow("ComponentAdder")){
                if(ImGui.menuItem("Add Rigidbody")){
                    if(activeGameObject.getComponent(Rigidbody2D.class) == null){
                        activeGameObject.addComponent(new Rigidbody2D());
                    }
                }

                if(ImGui.menuItem("Add Box Collider")){
                    if(activeGameObject.getComponent(Box2DCollider.class) == null &&
                             activeGameObject.getComponent(CircleCollider.class) == null){
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if(ImGui.menuItem("Add Circle Collider")){
                    if(activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(CircleCollider.class) == null){
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject(){
        return this.activeGameObject;
    }
}
