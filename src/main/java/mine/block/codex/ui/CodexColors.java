package mine.block.codex.ui;

import net.minecraft.util.math.MathHelper;

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
        return (color & 0x00ffffff) | (alpha << 24);
    }

    public static int getAlpha(int color) {
        return color >> 24 & 0xFF;
    }

    public static int getRed(int color) {
        return color >> 16 & 0xFF;
    }

    public static int getGreen(int color) {
        return color >> 8 & 0xFF;
    }

    public static int getBlue(int color) {
        return color & 0xFF;
    }

    public static int interpolateTwoColors(float step, int color1, int color2) {
        step = MathHelper.clamp(step, 0.0f, 1.0f);
        int deltaAlpha = getAlpha(color2) - getAlpha(color1);
        int deltaRed = getRed(color2) - getRed(color1);
        int deltaGreen = getGreen(color2) - getGreen(color1);
        int deltaBlue = getBlue(color2) - getBlue(color1);
        int resultAlpha = (int) (getAlpha(color1) + (deltaAlpha * step));
        int resultRed = (int) (getRed(color1) + (deltaRed * step));
        int resultGreen = (int) (getGreen(color1) + (deltaGreen * step));
        int resultBlue = (int) (getBlue(color1) + (deltaBlue * step));
        resultAlpha = Math.max(Math.min(resultAlpha, 255), 0);
        resultRed = Math.max(Math.min(resultRed, 255), 0);
        resultGreen = Math.max(Math.min(resultGreen, 255), 0);
        resultBlue = Math.max(Math.min(resultBlue, 255), 0);
        return resultAlpha << 24 | resultRed << 16 | resultGreen << 8 | resultBlue;
    }
}
