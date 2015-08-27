// PEToolBar.java --- -*- coding: iso-8859-1 -*-
//  jPicEdt, a picture editor for LaTeX.
//  Copyright (C) 1999/2006  Sylvain Reynal
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
// Version: $Id: PEToolBar.java,v 1.15 2013/03/27 06:51:01 vincentb1 Exp $
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
package jpicedt.ui.internal;

import jpicedt.JPicEdt;
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.toolkit.EditorKit;
import jpicedt.ui.action.ActionRegistry;
import jpicedt.ui.util.RunExternalCommand;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;

/**
 * The main application toolbar.
 * @author Sylvain Reynal
 * @since PicEdt 1.0
 * @version $Id: PEToolBar.java,v 1.15 2013/03/27 06:51:01 vincentb1 Exp $
 */
public class PEToolBar extends jpicedt.graphic.toolkit.PEToolBar {

	//private JLabel currentToolLbl = new JLabel(jpicedt.Localizer.currentLocalizer().get(EditorKit.ToolType.SELECT+".tooltip"));


	public PEToolBar() {

		ActionRegistry actionRegistry = JPicEdt.getActionRegistry();
		this.setFloatable(false);
		this.setBorder(BorderFactory.createEtchedBorder());

		add(actionRegistry.getAction(JPicEdt.FileNewAction.KEY));
		add(actionRegistry.getAction(JPicEdt.FileOpenAction.KEY));
		add(actionRegistry.getAction(JPicEdt.FileSaveAction.KEY));
		add(actionRegistry.getAction(JPicEdt.FileReloadAction.KEY));
		add(actionRegistry.getAction(JPicEdt.ExitAction.KEY));
		addSeparator();
		add(actionRegistry.getAction(EditorKit.CopyAction.KEY));
		add(actionRegistry.getAction(EditorKit.CutAction.KEY));
		add(actionRegistry.getAction(EditorKit.PasteAction.KEY_PASTE));
		addSeparator();
		add(actionRegistry.getAction(JPicEdt.UndoAction.KEY));
		add(actionRegistry.getAction(JPicEdt.RedoAction.KEY));
		addSeparator();
		add(actionRegistry.getAction(JPicEdt.ToggleAttributesPanelAction.KEY));
		add(actionRegistry.getAction(JPicEdt.ToggleToolkitPanelAction.KEY));
		add(actionRegistry.getAction(JPicEdt.ToggleConvexZonePanelAction.KEY));
		add(actionRegistry.getAction(JPicEdt.PreferencesAction.KEY));
		add(actionRegistry.getAction(JPicEdt.ShowHelpAction.KEY));
		addSeparator();
		add(actionRegistry.getAction(RunExternalCommand.Command.LATEX.getName()));
		add(actionRegistry.getAction(RunExternalCommand.Command.DVIVIEWER.getName()));
		add(actionRegistry.getAction(RunExternalCommand.Command.DVIPS.getName()));
		add(actionRegistry.getAction(RunExternalCommand.Command.GHOSTVIEW.getName()));
		// current tool :
		//addSeparator();
		//add(currentToolLbl);
		// remaining gap:
		add(Box.createGlue());
	}

	/**
	 * Implementation of <code>PropertyChangeListener</code> interface. Allows this tool-bar to get notified
	 * of change from the target <code>EditorKit</code>.
	 */
	public void updateCurrentTool(String mouseToolName){
		//currentToolLbl.setText(jpicedt.Localizer.currentLocalizer().get(mouseToolName+".tooltip"));
	}

} // classe PEToolBar
