// PEScrollPane.java --- -*- coding: iso-8859-1 -*-
// 1999 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
//
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ÉNSÉA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: PEScrollPane.java,v 1.15 2013/03/27 07:00:48 vincentb1 Exp $
// Keywords:
// X-URL: http://www.jpicedt.org/
//
// Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilité au code source et des droits de copie, de modification et de
// redistribution accordés par cette licence, il n'est offert aux utilisateurs qu'une garantie limitée.  Pour
// les mêmes raisons, seule une responsabilité restreinte pèse sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les concédants successifs.
//
// À cet égard l'attention de l'utilisateur est attirée sur les risques associés au chargement, à
// l'utilisation, à la modification et/ou au développement et à la reproduction du logiciel par l'utilisateur
// étant donné sa spécificité de logiciel libre, qui peut le rendre complexe à manipuler et qui le réserve
// donc à des développeurs et des professionnels avertis possédant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invités à charger et tester l'adéquation du logiciel à leurs
// besoins dans des conditions permettant d'assurer la sécurité de leurs systèmes et ou de leurs données et,
// plus généralement, à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
//
// Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accepté les termes.
//
/// Commentary:

//



/// Code:
package jpicedt.graphic;

import jpicedt.Localizer;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.event.ZoomListener;
import jpicedt.graphic.event.ZoomEvent;
import jpicedt.graphic.event.PEMouseInputAdapter;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.toolkit.EditorKit;
import jpicedt.graphic.toolkit.DefaultActionDispatcher;
import jpicedt.graphic.toolkit.PEAction;
import jpicedt.graphic.toolkit.ActionDispatcher;
import jpicedt.graphic.toolkit.ActionLocalizer;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;

import static java.lang.Math.floor;
import static java.lang.Math.round;
import static java.lang.Math.abs;
import static jpicedt.Log.*;

/**
 * A scrollpane that is able to host a {@link jpicedt.graphic.PECanvas PECanvas}.
 * It has rulers and is able to respond to changes in the zoom factor of the hosted view.<p>
 * [SR:todo] implement mouse position markers in the ruler area.
 * @since jpicedt 1.0
 * @author Sylvain Reynal
 * @version $Id: PEScrollPane.java,v 1.15 2013/03/27 07:00:48 vincentb1 Exp $
 */
public class PEScrollPane extends JScrollPane {

	private PECanvas canvas; // the View
	private Rule horRuler, vertRuler; // the Rulers
	private AffineTransform horRulerTransform, vertRulerTransform; // Graphics2D affine transforms used to paint rulers
	private double horRulerOrg, vertRulerOrg;
	private double horRulerScale, vertRulerScale;
	private boolean isAlreadyCentered = false; // used by ComponentHandler

	/**
	 * Construct a new PEScrollPane using the given canvas for the View.
	 */
	public PEScrollPane(PECanvas canvas) {

		super(canvas);
		this.canvas = canvas;

		this.getViewport().setBackground(Color.lightGray);

		updateAffineTransforms();

		horRuler = new Rule(Rule.HORIZONTAL);
		horRuler.setPreferredWidth(canvas.getPreferredSize().width);
		setColumnHeaderView(horRuler);

		vertRuler = new Rule(Rule.VERTICAL);
		vertRuler.setPreferredHeight(canvas.getPreferredSize().height);
		setRowHeaderView(vertRuler);


		setCorner(JScrollPane.UPPER_LEFT_CORNER, createButtonCorner()); // [pending] should be called from outside this class

		setViewportBorder(BorderFactory.createLineBorder(Color.black));
		setCorner(JScrollPane.LOWER_LEFT_CORNER, new Corner());
		setCorner(JScrollPane.UPPER_RIGHT_CORNER, new Corner());

		//canvas.addPropertyChangeListener(new PropertyChangeHandler()); // handle PageFormat change events
		// now handled by ZoomUpdateHandler
		canvas.addZoomListener(new ZoomUpdateHandler()); // handle PageFormat change events
		canvas.addMouseMotionListener(new AutoScrollHandler());
		canvas.addPEMouseInputListener(new MouseMarkerHandler());

		canvas.addComponentListener(new ComponentHandler()); // adding the listener to this scrollpane has no effect ! Why ?
		if (DEBUG) getViewport().addChangeListener(new ChangeListener(){
			        public void stateChanged(ChangeEvent e){
				        if (DEBUG) debug("viewRect=" + getViewport().getViewRect());}});
	}

	/**
	 * create a snapOn button located in the scrollpane top-left corner
	 * @since jPicEdt
	 */
	private JPanel createButtonCorner(){

		// [SR:14/11/2004: button moved to PEDrawingBoard.GridZoomToolbar]

		JButton b = new JButton();
		b.setAction(new CenterViewportAction(new DefaultActionDispatcher(canvas), Localizer.currentLocalizer().getActionLocalizer(), this));
		b.setText(null);

		b.setBorder(BorderFactory.createRaisedBevelBorder());
		b.setMargin(new Insets(0,2,0,2));

		JPanel buttonCorner = new JPanel();
		buttonCorner.setBackground(Color.white);
		buttonCorner.add(b);
		return buttonCorner;
	}

