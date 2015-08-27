// EditorKitEvent.java --- -*- coding: iso-8859-1 -*-
// January 2, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: EditorKitEvent.java,v 1.7 2013/03/27 07:06:52 vincentb1 Exp $
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

import java.util.EventListener;
import javax.swing.event.*;

import jpicedt.graphic.model.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.view.*;
import jpicedt.graphic.*;

/**
 * A class that allows an EditorKit's mousetool to send high-level mouse-events, i.e. somehow pre-processed
 * by the EditorKit machinery. This is inspired from
 * {@link javax.swing.event.HyperlinkListener javax.swing.event.HyperlinkListener}.<br>
 * Application cover :
 * <ul>
 * <li> UI implementation outside the jpicedt.graphic package can react to mouse-event w/o the burden
 *      of processing the event.
 * <li> Internal use by other EditorKit's mousetools.
 * <p> [pending] underway ; compile ok but not used yet.
 * @author Sylvain Reynal
 * @version $Id: EditorKitEvent.java,v 1.7 2013/03/27 07:06:52 vincentb1 Exp $
 * @since jpicedt 1.3.2
 */
public class EditorKitEvent  {

    private EventType type;
    private EditorKit source;
	private HitInfo hitInfo;

	/**
	 * contruct a new EditorKitEvent.
	 * @param source the editorkit that sourced the event
	 * @param type the event type
	 * @param hitInfo the HitInfo associated with the mouse event
	 */
    public EditorKitEvent(EditorKit source, EventType type, HitInfo hitInfo){

		this.source=source;
		this.type=type;
		this.hitInfo = hitInfo;
    }

	/**
 	 * Return the editor kit that sourced this event
 	 */
    public EditorKit getSource(){
		return source;
	}

	/**
	 * Return the type of this event
	 */
    public EventType getType(){
		return type;
	}

	/**
	 * Return the HitInfo associated with the mouse-event
	 */
	public HitInfo getHitInfo(){
		return hitInfo;
	}

	/**
	 * Return a String representation of this event for debugging purpose.
	 */
    public String toString(){

		return "EditorKitEvent@" + Integer.toHexString(hashCode())
		    + ", type=" + type
	    	+ ", value=" + hitInfo
	    	+ ", source=" + source;
    }

	/**
	 * typesafe enumeration of allowed event types
	 */
	public static class EventType {

		private String name;
		private EventType(String s){name = s;}

		/**
		 * signal that the mouse-cursor entered the sensitive area of a graphic element
		 * [SR:pending] change name to ON_MOUVE_OVER to conform to W3C's DOM for XML-SVG ?
		 */
		public final static EventType ELEMENT_ENTERED = new EventType("element-entered");

		/**
		 * signals that the mouse-cursor exited the sensitive area of a graphic element
		 */
		public final static EventType ELEMENT_EXITED = new EventType("element-exited");

		/**
		 * signals that the user clicked on a graphic element
		 * [SR:pending] change name to ON_MOUSE_CLICK ?
		 */
		public final static EventType ELEMENT_CLICKED = new EventType("element-clicked");

		/**
		 * Return the name of this event type ; this can be used by a GUI, but it's in english, so
		 *  it needs to be localized.
		 */
		public String toString(){
			return name;
		}
	}
}
