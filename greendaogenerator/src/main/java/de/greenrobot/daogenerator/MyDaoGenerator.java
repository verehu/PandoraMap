package de.greenrobot.daogenerator;

public class MyDaoGenerator {
    public static final int VERSION = 1;

    public static void main(String[] args) throws Exception{
        Schema schema = new Schema(VERSION, "com.map.pandora");

        addPoiInfoHistory(schema);

        new DaoGenerator().generateAll(schema,"./app/src-gen");
    }

    public static void addPoiInfoHistory(Schema schema) {
        Entity entity = schema.addEntity("PoiHistory");
        entity.addStringProperty("key").primaryKey();
        entity.addStringProperty("city");
        entity.addStringProperty("district");
        entity.addDateProperty("timestamp");
    }
}