	/**
	 * Liste to mouse-move events and updates the position of the mobile coords-ticks markers accordingly.
	 * @since jPicEdt 1.4
	 */
	class MouseMarkerHandler extends PEMouseInputAdapter {

		public void mouseMoved(PEMouseEvent e){
			horRuler.setMousePosition(e.getPicPoint().x);
			vertRuler.setMousePosition(e.getPicPoint().y);
		}
		public void mouseDragged(PEMouseEvent e){
			mouseMoved(e);
		}
	}

	/**
	 * Start autoscrolling when the mouse exits the visible part of the View (ie PECanvas)
	 * and the left button is pressed (ie we're in the course of a mouse-dragged operation)
	 *
	 * @param ptMouse the current mouse coordinate
	 * @since jPicEdt 1.3
	 */
	class AutoScrollHandler extends MouseMotionAdapter {

		public void mouseDragged(MouseEvent e){
			Point ptMouse = e.getPoint();
			if (DEBUG) debug("ptMouse="+ptMouse);

			JViewport vp = getViewport();
			int vpVisRectWidth = vp.getViewRect().width;
			int vpVisRectHeight = vp.getViewRect().height;
			int vpVisRectX = vp.getViewRect().x;
			int vpVisRectY = vp.getViewRect().y;
			int vpViewSizeWidth = vp.getViewSize().width;
			int vpViewSizeHeight = vp.getViewSize().height;

			int x = ptMouse.x;
			int y = ptMouse.y;

			if (x<0) x=0;
			else if (x>vpViewSizeWidth) x = vpViewSizeWidth;

			if (y<0) y=0;
			else if (y>vpViewSizeHeight) y = vpViewSizeHeight;

			// first we compute in which direction the mouse's gone
			if (x < vpVisRectX) { // to the left
				vp.setViewPosition(new Point(x,vpVisRectY));
			}
			else if (x > vpVisRectX + vpVisRectWidth) { // to the right
				vp.setViewPosition(new Point(x-vpVisRectWidth,vpVisRectY));
			}

			if (y < vpVisRectY) { // above
				vp.setViewPosition(new Point(vpVisRectX,y));
			}
			else if (y > vpVisRectY + vpVisRectHeight) { // below
				vp.setViewPosition(new Point(vpVisRectX,y-vpVisRectHeight));
			}
		}
	}

	/**
	 * Bring the origin (i.e. (0,0)) at the center of the viewport
	 */
	public void centerViewport(){

		PicPoint centerPt = new PicPoint();

		// now, convert centerPt to view coords using the new zoom value :
		canvas.modelToView(centerPt, centerPt);
		Rectangle r = getViewport().getViewRect();
		Point upperLeftCorner = new Point(
		                            (int)(centerPt.x - 0.5*r.width),
		                            (int)(centerPt.y - 0.5*r.height));
		if (upperLeftCorner.x < 0) upperLeftCorner.x =0;
		if (upperLeftCorner.y < 0) upperLeftCorner.y =0;
		getViewport().setViewPosition(upperLeftCorner);
		repaint(); // repaint rulers !
	}


	/**
	 * update the two affine-transforms that map model-coord. to pixel-coord for the rulers
	 */
	private void updateAffineTransforms(){
		AffineTransform v2m = canvas.getPageFormat().getModel2ViewTransform(canvas.getZoomFactor());

		// compute ruler's transform from current PECanvas's transform, yet
		// cancelling out previously set vertical translation for horizontal ruler,
		// and (re)inversing y-axis scaling
		// so that font don't get displayed upside-down (i.e. now y-axis is the "JComponent" way around)
		/*		horRulerTransform = new AffineTransform(
					v2m.getScaleX(),0.0, // scaleX, shearY
					0.0,abs(v2m.getScaleY()), // shearX, scaleY
					v2m.getTranslateX(),0.0); // translateX, translateY
					*/
		horRulerTransform = new AffineTransform(
		                        1.0,0.0, // scaleX, shearY
		                        0.0,1.0, // shearX, scaleY
		                        v2m.getTranslateX(),0.0); // translateX, translateY
		horRulerScale = v2m.getScaleX(); // take current zoom into account (i.e. pixels = mm * horRulerScale)

		// cancel out horizontal translation for vertical ruler :
		/* 		vertRulerTransform = new AffineTransform(
					v2m.getScaleX(),0.0,
					0.0,abs(v2m.getScaleY()), // y-axis coordinate will have to be negative !
					0.0,v2m.getTranslateY()); */
		vertRulerTransform = new AffineTransform(
		                         1.0,0.0,
		                         0.0,1.0, // y-axis coordinate will have to be negative !
		                         0.0,v2m.getTranslateY());
		vertRulerScale = abs(v2m.getScaleY()); // make positive

	}

