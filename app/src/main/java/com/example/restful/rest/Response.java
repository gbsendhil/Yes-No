package com.example.restful.rest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sendhilgrandhi on 29/04/16.
 */
public class Response {

    private String answer;
    private Boolean forced;
    private String image;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     *
     * @param answer
     * The answer
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     *
     * @return
     * The forced
     */
    public Boolean getForced() {
        return forced;
    }

    /**
     *
     * @param forced
     * The forced
     */
    public void setForced(Boolean forced) {
        this.forced = forced;
    }

    /**
     *
     * @return
     * The image
     */
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image
     * The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
