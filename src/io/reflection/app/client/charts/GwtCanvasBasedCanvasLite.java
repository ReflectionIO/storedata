//
//  GwtCanvasBasedCanvasLite.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.charts;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gchart.client.GChartCanvasLite;

/**
 * @author billy1380
 * 
 */
public final class GwtCanvasBasedCanvasLite extends Widget implements GChartCanvasLite {

	private Canvas mCanvas;
	private Context2d mGraphics;

	public GwtCanvasBasedCanvasLite() {
		mCanvas = Canvas.createIfSupported();

		if (mCanvas == null) {
			// fail silently; null reference exceptions will follow
		} else {
			mGraphics = mCanvas.getContext2d();
		}
	}

	@Override
	public Element getElement() {
		return mCanvas.getElement();
	}

	@Override
	public Widget asWidget() {
		return mCanvas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#arc(double, double, double, double, double, boolean)
	 */
	@Override
	public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean antiClockwise) {
		mGraphics.arc(x, y, radius, startAngle, endAngle, antiClockwise);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#beginPath()
	 */
	@Override
	public void beginPath() {
		mGraphics.beginPath();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#clear()
	 */
	@Override
	public void clear() {
		mGraphics.clearRect(0, 0, mGraphics.getCanvas().getWidth(), mGraphics.getCanvas().getHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#closePath()
	 */
	@Override
	public void closePath() {
		mGraphics.closePath();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#fill()
	 */
	@Override
	public void fill() {
		mGraphics.fill();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#lineTo(double, double)
	 */
	@Override
	public void lineTo(double x, double y) {
		mGraphics.lineTo(x, y);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#moveTo(double, double)
	 */
	@Override
	public void moveTo(double x, double y) {
		mGraphics.moveTo(x, y);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		mCanvas.setWidth(width + "px");
		mCanvas.setCoordinateSpaceWidth(width);

		mCanvas.setHeight(height + "px");
		mCanvas.setCoordinateSpaceHeight(height);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#setFillStyle(java.lang.String)
	 */
	@Override
	public void setFillStyle(String canvasFillStyle) {
		mGraphics.setFillStyle(canvasFillStyle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#setLineWidth(double)
	 */
	@Override
	public void setLineWidth(double width) {
		mGraphics.setLineWidth(width);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#setStrokeStyle(java.lang.String)
	 */
	@Override
	public void setStrokeStyle(String canvasStrokeStyle) {
		mGraphics.setLineJoin(LineJoin.ROUND);
		mGraphics.setStrokeStyle(canvasStrokeStyle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.GChartCanvasLite#stroke()
	 */
	@Override
	public void stroke() {
		mGraphics.stroke();
	}

}
