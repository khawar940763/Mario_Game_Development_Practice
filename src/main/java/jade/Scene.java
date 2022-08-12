package jade;

import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

abstract public class Scene {

    protected   Renderer renderer = new Renderer();
    protected Camera camera;

    private boolean isRunning = false;
    public List<GameObject> gameObjects = new ArrayList<>();

    Scene(){
    }

    public void init(){

    }

    public void start(){
        for(GameObject go : gameObjects) {
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go){
        if(!isRunning){
            gameObjects.add(go);
        }else{
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }
    public  abstract void update(float dt);

    public Camera camera(){
        return this.camera;
    }
}
