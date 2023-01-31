package mine.block.codex.search;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class SearchManager {
    private static HashMap<String, SearchResult> SEARCH_MAP = new HashMap<>();
    public static SearchResult[] search(String query) {
        if(query.isBlank()) return new SearchResult[0];
        TreeSet<SearchResult> results = new TreeSet<>(Comparator.comparing(o -> o.getName().getString()));
        SEARCH_MAP.entrySet().forEach((pair) -> {
            if(pair.getKey().toLowerCase().contains(query.toLowerCase().trim())) {
                results.add(pair.getValue());
            }
        });
        return results.toArray(new SearchResult[0]);
    }

    public static void refresh() {
        SEARCH_MAP.clear();
        for (Map.Entry<RegistryKey<Item>, Item> registryKeyItemEntry : Registries.ITEM.getEntrySet()) {
            var key = registryKeyItemEntry.getKey();
            var item = registryKeyItemEntry.getValue();
            SEARCH_MAP.put(I18n.translate(item.getTranslationKey()), new SearchResult(Text.translatable(item.getTranslationKey()), item, key));
        }
    }
}
