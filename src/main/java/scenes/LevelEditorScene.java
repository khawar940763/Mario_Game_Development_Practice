package scenes;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.DebugDraw;
import util.AssetPool;

public class LevelEditorScene extends Scene {
public GameObject obj1;
    Spritesheet sprites;
    GameObject  levelEditorStuff = this.creatGameObject("Level Editor");

    public LevelEditorScene(){
    }


    public void init(){
        loadResources();

        sprites = AssetPool.getSpriteSheet("assets/images/decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpriteSheet("assets/images/gizmos.png");

        this.camera = new Camera(new Vector2f(-255 , 0));
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(this.camera));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
//        levelEditorStuff.addComponent(new TranslateGizmo(gizmos.getSprite(1),
//                Window.getImGuiLayer().getPropertiesWindow()));
//        levelEditorStuff.addComponent(new ScaleGizmo(gizmos.getSprite(2),
//                Window.getImGuiLayer().getPropertiesWindow()));
        levelEditorStuff.start();
//
//
//
//        SpriteRenderer objTest = new SpriteRenderer();
//        GameObject obj = this.creatGameObject("objTest");
//        obj.addComponent(new RigidBody());
////        SpriteRenderer obj1SpriteRenderer = new SpriteRenderer();
////        obj1SpriteRenderer.setSprite(sprites.getSprite(1));
////
////        SpriteRenderer obj4SpriteRenderer = new SpriteRenderer();
////        obj4SpriteRenderer.setColor(new Vector4f(1, 0 , 0 , 1));
////        GameObject obj4 = new GameObject("object4" , new Transform(new Vector2f(-150 , 50 ), new Vector2f(156 , 156)) , 9);
////        obj4.addComponent(obj4SpriteRenderer);
////        obj4.addComponent(new RigidBody());
////        this.addGameObjectToScene(obj4);
////
////        SpriteRenderer obj3SpriteRenderer = new SpriteRenderer();
////        Sprite obj3Sprite = new Sprite();
////        obj3Sprite.setTexture(AssetPool.getTexture("assets/images/blendImage1.png"));
////        obj3SpriteRenderer.setSprite(obj3Sprite);
////
////        GameObject obj3 = new GameObject("object3" , new Transform(new Vector2f(-200 , 50 ), new Vector2f(156 , 156)), 2);
////        obj3.addComponent(obj3SpriteRenderer);
////        this.addGameObjectToScene(obj3);
//
////        this.activeGameObject = obj4;
//
////        Gson gson = new GsonBuilder()
////                .setPrettyPrinting()
////                .registerTypeAdapter(Component.class , new ComponentDeserializer())
////                .registerTypeAdapter(GameObject.class , new GameObjectDeserializer())
////                .create();
////
////        String serialized = gson.toJson(obj3);
////        GameObject obj = gson.fromJson(serialized , GameObject.class);

    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/decorationsAndBlocks.png" ,
                new Spritesheet(AssetPool.getTexture("assets/images/decorationsAndBlocks.png")
                        , 16, 16 , 81 , 0));

        AssetPool.addSpritesheet("assets/images/gizmos.png" ,
                new Spritesheet(AssetPool.getTexture("assets/images/gizmos.png")
                        , 24, 48 , 3 , 0));

        AssetPool.getTexture("assets/images/blendImage1.png");


        //setting textures using file path rather than ids
        for(GameObject g : gameObjects){
            if(g.getComponent(SpriteRenderer.class) != null){
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
                }
            }
        }
    }

    @Override
    public void update(float dt) {

        levelEditorStuff.update(dt);
        this.camera.adjustProjection();

        for(GameObject go : this.gameObjects){
            go.update(dt);
        }

    }

    @Override
    public void render(){
        this.renderer.render();
    }

    @Override
    public void imgui(){
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();

        ImGui.begin("Test Window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for(int i = 0; i < sprites.size() ; i++){
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = (float) (sprite.getWidth() * 1.5);
            float spriteHeight = (float) (sprite.getHeight() * 1.5);
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if(ImGui.imageButton(id,spriteWidth,spriteHeight, texCoords[0].x , texCoords[2].y, texCoords[2].x , texCoords[0].y)){
                GameObject object = Prefabs.generateSpriteObject(sprite , 32 , 32);
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            if(i + 1 < sprites.size() && nextButtonX2 < windowX2){
                ImGui.sameLine();
            }

        }


        ImGui.end();
    }
}
