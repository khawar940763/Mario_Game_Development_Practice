package jade;


import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends  Scene{

    LevelEditorScene(){
    }


    public void init(){
        loadResources();

        this.camera = new Camera(new Vector2f());

        Spritesheet sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

        GameObject obj1 = new GameObject("objec1" , new Transform(new Vector2f(100 , 100 ), new Vector2f(256 , 256)));
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(25)));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("object2" , new Transform(new Vector2f(400 , 100 ), new Vector2f(256 , 256)));
        obj2.addComponent(new SpriteRenderer(sprites.getSprite(20)));
        this.addGameObjectToScene(obj2);

    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/images/spritesheet.png" ,
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png")
                        , 16, 16 , 26 , 0));
    }


    @Override
    public void update(float dt) {
//        System.out.println(1.0f/dt + "");
        for(GameObject go : this.gameObjects){
            go.update(dt);
        }
        this.renderer.render();

    }
}