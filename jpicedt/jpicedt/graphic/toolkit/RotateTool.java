// RotateTool.java --- -*- coding: iso-8859-1 -*-
// September 25, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: RotateTool.java,v 1.15 2013/03/27 06:56:31 vincentb1 Exp $
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
package jpicedt.graphic.toolkit;

import java.lang.Double;
import jpicedt.JPicEdt;
import jpicedt.Log;
import jpicedt.ui.dialog.RotateCenterChooser;

import jpicedt.graphic.event.*;
import jpicedt.graphic.*;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.PicVector;

import java.awt.*;
import java.awt.TextComponent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;

import static java.lang.Math.max;
import static jpicedt.Log.debug;




/**
 * A <code>MouseTool</code> used to rotate a target <code>Element</code> using a origin point and a mobile
 * point used to compute the rotation angle.
 * <p>
 * Sequence of mouse operations:
 * <ul>
 * <li>mousePressed &rarr; set origin
 * <li>mouseDragged &rarr; set initial position of mobile point ; draw corresponding vector
 * <li>mouseReleased &rarr; switch to "move mobile point" task
 * <li>mouseMoved &rarr; move mobile point, and rotate target element accordingly
 * <li>mousePressed &rarr; complete operation, then reset.
 * </ul>
 * @since jPicEdt 1.4
 * @author Sylvain Reynal
 * @version $Id: RotateTool.java,v 1.15 2013/03/27 06:56:31 vincentb1 Exp $
 *
 */
public class RotateTool extends MouseTool {

	private final static boolean DEBUG = false;

	private EditorKit kit;

	// paint related :
	private Line2D.Double lineSrc, lineDst; // initial and mobile vectors used to compute the rotation angle
	private Stroke stroke;
	private CursorFactory cursorFactory = new CursorFactory();

	// geometry related :
	private Element target;
	private PicPoint ptOrg = new PicPoint(); // rotation center
	private PicPoint lastDragPoint = new PicPoint(); // last mobile point position
	private PicVector v1=new PicVector(), v2=new PicVector(); // buffers used to compute rotation angle from lineSrc and lineDst

	private double lastAngle = Double.NaN; // dernière valeur d'angle de rotation passé via la GridZoomToolBar

	// task related :
	//
	//+---------------+-------------------------------------------------------------------------------+
	//|               |                                    évènements                                 |
	//+ current task  +---------------------------------------+-------------------+-------------------+
	//|               | mousePressed      | mouseDragged      | mouseMoved        |mouseReleased      |
	//+---------------+-------------------+-------------------+-------------------+-------------------+
	//|SET_SRC_VECT   |setRotationCenter  |setLineSrcEndPoint | néant             |set lastDragPoint  |
	//|               |                   |                   |                   |-> SET_MOBILE_VECT |
	//+---------------+-------------------+-------------------+-------------------+-------------------+
	//|SET_MOBILE_VECT|-> COMPLETED       | néant             | delta rotation    | néant             |
	//|               |                   |                   | set lastDragPoint |                   |
	//+---------------+-------------------+-------------------+-------------------+-------------------+
	//|COMPLETED      | néant             | néant             | néant             | complete          |
	//+---------------+-------------------+-------------------+-------------------+-------------------+

	private int currentTask;
	private final static int SET_SRC_VECT = 0;
	private final static int SET_MOBILE_VECT = 1;
	private final static int COMPLETED = 2;

	/**
	 * @param kit the EditorKit this mouse-tool is to registered with.
	 */
	public RotateTool(EditorKit kit, Element target){
		this.kit = kit;
		this.target = target;
		currentTask = SET_SRC_VECT; // first mouse pressed will set the rotation center (ptOrg)
		if(DEBUG) debug("X -> SET_SRC_VECT");

	}

	/**
	 * Définit le centre de rotation.
	 *
	 * @param pt le <code>PicPoint</code> centre de rotation.
	 */
	public void setRotationCenter(PicPoint pt){
		lineSrc = new Line2D.Double(pt.x,pt.y,pt.x,pt.y);
		lineDst = new Line2D.Double(pt.x,pt.y,pt.x,pt.y);
		ptOrg.setCoordinates(pt);
	}

