// SelectionEvent.java --- -*- coding: iso-8859-1 -*-
// February 15, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: SelectionEvent.java,v 1.9 2013/03/27 07:06:02 vincentb1 Exp $
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
package jpicedt.graphic.event;

import jpicedt.graphic.model.Element;
import jpicedt.graphic.PECanvas;

import java.util.EventObject;
import java.util.Iterator;

/**
 * Class for notifications of changes that occured in a SelectionHandler (strongly mimics
 * {@link javax.swing.event.CaretEvent javax.swing.event.CaretEvent})
 * @author Sylvain Reynal
 * @version $Id: SelectionEvent.java,v 1.9 2013/03/27 07:06:02 vincentb1 Exp $
 * @since jpicedt 1.3.2
 */
public class SelectionEvent extends EventObject {

	/** the event type */
	protected EventType type;
	/** the elements that were (un)selected */
	protected Element[] elements;

	/**
	 * a new SelectionEvent sourced from the given PECanvas
	 * Use this constructor when several elements were (un)selected.
	 * @param source the originator of the event
	 * @param elements the elements that were (un)selected
	 * @param type the event type
	 */
	public SelectionEvent(PECanvas source, Element[] elements, EventType type){
		super(source);
		this.type=type;
		this.elements=elements;
	}

	/**
	 * a new SelectionEvent sourced from the given PECanvas.
	 * Use this constructor when only one element was (un)selected.
	 * @param source the originator of the event
	 * @param element the element that was (un)selected
	 * @param type the event type
	 */
	public SelectionEvent(PECanvas source, Element element, EventType type){
		super(source);
		this.type=type;
		elements = new Element[1];
		elements[0] = element;
	}

	/**
	 * Return the graphic elements that got selected/unselected.
	 */
    public Element[] getElements(){
		return elements;
	}

	/**
	 * Return the event type
	 */
    public EventType getType(){
		return type;
	}

	/**
	 * a textual representation of this event
	 */
	public String toString(){
			String s = "[SelectionEvent@" + Integer.toHexString(hashCode()) + ", type=" + getType();
			for (int i=0; i<elements.length; i++){
				s += ", (un)selected element["+i+"]=" + getElements()[i];
			}
			s += ", new-selection-buffer-content=";
			for(Iterator<Element> it = ((PECanvas)getSource()).selection(); it.hasNext();){
				s += it.next().toString() + ",";
			}
			s += "]";
			return s;
	}

	//////////////////////////////////////////////////////////////

	/**
	 * enumeration for SelectionEvent types
	 */
	public static class EventType {

		/** an element was selected */
		public static final EventType SELECT = new EventType("selected");
		/** an element was unselected */
		public static final EventType UNSELECT = new EventType("unselected");

		private String type;

		private EventType(String str){
			this.type = str;
		}

		public String toString(){
			return type;
		}
	}

}
