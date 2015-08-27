// PSTInstanciationExpression.java --- -*- coding: iso-8859-1 -*-
// March 29, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2013 Sylvain Reynal
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
// Version: $Id: PSTInstanciationExpression.java,v 1.10 2013/03/31 06:55:24 vincentb1 Exp $
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
package jpicedt.format.input.pstricks;

import jpicedt.graphic.model.*;
import jpicedt.format.input.util.*;

import java.util.*;
import java.io.*;
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * Instanciates a new Element by cloning the given object, when a given tag gets found.
 * Then adds it to the current PicGroup in the Pool (i.e. <code>Pool.currentGroup</code>)
 * @author Sylvain Reynal
 * @version $Id: PSTInstanciationExpression.java,v 1.10 2013/03/31 06:55:24 vincentb1 Exp $
 */
public class PSTInstanciationExpression extends LiteralExpression   {

	private Element prototype;
	private Pool pool;
	private String tag;
	private PSTParametersExpression paramExp;

	/**
	 * @param tag expression to be matched
	 * @param prototype element to be instanciated by cloning
	 * @param pl pool where to add the instanciated element ; also used to fetch the current attribute set for PsTricks
	 */
	public PSTInstanciationExpression(String tag, Element prototype,Pool pl){

		super(tag);
		this.tag = tag;
		this.prototype = prototype;
		this.pool = pl;
		paramExp = new PSTParametersExpression(this.pool, Pool.CURRENT_OBJ_ATTRIBUTES);
	}

	public void action(ParserEvent e) throws REParserException {

		if (DEBUG) System.out.println(e);
		pool.currentObj = (Element)prototype.clone();
		pool.currentObj.getAttributeSet().setAttributes(pool.getAttributeSet(PstricksParser.KEY_ATTRIBUTES)); // overrides non-default values only
		pool.currentGroup.add(pool.currentObj);
		// possibly retrieve a parameter string associated with the tag, if it's been previously set by
		// PsObjectExpression :
		HashMap<String,String> map = (HashMap<String,String>)pool.get(PstricksParser.KEY_NEWPSOBJECTS);
		String paramStr = map.get(tag);
		if (paramStr != null){ // ok, there exists such a tag, previously set by a \\newpsobject command
			try {
				Context tmpContext = new Context(new StringReader(paramStr));
				paramExp.interpret(tmpContext); // set attributes for currentObj
			} catch (REParserException.EOF ex){}
		}

	}

	public String toString(){

		return "[PSTInstanciationExpression:tag="+tag+" prototype="+prototype.getClass().getName()+"]";
	}
}
