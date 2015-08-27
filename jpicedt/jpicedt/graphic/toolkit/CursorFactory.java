// CursorFactory.java --- -*- coding: iso-8859-1 -*-
// December 31, 2001 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
// Copyright (C) 2007/2013 Sylvain Reynal, Vincent Bela�che
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
// Version: $Id: CursorFactory.java,v 1.11 2013/03/27 06:59:01 vincentb1 Exp $
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

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;


/**
 * A factory for custom cursors.
 * @author Sylvain Reynal
 * @since jPicEdt 1.3.1
 * @version $Id: CursorFactory.java,v 1.11 2013/03/27 06:59:01 vincentb1 Exp $
 */
public class CursorFactory {

	/* custom cursor starts from 100 */
	public static final int SELECT    = Cursor.DEFAULT_CURSOR; 	// 0
	public static final int DRAW      = Cursor.CROSSHAIR_CURSOR;// 1
	public static final int MOVE      = Cursor.MOVE_CURSOR;	// 13
	public static final int N_RESIZE  = Cursor.N_RESIZE_CURSOR; // 8
	public static final int E_RESIZE  = Cursor.E_RESIZE_CURSOR; // 11
	public static final int S_RESIZE  = Cursor.S_RESIZE_CURSOR;	// 9
	public static final int W_RESIZE  = Cursor.W_RESIZE_CURSOR;	// 10
	public static final int NE_RESIZE = Cursor.NE_RESIZE_CURSOR;// 7
	public static final int NW_RESIZE = Cursor.NW_RESIZE_CURSOR;// 6
	public static final int SE_RESIZE = Cursor.SE_RESIZE_CURSOR;// 5
	public static final int SW_RESIZE = Cursor.SW_RESIZE_CURSOR;// 4
	public static final int ZOOM_IN   = 100;
	public static final int ZOOM_OUT  = 101;
	public static final int MOVE_ENDPT= 102; // arrow + "°"
	public static final int REMOVE_ENDPT= 103; // arrow + "-"
	public static final int ADD_ENDPT= 104; // arrow + "+"
	public static final int INVALID= 105;
	public static final int MOVE_ENDPT_FROM_CENTER = 106; // arrow + "O"
	public static final int MOVE_ONE_ONLY = 107; // araign�e + "1"
	public static final int CZ_SELECT = 108; // s�lection de zone convexe

	// cached cursors :
	private Cursor zoomInCursor, zoomOutCursor, moveEndPtCursor, removeEndPtCursor, addEndPtCursor;
	private Cursor invalidCursor, moveEndPtFromCenterCursor,moveOneOnlyCursor;
	private Cursor czSelectCursor;

	private final int width = 64;
	private final int height = 64;
	private final String size = Integer.toString(width) + "x" + Integer.toString(height);

	/**
	 * @return a custom cursor from the given type.
	 * @param type if type relates to a custom cursor, the cursor is cached the first time.
	 */
	public Cursor getPECursor(int type){

		//String size = "16x16";
		//String size = "32x32"; // [pending] use 32x32 on Windows ? They look too small...
		// [pending] We should test prefered size with ...
		// Dimension dim = Toolkit.getBestCursorSize(32,32)

		switch(type){

		case ZOOM_IN :
			if (zoomInCursor != null) return zoomInCursor;
			zoomInCursor = createCustomCursor("CursorZoomin",size,"ZoomIn",16,16);
			return zoomInCursor;
		case ZOOM_OUT :
			if (zoomOutCursor != null) return zoomOutCursor;
			zoomOutCursor = createCustomCursor("CursorZoomout",size,"ZoomOut",16,16);
			return zoomOutCursor;
		case MOVE_ENDPT :
			if (moveEndPtCursor != null) return moveEndPtCursor;
			moveEndPtCursor = createCustomCursor("CursorMoveEndPoint",size,"MoveEndPoint",3,3);
			return moveEndPtCursor;
		case MOVE_ENDPT_FROM_CENTER :
			if (moveEndPtFromCenterCursor != null) return moveEndPtFromCenterCursor;
			moveEndPtFromCenterCursor = createCustomCursor("CursorMoveEndPointFromCenter",size,"MoveEndPointFromCenter",8,8);
			return moveEndPtFromCenterCursor;
		case ADD_ENDPT :
			if (addEndPtCursor != null) return addEndPtCursor;
			addEndPtCursor = createCustomCursor("CursorAddEndPoint",size,"AddEndPoint",3,3);
			return addEndPtCursor;
		case REMOVE_ENDPT :
			if (removeEndPtCursor != null) return removeEndPtCursor;
			removeEndPtCursor = createCustomCursor("CursorRemoveEndPoint",size,"RemoveEndPoint",3,3);
			return removeEndPtCursor;
		case INVALID :
			if (invalidCursor != null) return invalidCursor;
			invalidCursor = createCustomCursor("CursorInvalid",size,"Invalid",8,8);
			return invalidCursor;
		case MOVE_ONE_ONLY :
			if (moveOneOnlyCursor != null) return moveOneOnlyCursor;
			moveOneOnlyCursor = createCustomCursor("CursorMoveOneOnly",size,"MoveOneOnly",16,16);
			return moveOneOnlyCursor;
		case CZ_SELECT:
			if(czSelectCursor == null)
				czSelectCursor = createCustomCursor("CursorCZSelect",size,"CZSelect",1,1);
			return czSelectCursor;
		default :
			return Cursor.getPredefinedCursor(type);
		}
	}

	/**
	 * @return a custom cursor, whose image is build from
	 * <code>/jpicedt/graphic/toolkit/cursor/ + imageFileName + size + .png</code>
	 */
	private Cursor createCustomCursor(String imageFileName, String size, String cursorName, int hotSpotX, int hotSpotY){

		// custom cursor construction :
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage(getClass().getResource("/jpicedt/graphic/toolkit/cursor/" + imageFileName + size + ".png"));
		return toolkit.createCustomCursor(image, new Point(hotSpotX,hotSpotY), cursorName);
	}


} // PECursor

// Local Variables:
// coding: utf-8-unix
// End:
