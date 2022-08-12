package renderer;

import components.SpriteRenderer;
import jade.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;
    public List<RenderBatch> batches;

    public Renderer(){
        this.batches = new ArrayList<>();
    }


    public void add(GameObject go){
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if(spr != null){
            add(spr);
        }
    }

    private void add( SpriteRenderer spr){
        boolean added = false;
        for(RenderBatch batch : batches){
            if(batch.hasRoom() && batch.zIndex() == spr.gameObject.zIndex()){
                Texture tex = spr.getTexture();
                if(tex == null && ( batch.hasTexture(tex) || batch.hasTextureRoom())) {
                    batch.addSprite(spr);
                    added = true;
                    break;
                }
            }
        }
        if(!added){
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE , spr.gameObject.zIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(spr);
            Collections.sort(batches);
        }
    }

    public  void render(){
        for(RenderBatch batch : batches){
            batch.render();
        }
    }
}
