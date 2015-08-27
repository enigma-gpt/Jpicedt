// PsUnitLengthExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PsUnitLengthExpression.java,v 1.8 2013/03/31 06:53:39 vincentb1 Exp $
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
package jpicedt.format.input.pstricks;

import jpicedt.format.input.util.*;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Used by PSTParamExpression to parse statement involving the 4 pstricks length registers, that is
 * "unit=1mm,xunit=1mm,yunit=1mm,runit=1mm" (or pt or cm), where setting "unit" sets "xunit",
 * "yunit" and "runit" simultaneously.
 * <p>
 * Parsed value are stored in Pool's hashtable using
 * PstricksParser.KEY_X_UNIT and related keys.
 * <p>
 * Note that default PsTricks's unit is 1cm ! (whereas jPicEdt default unit is 1mm) hence no unit leads
 * to pstXunit = 0.1, etc...
 * <p>
 * Allowed syntaxs are : "0.11" (i.e. cm), "0.11mm", "0.11cm", "0.11pt"
 * <p>
 * According to PsTricks's doc, whitespaces are allowed ONLY after the comma (see PsTricks doc. page 6),
 * though here we use a StatementExpression which swallows them, which makes it less stringent.
 * <p>
 */
public class PsUnitLengthExpression extends SequenceExpression  {

	private Pool pool;
	public static final String XUNIT="xunit";
	public static final String YUNIT="yunit";
	public static final String RUNIT="runit";
	public static final String UNIT="unit";
	private String type;

	/**
	 * unit actually sets the following three parameters : xunit, yunit, and runit.
	 * @param type XUNIT, YUNIT,...
	 */
	public PsUnitLengthExpression(Pool pl, String type){

		super(false);
		this.pool = pl;
		if (type!=XUNIT && type!=YUNIT && type!= RUNIT && type!=UNIT) throw new IllegalArgumentException(type);
		this.type = type;

		add(new StatementExpression(type,"=",null,DOUBLE,POSITIVE){ // no postfix, but possibly whitespaces before
			            public void action(ParserEvent e){
				            if(DEBUG) System.out.println(e);
							setUnit(10.0 * ((Number)e.getValue()).doubleValue());// suppose it's in cm (the default) and scale to mm
			            }});
		add(new OptionalExpression(
				new AlternateExpression(
					new LiteralExpression("cm"), // do nothing !
		            new LiteralExpression("mm"){
						public void action(ParserEvent e){
							if (DEBUG) System.out.println(e);
							scaleUnit(0.1);}},
		            new LiteralExpression("pt"){
			            public void action(ParserEvent e){
							if (DEBUG) System.out.println(e);
				            scaleUnit(2.56/72.0);}}))); // rescale from pt to mm
	}

	private void setUnit(double value){

		if (type==XUNIT) pool.put(PstricksParser.KEY_X_UNIT, value);
		else if (type==YUNIT) pool.put(PstricksParser.KEY_Y_UNIT, value);
		else if (type==RUNIT) pool.put(PstricksParser.KEY_R_UNIT, value);
		else if (type==UNIT) {
			pool.put(PstricksParser.KEY_X_UNIT, value);
			pool.put(PstricksParser.KEY_Y_UNIT, value);
			pool.put(PstricksParser.KEY_R_UNIT, value);
		}
	}

	private void scaleUnit(double scale){
		if (type==XUNIT) pool.put(PstricksParser.KEY_X_UNIT, scale*pool.get(PstricksParser.KEY_X_UNIT));
		else if (type==YUNIT) pool.put(PstricksParser.KEY_Y_UNIT, scale*pool.get(PstricksParser.KEY_Y_UNIT));
		else if (type==RUNIT) pool.put(PstricksParser.KEY_R_UNIT, scale*pool.get(PstricksParser.KEY_R_UNIT));
		else if (type==UNIT) {
			pool.put(PstricksParser.KEY_X_UNIT, scale*pool.get(PstricksParser.KEY_X_UNIT));
			pool.put(PstricksParser.KEY_Y_UNIT, scale*pool.get(PstricksParser.KEY_Y_UNIT));
			pool.put(PstricksParser.KEY_R_UNIT, scale*pool.get(PstricksParser.KEY_R_UNIT));
		}
	}

} // PsUnitLengthExpression
