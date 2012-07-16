package com.umlet.element.experimental;

import java.awt.Color;
import java.util.HashMap;

import com.baselet.control.Constants;
import com.baselet.control.Utils;
import com.baselet.diagram.draw.BaseDrawHandler;

public class Properties {

	public static final String FG_COLOR = "fg";
	public static final String BG_COLOR = "bg";
	public static final String AUTORESIZE = "autoresize";
	
	private static final String SEPARATOR = "=";

	protected String panelAttributes = "";
	protected String panelAttributesAdditional = "";

	private BaseDrawHandler drawer;
	
	protected HashMap<String, String> settings = new HashMap<String, String>();

	public Properties(String panelAttributes, String panelAttributesAdditional, BaseDrawHandler drawer) {
		this.panelAttributes = panelAttributes;
		this.panelAttributesAdditional = panelAttributesAdditional;
		this.drawer = drawer;
	}

	public String getPanelAttributes() {
		return panelAttributes;
	}

	public String getPanelAttributesAdditional() {
		return panelAttributesAdditional;
	}

	public void setPanelAttributes(String panelAttributes) {
		this.panelAttributes = panelAttributes;
	}

	public void setPanelAttributesAdditional(String panelAttributesAdditional) {
		this.panelAttributesAdditional = panelAttributesAdditional;
	}
	
	private void applyProperties() {
		Color fgColor = Utils.getColor(settings.get(FG_COLOR));
		if (fgColor == null) { // if fg is not set or invalid
			fgColor = Constants.DEFAULT_FOREGROUND_COLOR;
		}
		drawer.setForegroundColor(fgColor);
		
		float bgAlpha = Constants.ALPHA_MIDDLE_TRANSPARENCY;
		Color bgColor = Utils.getColor(settings.get(BG_COLOR));
		if (bgColor == null) { // if bg is not set or invalid, the background is white at full transparency
			bgColor = Constants.DEFAULT_BACKGROUND_COLOR;
			bgAlpha = Constants.ALPHA_FULL_TRANSPARENCY;
		}
		drawer.setBackground(bgColor, bgAlpha);
	}

	public void initSettingsFromText() {
		settings.clear();
		for (String line : Utils.decomposeStringsWithComments(this.getPanelAttributes())) {
			if (line.contains(SEPARATOR)) {
				String[] split = line.split(SEPARATOR, 2);
				settings.put(split[0], split[1]);
			}
		}
		applyProperties();
	}

	public String getSetting(String key) {
		return settings.get(key);
	}

	public boolean containsSetting(String key, String checkValue) {
		String realValue = settings.get(key);
		if (realValue == null && checkValue == null) return true;
		return realValue != null && realValue.equals(checkValue);
	}
	
	public void updateSetting(String key, String newValue) {
		String newState = "";
		for (String line : Utils.decomposeStringsWithComments(this.getPanelAttributes())) {
			if (!line.startsWith(key)) newState += line + "\n";
		}
		newState = newState.substring(0, newState.length()-1); //remove last linebreak
		if (newValue != null) newState += "\n" + key + SEPARATOR + newValue; // null will not be added as a value
		this.setPanelAttributes(newState);
	}
}
