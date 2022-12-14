package Physics2d.components;

import components.Component;
import org.joml.Vector2f;

import java.util.Vector;

public abstract class Collider extends Component {
    private Vector2f offset = new Vector2f();

    public Vector2f getOffset(){
        return this.offset;
    }
}