	/**
	 * Listen to zoom changes sourced from PECanvas. Scroll the view accordingly.
	 */
	class ZoomUpdateHandler implements ZoomListener {

		/**
		 * Called when canvas's zoom factor has changed.<br>
		 * 1/ update ruler's size according to view's (=canvas) preferred size <br>
		 * 2/ update view's position (ie we translate the viewport so that the click point remains at the centre of the sheet)<br>
		 */
		public void zoomUpdate(ZoomEvent ze){

			updateAffineTransforms();

			// update ruler's size according to the size of the View :
			horRuler.setPreferredWidth(canvas.getPreferredSize().width);
			vertRuler.setPreferredHeight(canvas.getPreferredSize().height);
			horRuler.invalidate();
			vertRuler.invalidate();
			invalidate();
			validate(); // indirectly call repaint on rulers

			// scroll view so that centerPt lies at the center of the view AFTER the zooming action :

			PicPoint centerPt = ze.getCenterPoint();

			// if no center point specified, compute it from the visible rectangle, bearing in
			// mind that zoom has already changed... hence we must use the old value.
			// Actually, all this could be done from PECanvas, yet this would create a dependency
			// b/w PECanvas and PEScrollpane (fetch parent container, fetch the viewport,...)
			if (centerPt == null){
				Rectangle r = getViewport().getViewRect(); // in pixel-coordinates
				centerPt = new PicPoint((r.x + 0.5*r.width), (r.y + 0.5*r.height)); // center of the vis. rect.
				// translate from pixel to model-coordinates using old zoom value (viewport hasn't moved yet)
				AffineTransform v2m = canvas.getPageFormat().getView2ModelTransform(ze.getOldZoomValue());
				double[] tmp = new double[2];
				tmp[0] = centerPt.x;
				tmp[1] = centerPt.y;
				// translate it to view coord system :
				v2m.transform(tmp,0,tmp,0,1); // transform 1 point
				centerPt.x = tmp[0];
				centerPt.y = tmp[1]; // now, centerPt should be in model-coordinates
			}

			// now, convert centerPt to view coords using the new zoom value :
			canvas.modelToView(centerPt, centerPt); // use new zoom value
			Rectangle r = getViewport().getViewRect();
			Point upperLeftCorner = new Point(
			                            (int)(centerPt.x - 0.5*r.width),
			                            (int)(centerPt.y - 0.5*r.height));
			if (upperLeftCorner.x < 0) upperLeftCorner.x =0;
			if (upperLeftCorner.y < 0) upperLeftCorner.y =0;
			getViewport().setViewPosition(upperLeftCorner);
			repaint(); // repaint rulers !
		}
	}


	///////////////////////////////////////////////////////////////////////////

