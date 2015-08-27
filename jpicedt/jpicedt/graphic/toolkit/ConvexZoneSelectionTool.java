// ConvexZoneSelectionTool.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneSelectionTool.java,v 1.3 2013/03/27 06:59:26 vincentb1 Exp $
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

import javax.swing.JPopupMenu;
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.toolkit.EditorKit;
import jpicedt.graphic.toolkit.MouseTransformFactory;
import jpicedt.graphic.view.HitInfo;

public class ConvexZoneSelectionTool extends SelectionTool{

	/**
	 * @see MouseTool#getMouseToolType
	 * @return renvoie la valeur <code>CONVEXZONE_MOUSE_TOOL</code> de <code>MouseToolType</code>.
	 * @since jPicEdt 1.6
	 */
	MouseToolType getMouseToolType(){ return MouseToolType.CONVEXZONE_MOUSE_TOOL; }


	public ConvexZoneSelectionTool(EditorKit editorKit, MouseTransformFactory mtFactory){
		super(editorKit,mtFactory);
	}


	void setCursor(PECanvas canvas){
		canvas.setCursor(cursorFactory.getPECursor(CursorFactory.SELECT));
	}

	void setMouseTool(){
		editorKit.setCurrentMouseTool(EditorKit.CZ_SELECT);
	}

	JPopupMenu createPopupMenu(PEMouseEvent e,PopupMenuFactory factory){
		ConvexZoneHitInfo hi = this.editorKit.convexZoneHitTest(e, true); // selection first
		if (hi==null) hi = this.editorKit.convexZoneHitTest(e, false); // whole drawing (and it's up to the receiver to determine if element is selected or not)
		return factory.createPopupMenu(e.getCanvas(),hi);
	}
}

/// ConvexZoneSelectionTool.java ends here
