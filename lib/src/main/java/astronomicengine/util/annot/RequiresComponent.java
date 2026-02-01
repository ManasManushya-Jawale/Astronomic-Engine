package astronomicengine.util.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import astronomicengine.comp.Component;

@Target(ElementType.TYPE)
public @interface RequiresComponent {
    Class<? extends Component> value();
}
