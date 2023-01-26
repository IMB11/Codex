package mine.block.quicksearch.datagen;

import io.umehara.ogmapper.OgMapper;
import io.umehara.ogmapper.domain.OgTags;
import io.umehara.ogmapper.jsoup.JsoupOgMapperFactory;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;

public class MinecraftWikiProvider extends FabricLanguageProvider {
    private TranslationBuilder translationBuilder;
    private final HashMap<RegistryKey<Item>, Exception> failed = new HashMap<>();

    protected MinecraftWikiProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    private void handleItem(RegistryKey<Item> key) {
        try {
            String descriptionKey = key.getValue().toTranslationKey("description");

            if (key.getValue().getPath().contains("wool")) {
                translationBuilder.add(descriptionKey, "Wool is a block obtained from sheep that can be dyed in any of the sixteen different colors. It can be used as a crafting material and to block vibrations.");
                return;
            } else if (key.getValue().getPath().contains("concrete_powder")) {
                translationBuilder.add(descriptionKey, "Concrete powder is a gravity-affected block that is converted to concrete when touching water or lava. It comes in the 16 regular dye colors. ");
                return;
            } else if (key.getValue().getPath().contains("concrete")) {
                translationBuilder.add(descriptionKey, "Concrete is a solid block available in the 16 regular dye colors");
                return;
            } else if (key.getValue().getPath().contains("froglight")) {
                translationBuilder.add(descriptionKey, "A froglight is a light-emitting block that is obtained when a frog eats a tiny magma cube. It comes in three variants based on the variant of frog that eats the magma cube. ");
                return;
            } else if (key.getValue().getPath().contains("bed")) {
                translationBuilder.add(descriptionKey, "A bed is a block that allows a player to sleep and to reset their spawn point to within a few blocks of the bed in the Overworld. If the bed is obstructed or removed, the player spawns at the default world spawning location. ");
                return;
            } else if (key.getValue().getPath().contains("spawn_egg")) {
                translationBuilder.add(descriptionKey, "A spawn egg is an item used to spawn mobs directly.");
                return;
            } else if (key.getValue().getPath().contains("music_disc")) {
                translationBuilder.add(descriptionKey, "Music discs are unique items that can be played in jukeboxes.");
                return;
            } else if (key.getValue().getPath().contains("hanging_sign")) {
                translationBuilder.add(descriptionKey, "A sign is a non-solid block that can display text. A sign can also be used to block or redirect the flow of water or lava while still allowing entities to pass.\n" +
                        "\n" +
                        "A hanging sign can also display text, but it has more variable ways to be placed: on the side of and the underside of solid blocks, fences, walls, chains, and other hanging signs.");
                return;
            } else if (key.getValue().getPath().contains("slab")) {
                translationBuilder.add(descriptionKey, "Slabs are half-height versions of their respective blocks.");
                return;
            } else if (key.getValue().getPath().contains("cut_copper")) {
                translationBuilder.add(descriptionKey, "The block of copper and cut copper are blocks that oxidize over time, gaining a verdigris appearance over four stages. They can be prevented from oxidising by being waxed with honeycombs. Non-cut, non-oxidised, non-waxed copper blocks are storage blocks equivalent to nine copper ingots. ");
                return;
            } else if (key.getValue().getPath().contains("fence_gate")) {
                translationBuilder.add(descriptionKey, "A fence gate is a block that shares the functions of both the door and the fence. ");
                return;
            } else if (key.getValue().getPath().contains("fence")) {
                translationBuilder.add(descriptionKey, "A fence is a barrier block that cannot normally be jumped over, similar to a wall. Unlike a wall, a player (but not mobs) can see through the openings in a fence. ");
                return;
            } else if (key.getValue().getPath().contains("shulker_box")) {
                translationBuilder.add(descriptionKey, "A shulker box is a block that can store and transport items. ");
                return;
            } else if (key.getValue().getPath().contains("stairs")) {
                translationBuilder.add(descriptionKey, "Stairs are blocks that allow mobs and players to change elevation without jumping. ");
                return;
            } else if (key.getValue().getPath().contains("tadpole_bucket")) {
                translationBuilder.add(descriptionKey, "A bucket of aquatic mob is a form of a water bucket with an aquatic mob (a fish, axolotl, or tadpole) inside. ");
                return;
            } else if (key.getValue().getPath().contains("leather_leggings")) {
                translationBuilder.add(descriptionKey, "Leggings are a type of armor that covers the lower body of the player.");
                return;
            } else if (key.getValue().getPath().contains("carpet") && !key.getValue().getPath().contains("moss")) {
                translationBuilder.add(descriptionKey, "Carpets are thin variants of wool that can be dyed in any of the 16 colors. ");
                return;
            } else if (key.getValue().getPath().contains("chest_boat")) {
                translationBuilder.add(descriptionKey, "A boat with chest is a single chest occupying the passenger seat of a boat, and functions as such. As it can still be driven it can be used to transport items over bodies of water. ");
                return;
            } else if (key.getValue().getPath().contains("sign")) {
                translationBuilder.add(descriptionKey, "A sign is a non-solid block that can display text. A sign can also be used to block or redirect the flow of water or lava while still allowing entities to pass.");
                return;
            } else if (key.getValue().getPath().contains("button")) {
                translationBuilder.add(descriptionKey, "A button is a non-solid block that can provide temporary redstone power. ");
                return;
            }

            String baseURL = "https://minecraft.fandom.com/wiki/";
            OgTags tags = MAPPER.process(new URL(baseURL + key.getValue().getPath()));
            String description = tags.getDescription();
            int position = description.indexOf('.', description.indexOf('.'));
            LOGGER.info(key.getValue() + ": " + description.substring(0, position + 1).trim());
            translationBuilder.add(descriptionKey, description.substring(0, position + 1).trim());
        } catch (Exception e) {
            failed.put(key, e);
        }
    }


    private static Logger LOGGER = LoggerFactory.getLogger("MinecraftWikiProvider");
    private static OgMapper MAPPER = new JsoupOgMapperFactory().build();

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        this.translationBuilder = translationBuilder;

        try {
            translationBuilder.add(dataOutput.getModContainer().findPath("assets/quicksearch/lang/en_us.base.json").get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Registries.ITEM.getKeys().parallelStream().forEach(this::handleItem);

        LOGGER.warn("The following items failed to generate descriptions for:");
        failed.forEach((left, right) -> {
            LOGGER.warn(left.getValue() + " failed because: " + right.getMessage());
            if(right instanceof SocketTimeoutException || right instanceof SocketException || right instanceof EOFException) {
                LOGGER.info("Retrying " + left.getValue());
                this.handleItem(left);
            }
        });
    }
}
