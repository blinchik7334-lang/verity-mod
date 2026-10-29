package com.example.verity.client;

import com.example.verity.VerityMod;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Реестр 3D-моделей. Чтобы добавить новую — смотри метод registerAll(). */
public final class VerityModels {

    /**
     * Одна 3D-форма: id, текстура, текстура "говорит" (может быть null), слой модели и масштаб.
     * Если talkTexture не null — модель открывает рот, пока игрок пишет в чат или говорит в микрофон.
     */
    public record Form(String id, Identifier texture, Identifier talkTexture, EntityModelLayer layer,
                       Supplier<TexturedModelData> data, float scale) {}

    private static final Map<String, Form> FORMS = new LinkedHashMap<>();
    private static final Map<String, ModelPart> BAKED = new HashMap<>();

    public static void registerAll() {
        add(new Form("smile",
            new Identifier(VerityMod.MOD_ID, "textures/entity/smile_ball.png"), null,
            SmileModel.LAYER, SmileModel::createModelData, 1.0f));

        add(new Form("evil",
            new Identifier(VerityMod.MOD_ID, "textures/entity/evil_ball.png"), null,
            EvilModel.LAYER, EvilModel::createModelData, 1.0f));

        add(new Form("talk",
            new Identifier(VerityMod.MOD_ID, "textures/entity/talk_ball.png"),
            new Identifier(VerityMod.MOD_ID, "textures/entity/talk_ball_open.png"),
            TalkModel.LAYER, TalkModel::createModelData, 1.0f));

        // ===== ДОБАВЛЯЙ НОВЫЕ МОДЕЛИ ЗДЕСЬ =====
        // add(new Form("my",
        //     new Identifier(VerityMod.MOD_ID, "textures/entity/my_ball.png"), null,
        //     MyModel.LAYER, MyModel::createModelData, 1.0f));

        for (Form f : FORMS.values()) {
            EntityModelLayerRegistry.registerModelLayer(f.layer(), () -> f.data().get());
        }
    }

    private static void add(Form f) {
        FORMS.put(f.id(), f);
    }

    public static Form get(String id) {
        return FORMS.get(id);
    }

    public static ModelPart root(Form f) {
        return BAKED.computeIfAbsent(f.id(),
            k -> MinecraftClient.getInstance().getEntityModelLoader().getModelPart(f.layer()));
    }

    private VerityModels() {}
}