	/**
	 * Listen to PageFormat changes in PECanvas (not used anymore)
	 * @author Sylvain Reynal
	 * @since jPicEdt
	 */
	class PropertyChangeHandler implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt){
			if (evt.getPropertyName().equals("preferredSize")){

				updateAffineTransforms(); // in case margin changed
				// update rulers size
				// was : zoomChanged(null);
				horRuler.setPreferredWidth(canvas.getPreferredSize().width);
				vertRuler.setPreferredHeight(canvas.getPreferredSize().height);
				horRuler.invalidate();
				vertRuler.invalidate();
				invalidate();
				validate();
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * An inner class implementing a component that draws scollpane corners
	 * @since PicEdt 1.0 (inner class as of PicEdt 1.2)
	 */
	class Corner extends JComponent {

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * An inner class implementing scrollpane rulers
	 * @since PicEdt 1.0 (inner class as of PicEdt 1.2)
	 */
	class Rule extends JPanel {

		public static final int HORIZONTAL = 0;
		public static final int VERTICAL = 1;
		public static final int SIZE = 35; // ruler size along the secondary axis

		private int orientation;
		private Line2D.Double line = new Line2D.Double(); // buffer to paint ticks
		private Font labelFont = new Font("SansSerif", Font.PLAIN, 10);
		private	double increment=10; // 10 mm
		private	int tickLength = 10; // pixels
		private Font horRulerFont = new Font("SansSerif", Font.PLAIN, 10);//.deriveFont(AffineTransform.getScaleInstance(0.5,0.5));
		private Font vertRulerFont = new Font("SansSerif", Font.PLAIN, 10);//.deriveFont(AffineTransform.getScaleInstance(0.5,-0.5));

		private double mousePositionMm; // mouse (X or Y) position, in mm (driven by mouse-moved events, see CoordinatesTickHandler)

		/**
		 * @param orientation = create a vertical or horizontal ruler
		 */
		public Rule(int orientation) {
			this.orientation = orientation;
			this.setBackground(Color.white);
		}

		/**
		 * set the preferred height for the vertical ruler (row)
		 */
		public void setPreferredHeight(int ph) {
			this.setPreferredSize(new Dimension(SIZE, ph));
		}

		/**
		 * set the preferred width for the horizontal ruler (column)
		 */
		public void setPreferredWidth(int pw) {
			this.setPreferredSize(new Dimension(pw, SIZE));
		}

		public void setMousePosition(double posMm){
			this.mousePositionMm = posMm;
			repaint();
		}

		/**
		 * paint the ruler ; no transform version
		 */
		public void paintComponent(Graphics gc) {
			super.paintComponent(gc); // paint background
			Graphics2D g = (Graphics2D)gc;
			g.setPaint(Color.black);
			String text = null;
			Rectangle2D clip;

			// use clipping bounds to calculate first tick and last tick location
			if (orientation == HORIZONTAL) {
				g.transform(horRulerTransform); // see updateAffineTransform
				clip = g.getClip().getBounds2D(); // translated to xOrg but with scaleX=1 (i.e. in PIXELS !!!)
				if (DEBUG) debug("clip=" + clip);
				double viewIncrement = increment * horRulerScale; // scale by zoom and DPMM (ie pixels = mm * horRulerScale)
				line.x2 = line.x1 = 1.0+(-1.0+floor(clip.getMinX()/viewIncrement))*viewIncrement; // in pixels
				line.y1 = SIZE-1;
				line.y2 = SIZE-tickLength-1;
				while(line.x1 <= clip.getMaxX()){
					g.draw(line);
					line.x1 += viewIncrement;
					line.x2 = line.x1;
					FontRenderContext frc = g.getFontRenderContext();
					TextLayout tl = new TextLayout(Long.toString(round(line.x1/horRulerScale)), horRulerFont, frc);
					tl.draw(g,(float)(line.x1-2),(float)(SIZE-tickLength-4));
				}
				// draw mouse marker
				g.setPaint(Color.red);
				line.x1 = line.x2 = this.mousePositionMm * horRulerScale;
				g.draw(line);
			}
			else { // VERTICAL RULER
				g.transform(vertRulerTransform); // y-axis is
				clip = g.getClip().getBounds2D(); // now in model-coordinate
				if (DEBUG) debugAppend("clip=" + clip);
				double viewIncrement = increment * vertRulerScale; // scale by zoom and DPMM
				line.y1 = line.y2 = 1.0+(-1.0+floor(clip.getMinY() / viewIncrement)) * viewIncrement; // e.g. clip.y=-13.0 => pty = -20.0
				line.x1 = SIZE-1;
				line.x2 = SIZE-tickLength-1;
				while(line.y1 <= clip.getMaxY()){
					g.draw(line);
					line.y1 += viewIncrement;
					line.y2 = line.y1;
					FontRenderContext frc = g.getFontRenderContext();
					TextLayout tl = new TextLayout(Long.toString(-round(line.y1/vertRulerScale)), vertRulerFont, frc);
					tl.draw(g,(float)(line.x2 -3 - tl.getBounds().getWidth()),(float)line.y1+2);
					if (DEBUG) debugAppend("painting : line.x1="+line.x1 + ", text="+Long.toString(round(line.x1/horRulerScale)));
				}
				if (DEBUG) debug("");
				// draw mouse marker
				g.setPaint(Color.red);
				line.y1 = line.y2 = -this.mousePositionMm * vertRulerScale;
				g.draw(line);
			}
			// 			g.setPaint(Color.green);
			// 			g.draw(clip);
		} // paintComponent

	} // inner class Rule

	/**
	 * Tricky hack to call centerViewport() (which bring (0,0) at the center of the viewport)
	 * when the scrollpane (actually its view) shows up for the first time.
	 * Note that relying on componentShown() does not work (but i don't know why)
	 */
	class ComponentHandler extends ComponentAdapter {

		public void componentResized(ComponentEvent e){
			//System.out.println(e);
			if (!isAlreadyCentered) {
				centerViewport();
				isAlreadyCentered = true;
			}
		}

	}

	/**
	 * Move the view so that (0,0) is at the center of the viewport
	 */
	public static class CenterViewportAction extends PEAction {

		public static final String KEY = "action.editorkit.CenterViewport";
		private PEScrollPane scrollPane;

		public CenterViewportAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer, PEScrollPane scrollPane){
			super(actionDispatcher, KEY, localizer);
			this.scrollPane = scrollPane;
		}

		public void actionPerformed(ActionEvent e){
			scrollPane.centerViewport();
		}
	}

} // PEScrollPane
