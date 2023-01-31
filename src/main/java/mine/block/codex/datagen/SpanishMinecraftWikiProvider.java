package mine.block.codex.datagen;

import com.google.gson.JsonObject;
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

public class SpanishMinecraftWikiProvider extends FabricLanguageProvider {
    private TranslationBuilder translationBuilder;
    private final HashMap<RegistryKey<Item>, Exception> failed = new HashMap<>();

    protected SpanishMinecraftWikiProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "es_es");
        this.TRANSLATIONS = CodexDatagen.LANGUAGES.get("es_es");
    }

    private void handleItem(RegistryKey<Item> key) {
        try {
            String descriptionKey = key.getValue().toTranslationKey("description");
            String translatedName = this.TRANSLATIONS.get(Registries.ITEM.get(key).getTranslationKey()).getAsString();
            if (key.getValue().getPath().contains("wool")) {
                translationBuilder.add(descriptionKey, "La lana (llamada en los comienzos de Minecraft) es un bloque derivado de la oveja que puede tintarse de 16 colores distintos.");
                return;
            } else if (key.getValue().getPath().contains("concrete_powder")) {
                translationBuilder.add(descriptionKey, "El Cemento es un bloque sólido que viene con los 16 colores regulares de tinte. Al igual que la arena, la grava, el huevo de dragón y el yunque, el cemento se ve afectado por la gravedad. ");
                return;
            } else if (key.getValue().getPath().contains("concrete")) {
                translationBuilder.add(descriptionKey, "El hormigón es un bloque sólido que viene con los 16 tintes de colores. ");
                return;
            } else if (key.getValue().getPath().contains("froglight")) {
                translationBuilder.add(descriptionKey, "Una ranaluz es un bloque natural luminoso que se puede obtener si una rana se come un pequeño cubo de magma. Se utiliza como decoración. ");
                return;
            } else if (key.getValue().getPath().contains("bed")) {
                translationBuilder.add(descriptionKey, "Una cama es un bloque que permite al jugador dormir y reiniciar su punto de aparición a unos pocos bloques de la cama en la Superficie. Si la cama está obstruida o destruida, el jugador se genera en la ubicación predeterminada de generación del mundo. ");
                return;
            } else if (key.getValue().getPath().contains("spawn_egg")) {
                translationBuilder.add(descriptionKey, "Los huevos generadores de criaturas son objetos que, como su propio nombre indica, generan criaturas directamente. ");
                return;
            }

            String baseURL = "https://minecraft.fandom.com/es/wiki/";
            OgTags tags = MAPPER.process(new URL(baseURL + translatedName));
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
    private JsonObject TRANSLATIONS;

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
