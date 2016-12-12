package fi.jyu.ln.luontonurkka;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.View;
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

    private final SettingsManager sm;

    public CustomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View headerView = getHeaderView(0);

        if(!isInEditMode()) {
            sm = new SettingsManager((Activity) context);

//            final String nightSettingsString = context.getString(R.string.setting_night_theme);
//            Switch nightSwitch = (Switch) headerView.findViewById(R.id.switch_night);
//            boolean nightTheme = sm.getBool(nightSettingsString);
//            if (nightTheme) {
//                nightSwitch.toggle();
//            }
//            nightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    sm.setBool(nightSettingsString, isChecked);
//                }
//            });

            final String mapSettingsString = context.getString(R.string.setting_map_default);
            Switch mapSwitch = (Switch) headerView.findViewById(R.id.switch_map);
            boolean mapDefault = sm.getBool(mapSettingsString);
            if (mapDefault) {
                mapSwitch.toggle();
            }
            mapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sm.setBool(mapSettingsString, isChecked);
                }
            });
        } else {
            sm = null;
        }
    }

    public SettingsManager getSettingsManager() {
        return sm;
    }
}
