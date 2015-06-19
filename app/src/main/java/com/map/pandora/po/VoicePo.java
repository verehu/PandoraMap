package com.map.pandora.po;

import java.util.List;

/**
 * @author jayce
 * @date 2015/05/24
 */
public class VoicePo {
    private String raw_text;
    private String parsed_text;
    private List<VoiceResult> results;

    public String getRaw_text() {
        return raw_text;
    }

    public void setRaw_text(String raw_text) {
        this.raw_text = raw_text;
    }

    public String getParsed_text() {
        return parsed_text;
    }

    public void setParsed_text(String parsed_text) {
        this.parsed_text = parsed_text;
    }

    public List<VoiceResult> getResults() {
        return results;
    }

    public void setResults(List<VoiceResult> results) {
        this.results = results;
    }
}
