package io.lightbeat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.prefs.Preferences;

/**
 * Configuration handler for application. Access data via various get methods.
 * Also contains static default values if no other value is stored.
 */
public class LBConfig implements Config {

    private static final String LIST_SPACER = "■";

    private static final Logger logger = LoggerFactory.getLogger(LBConfig.class);

    private final Preferences preferences;

    private final Map<String, String> defaults = new HashMap<>();
    private final Map<String, Integer> defaultInts = new HashMap<>();


    public LBConfig() {
        preferences = Preferences.userNodeForPackage(getClass());
        preferences.addPreferenceChangeListener(evt -> logger.info("Set {} to value {}", evt.getKey(), evt.getNewValue()));

        defaultInts.put(ConfigNode.BEAT_SENSITIVITY.getKey(), 5);
        defaultInts.put(ConfigNode.BEAT_MIN_TIME_BETWEEN.getKey(), 200);
        defaultInts.put(ConfigNode.BRIGHTNESS_MAX.getKey(), 254);
        defaultInts.put(ConfigNode.BRIGHTNESS_SENSITIVITY.getKey(), 20);

        // hardcoded color presets
        defaults.put(ConfigNode.COLOR_SET_PRESET_LIST.getKey(), "Rainbow■Light Colors■Saturation Ladder■Club■Very Light");
        defaults.put("color.sets.Rainbow", "-65536■-63776■-30976■-13824■-2359552■-9568512■-16711882■-16711750■-16721153■-16760321■-5897985");
        defaults.put("color.sets.Light_Colors", "-8988417■-5505162■-35124■-8978452■-35181■-2359434■-887041■-4746■-28042■-9003521■-65674■-6815882");
        defaults.put("color.sets.Saturation_Ladder", "-65536■-16711924■-16774913■-65284■-47546■-9830564■-10722561■-243201■-24673■-5177457■-9011457■-30983■-1");
        defaults.put("color.sets.Club", "-65536■-65536■-65536■-65536■-16711899■-16774913■-13106945■-4521729■-65341■-65415■-16750337■-11206912■-14483712■-16711931■-5066753■-1■-65497■-65309");
        defaults.put("color.sets.Very_Light", "-6496769■-6503937■-6508033■-5268225■-17764■-6488156■-6501121■-3433217■-25422■-21092■-25345■-25371■-11364■-25439■-25438■-5046372■-7340129");
    }

    @Override
    public String get(ConfigNode node) {
        return preferences.get(node.getKey(), getDefault(node));
    }

    private String getDefault(ConfigNode node) {
        return defaults.getOrDefault(node.getKey(), null);
    }

    @Override
    public void put(ConfigNode node, String value) {
        preferences.put(node.getKey(), value);
    }

    @Override
    public int getInt(ConfigNode node) {
        return preferences.getInt(node.getKey(), getDefaultInt(node));
    }

    @Override
    public int getDefaultInt(ConfigNode node) {
        return defaultInts.getOrDefault(node.getKey(), 0);
    }

    @Override
    public void putInt(ConfigNode node, int value) {
        preferences.putInt(node.getKey(), value);
    }

    @Override
    public long getLong(ConfigNode node) {
        return preferences.getLong(node.getKey(), 0);
    }

    @Override
    public void putLong(ConfigNode node, long value) {
        preferences.putLong(node.getKey(), value);
    }

    @Override
    public boolean getBoolean(ConfigNode node) {
        return preferences.getBoolean(node.getKey(), false);
    }

    @Override
    public void putBoolean(ConfigNode node, boolean value) {
        preferences.putBoolean(node.getKey(), value);
    }

    @Override
    public List<String> getStringList(ConfigNode node) {

        String value = preferences.get(node.getKey(), null);
        if (value == null || value.length() == 0) {
            value = getDefault(node);
            if (value == null || !value.contains(LIST_SPACER)) {
                return new ArrayList<>();
            }
        }

        return new ArrayList<>(Arrays.asList(value.split(LIST_SPACER)));
    }

    @Override
    public void putList(ConfigNode node, List<?> list) {

        if (list.isEmpty()) {
            remove(node);
            return;
        }

        StringBuilder listToString = new StringBuilder();
        for (Object listEntry : list) {
            listToString.append(String.valueOf(listEntry)).append(LIST_SPACER);
        }
        if (listToString.length() > 0) {
            listToString.setLength(listToString.length() - 1);
        }

        preferences.put(node.getKey(), listToString.toString());
    }

    @Override
    public void remove(ConfigNode node) {
        preferences.remove(node.getKey());
    }
}
