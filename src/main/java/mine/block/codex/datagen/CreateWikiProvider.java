package mine.block.codex.datagen;

import com.google.gson.JsonObject;
import io.umehara.ogmapper.OgMapper;
import io.umehara.ogmapper.domain.OgTags;
import io.umehara.ogmapper.jsoup.JsoupOgMapperFactory;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;

public class CreateWikiProvider extends FabricLanguageProvider {
    private TranslationBuilder translationBuilder;
    private final HashMap<String, Exception> failed = new HashMap<>();

    protected CreateWikiProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "en_us_create");
        this.TRANSLATIONS = CodexDatagen.LANGUAGES.get("create_en_us");
    }

    private void handleItem(String id, String val) {
        try {
            String descriptionKey = "description." + id.replace("block.", "").replace("item.", "");

            String baseURL = "https://create.fandom.com/wiki/";
            OgTags tags = MAPPER.process(new URL(baseURL + val));
            String description = tags.getDescription();
            int position = description.indexOf('.', description.indexOf('.'));
            LOGGER.info(id + ": " + description.substring(0, position + 1).trim());
            translationBuilder.add(descriptionKey, description.substring(0, position + 1).trim());
        } catch (Exception e) {
            failed.put(id, e);
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

        TRANSLATIONS.keySet().parallelStream().forEach(s -> this.handleItem(s, TRANSLATIONS.get(s).getAsString()));

        LOGGER.warn("The following items failed to generate descriptions for:");
        failed.forEach((left, right) -> {
            LOGGER.warn(left + " failed because: " + right.getMessage());
            if(right instanceof SocketTimeoutException || right instanceof SocketException || right instanceof EOFException) {
                LOGGER.info("Retrying " + left);
                this.handleItem(left, TRANSLATIONS.get(left).getAsString());
            }
        });
    }
}
