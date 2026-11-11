package vn.edu.nlu.plantcare.data.remote.gemini;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeminiRequest {

    public static class ContentItem {
        @SerializedName("parts")
        private List<GeminiPart> parts;

        public ContentItem(List<GeminiPart> parts) {
            this.parts = parts;
        }

        public List<GeminiPart> getParts() {
            return parts;
        }
    }

    @SerializedName("contents")
    private List<ContentItem> contents;

    public GeminiRequest(List<GeminiPart> parts) {
        this.contents = Collections.singletonList(new ContentItem(parts));
    }

    public List<ContentItem> getContents() {
        return contents;
    }

    public static GeminiRequest createWithImage(String prompt, String mimeType, String base64Image) {
        List<GeminiPart> parts = new ArrayList<>();
        parts.add(new GeminiPart(prompt));
        parts.add(new GeminiPart(new GeminiInlineData(mimeType, base64Image)));
        return new GeminiRequest(parts);
    }
}
