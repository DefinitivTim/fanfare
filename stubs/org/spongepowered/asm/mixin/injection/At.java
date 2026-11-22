package org.spongepowered.asm.mixin.injection;
import java.lang.annotation.*;
// Must be RUNTIME like in real Mixin.
@Retention(RetentionPolicy.RUNTIME) @Target({})
public @interface At { String value(); String target() default ""; boolean remap() default true; }
