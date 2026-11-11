package vn.edu.nlu.plantcare.data.remote.gemini;

import com.google.gson.annotations.SerializedName;

public class GeminiInlineData {
    @SerializedName("mime_type")
    private String mimeType;

    @SerializedName("data")
    private String data;

    public GeminiInlineData(String mimeType, String data) {
        this.mimeType = mimeType;
        this.data = data;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
