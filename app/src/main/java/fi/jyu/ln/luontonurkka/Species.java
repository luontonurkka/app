package fi.jyu.ln.luontonurkka;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A class representing a species, using builder design pattern.
 * Created by sinikka on 7.10.2016.
 * Edited by Jarno on 26.10.2016.
 */

public class Species implements Serializable {

    /*
    1 = plant
    2 = bird
     */
    public static final int PLANT = 1;
    public static final int BIRD = 2;

    private final String name;      //required
    private final int type;         //required
    private String idEng;
    private String idFin;
    private String imgUrl;
    private int freq;

    private Species(SpeciesBuilder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.idEng = builder.idEng;
        this.idFin = builder.idFin;
        this.imgUrl = builder.imgUrl;
        this.freq = builder.freq;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String toString() {
        return name;
    }

    public String getIdEng() { return idEng; }
    public String getIdFin() { return idFin; }

    public String getImgUrl() { return imgUrl; }

    public int getFreq() { return freq; }

    public static class SpeciesBuilder {
        private final String name;      //required
        private final int type;         //required
        private String idEng = "";
        private String idFin = "";
        private String imgUrl = "";
        private int freq = 0;

        public SpeciesBuilder(String name, int type) {
            this.name = name;
            this.type = type;
        }

        public SpeciesBuilder setWikiIdEng(String id) {
            idEng = id;
            return this;
        }

        public SpeciesBuilder setWikiIdFin(String id) {
            idFin = id;
            return this;
        }

        public SpeciesBuilder setImageUrl(String url) {
            imgUrl = url;
            return this;
        }

        public SpeciesBuilder setFreq(int frequency) {
            freq = frequency;
            return this;
        }

        public Species build() {
            return new Species(this);
        }
    }
}