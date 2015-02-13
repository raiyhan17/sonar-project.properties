package com.baselet.element.facet.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baselet.control.enums.Priority;
import com.baselet.element.facet.GlobalFacet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.gui.AutocompletionText;

/**
 * the collector is the last global facet which should be applied (therefore LOW prio)
 * it must be global to be applied before the drawCommonContent of the class but after any other global facet
 */
public class TextBeforeFirstSeparatorCollectorFacet extends GlobalFacet {

	public static final TextBeforeFirstSeparatorCollectorFacet INSTANCE = new TextBeforeFirstSeparatorCollectorFacet();

	protected TextBeforeFirstSeparatorCollectorFacet() {}

	public static class TextBeforeFirstSeparatorCollectorFacetResponse {
		private boolean firstSepFound = false;
		private final List<String> lines = new ArrayList<String>();

		public List<String> getLines() {
			return lines;
		}
	}

	@Override
	public boolean checkStart(String line, PropertiesParserState state) {
		return !getOrInit(state).firstSepFound;
	}

	@Override
	public void handleLine(String line, PropertiesParserState state) {
		if (line.equals(SeparatorLineFacet.KEY)) {
			getOrInit(state).firstSepFound = true;
			return;
		}
		else {
			getOrInit(state).getLines().add(line);
		}
	}

	@Override
	public List<AutocompletionText> getAutocompletionStrings() {
		return Arrays.asList(new AutocompletionText(SeparatorLineFacet.KEY, "ends package title part"));
	}

	@Override
	public Priority getPriority() {
		return Priority.LOW;
	}

	private TextBeforeFirstSeparatorCollectorFacetResponse getOrInit(PropertiesParserState state) {
		return state.getOrInitFacetResponse(TextBeforeFirstSeparatorCollectorFacet.class, new TextBeforeFirstSeparatorCollectorFacetResponse());
	}

}