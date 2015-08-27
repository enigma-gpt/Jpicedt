// PEAction.java  --- -*- coding: iso-8859-1 -*- January 2, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PEAction.java,v 1.14 2013/03/27 06:57:01 vincentb1 Exp $
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
 * associated with this PEAction from a simple String (the "actionName" parameter), using a
 * user-provided ActionLocalizer.
 * <br>
 * Note : if an ActionLocalizer is provided to the constructor, and some of its methods return null values,
 * the corresponding properties are set to null. However, all jPicEdt components (e.g. PEMenu, PEToolBar,...),
 * like Swing components, are guaranteed to handle this case properly w/o throwing an exception.
 * <br>
 * For instance, simply setting the NAME property to null and filling the SMALL_ICON property
 * create a JButton without text under the component's Icon (this is the standard Swing behaviour).
 * <p>
 * <b>Example of use : </b> [todo]
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: PEAction.java,v 1.14 2013/03/27 06:57:01 vincentb1 Exp $
 *
 */
public abstract class PEAction extends PEAbstractAction<Element> {

	protected Iterator<Element> getIterator(){
		return getCanvas().selection();
	}

	public PEAction(ActionDispatcher actionDispatcher, String actionName, ActionLocalizer localizer){
		super(actionDispatcher, actionName, localizer);
	}

	/**
	 * Applies the given attribute to the drawing content.  If there is a selection, the attribute
	 * is applied to each element in the selection.  If there is no selection, the attribute
	 * is applied to the input attribute set of the EditorKit (this specifies the attributes
	 * to be used for any new Element that gets added to the underlying Drawing).
	 * <p>
	 * If there's a selected element, we add an UndoableEdit (hence PEAction that explicitly call
	 * setAttribute() must not override <code>undoableActionPerformed</code>, but simply <code>actionPerformed</code>).
	 */
     protected final <T> void setAttribute(PicAttributeName<T> name, T value) {
		 if (getCanvas()==null) return;
		 if (getCanvas().getSelectionSize()==0){
			 EditorKit kit = getCanvas().getEditorKit();
			 PicAttributeSet inputAttributes = kit.getInputAttributes();
			 inputAttributes.setAttribute(name,value);
		 }
		 else {
			getCanvas().beginUndoableUpdate((String)getValue(ACTION_COMMAND_KEY));
			 for(Iterator<Element> it = getCanvas().selection(); it.hasNext(); ){
				 Element e = it.next();
				 e.setAttribute(name, value);
			 }
			getCanvas().endUndoableUpdate();
		 }
		 getCanvas().repaint();
	}


}
