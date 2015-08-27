// ScaleTool.java --- -*- coding: iso-8859-1 -*-
// February 28, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999/2006 Sylvain Reynal
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
// Version: $Id: ScaleTool.java,v 1.12 2013/03/27 06:56:26 vincentb1 Exp $
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

import java.awt.*;
import java.awt.geom.*;

import jpicedt.JPicEdt;
import jpicedt.graphic.*;
import jpicedt.graphic.event.*;
import jpicedt.graphic.model.Element;
import jpicedt.ui.dialog.UserConfirmationCache;


/**
 * A <code>MouseTool</code> used to scale a target <code>Element</code> using a source and a destination
 * vector.
 * <p>
 * Sequence of mouse operations:
 * <ul>
 * <li>mousePressed &rarr; set first corner of source rectangle
 * <li>mouseDragged &rarr; drag second corner source rectangle
 * <li>mouseReleased &rarr; switch to destination rectangle
 * <li>mouseMoved &rarr; set second corner of destination rectangle, and rescale target element accordingly
 * <li>mousePressed &rarr; complete operation, then reset.
 * </ul>
 * @since jPicEdt 1.3
 * @author Sylvain Reynal
 * @version $Id: ScaleTool.java,v 1.12 2013/03/27 06:56:26 vincentb1 Exp $
 *
 */
public class ScaleTool extends MouseTool {

	private EditorKit kit;
	private UserConfirmationCache ucc;
	private Line2D.Double vecSrc, vecDst;
	private PicPoint ptOrg = new PicPoint();
	private CursorFactory cursorFactory = new CursorFactory();
	private Element target;
	private PicPoint lastDragPoint = new PicPoint();
	private enum CurrentTask{
		SET_SRC_RECT, SET_DST_RECT, COMPLETED };
	private CurrentTask currentTask;

	/**
	 * @param kit the EditorKit this mouse-tool is to registered with.
	 */
	public ScaleTool(EditorKit kit, Element target){
		this.kit = kit;
		this.target = target;
		currentTask = CurrentTask.SET_SRC_RECT; // first mouse pressed will set the first corner of the
		                                        // source rectangle
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
			case SET_SRC_RECT :
				e.getCanvas().beginUndoableUpdate(jpicedt.Localizer.currentLocalizer().get("action.editorkit.Scale.tooltip"));
				vecSrc = new Line2D.Double(pt.x,pt.y,pt.x,pt.y);
				vecDst = new Line2D.Double(pt.x,pt.y,pt.x,pt.y);
				ptOrg.x = pt.x;
				ptOrg.y = pt.y;
				break;
			case SET_DST_RECT : // after a mouseMoved
				currentTask = CurrentTask.COMPLETED;
				break;
			default:
		}
	}

	/** */
	public void mouseReleased(PEMouseEvent e){
		super.mouseReleased(e);
		if (e.isPopupTrigger())  return;
		switch (currentTask){
			case SET_SRC_RECT : // complete drawing of source rectangle, then switch to destination rect.
				PicPoint pt = e.getPicPoint();
				e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
				lastDragPoint.x = pt.x;
				lastDragPoint.y = pt.y;
				currentTask = CurrentTask.SET_DST_RECT;
				break;
			case COMPLETED :
				flush();
				e.getCanvas().repaint();
				e.getCanvas().endUndoableUpdate();
				break;
			default:
		}
	}

	/**  */
	public void mouseMoved(PEMouseEvent e){
		super.mouseMoved(e);
		e.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.NE_RESIZE));
		kit.postHelpMessage("help-message.MoveEndPointTransform");
		switch (currentTask){
			case SET_SRC_RECT :
				break;
			case SET_DST_RECT :
				PicPoint pt = e.getPicPoint();
				e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
				if (vecDst.x2 == pt.x && vecDst.y2 == pt.y) return;
				vecDst.x2 = pt.x;
				vecDst.y2 = pt.y;
				e.getCanvas().repaint(); // [pending]
				if (lastDragPoint.x==ptOrg.x || lastDragPoint.y==ptOrg.y) return; // div by 0
				double sx = (vecDst.x2-ptOrg.x)/(lastDragPoint.x-ptOrg.x);
				double sy = (vecDst.y2-ptOrg.y)/(lastDragPoint.y-ptOrg.y);
				if (sx == 0 || sy ==0) return; // interdire l'applatissement total
				if(sx != 1.0 || sy != 1.0)
					target.scale(ptOrg,sx,sy,ucc); // scale element
				lastDragPoint.setCoordinates(pt);
				break;
			default:
		}
	}

	/**  */
	public void mouseDragged(PEMouseEvent e){
		super.mouseDragged(e);
		if (e.isPopupTrigger()) return;
		switch (currentTask){
			case SET_SRC_RECT :
				PicPoint pt = e.getPicPoint();
				e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
				if (vecSrc.x2 == pt.x && vecSrc.y2 == pt.y) return;
				vecSrc.x2 = pt.x;
				vecSrc.y2 = pt.y;
				e.getCanvas().repaint(); // [pending]
				break;
			case SET_DST_RECT :
				break;
			default:
		}
	}

	/**
	 * Called when this tool is being replaced by another tool in the hosting kit
	 */
	public void flush(){
		vecSrc = vecDst = null;
		currentTask = CurrentTask.SET_SRC_RECT; // prepare next mousePressed
	}

	public void init(){
		ucc = new UserConfirmationCache(JPicEdt.getMDIManager());
	}

	/**
	 * Allow the tool to paint shapes that are specific to this tool.
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		if (vecSrc == null || vecDst == null) return;
		float[] dash = {1.0f,1.0f};
		g.setStroke(new BasicStroke((float)(1.0/scale),BasicStroke.CAP_ROUND,
		BasicStroke.JOIN_ROUND,10.0f,dash,0.5f));
		g.setPaint(Color.blue);
		g.draw(vecSrc.getBounds2D());
		g.setPaint(Color.red);
		g.draw(vecDst.getBounds2D());
	}

} // class
