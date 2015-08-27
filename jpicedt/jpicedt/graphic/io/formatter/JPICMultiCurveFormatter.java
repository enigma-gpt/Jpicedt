/*
 JPICMultiCurveFormatter.java - August 26, 2003 - jPicEdt, a picture editor for LaTeX.
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
import java.util.HashMap;

import java.awt.*;

import static jpicedt.graphic.io.formatter.JPICConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.PolydotsStyle.*;


/**
 * JPIC-XML formatter for PicMultiCurve objects.
 * JPIC-XML syntax :
 * <multicurve points="(x1,y1);(x2,y2);...;(xn,yn)" polydots-attribs other-attribs />
 *
 * @since jpicedt 1.4
 * @author Sylvain Reynal
 * @version $Id: JPICMultiCurveFormatter.java,v 1.12 2013/03/27 07:04:54 vincentb1 Exp $
 *
 */
public class JPICMultiCurveFormatter extends AbstractFormatter {

	/** the Element this formatter acts upon */
	private PicMultiCurve curve;
	/** the factory that produced this formatter */
	private JPICFormatter factory;

	public Element getElement(){ return curve; }

	/**
	 * @param curve the PicMultiCurve to be formatted
	 * @param factory the factory that produced this formatter
	 */
	public JPICMultiCurveFormatter(PicMultiCurve curve, JPICFormatter factory){
		this.curve = curve;
		this.factory=factory;
	}

	public String format(){
		StringBuffer buf = new StringBuffer(100); // 100 as initial capacity seems to be a good guess
		PicAttributeSet set = curve.getAttributeSet();

		buf.append("<multicurve");

		PicPoint[] pts = new PicPoint[curve.getLastPointIndex()+1];
		for (int i = 0; i <= curve.getLastPointIndex(); i++)
			pts[i]=curve.getCtrlPt(i, null);

		XmlAttributeSet map = new XmlAttributeSet();
		map.putNameValuePair("points", pts);

		// polydots :
		if (set.getAttribute(POLYDOTS_STYLE) != NONE){
			map.putNameValuePair(POLYDOTS_STYLE,set);
			map.putNameValuePair(POLYDOTS_SIZE_MINIMUM_MM,set);
			map.putNameValuePair(POLYDOTS_SIZE_LINEWIDTH_SCALE,set);
			map.putNameValuePair(POLYDOTS_ANGLE,set);
			map.putNameValuePair(POLYDOTS_SCALE_H,set);
			map.putNameValuePair(POLYDOTS_SCALE_V,set);
			map.putNameValuePair(POLYDOTS_SUPERIMPOSE,set);
		}

		//buf.append("\" closed="); // not necessary, since the nb of bezier-points is enough to know
		//buf.append(curve.isClosed()); // whether curve is closed or not.

		map.putCommonAttributes(curve);
		buf.append(map.toXML()); // translate map into XML name=value pairs
		buf.append(" />");
		buf.append(CR_LF);
		return buf.toString();


	}

}
