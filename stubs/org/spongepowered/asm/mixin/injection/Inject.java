package org.spongepowered.asm.mixin.injection;
import java.lang.annotation.*;
// Must be RUNTIME like in real Mixin: with CLASS the handler is merged as a plain method and never called.
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD)
public @interface Inject { String[] method(); At[] at(); boolean cancellable() default false; boolean remap() default true; int require() default -1; }