	/** set the source rect, then the destination rect */
	public void mousePressed(PEMouseEvent e){
		super.mousePressed(e);
		if (e.isPopupTrigger())  {
			kit.setCurrentMouseTool(EditorKit.SELECT); // callbacks flush()
			return;
		}
		PicPoint pt = e.getPicPoint();
		e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
		switch (currentTask){
			case SET_SRC_VECT :
				e.getCanvas().beginUndoableUpdate(jpicedt.Localizer.currentLocalizer().get("action.editorkit.Rotate.tooltip"));
				setRotationCenter(pt);
				break;
			case SET_MOBILE_VECT : // after a mouseMoved
				currentTask = COMPLETED;
				if(DEBUG) debug("SET_MOBILE_VECT -> COMPLETED");
				break;
			default:
		}
	}

	void setLineSrcEndPoint(PicPoint pt,PECanvas canvas){
		if (lineSrc.x2 == pt.x && lineSrc.y2 == pt.y) return;
		lineSrc.x2 = pt.x;
		lineSrc.y2 = pt.y;
		canvas.repaint(); // [pending]
	}

	/**  */
	public void mouseDragged(PEMouseEvent e){
		super.mouseDragged(e);
		if (e.isPopupTrigger()) return;
		switch (currentTask){
			case SET_SRC_VECT : // set initial position of mobile point
				PicPoint pt = e.getPicPoint();
				e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
				setLineSrcEndPoint(pt,e.getCanvas());
				break;
			case SET_MOBILE_VECT :
				break;
			default:
		}
	}

	/** */
	public void mouseReleased(PEMouseEvent e){
		super.mouseReleased(e);
		if (e.isPopupTrigger())  return;
		switch (currentTask){
			case SET_SRC_VECT : // complete drawing of source vector, then switch to mobile vector.
				PicPoint pt = e.getPicPoint();
				e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
				lastDragPoint.setCoordinates(pt);
				lastAngle = 0;
				currentTask = SET_MOBILE_VECT;
				break;
			case COMPLETED :
				complete(e.getCanvas());
				break;
			default:
		}
	}

	void rotateToPoint(PicPoint pt,PECanvas canvas){
		if (lineDst.x2 == pt.x && lineDst.y2 == pt.y) return;
		lineDst.x2 = pt.x;
		lineDst.y2 = pt.y;
		canvas.repaint(); // [pending]
		// compute rotation angle :
		v1.setCoordinates(ptOrg, lastDragPoint);
		v2.setCoordinates(ptOrg, pt);
		double deltaAngle = v1.angle(v2); // no worry, this handles null vectors by returning a null angle
		//debug("v1="+v1+", v2="+v2+", angle="+Math.toDegrees(angle)+" deg");
		if (deltaAngle == 0) return;
		target.rotate(ptOrg,deltaAngle); // rotate element
		v1.setCoordinates(lineSrc); // source-vector
		v2.setCoordinates(lineDst); // movable-vector
		lastDragPoint.setCoordinates(pt);
		lastAngle += deltaAngle;
	}

