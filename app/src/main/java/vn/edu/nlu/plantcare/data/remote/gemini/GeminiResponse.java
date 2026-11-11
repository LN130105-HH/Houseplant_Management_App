package vn.edu.nlu.plantcare.data.remote.gemini;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeminiResponse {

    public static class Candidate {
        @SerializedName("content")
        private Content content;

        public Content getContent() {
            return content;
        }
    }

    public static class Content {
        @SerializedName("parts")
        private List<GeminiPart> parts;

        public List<GeminiPart> getParts() {
            return parts;
        }
    }

    @SerializedName("candidates")
    private List<Candidate> candidates;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public String getFirstText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate candidate = candidates.get(0);
            if (candidate != null && candidate.getContent() != null) {
                List<GeminiPart> parts = candidate.getContent().getParts();
                if (parts != null && !parts.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (GeminiPart part : parts) {
                        if (part.getText() != null) {
                            sb.append(part.getText());
                        }
                    }
                    return sb.toString();
                }
            }
        }
        return "";
    }
}
