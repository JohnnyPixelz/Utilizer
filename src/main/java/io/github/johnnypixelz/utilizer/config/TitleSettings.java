package io.github.johnnypixelz.utilizer.config;

public class TitleSettings {
    private int titleFadeIn;
    private int titleStay;
    private int titleFadeOut;

    public TitleSettings() {
        this(20, 50, 30);
    }

    public TitleSettings(int titleFadeIn, int titleStay, int titleFadeOut) {
        this.titleFadeIn = titleFadeIn;
        this.titleStay = titleStay;
        this.titleFadeOut = titleFadeOut;
    }

    public int getTitleFadeIn() {
        return titleFadeIn;
    }

    public void setTitleFadeIn(int titleFadeIn) {
        this.titleFadeIn = titleFadeIn;
    }

    public int getTitleStay() {
        return titleStay;
    }

    public void setTitleStay(int titleStay) {
        this.titleStay = titleStay;
    }

    public int getTitleFadeOut() {
        return titleFadeOut;
    }

    public void setTitleFadeOut(int titleFadeOut) {
        this.titleFadeOut = titleFadeOut;
    }
}
