package org.spongepowered.asm.mixin.injection;
import java.lang.annotation.*;
// Must be RUNTIME like in real Mixin.
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD)
public @interface Redirect { String[] method(); At at(); boolean remap() default true; int require() default -1; }
