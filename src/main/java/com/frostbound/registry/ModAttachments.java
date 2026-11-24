package com.frostbound.registry;

import java.util.function.Supplier;

import com.frostbound.Frostbound;
import com.mojang.serialization.Codec;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Frostbound.MODID);

    /** Температура тела игрока в °C. Сбрасывается до 36.6 после смерти. */
    public static final Supplier<AttachmentType<Float>> BODY_TEMP = ATTACHMENTS.register("body_temp",
            () -> AttachmentType.builder(() -> 36.6F).serialize(Codec.FLOAT).build());

    private ModAttachments() {}
}
