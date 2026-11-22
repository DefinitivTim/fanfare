package org.spongepowered.asm.mixin;
import java.lang.annotation.*;
// Must be RUNTIME like in real Mixin.
@Retention(RetentionPolicy.RUNTIME) @Target({ElementType.TYPE})
public @interface Pseudo {}
