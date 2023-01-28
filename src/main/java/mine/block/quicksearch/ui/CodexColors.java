package mine.block.quicksearch.ui;

public class CodexColors {
    public static final int CODEX_BG = 0xFF1D1D1D;
    public static final int CODEX_ELEVATED = 0xFF2D3134;
    public static final int CODEX_ELEVATED_HOVER = 0xFF54585C;
    public static final int CODEX_ACCENT_A = 0xFF16A5A0;
    public static final int CODEX_ACCENT_B = 0xFFAC4312;

    public static final int ALPHA_100 = 0xFF;
    public static final int ALPHA_75 = 0xBF;
    public static final int ALPHA_50 = 0x7F;
    public static final int ALPHA_25 = 0x40;
    public static final int ALPHA_0 = 0x00;

    public static final int WHITE = 0xFFFFFFFF;
    public static final int DARK_GRAY = 0xFF5A5A5A;

    public static int withAlpha(int color, int alpha) {
        color &= ~(0x0FL << 4);
        color |= (long) alpha << 4;
        color &= ~(0x0FL << 8);
        color |= (long) alpha << 8;
        return color;
    }
}
