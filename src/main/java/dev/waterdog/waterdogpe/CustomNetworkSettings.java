package dev.waterdog.waterdogpe;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.EntityDataTypeMap;
import org.cloudburstmc.protocol.bedrock.codec.v291.serializer.LevelEventSerializer_v291;
import org.cloudburstmc.protocol.bedrock.codec.v361.serializer.LevelEventGenericSerializer_v361;
import org.cloudburstmc.protocol.bedrock.codec.v575.BedrockCodecHelper_v575;
import org.cloudburstmc.protocol.bedrock.codec.v662.Bedrock_v662;
import org.cloudburstmc.protocol.bedrock.codec.v671.Bedrock_v671;
import org.cloudburstmc.protocol.bedrock.codec.v671.serializer.*;
import org.cloudburstmc.protocol.bedrock.data.Ability;
import org.cloudburstmc.protocol.bedrock.data.EncodingSettings;
import org.cloudburstmc.protocol.bedrock.data.LevelEventType;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.TextProcessingEventOrigin;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.skin.*;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.transformer.FlagTransformer;
import org.cloudburstmc.protocol.common.util.TypeMap;

import java.util.List;

public class CustomNetworkSettings extends Bedrock_v671 {

    protected static final TypeMap<ParticleType> PARTICLE_TYPES = Bedrock_v662.PARTICLE_TYPES.toBuilder()
            .insert(92, ParticleType.WOLF_ARMOR_BREAK)
            .build();

    protected static final TypeMap<LevelEventType> LEVEL_EVENTS = Bedrock_v662.LEVEL_EVENTS.toBuilder()
            .insert(LEVEL_EVENT_PARTICLE_TYPE, PARTICLE_TYPES)
            .build();

    public static final TypeMap<EntityFlag> ENTITY_FLAGS = Bedrock_v662.ENTITY_FLAGS
            .toBuilder()
            .insert(118, EntityFlag.BODY_ROTATION_BLOCKED)
            .build();

    protected static final EntityDataTypeMap ENTITY_DATA = Bedrock_v662.ENTITY_DATA
            .toBuilder()
            .update(EntityDataTypes.FLAGS, new FlagTransformer(ENTITY_FLAGS, 0))
            .update(EntityDataTypes.FLAGS_2, new FlagTransformer(ENTITY_FLAGS, 1))
            .build();

    public static final EncodingSettings SETTINGS = EncodingSettings.builder()
            .maxByteArraySize(1024 * 1024 * 1024)//1G
            .maxListSize(1024 * 1024 * 1024)//1G
            .maxNetworkNBTSize(1024 * 1024 * 1024)//1G
            .maxItemNBTSize(1024 * 1024 * 1024)//1G
            .maxStringLength(1024 * 1024 * 1024)//1G
            .build();

    private static class CustomBedrockCodecHelper_v575 extends BedrockCodecHelper_v575 {
        public CustomBedrockCodecHelper_v575(EntityDataTypeMap entityData, TypeMap<Class<?>> gameRulesTypes, TypeMap<ItemStackRequestActionType> stackRequestActionTypes, TypeMap<ContainerSlotType> containerSlotTypes, TypeMap<Ability> abilities, TypeMap<TextProcessingEventOrigin> textProcessingEventOrigins) {
            super(entityData, gameRulesTypes, stackRequestActionTypes, containerSlotTypes, abilities, textProcessingEventOrigins);
        }

        public static final int CUSTOM_MODEL_SIZE = 50 * 1024 * 1024;

        @Override
        public SerializedSkin readSkin(ByteBuf buffer) {
            String skinId = this.readString(buffer);
            String playFabId = this.readString(buffer);
            String skinResourcePatch = this.readString(buffer);
            ImageData skinData = this.readImage(buffer, CUSTOM_MODEL_SIZE);

            List<AnimationData> animations = new ObjectArrayList<>();
            this.readArray(buffer, animations, ByteBuf::readIntLE, (b, h) -> this.readAnimationData(b));

            ImageData capeData = this.readImage(buffer, CUSTOM_MODEL_SIZE);
            String geometryData = this.readString(buffer);
            String geometryDataEngineVersion = this.readString(buffer);
            String animationData = this.readString(buffer);
            String capeId = this.readString(buffer);
            String fullSkinId = this.readString(buffer);
            String armSize = this.readString(buffer);
            String skinColor = this.readString(buffer);

            List<PersonaPieceData> personaPieces = new ObjectArrayList<>();
            this.readArray(buffer, personaPieces, ByteBuf::readIntLE, (buf, h) -> {
                String pieceId = this.readString(buf);
                String pieceType = this.readString(buf);
                String packId = this.readString(buf);
                boolean isDefault = buf.readBoolean();
                String productId = this.readString(buf);
                return new PersonaPieceData(pieceId, pieceType, packId, isDefault, productId);
            });

            List<PersonaPieceTintData> tintColors = new ObjectArrayList<>();
            this.readArray(buffer, tintColors, ByteBuf::readIntLE, (buf, h) -> {
                String pieceType = this.readString(buf);
                List<String> colors = new ObjectArrayList<>();
                int colorsLength = buf.readIntLE();
                for (int i2 = 0; i2 < colorsLength; i2++) {
                    colors.add(this.readString(buf));
                }
                return new PersonaPieceTintData(pieceType, colors);
            });

            boolean premium = buffer.readBoolean();
            boolean persona = buffer.readBoolean();
            boolean capeOnClassic = buffer.readBoolean();
            boolean primaryUser = buffer.readBoolean();
            boolean overridingPlayerAppearance = buffer.readBoolean();

            return SerializedSkin.of(skinId, playFabId, skinResourcePatch, skinData, animations, capeData, geometryData, geometryDataEngineVersion,
                    animationData, premium, persona, capeOnClassic, primaryUser, capeId, fullSkinId, armSize, skinColor, personaPieces, tintColors,
                    overridingPlayerAppearance);
        }
    }

    public static final BedrockCodec CODEC = Bedrock_v662.CODEC.toBuilder()
            .raknetProtocolVersion(11)
            .protocolVersion(671)
            .minecraftVersion("1.20.80")
            .helper(() -> new CustomBedrockCodecHelper_v575(ENTITY_DATA, GAME_RULE_TYPES, ITEM_STACK_REQUEST_TYPES, CONTAINER_SLOT_TYPES, PLAYER_ABILITIES, TEXT_PROCESSING_ORIGINS))
            .updateSerializer(LevelEventPacket.class, new LevelEventSerializer_v291(LEVEL_EVENTS))
            .updateSerializer(LevelEventGenericPacket.class, new LevelEventGenericSerializer_v361(LEVEL_EVENTS))
            .updateSerializer(ClientboundDebugRendererPacket.class, ClientboundDebugRendererSerializer_v671.INSTANCE)
            .updateSerializer(CorrectPlayerMovePredictionPacket.class, CorrectPlayerMovePredictionSerializer_v671.INSTANCE)
            .updateSerializer(ResourcePackStackPacket.class, ResourcePackStackSerializer_v671.INSTANCE)
            .updateSerializer(UpdatePlayerGameTypePacket.class, UpdatePlayerGameTypeSerializer_v671.INSTANCE)
            .updateSerializer(StartGamePacket.class, StartGameSerializer_v671.INSTANCE)
            .updateSerializer(CraftingDataPacket.class, CraftingDataSerializer_v671.INSTANCE)
            .deregisterPacket(FilterTextPacket.class)
            .build();
}