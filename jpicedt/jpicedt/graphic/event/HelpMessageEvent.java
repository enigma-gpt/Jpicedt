// HelpMessageEvent.java --- -*- coding: iso-8859-1 -*-
// February 28, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: HelpMessageEvent.java,v 1.7 2013/03/27 07:06:42 vincentb1 Exp $
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

import jpicedt.graphic.toolkit.EditorKit;

/**
 * A class that allows an EditorKit to post help-messages (or tooltips) that make sense with the current
 * operation being performed. This may be used e.g. by the UI machinery.
 * @author Sylvain Reynal
 * @version $Id: HelpMessageEvent.java,v 1.7 2013/03/27 07:06:42 vincentb1 Exp $
 * @since jpicedt 1.3.2
 */
public class HelpMessageEvent  {

    private EditorKit source;
	private String message;

	/**
	 * contruct a new event
	 * @param source the editorkit that sourced the event
	 * @param message the help-message
	 */
    public HelpMessageEvent(EditorKit source, String message){

		this.source=source;
		this.message = message;
    }

	/**
 	 * Return the editor kit that sourced this event.
 	 */
    public EditorKit getSource(){
		return source;
	}

	/**
	 * Return the help-message.
	 */
    public String getMessage(){
		return message;
	}

	/**
	 * Return a String representation of this event for debugging purpose.
	 */
    public String toString(){

		return "HelpMessageEvent@" + Integer.toHexString(hashCode())
		    + ", msg=" + message
	    	+ ", source=" + source;
    }
}
