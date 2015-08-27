// ZoomTool.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: ZoomTool.java,v 1.10 2013/03/27 06:56:11 vincentb1 Exp $
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

import jpicedt.graphic.event.PEMouseEvent;


/**
 * A mouse tool for EditorKit, which allows to zoom in and out while retaining the current scene at the center
 * of the drawing sheet.
 * @author Sylvain Reynal
 * @since jPicEdt 1.3.2
 */
public class ZoomTool extends MouseTool {

	private static final double zoomIncrement = 2.0; // ie zoom = 2.0 * zoom when increased
	private EditorKit kit;
	private CursorFactory cursorFactory = new CursorFactory();

	/**
	 * construct a new ZoomTool for the given EditorKit
	 * @since jPicEdt 1.3.2
	 */
	public ZoomTool(EditorKit kit){
		this.kit = kit;
	}

	/**
	 * Depending on the button and the modifiers, we increase or decrease zoom, or do nothing.
	 */
	public void mousePressed(PEMouseEvent e) {
		super.mousePressed(e);

		double zoom = e.getCanvas().getZoomFactor(); // fetch current zoom value

		// right button click : back to SELECT_MODE
		if (e.isPopupTrigger()){
			kit.setCurrentMouseTool(EditorKit.SELECT);
			return;
		}

		// shift+left-click : zoom out
		else if (e.isShiftDown()) {
			zoom /= zoomIncrement;
			e.getCanvas().setZoomFactor(zoom, e.getPicPoint());
			return;
		}

		// left-click : zoom in
		else {
			zoom *= zoomIncrement;
			e.getCanvas().setZoomFactor(zoom, e.getPicPoint());
			return;
		}
	}

	/** set cursor */
	public void mouseMoved(PEMouseEvent e){
		super.mouseMoved(e);
		kit.postHelpMessage("help-message.Zoom");
		if (e.isShiftDown()) e.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.ZOOM_OUT));
		else e.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.ZOOM_IN));
	}

} // ZoomTool class
