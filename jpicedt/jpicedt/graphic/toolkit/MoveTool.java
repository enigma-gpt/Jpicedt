// MoveTool.java --- February 28, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: MoveTool.java,v 1.14 2013/03/27 06:57:11 vincentb1 Exp $
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

import jpicedt.graphic.event.*;
import jpicedt.graphic.*;
import jpicedt.graphic.model.Element;

import java.awt.*;
import java.awt.geom.*;


/**
 * A <code>MouseTool</code> used to translate a target <code>Element</code> by a given vector.
 * @since jPicEdt 1.3
 * @author Sylvain Reynal
 * @version $Id: MoveTool.java,v 1.14 2013/03/27 06:57:11 vincentb1 Exp $
 */
public class MoveTool extends MouseTool {

	private EditorKit kit;
	private Line2D.Double vec;
	private CursorFactory cursorFactory = new CursorFactory();
	private Element target;
	private PicPoint lastDragPoint = new PicPoint();


	/**
	 * @param kit the <code>EditorKit</code> this mouse-tool is to registered with.
	 * @param target the target <code>Element</code> to be moved
	 */
	public MoveTool(EditorKit kit, Element target){
		this.kit = kit;
		this.target = target;
	}

	/** Set the first point of the translation vector. */
	public void mousePressed(PEMouseEvent e){
		super.mousePressed(e);
		if (e.isPopupTrigger())  {
			kit.setCurrentMouseTool(EditorKit.SELECT);
			return;
		}
		e.getCanvas().beginUndoableUpdate(jpicedt.Localizer.currentLocalizer().get("action.editorkit.Translate.tooltip"));
		PicPoint pt = e.getPicPoint();
		e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
		vec = new Line2D.Double(pt.x,pt.y,pt.x,pt.y);
		lastDragPoint.x = pt.x;
		lastDragPoint.y = pt.y;
	}

	/** Set the 2nd pt of the translation vector and translate the target. */
	public void mouseDragged(PEMouseEvent e){
		super.mouseDragged(e);
		PicPoint pt = e.getPicPoint();
		e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
		if (vec.x2 == pt.x && vec.y2 == pt.y) return;
		vec.x2 = pt.x;
		vec.y2 = pt.y;
		//e.getCanvas().repaintFromModelRect(vec.getBounds2D());
		e.getCanvas().repaint(); // [pending]
		target.translate(vec.x2-lastDragPoint.x,vec.y2-lastDragPoint.y);

		lastDragPoint.x = pt.x;
		lastDragPoint.y = pt.y;
	}


	/** */
	public void mouseReleased(PEMouseEvent e){
		super.mouseReleased(e);
		if (e.isPopupTrigger())  return;
		vec = null;
		e.getCanvas().endUndoableUpdate();
		e.getCanvas().repaint();
	}

	/** set the cursor */
	public void mouseMoved(PEMouseEvent e){
		super.mouseMoved(e);
		kit.postHelpMessage("help-message.MoveTransform");
		e.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.MOVE));
	}

	/**
	 * Allow the tool to paint shapes that are specific to this tool.
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		if (vec == null) return;
		g.setPaint(Color.blue);
		float[] dash = {1.0f,1.0f};
		g.setStroke(new BasicStroke((float)(1.0/scale),BasicStroke.CAP_ROUND,
		BasicStroke.JOIN_ROUND,10.0f,dash,0.5f));
		g.draw(vec);
	}

} // class
