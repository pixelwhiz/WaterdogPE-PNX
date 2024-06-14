package dev.waterdog.waterdogpe;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.EntityDataTypeMap;
import org.cloudburstmc.protocol.bedrock.codec.v291.serializer.LevelEventSerializer_v291;
import org.cloudburstmc.protocol.bedrock.codec.v291.serializer.LevelSoundEvent1Serializer_v291;
import org.cloudburstmc.protocol.bedrock.codec.v313.serializer.LevelSoundEvent2Serializer_v313;
import org.cloudburstmc.protocol.bedrock.codec.v332.serializer.LevelSoundEventSerializer_v332;
import org.cloudburstmc.protocol.bedrock.codec.v361.serializer.LevelEventGenericSerializer_v361;
import org.cloudburstmc.protocol.bedrock.codec.v575.BedrockCodecHelper_v575;
import org.cloudburstmc.protocol.bedrock.codec.v594.serializer.AvailableCommandsSerializer_v594;
import org.cloudburstmc.protocol.bedrock.codec.v671.Bedrock_v671;
import org.cloudburstmc.protocol.bedrock.codec.v685.Bedrock_v685;
import org.cloudburstmc.protocol.bedrock.codec.v685.serializer.*;
import org.cloudburstmc.protocol.bedrock.data.*;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParam;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataFormat;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.TextProcessingEventOrigin;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.skin.*;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.util.TypeMap;

import java.util.List;

public class CustomNetworkSettings extends Bedrock_v685 {

    protected static final TypeMap<ParticleType> PARTICLE_TYPES = Bedrock_v671.PARTICLE_TYPES.toBuilder()
            .insert(93, ParticleType.OMINOUS_ITEM_SPAWNER)
            .build();

    protected static final TypeMap<SoundEvent> SOUND_EVENTS = Bedrock_v671.SOUND_EVENTS
            .toBuilder()
            .insert(516, SoundEvent.TRAIL_SPAWNER_CHARGE_ACTIVATE)
            .insert(517, SoundEvent.TRAIL_SPAWNER_AMBIENT_OMINOUS)
            .insert(518, SoundEvent.OMINOUS_ITEM_SPAWNER_SPAWN_ITEM)
            .insert(519, SoundEvent.OMINOUS_BOTTLE_END_USE)
            .replace(521, SoundEvent.OMINOUS_ITEM_SPAWNER_SPAWN_ITEM_BEGIN)
            .insert(523, SoundEvent.APPLY_EFFECT_BAD_OMEN)
            .insert(524, SoundEvent.APPLY_EFFECT_RAID_OMEN)
            .insert(525, SoundEvent.APPLY_EFFECT_TRIAL_OMEN)
            .insert(526, SoundEvent.OMINOUS_ITEM_SPAWNER_ABOUT_TO_SPAWN_ITEM)
            .insert(527, SoundEvent.RECORD_CREATOR)
            .insert(528, SoundEvent.RECORD_CREATOR_MUSIC_BOX)
            .insert(529, SoundEvent.RECORD_PRECIPICE)
            .insert(530, SoundEvent.UNDEFINED)
            .build();

    protected static final TypeMap<CommandParam> COMMAND_PARAMS = Bedrock_v671.COMMAND_PARAMS.toBuilder()
            .shift(86, 4)
            .insert(86, CommandParam.CODE_BUILDER_ARG)
            .insert(87, CommandParam.CODE_BUILDER_ARGS)
            .insert(88, CommandParam.CODE_BUILDER_SELECT_PARAM)
            .insert(89, CommandParam.CODE_BUILDER_SELECTOR)
            .build();

    protected static final EntityDataTypeMap ENTITY_DATA = Bedrock_v671.ENTITY_DATA
            .toBuilder()
            .insert(EntityDataTypes.VISIBLE_MOB_EFFECTS, 131, EntityDataFormat.NBT) // TODO check data format
            .build();

