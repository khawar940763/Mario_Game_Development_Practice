package components;

import editor.PropertiesWindow;
import jade.GameObject;
import jade.MouseListener;
import jade.Prefabs;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class TranslateGizmo extends Gizmo{
    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow){
        super(arrowSprite , propertiesWindow);
    }

    @Override
    public void update(float dt){

        if(activeGameObject != null){
            if(xAxisActive && !yAxisActive){
                activeGameObject.transform.position.x -= MouseListener.getWorldDx();
            }else if(yAxisActive && !xAxisActive){
                activeGameObject.transform.position.y -= MouseListener.getWorldDy();
            }
        }
        super.update(dt);
    }

}
