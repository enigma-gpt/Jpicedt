// ZoomTool.java --- -*- coding: iso-8859-1 -*-
// Copyright (C) 1999/2013 Sylvain Reynal
//
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (�NS�A)
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
// Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilit� au code source et des droits de copie, de modification et de
// redistribution accord�s par cette licence, il n'est offert aux utilisateurs qu'une garantie limit�e.  Pour
// les m�mes raisons, seule une responsabilit� restreinte p�se sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les conc�dants successifs.
//
// � cet �gard l'attention de l'utilisateur est attir�e sur les risques associ�s au chargement, �
// l'utilisation, � la modification et/ou au d�veloppement et � la reproduction du logiciel par l'utilisateur
// �tant donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � manipuler et qui le r�serve
// donc � des d�veloppeurs et des professionnels avertis poss�dant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invit�s � charger et tester l'ad�quation du logiciel � leurs
// besoins dans des conditions permettant d'assurer la s�curit� de leurs syst�mes et ou de leurs donn�es et,
// plus g�n�ralement, � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�.
//
// Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accept� les termes.
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
