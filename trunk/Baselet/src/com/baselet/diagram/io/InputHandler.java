package com.baselet.diagram.io;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.baselet.control.Main;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.diagram.command.HelpPanelChanged;
import com.baselet.diagram.draw.geom.Rectangle;
import com.baselet.element.ErrorOccurred;
import com.baselet.element.GridElement;
import com.baselet.elementnew.ElementId;
import com.baselet.elementnew.NewGridElement;
import com.baselet.elementnew.facet.common.GroupFacet;
import com.umlet.custom.CustomElementCompiler;
import com.umlet.element.experimental.ElementFactory;

/**
 * Describes what should happen with parsed elements from the input file
 * eg: set DiagramHandler variables, create GridElements etc.
 */
public class InputHandler extends DefaultHandler {

	private static final Logger log = Logger.getLogger(InputHandler.class);

	private DrawPanel _p = null;
	private GridElement e = null;
	private String elementtext;

	private int x;
	private int y;
	private int w;
	private int h;
	private String entityname;
	private String code;
	private String panel_attributes;
	private String additional_attributes;

	private Integer currentGroup;
	private DiagramHandler handler;

	// to be backward compatible - add list of old elements that were removed so that they are ignored when loading old files
	private List<String> ignoreElements;

	private String id; // Experimental elements have an id instead of an entityname

	public InputHandler(DiagramHandler handler) {
		this.handler = handler;
		_p = handler.getDrawPanel();
		ignoreElements = new ArrayList<String>();
		ignoreElements.add("main.control.Group");
		currentGroup = null;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		elementtext = "";
		if (qName.equals("element")) {
			this.panel_attributes = "";
			this.additional_attributes = "";
			this.code = null;
		}
		if (qName.equals("group")) { // TODO remove group-handling in InputHandler. Until UMLet v13, groups used own element-tags in XML. This has changed to the property group=x, so this handling is only for backwards compatibility
			currentGroup = this.handler.getDrawPanel().getSelector().getUnusedGroup();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		String elementname = qName; // [UB]: we are not name-space aware, so use the qualified name

		if (elementname.equals("help_text")) {
			handler.setHelpText(elementtext);
			handler.getFontHandler().setDiagramDefaultFontSize(HelpPanelChanged.getFontsize(elementtext));
			handler.getFontHandler().setDiagramDefaultFontFamily(HelpPanelChanged.getFontfamily(elementtext));
		}
		else if (elementname.equals("zoom_level")) {
			if (handler != null) handler.setGridSize(Integer.parseInt(elementtext));
		}
		else if (elementname.equals("group")) {
			this.currentGroup = null;
		}
		else if (elementname.equals("element")) {
			if (this.id != null) {
				try {
					NewGridElement e = ElementFactory.create(ElementId.valueOf(this.id), new Rectangle(x, y, w, h), this.panel_attributes, this.additional_attributes, this.handler);
					if (this.currentGroup != null) e.setProperty(GroupFacet.KEY, currentGroup);
					_p.addElement(e);
				} catch (Exception e) {
					log.error("Cannot instantiate element with id: " + this.id, e);
				}
				id = null;
			}
			else if (!this.ignoreElements.contains(this.entityname)) { // OldGridElement handling which can be removed as soon as all OldGridElements have been replaced
				try {
					if (this.code == null) e =  Main.getInstance().getGridElementFromPath(this.entityname);
					else e = CustomElementCompiler.getInstance().genEntity(this.code);
				} catch (Exception ex) {
					e = new ErrorOccurred();
				}
				e.setRectangle(new Rectangle(x, y, w, h));
				e.setPanelAttributes(this.panel_attributes);
				e.setAdditionalAttributes(this.additional_attributes);
				this.handler.setHandlerAndInitListeners(e);

				if (this.currentGroup != null) e.setProperty(GroupFacet.KEY, currentGroup);
				_p.addElement(e);
			}
		}
		else if (elementname.equals("type")) {
			this.entityname = elementtext;
		}
		else if (elementname.equals("id")) { // new elements have an id
			this.id = elementtext;
		}
		else if (elementname.equals("x")) {
			Integer i = new Integer(elementtext);
			x = i.intValue();
		}
		else if (elementname.equals("y")) {
			Integer i = new Integer(elementtext);
			y = i.intValue();
		}
		else if (elementname.equals("w")) {
			Integer i = new Integer(elementtext);
			w = i.intValue();
		}
		else if (elementname.equals("h")) {
			Integer i = new Integer(elementtext);
			h = i.intValue();
		}
		else if (elementname.equals("panel_attributes")) this.panel_attributes = elementtext;
		else if (elementname.equals("additional_attributes")) this.additional_attributes = elementtext;
		else if (elementname.equals("custom_code")) this.code = elementtext;
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		elementtext += (new String(ch)).substring(start, start + length);
	}

}
