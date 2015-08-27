// PsObjectExpression.java --- -*- coding: iso-8859-1 -*-
// August 4, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PsObjectExpression.java,v 1.8 2013/03/31 06:55:04 vincentb1 Exp $
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

import jpicedt.format.input.util.*;
import jpicedt.graphic.model.*;

import java.util.*;
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * Handles <b>\\newpsobject{myobj}{ps_object}{par1=val1,...}</B>, for instance
 * <b>\\newpsobject{myline}{psline}{linecolor=green}</B> (param is optional, though in this case, it'd be pretty useless!)
 * @author Sylvain Reynal
 * @version $Id: PsObjectExpression.java,v 1.8 2013/03/31 06:55:04 vincentb1 Exp $
 */
public class PsObjectExpression extends AbstractRegularExpression {

	private Pool pool;
	private AbstractRegularExpression exp1,exp2,exp3;
	private String macroName; // e.g. "\\myline"
	private String shape; // e.g. "psline"
	private String param; // e.g. "linecolor=green,fillstyle=solid"
	private PstricksParser parser; // used to add new rules
	private Element prototype;

	public PsObjectExpression(PstricksParser p, Pool pl){
		this.pool = pl;
		this.parser = p;
		// myobj}
		exp1 = new WordExpression("}", true, true, true){ // no line-feed + postfix.
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				macroName = "\\" + getValue(); // e.g. "\\PST@Border"
		}};
		// ps_object}
		exp2 = new WordExpression("}", true, true, true){ // no line-feed
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				shape = getValue(); // e.g. "psline" or "psbezier"
		}};
		// params :
		exp3 = new OptionalExpression(new EnclosingExpression("{",null,"}"){
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				param = getEnclosedString(); // e.g. "linecolor=green,fillstyle=solid"
		}});


	}

	public boolean interpret(Context context) throws REParserException {

		int oldCaretPos = context.getCaretPosition(); // if fails in the end

		if (!context.matchAndMove("\\newpsobject{")) return false;
		WHITE_SPACES_OR_EOL.interpret(context);

		// retrieve "macroName" :
		if (!exp1.interpret(context)) throw new REParserException.IncompleteSequence(context,this);
		WHITE_SPACES_OR_EOL.interpret(context);

		// retrieve "shape" :
		if (!context.matchAndMove("{")) throw new REParserException.IncompleteSequence(context,this);
		if (!exp2.interpret(context)) throw new REParserException.IncompleteSequence(context,this);

		// (optionally) fills attribute set assoc. with PstricksParser.KEY_ATTRIBUTES_PSOBJECTS :
		if (exp3.interpret(context)){

			// associates macroName with paramString for later use at instanciation time,
			// e.g. associates "\\psline" with "linecolor=green" :
			HashMap<String,String> map = (HashMap<String,String>)pool.get(PstricksParser.KEY_NEWPSOBJECTS);
			map.put(macroName,param);
		}

		// add new rule to parser :
		if (shape.equals("psbezier")) {
			parser.add(new PsBezierExpression(pool,macroName));
			return true;
		}
		else if (shape.equals("pscircle")) {
			parser.add(new PsCircleExpression(pool,macroName));
			return true;
		}
		else if (shape.equals("psellipse")) {
			parser.add(new PsEllipseExpression(pool,macroName));
			return true;
		}
		else if (shape.equals("psframe")) {
			parser.add(new PsFrameExpression(pool,macroName));
			return true;
		}
		else if (shape.equals("pspolygon")) {
			parser.add(new PsPolygonExpression(pool,PsPolygonExpression.POLYGON,macroName));
			return true;
		}
		else if (shape.equals("psline")) {
			parser.add(new PsPolygonExpression(pool,PsPolygonExpression.LINE,macroName));
			return true;
		}
		else if (shape.equals("psdots")) {
			parser.add(new PsPolygonExpression(pool,PsPolygonExpression.DOTS,macroName));
			return true;
		}
		else if (shape.equals("psarc")) {
			parser.add(new PsArcExpression(pool,PsArcExpression.ARC,macroName));
			return true;
		}
		else if (shape.equals("pswedge")) {
			parser.add(new PsArcExpression(pool,PsArcExpression.WEDGE,macroName));
			return true;
		}
		else { // shape not supported
			context.moveCaretTo(oldCaretPos);
			return false; // skip, and go to next expression in the parser tree
		}


	}

	public String toString(){
		return "[PsObjectExpression]";
	}

}
