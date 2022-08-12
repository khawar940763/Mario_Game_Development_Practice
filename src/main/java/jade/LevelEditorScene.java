package jade;


import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends  Scene{
public  GameObject obj1;
    Spritesheet sprites;
    LevelEditorScene(){
    }


    public void init(){
        loadResources();

        this.camera = new Camera(new Vector2f());

        sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

        obj1 = new GameObject("objec1" , new Transform(new Vector2f(100 , 100 ), new Vector2f(256 , 256)) , 2);
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(25)));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("object2" , new Transform(new Vector2f(400 , 100 ), new Vector2f(856 , 156)),2);
        obj2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/spritesheet.png"))));
        this.addGameObjectToScene(obj2);


        GameObject obj4 = new GameObject("object4" , new Transform(new Vector2f(200 , 400 ), new Vector2f(256 , 256)) , 1);
        obj4.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/blendImage2.png"))));
        this.addGameObjectToScene(obj4);

        GameObject obj3 = new GameObject("object3" , new Transform(new Vector2f(100 , 400 ), new Vector2f(256 , 256)), 2);
        obj3.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/blendImage1.png"))));
        this.addGameObjectToScene(obj3);


    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/images/spritesheet.png" ,
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png")
                        , 16, 16 , 26 , 0));
    }


    private int spriteIndex = 0 ;
    private float spriteFlipTime = 0.2f;
    private float SpriteFlipTimeLeft = 0.0f;
    @Override
    public void update(float dt) {
        SpriteFlipTimeLeft -= dt / 100;
        if(SpriteFlipTimeLeft <= 0){
            SpriteFlipTimeLeft = spriteFlipTime;
            spriteIndex++;
            if(spriteIndex > 5){
                spriteIndex = 0;
            }
            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
        }
//        obj1.transform.position.x += 10 * dt;
//        System.out.println(1.0f/dt + "");
        for(GameObject go : this.gameObjects){
            go.update(dt);
        }
        this.renderer.render();

    }
}
