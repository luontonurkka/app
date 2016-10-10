package fi.jyu.ln.luontonurkka;

/**
 * A class representing a species, using builder design pattern.
 * Created by sinikka on 7.10.2016.
 */

public class Species {

    private final String name;      //required
    private final int type;         //required
    private final String descr;     //optional

    private Species(SpeciesBuilder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.descr = builder.descr;
    }

    protected String getName() {
        return name;
    }

    protected int getType() {
        return type;
    }

    public String toString() {
        return name;
    }

    public static class SpeciesBuilder {
        private final String name;      //required
        private final int type;         //required
        private String descr;         //optional

        public SpeciesBuilder(String name, int type) {
            this.name = name;
            this.type = type;
        }

        public SpeciesBuilder descr(String descr) {
            this.descr = descr;
            return this;
        }

        public Species build() {
            return new Species(this);
        }
    }

}