    protected static final TypeMap<LevelEventType> LEVEL_EVENTS = Bedrock_v671.LEVEL_EVENTS.toBuilder()
            .insert(LEVEL_EVENT_PARTICLE_TYPE, PARTICLE_TYPES)
            .replace(LEVEL_EVENT_BLOCK + 115, LevelEvent.PARTICLE_TRIAL_SPAWNER_DETECTION_CHARGED)
            .insert(LEVEL_EVENT_BLOCK + 116, LevelEvent.PARTICLE_TRIAL_SPAWNER_BECOME_CHARGED)
            .insert(LEVEL_EVENT_BLOCK + 117, LevelEvent.ALL_PLAYERS_SLEEPING)
            .insert(9814, LevelEvent.ANIMATION_SPAWN_COBWEB)
            .insert(9815, LevelEvent.PARTICLE_SMASH_ATTACK_GROUND_DUST)
            .build();

    public static final BedrockCodec CODEC = Bedrock_v671.CODEC.toBuilder()
            .raknetProtocolVersion(11)
            .protocolVersion(685)
            .minecraftVersion("1.21.0")
            .helper(() -> new CustomBedrockCodecHelper_v575(ENTITY_DATA, GAME_RULE_TYPES, ITEM_STACK_REQUEST_TYPES, CONTAINER_SLOT_TYPES, PLAYER_ABILITIES, TEXT_PROCESSING_ORIGINS))
            .updateSerializer(LevelEventPacket.class, new LevelEventSerializer_v291(LEVEL_EVENTS))
            .updateSerializer(LevelEventGenericPacket.class, new LevelEventGenericSerializer_v361(LEVEL_EVENTS))
            .updateSerializer(AvailableCommandsPacket.class, new AvailableCommandsSerializer_v594(COMMAND_PARAMS))
            .updateSerializer(LevelSoundEvent1Packet.class, new LevelSoundEvent1Serializer_v291(SOUND_EVENTS))
            .updateSerializer(LevelSoundEvent2Packet.class, new LevelSoundEvent2Serializer_v313(SOUND_EVENTS))
            .updateSerializer(LevelSoundEventPacket.class, new LevelSoundEventSerializer_v332(SOUND_EVENTS))
            .updateSerializer(ContainerClosePacket.class, ContainerCloseSerializer_v685.INSTANCE)
            .updateSerializer(CraftingDataPacket.class, CraftingDataSerializer_v685.INSTANCE)
            .updateSerializer(CodeBuilderSourcePacket.class, CodeBuilderSourceSerializer_v685.INSTANCE)
            .updateSerializer(EventPacket.class, EventSerializer_v685.INSTANCE)
            .updateSerializer(StartGamePacket.class, StartGameSerializer_v685.INSTANCE)
            .updateSerializer(TextPacket.class, TextSerializer_v685.INSTANCE)
            .registerPacket(AwardAchievementPacket::new, AwardAchievementSerializer_v685.INSTANCE, 309, PacketRecipient.CLIENT)
            .deregisterPacket(TickSyncPacket.class) // this packet is now deprecated
            .build();

    private static class CustomBedrockCodecHelper_v575 extends BedrockCodecHelper_v575 {
        public CustomBedrockCodecHelper_v575(EntityDataTypeMap entityData, TypeMap<Class<?>> gameRulesTypes, TypeMap<ItemStackRequestActionType> stackRequestActionTypes, TypeMap<ContainerSlotType> containerSlotTypes, TypeMap<Ability> abilities, TypeMap<TextProcessingEventOrigin> textProcessingEventOrigins) {
            super(entityData, gameRulesTypes, stackRequestActionTypes, containerSlotTypes, abilities, textProcessingEventOrigins);
        }

        public final int CUSTOM_MODEL_SIZE = ProxyServer.getInstance().getConfiguration().getNetworkSettings().maxSkinLength();

        @Override
        public AnimationData readAnimationData(ByteBuf buffer) {
            ImageData image = this.readImage(buffer, CUSTOM_MODEL_SIZE);
            AnimatedTextureType textureType = TEXTURE_TYPES[buffer.readIntLE()];
            float frames = buffer.readFloatLE();
            AnimationExpressionType expressionType = EXPRESSION_TYPES[buffer.readIntLE()];
            return new AnimationData(image, textureType, frames, expressionType);
        }

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
}