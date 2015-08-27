// ParserEvent.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013  Sylvain Reynal
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
// Version: $Id: ParserEvent.java,v 1.8 2013/03/31 06:59:09 vincentb1 Exp $
// Keywords: parser
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
package jpicedt.format.input.util;

/**
 * An event that gets sent as an argument of the "action" method during an interpret operation
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: ParserEvent.java,v 1.8 2013/03/31 06:59:09 vincentb1 Exp $
 *
 */
public class ParserEvent {

	private AbstractRegularExpression source;
	private Context context;
	private boolean parsingSuccessful;
	private Object value;

	public ParserEvent(AbstractRegularExpression source,
	                   Context context,
	                   boolean parsingSuccessful,
	                   Object value){

		this.source=source;
		this.context=context;
		this.parsingSuccessful=parsingSuccessful;
		this.value=value;
	}

	/**
	 *
	 */
	public String toString(){

		String vv = value.toString().replace('\n','|');
		return "{ParserEvent:class=" + source.getClass().getName()
		       + "\n             context=" + context
		       + "\n             value=\"" + vv + "\""
		       	   + "\n             source=" + source
		       + "\n}";
	}

	/**
	 * @return the Expression that sourced this event
	 */
	public AbstractRegularExpression getSource(){
		return source;
	}

	/**
	 * @return the Context that is attached to current the parsing operation
	 */
	public Context getContext(){
		return context;
	}

	/**
	 * @return TRUE if this event was emitted as a result of a successful operation
	 */
	public boolean isSuccessful(){
		return parsingSuccessful;
	}

	/**
	 * @return a value that generally identifies the result of the parsing operation, such as : number, word, ...
	 */
	public Object getValue(){
		return value;
	}
}
