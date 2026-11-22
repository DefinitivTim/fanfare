package org.spongepowered.asm.mixin;
import java.lang.annotation.*;
// Must be RUNTIME like in real Mixin.
@Retention(RetentionPolicy.RUNTIME) @Target({ElementType.METHOD, ElementType.FIELD})
public @interface Shadow { String prefix() default "shadow$"; boolean remap() default true; String[] aliases() default {}; }
