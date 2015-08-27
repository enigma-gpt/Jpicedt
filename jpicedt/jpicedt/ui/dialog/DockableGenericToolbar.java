// DockableGenericToolbar.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DockableGenericToolbar.java,v 1.5 2013/03/27 06:52:21 vincentb1 Exp $
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
 * Une barre d'outils qui offre diff�rents outils pour la manipulation de
 * zones convexes&hellip;
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela&iuml;che</a>
 * @since jPicEdt 1.6
 * @version $Id: DockableGenericToolbar.java,v 1.5 2013/03/27 06:52:21 vincentb1 Exp $
 */
public abstract class DockableGenericToolbar extends JPanel implements PropertyChangeListener {


	private ArrayList<AbstractButton> buttons = new ArrayList<AbstractButton>();
	private String currentMouseTool=EditorKit.SELECT;

	// --- class methods ------------------------------------------------
	protected abstract LayoutManager getNewLayout();
	protected abstract void completeConstruction(ActionRegistry ar);
	protected abstract Object[][] getToolNames();

	public DockableGenericToolbar(Object[][] allToolNames) {

		super();
		this.setLayout(getNewLayout());
		this.setBorder(BorderFactory.createEtchedBorder());

		// a set of mutually exclusive buttons, composing the tools palette :
		ButtonGroup bg = new ButtonGroup();
		JToggleButton b;

		ActionRegistry actionRegistry = JPicEdt.getActionRegistry();

		// add kit's registered mouse tool :
		for (int i=0; i<allToolNames.length; i++){
			for (int j=0; j<allToolNames[i].length; j++){
				Action a = actionRegistry.getAction(allToolNames[i][j].toString());
				b = addToggle(a);
				bg.add(b);
				if (allToolNames[i][j].equals(EditorKit.SELECT)) b.setSelected(true);
			}
		}
		completeConstruction(actionRegistry);
	}

	/**
	 * Creates a JToggleButton from the given PEAction's properties
	 * @param a Action from which this button gets created.
	 */
	public JToggleButton addToggle(Action a){

		JToggleButton b = new JToggleButton(a);
		if (b.getIcon()!=null) b.setText(null);
		b.setMargin(new Insets(2,2,2,2));
		//b.setAlignmentY(CENTER_ALIGNMENT);
		//b.setAlignmentX(CENTER_ALIGNMENT);
		b.setRolloverEnabled(true);
		add(b);
		buttons.add(b);
		return b;
	}

	/**
	 * Creates a JButton from the given PEAction's properties
	 * @param a Action from which this button gets created.
	 */
	public JButton add(Action a){

		JButton b = new JButton(a);
		if (b.getIcon()!=null) b.setText(null);
		b.setMargin(new Insets(2,2,2,2));
		//b.setAlignmentY(CENTER_ALIGNMENT);
		//b.setAlignmentX(CENTER_ALIGNMENT);
		b.setRolloverEnabled(true);
		add(b);
		buttons.add(b);
        return b;
	}

	/**
	 * Implementation of PropertyChangeListener interface
	 * Allows this tool-bar to get notified of change from the target EditorKit, e.g. when
	 * a right-click switches the current EditorKit's mousetool back to "select-tool", or
	 * when a new mousetool gets selected from the "Tools" menu or from a BSH script.
	 * <p>
	 * This DockablePanel gets registered as a PropertyChangeListener (with events sourced from EditorKit's)
	 * from inside MDIManager whenever a new Board is added.
	 * @param e the PropertyChangeEvent ; only the "getNewValue()" method is used here.
	 */
	public void propertyChange(PropertyChangeEvent e){
		if (e.getPropertyName()==EditorKit.EDIT_MODE_CHANGE){
			this.currentMouseTool = (String)e.getNewValue();
			//System.out.println("EditorKit.EDIT_MODE_CHANGE : newValue = " + e.getNewValue());
			for (Iterator it = buttons.iterator(); it.hasNext();){
				Object o = it.next();
				if (!(o instanceof JToggleButton)) continue; // filter out not selectable buttons
				JToggleButton c = (JToggleButton)o;
				Action a = c.getAction();
				if (e.getNewValue().equals(a.getValue(Action.ACTION_COMMAND_KEY))){ // actionCommand is used to build a PEAction
					c.setSelected(true);
					c.doClick();
				}
			}
		}
		// synchronize state of new board with this toolbar:
		else if (e.getPropertyName()==MDIManager.ACTIVE_BOARD_CHANGE){
			if (e.getNewValue()==null) {}
			else {
				PEDrawingBoard b = (PEDrawingBoard)e.getNewValue();
				EditorKit kit = b.getCanvas().getEditorKit();
				kit.setCurrentMouseTool(currentMouseTool);
			}
		}
	}


} // classe DockableGenericToolbar

/// DockableGenericToolbar.java ends here
