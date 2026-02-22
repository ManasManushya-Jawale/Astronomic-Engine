package org.astroEngine.util.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import org.astroEngine.comp.Component;

@Target(ElementType.TYPE)
public @interface RequiresComponent {
    Class<? extends Component> value();
}
