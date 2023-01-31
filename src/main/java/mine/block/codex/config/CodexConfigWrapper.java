package mine.block.codex.config;

import io.wispforest.owo.config.annotation.Config;

@Config(name="codex", wrapperName = "CodexConfig")
public class CodexConfigWrapper {
    public String baseUrl = "https://raw.githubusercontent.com/mineblock11/Quicksearch/data";
}
