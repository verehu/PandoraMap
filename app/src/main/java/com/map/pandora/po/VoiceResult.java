package com.map.pandora.po;

import java.util.Map;

/**
 * @author jayce
 * @date 2015/05/24
 */
public class VoiceResult {

    public static final String POI="poi";
    public static final String ROUTE="route";
    public static final String LOCATION="location";

    private String domain;
    private String intent;
    private int score;
    private int demand;
    private Map<String,String> object;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public Map<String, String> getObject() {
        return object;
    }

    public String getObjectStr(){
        StringBuffer res =new StringBuffer();
        res.append("{");
        int i=0;
        for(String key : object.keySet()){
            String value = object.get(key);
            res.append("\""+key+"\":");
            res.append("\""+value+"\"");
            if(i!=object.size()-1){
                res.append(",");
            }
            i++;
        }
        res.append("}");
        return res.toString();
    }
}
