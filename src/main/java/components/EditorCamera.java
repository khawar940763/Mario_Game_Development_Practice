package components;

import jade.Camera;
import jade.KeyListener;
import jade.MouseListener;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component{

    private float dragDebounce = 0.32f;
    private Camera levelEditorCamera;
    private Vector2f clickOrigin;
    private float dragSensitivity = 5.0f;
    private float scrollSensitivity = 0.1f;
    private boolean reset = false;
    private float lerpTime = 0.0f;

    public EditorCamera(Camera levelEditorCamera){
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void update(float dt){
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT) && dragDebounce > 0){
            this.clickOrigin = new Vector2f(MouseListener.getOrthoX() , MouseListener.getOrthoY());
            dragDebounce -= dt;
            return;
        }else if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX() , MouseListener.getOrthoY());
            Vector2f delta = mousePos.sub(this.clickOrigin);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos , dt);
        }

        if(dragDebounce <= 0.0f && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
            dragDebounce = 0.1f;
        }

        if(MouseListener.getOrthoY() != 0.0f){
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity), 1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE )){
            reset = true;
        }

        if(reset){
            levelEditorCamera.position.lerp(new Vector2f() , lerpTime);
            levelEditorCamera.setZoom(levelEditorCamera.getZoom() + ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            lerpTime += 0.01f * dt;
            if(Math.abs(levelEditorCamera.position.x) <= 1.0f && Math.abs(levelEditorCamera.position.y) <= 1.0f){
                this.lerpTime = 0.0f;
                levelEditorCamera.position.set(0f , 0f);
                this.levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }
    }
}
