package vn.edu.nlu.plantcare.data.remote.gemini;

import com.google.gson.annotations.SerializedName;

public class GeminiPart {
    @SerializedName("text")
    private String text;

    @SerializedName("inline_data")
    private GeminiInlineData inlineData;

    public GeminiPart(String text) {
        this.text = text;
    }

    public GeminiPart(GeminiInlineData inlineData) {
        this.inlineData = inlineData;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public GeminiInlineData getInlineData() {
        return inlineData;
    }

    public void setInlineData(GeminiInlineData inlineData) {
        this.inlineData = inlineData;
    }
}