	/**  */
	public void mouseMoved(PEMouseEvent e){
		super.mouseMoved(e);
		e.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.SELECT));// [SR:pending] create specific ROTATE cursor
		kit.postHelpMessage("help-message.MoveEndPointTransform");
		switch (currentTask){
			case SET_SRC_VECT :
				break;
			case SET_MOBILE_VECT :
				PicPoint pt = e.getPicPoint();
				e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
				rotateToPoint(pt,e.getCanvas());
				e.getCanvas().setRotateAngle(transformRad2Deg(lastAngle));
				break;
			default:
		}
	}

	// new *************************** begin (by ss & bp)
	/**
	 * transforms an angle from radians to degree(0-360°)
	 */
	private double transformRad2Deg(double rad){
		double deg = Math.toDegrees(rad);
		if(deg == 360.0) return 0.0;
		if(deg < 0.0) return 360.0 + deg;
		return deg;
	}
	// new *************************** end (by ss & bp)

	void complete(PECanvas canvas){
		lineSrc = lineDst = null;
		currentTask = SET_SRC_VECT; // prepare next mousePressed
		if(DEBUG) debug("COMPLETED -> SET_SRC_VECT");
		lastAngle = Double.NaN;
		canvas.repaint();
		canvas.endUndoableUpdate();
	}

	/**
	 * Called when this tool is being replaced by another tool in the hosting kit
	 */
	public void flush(){
		PECanvas canvas = JPicEdt.getActiveCanvas();
		complete(canvas);
		canvas.setRotateAngleLabelVisible(false);
	}

	/**
	 * Allow the tool to paint shapes that are specific to this tool.
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		if (lineSrc == null || lineDst == null) return;
		if (stroke==null) {
			float[] dash = {1.0f,1.0f};
			stroke = new BasicStroke((float)(1.0/scale),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,10.0f,dash,0.5f);
		}
		g.setStroke(stroke);
		g.setPaint(Color.blue);
		g.draw(lineSrc);
		g.setPaint(Color.red);
		g.draw(lineDst);
	}

	/**
	 * initialisation process
	 */
	public void init() {
		this.kit.getCanvas().setRotateAngleLabelVisible(true);
		lastAngle = Double.NaN;
	}

	/**
	 * Rotation à partir du <code>GridZoomToolBar</code>.
	 *
	 * @param angle Angle de rotation à partir de la position initiale
	 * @see jpicedt.ui.PEDrawingBoard.GridZoomToolBar
	 */
	public void rotateTarget(double angle,PECanvas canvas){
		RotateCenterChooser.RotateCenterChoice choice = new RotateCenterChooser.RotateCenterChoice();
		double radAngle;
		for(;;){
			switch (currentTask){
			case SET_SRC_VECT :
				target.getBoundingBox(choice.bb);

				// traitement équivalent à mousePressed
				new RotateCenterChooser(JPicEdt.getMDIManager(),choice);
				if(choice.cancelled)
					return;
				setRotationCenter(choice.pt);

				// traitement équivalent à mouseDragged. La longeur du trait est le maximum
				// de la plus grande dimension de la cible et de 2.5 le pas de grille
				double length;
				length = max(choice.bb.getWidth(),choice.bb.getHeight());
				length = max(length,2.5*canvas.getGrid().getGridStep());
				choice.pt.setCoordinates(ptOrg).translate(PicVector.X_AXIS,length);
				setLineSrcEndPoint(choice.pt,canvas);

				// traitement équivalent à mouseReleased
				lastDragPoint.setCoordinates(choice.pt);
				if(choice.reajustable)
				{
					currentTask = SET_MOBILE_VECT;
					if(DEBUG) debug("SET_SRC_VECT -> SET_MOBILE_VECT");
				}
				else
				{
					currentTask = COMPLETED;
					target.rotate(ptOrg,Math.toRadians(angle));
					if(DEBUG) debug("SET_SRC_VECT -> COMPLETED");
				}
				break;

			case SET_MOBILE_VECT :
				radAngle = Math.toRadians(angle);
				if(radAngle == lastAngle){
					// traitement équivalent à mousePressed
					currentTask = COMPLETED;
					if(DEBUG) debug("SET_MOBILE_VECT -> COMPLETED");
				}
				else
				{
					// traitement équivalent à mouseMoved
					choice.pt.setCoordinates(lineSrc.x2,lineSrc.y2);
					v1.setCoordinates(ptOrg, choice.pt);
					v1.rotate(radAngle);
					choice.pt.setCoordinates(ptOrg).translate(v1);
					rotateToPoint(choice.pt,canvas);
					lastAngle = radAngle;
					canvas.setRotateAngle(angle);
				}
				return;
			case COMPLETED:
				complete(canvas); // prepare next mousePressed
				if(DEBUG) debug("COMPLETED -> SET_SRC_VECT");
				return;
			}
		}
	}
} // class
