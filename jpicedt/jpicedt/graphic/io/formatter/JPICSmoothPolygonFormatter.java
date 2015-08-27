/*
 JPICSmoothPolygonFormatter.java - August 26, 2003 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Departement de Physique
 École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org/

*/
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
package jpicedt.graphic.io.formatter;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;

import java.awt.*;

import static jpicedt.graphic.io.formatter.JPICConstants.*;

/**
 * JPIC-XML formatter for PicSmoothPolygon objects.
 * JPIC-XML syntax :
 * <smoothpolygon points="(x1,y1);(x2,y2);...;(xn,yn)" smoothness="c1;c2;...;cn" closed="false|true" other-attribs />
 *
 * @since jpicedt 1.4
 * @author Sylvain Reynal
 * @version $Id: JPICSmoothPolygonFormatter.java,v 1.12 2013/03/27 07:04:39 vincentb1 Exp $
 *
 */
public class JPICSmoothPolygonFormatter extends AbstractFormatter {

	/** the Element this formatter acts upon */
	private PicSmoothPolygon poly;
	/** the factory that produced this formatter */
	private JPICFormatter factory;

	public Element getElement(){ return poly; }

	/**
	 * @param poly the PicSmoothPolygon to be formatted
	 * @param factory the factory that produced this formatter
	 */
	public JPICSmoothPolygonFormatter(PicSmoothPolygon poly, JPICFormatter factory){
		this.poly = poly;
		this.factory=factory;
	}

	/**
	 * @return a String representing this Element in the JPIC-XML format
	 */
	public String format(){
		StringBuffer buf = new StringBuffer(100); // 100 as initial capacity seems to be a good guess

		buf.append("<smoothpolygon");

		// points=...
		PicPoint[] pts = new PicPoint[poly.getLastPointIndex()+1];
		for (int i = 0; i <= poly.getLastPointIndex(); i++)
			pts[i]=poly.getCtrlPt(i, null);

		XmlAttributeSet map = new XmlAttributeSet();
		map.putNameValuePair("points", pts);
		map.putNameValuePair("closed", poly.isClosed());

		// smoothness=...
		double[] smoothness = new double[poly.getLastPointIndex()+1];
		for (int i = 0; i <= poly.getLastPointIndex(); i++)
			smoothness[i] = poly.getSmoothCoefficient(i);
		map.putNameValuePair("smoothness", smoothness);

		// other attributes...
		map.putCommonAttributes(poly);

		buf.append(map.toXML());
		buf.append(" />");
		buf.append(CR_LF);
		return buf.toString();
	}

}
