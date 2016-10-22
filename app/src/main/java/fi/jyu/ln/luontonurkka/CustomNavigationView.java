package fi.jyu.ln.luontonurkka;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.Switch;

import fi.jyu.ln.luontonurkka.tools.SettingsManager;

/**
 * Extension to navigation view that saves the
 * settings toggles in the drawer.
 *
 * Created by Jarno on 22.10.16.
 */

public class CustomNavigationView extends NavigationView {
    public CustomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        final SettingsManager sm = new SettingsManager((Activity) context);

        final String nightSettingsString = context.getString(R.string.setting_night_theme);
        Switch nightSwitch = (Switch)getHeaderView(0).findViewById(R.id.switch_night);
        boolean nightTheme = sm.getBool(nightSettingsString);
        if(nightTheme) {
            nightSwitch.toggle();
        }
        nightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sm.setBool(nightSettingsString, isChecked);
            }
        });
    }
}
