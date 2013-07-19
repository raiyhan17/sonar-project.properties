package com.baselet.gwt.client.view;

import java.util.List;

import com.baselet.control.NewGridElementConstants;
import com.baselet.diagram.draw.geom.Rectangle;
import com.baselet.element.GridElement;
import com.baselet.element.Selector;
import com.baselet.gwt.client.element.ElementFactory;
import com.baselet.gwt.client.element.GwtComponent;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.user.client.ui.FocusWidget;
import com.umlet.element.experimental.ElementId;

public class DrawCanvas {
	private Canvas canvas = Canvas.createIfSupported();
	private BackgroundCanvas backgroundCanvas = new BackgroundCanvas();
	
	public DrawCanvas() {
		if (NewGridElementConstants.isDevMode) {
			backgroundCanvas.drawBackgroundGrid();
		}
	}
	
	public FocusWidget getWidget() {
		return canvas;
	}
	
	public Context2d getContext2d() {
		return canvas.getContext2d();
	}
	
	public void clearAndSetSize(int width, int height) {
		// setCoordinateSpace always clears the canvas. To avoid that see https://groups.google.com/d/msg/google-web-toolkit/dpc84mHeKkA/3EKxrlyFCEAJ
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
	}
	
	public int getWidth() {
		return canvas.getCoordinateSpaceWidth();
	}
	
	public int getHeight() {
		return canvas.getCoordinateSpaceHeight();
	}

	public String toPng() {
		return canvas.toDataUrl("image/png");
	}

	public CanvasElement getCanvasElement() {
		return canvas.getCanvasElement();
	}
	
	void draw(boolean drawEmptyInfo, List<GridElement> gridElements, Selector selector) {
		if (NewGridElementConstants.isDevMode) {
			getContext2d().drawImage(backgroundCanvas.getCanvasElement(), 0, 0);
		}

		if (drawEmptyInfo && gridElements.isEmpty()) {
			drawEmptyInfoText();
		} else {
			//		if (tryOptimizedDrawing()) return;
			for (GridElement ge : gridElements) {
				((GwtComponent) ge.getComponent()).drawOn(canvas.getContext2d(), selector.isSelected(ge));
			}
		}
	}

	private void drawEmptyInfoText() {
		double elWidth = 440;
		double elHeight = 80;
		double elXPos = getWidth()/2 - elWidth/2;
		double elYPos = getHeight()/2 - elHeight;
		GridElement emptyElement = ElementFactory.create(ElementId.Text, new Rectangle(elXPos, elYPos, elWidth, elHeight), "halign=center\nDouble-click on an element to add it to the diagram\n\nImport uxf Files using the Menu \"Import\" or simply drag them into the diagram\n\nSave diagrams persistent in browser storage using the \"Save\" menu", "");
		((GwtComponent) emptyElement.getComponent()).drawOn(canvas.getContext2d(), false);

	}

	//TODO would not work because canvas gets always resized and therefore cleaned -> so everything must be redrawn
	//	private boolean tryOptimizedDrawing() {
	//		List<GridElement> geToRedraw = new ArrayList<GridElement>();
	//		for (GridElement ge : gridElements) {
	//			if(((GwtComponent) ge.getComponent()).isRedrawNecessary()) {
	//				for (GridElement geRedraw : geToRedraw) {
	//					if (geRedraw.getRectangle().intersects(ge.getRectangle())) {
	//						return false;
	//					}
	//				}
	//				geToRedraw.add(ge);
	//			}
	//		}
	//
	//		for (GridElement ge : gridElements) {
	//			elementCanvas.getContext2d().clearRect(0, 0, ge.getRectangle().getWidth(), ge.getRectangle().getHeight());
	//			((GwtComponent) ge.getComponent()).drawOn(elementCanvas.getContext2d());
	//		}
	//		return true;
	//	}
}
