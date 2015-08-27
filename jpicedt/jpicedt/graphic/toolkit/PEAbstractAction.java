// PEAbstractAction.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: PEAbstractAction.java,v 1.2 2013/03/27 06:57:06 vincentb1 Exp $
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

import jpicedt.graphic.PECanvas;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.model.PicAttributeName;
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.Element;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.Iterator;

/**
 * An abstract subclass of Swing's AbstractAction that can (but may as well not)
 * be shared across multiple PECanvas.
 * <p>
 * This implementation uses the same set of properties as AbstractAction.
 * <p>
 * The constructor gives a convenient way of automatically building localized properties
 * associated with this PEAbstractAction from a simple String (the "actionName" parameter), using a
 * user-provided ActionLocalizer.
 * <br>
 * Note : if an ActionLocalizer is provided to the constructor, and some of its methods return null values,
 * the corresponding properties are set to null. However, all jPicEdt components (e.g. PEMenu, PEToolBar,...),
 * like Swing components, are guaranteed to handle this case properly w/o throwing an exception.
 * <br>
 * For instance, simply setting the NAME property to null and filling the SMALL_ICON property
 * create a JButton without text under the component's Icon (this is the standard Swing behaviour).
 * <p>
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela&iuml;che</a>
 * @since jPicEdt 1.6
 * @version $Id: PEAbstractAction.java,v 1.2 2013/03/27 06:57:06 vincentb1 Exp $
 */
public abstract class PEAbstractAction<T> extends AbstractAction {

	/* the ActionDispatcher that provides a target (PECanvas) to this action */
	private ActionDispatcher actionDispatcher;

	/* target T, if set manually */
	protected T targetElement = null;


	/**
	 * Constructor to be used either :
	 * <ul>
	 * <li> when the target board is not known in advance (e.g. MDI), and one wants to
	 * provide an own ActionDispatcher implementation
	 * <li> or when the target board is known : in this case use a DefaultActionDispatcher
	 * constructed with the given target board.
	 * </ul>
	 * @param actionDispatcher the ActionDispatcher that provides a
	 *        target when this Action gets invoked. If set to null, then subclasses may want to simply override actionPerformed().
	 * @param actionName the actionName of this Action that serve to build this Action's properties using
	 *        the given ActionLocalizer (if non-null) ; in any case, actionName is used to fill the
	 *        ACTION_COMMAND_KEY property.
	 * @param localizer the ActionLocalizer that feeds this Action with localized properties ;
	 *        if null, the Action's NAME property is simply initialized to actionName, but other
	 *        properties are not set (this allow a subclass to set these properties manually).
	 */
	public PEAbstractAction(ActionDispatcher actionDispatcher, String actionName, ActionLocalizer localizer){
		super(actionName); // default, if no localizer is provided
		this.actionDispatcher = actionDispatcher;
		putValue(ACTION_COMMAND_KEY,actionName);
		if (localizer != null){
			putValue(NAME, localizer.getActionName(actionName));
			putValue(SHORT_DESCRIPTION, localizer.getActionTooltip(actionName));
			putValue(LONG_DESCRIPTION, localizer.getActionHelper(actionName));
			putValue(MNEMONIC_KEY, localizer.getActionMnemonic(actionName));
			putValue(ACCELERATOR_KEY, localizer.getActionAccelerator(actionName));
			putValue(SMALL_ICON, localizer.getActionIcon(actionName));
		}
	}

	/**
	 * This is a default implementation of the "actionPerformed" method suited for undoable actions :
	 * we first call "beginUndoableUpdate" with "actionName" as the presentation name,
	 * then delegate to "undoableActionPerformed", finally mark the undoable-update as ended.
	 * <p>
	 * Action that don't want to be undoable should simply override this method as usual.
	 */
	public void actionPerformed(ActionEvent e){
			PECanvas canvas = getCanvas();
			if (canvas==null) return;
			canvas.beginUndoableUpdate((String)getValue(NAME)); // i18n'd if there was a localizer provided in the contructor
			undoableActionPerformed(e);
			canvas.endUndoableUpdate();
	}

	/**
	 * called by "actionPerformed" ; default implementation does nothing. Action that want
	 * to be undoable override this method instead of "actionPerformed".
	 * <p>
	 * Note : this is not an abstract method, since this would force all PEAbstractAction's, including
	 * those we don't want to be undoable, to implement this method.
	 */
	protected void undoableActionPerformed(ActionEvent e){
	}

	/**
	 * Returns the PECanvas this Action acts upon, as specified by the ActionDispatcher
	 *         provided in the constructor.
	 * @return a null reference if no suitable canvas is found by the ActionDispatcher.
	 */
	protected PECanvas getCanvas(){
		if (actionDispatcher==null) return null;
		return actionDispatcher.getTarget();
	}

	/**
	 * Returns the Drawing that serves as a model for the target PECanvas (this is a convenience
	 *         call to <code>getCanvas()</code>.
	 */
	protected Drawing getDrawing(){
		if (getCanvas()==null) return null;
		return getCanvas().getDrawing();
	}

	/**
	 * Returns the EditorKit associated with the target PECanvas (this is a convenience
	 *         call to <code>getCanvas()</code>.
	 */
	protected EditorKit getEditorKit(){
		if (getCanvas()==null) return null;
		return getCanvas().getEditorKit();
	}

	/**
	 * Returns the selected graphic element in the target board ; if setSelectedObject()
	 * has been previously called with a non-null argument, it's returned here.
	 * @return null if there's no selected graphic element, OR more than one.
	 */
	protected T getSelectedObject(){
		if (targetElement != null)
			return targetElement;

		if (getCanvas()==null) return null;
		Iterator<T> it = getIterator();
		if(it.hasNext()){
			T ret = it.next();
			if(!it.hasNext())
				return ret;
		}
		return null;
	}

	/**
	 * Sets the selected graphic element "by hand". Set it to null to revert
	 * to the standard behavior.
	 */
	public  void setSelectedObject(T e){
		targetElement = e;
	}

	abstract protected Iterator<T> getIterator();

}

/// PEAbstartAction.java ends here
