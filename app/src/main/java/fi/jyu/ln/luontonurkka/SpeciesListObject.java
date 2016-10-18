package fi.jyu.ln.luontonurkka;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jaenkies on 10/18/16.
 */

public class SpeciesListObject extends View {
    public SpeciesListObject(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        inflate(context, R.layout.specieslistobject_layout, null);
    }
}
