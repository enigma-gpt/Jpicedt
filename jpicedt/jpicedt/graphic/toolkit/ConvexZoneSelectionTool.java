// ConvexZoneSelectionTool.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneSelectionTool.java,v 1.3 2013/03/27 06:59:26 vincentb1 Exp $
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
