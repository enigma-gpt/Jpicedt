// DockableConvexZoneToolBar.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: DockableConvexZoneToolBar.java,v 1.4 2013/03/27 06:52:31 vincentb1 Exp $
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
package jpicedt.ui.dialog;

import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.*;
import jpicedt.graphic.event.*;
import jpicedt.ui.*;
import jpicedt.ui.action.*;
import jpicedt.*;

import javax.swing.*;
import java.util.*;
import java.beans.*;
import java.awt.*;

/**
 * Une barre d'outils qui offre différents outils pour la manipulation de
 * zones convexes&hellip;
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela&iuml;che</a>
 * @since jPicEdt 1.6
 * @version $Id: DockableConvexZoneToolBar.java,v 1.4 2013/03/27 06:52:31 vincentb1 Exp $
 */
public class DockableConvexZoneToolBar extends DockableGenericToolbar {

	/** clef pour le stockage persistant des dimenskions dans les préférences. */
	public static final String KEY = "dockable-panel.ConvexZone";


	// --- class methods ------------------------------------------------
	protected LayoutManager getNewLayout(){
		return new GridLayout(3,4,5,5);
	}
	protected void completeConstruction(ActionRegistry actionRegistry){
		// use convexe zone mode :
		addToggle(actionRegistry.getAction(EditorKit.ToggleUseConvexZoneModeAction.KEY));
		return;
	}
	protected Object[][] getToolNames(){
		return EditorKit.getAvailableConvexZoneToolNames();
	}


	/**
	 */
	public DockableConvexZoneToolBar() { super(EditorKit.getAvailableConvexZoneToolNames()); }

} // classe DockableConvexZoneToolBar



/// DockableConvexZoneToolBar.java ends here
