/*
 JPICPsCurveFormatter.java - April, 2005 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Departement de Physique
 �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org/

*/
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
package jpicedt.graphic.io.formatter;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;

import java.awt.*;

import static jpicedt.graphic.io.formatter.JPICConstants.*;

/**
 * JPIC-XML formatter for PicPsCurve objects, aka PsTricks \\pscurve macro.
 * JPIC-XML syntax :
 * <pscurve points="(x1,y1);(x2,y2);...;(xn,yn)" smoothness="c1;c2;...;cn" closed="false|true" other-attribs />
 *
 * @since jpicedt 1.4pre5
 * @author Sylvain Reynal
 * @version $Id: JPICPsCurveFormatter.java,v 1.8 2013/03/27 07:04:44 vincentb1 Exp $
 *
 */
public class JPICPsCurveFormatter extends AbstractFormatter {

	/** the Element this formatter acts upon */
	private PicPsCurve curve;
	/** the factory that produced this formatter */
	private JPICFormatter factory;

	public Element getElement(){ return curve; }

	/**
	 * @param curve the PicPsCurve to be formatted
	 * @param factory the factory that produced this formatter
	 */
	public JPICPsCurveFormatter(PicPsCurve curve, JPICFormatter factory){
		this.curve = curve;
		this.factory=factory;
	}

	/**
	 * @return a String representing this Element in the JPIC-XML format
	 */
	public String format(){
		StringBuffer buf = new StringBuffer(100); // 100 as initial capacity seems to be a good guess

		buf.append("<pscurve");

		// points=...
		PicPoint[] pts = new PicPoint[curve.getLastPointIndex()+1];
		for (int i = 0; i <= curve.getLastPointIndex(); i++)
			pts[i]=curve.getCtrlPt(i, null);

		XmlAttributeSet map = new XmlAttributeSet();
		map.putNameValuePair("points", pts);
		map.putNameValuePair("closed", curve.isClosed());

		// curvature=...
		double[] curvatures = curve.getCurvatures();
		map.putNameValuePair("curvature", curvatures); // a, b and c

		// other attributes...
		map.putCommonAttributes(curve);

		buf.append(map.toXML());
		buf.append(" />");
		buf.append(CR_LF);
		return buf.toString();
	}

}
