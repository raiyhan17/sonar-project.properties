package com.baselet.gwt.client.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.baselet.control.NewGridElementConstants;
import com.baselet.control.SharedUtils;
import com.baselet.diagram.commandnew.AddGridElementCommand;
import com.baselet.diagram.commandnew.CanAddAndRemoveGridElement;
import com.baselet.diagram.commandnew.Controller;
import com.baselet.diagram.commandnew.RemoveGridElementCommand;
import com.baselet.diagram.draw.geom.Rectangle;
import com.baselet.element.GridElement;
import com.baselet.gwt.client.BrowserStorage;
import com.baselet.gwt.client.element.Diagram;
import com.baselet.gwt.client.element.ElementFactory;

public class CommandInvoker extends Controller {

	private static final CommandInvoker instance = new CommandInvoker();
	public static CommandInvoker getInstance() {
		return instance;
	}
	private CommandInvoker() {
		super();
	}
	
	void addElements(CanAddAndRemoveGridElement target, GridElement ... elements) {
		this.executeCommand(new AddGridElementCommand(target, elements));
	}

	void addElements(CanAddAndRemoveGridElement target, Collection<GridElement> elements) {
		addElements(target, elements.toArray(new GridElement[elements.size()]));
	}
	void removeElements(DrawPanel target, List<GridElement> elements) {
		GridElement[] elementsArray = elements.toArray(new GridElement[elements.size()]);
		this.executeCommand(new RemoveGridElementCommand(target, elementsArray));
	}
	
	void removeSelectedElements(DrawPanel target) {
		removeElements(target, target.getSelector().getSelectedElements());
	}
	
	
	
	
	
	//TODO implement copy & paste as commands

	void copySelectedElements(DrawPanel target) {
		BrowserStorage.setClipboard(copyElementsInList(target.getSelector().getSelectedElements(), target.getDiagram())); // must be copied here to ensure location etc. will not be changed
	}

	void cutSelectedElements(DrawPanel target) {
		copySelectedElements(target);
		removeSelectedElements(target);
	}
	
	void pasteElements(DrawPanel target) {
		List<GridElement> copyOfElements = copyElementsInList(BrowserStorage.getClipboard(), target.getDiagram());
		realignElementsToVisibleRect(target, copyOfElements);
		addElements(target, copyOfElements); // copy here to make sure it can be pasted multiple times
	}

	private List<GridElement> copyElementsInList(Collection<GridElement> sourceElements, Diagram targetDiagram) {
		List<GridElement> targetElements = new ArrayList<GridElement>();
		for (GridElement ge : sourceElements) {
			GridElement e = ElementFactory.create(ge, targetDiagram);
			targetElements.add(e);
		}
		return targetElements;
	}
	
	void realignElementsToVisibleRect(DrawPanel target, List<GridElement> gridElements) {
		Rectangle rect = SharedUtils.getGridElementsRectangle(gridElements);
		Rectangle visible = target.getVisibleBounds();
		for (GridElement ge : gridElements) {
			ge.getRectangle().move(visible.getX()-rect.getX() + NewGridElementConstants.DEFAULT_GRID_SIZE, visible.getY()-rect.getY() + NewGridElementConstants.DEFAULT_GRID_SIZE);
		}
	}
}
