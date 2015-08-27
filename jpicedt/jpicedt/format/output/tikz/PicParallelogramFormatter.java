// PicParallelogramFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: PicParallelogramFormatter.java,v 1.9 2013/06/16 20:35:15 vincentb1 Exp $
// Keywords: Tikz, PGF
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
package jpicedt.format.output.tikz;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.model.PicParallelogram;
import java.lang.String;
import java.lang.StringBuffer;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.PEToolKit;
import static java.lang.Math.toDegrees;

/**
 * Formateur de PicParallelogram au format Tikz. Lorsque le parallelogramme
 * est rectangle la primitive Tikz &laquo;&nsbp;rectangle&nbsp;&raquo; est
 * utilisée. Sinon le parallelogramme est converti en chemin.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jpicedt 1.6
 */
public class PicParallelogramFormatter extends AbstractFormatter
{
	protected PicParallelogram parallelogram;
	protected TikzFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return parallelogram;}

	/** */
	public PicParallelogramFormatter(
		PicParallelogram parallelogram, TikzFormatter factory){
		this.parallelogram = parallelogram;
		this.factory=factory;
	}

   /**
	* @return une chaîne de caractères contenant le code Tikz formatant le
	* PicParallelogram passée à la construction.
	* @since jPicEdt 1.6
	*/
	public String format(){
		StringBuffer buf = new StringBuffer(100);
		PicPoint pt = new PicPoint();
		PicParallelogram parallelogram = this.parallelogram;
		if(parallelogram.isRectangle())
		{
			if(parallelogram.isXYorYXRectangle())
				factory.draw(buf,parallelogram);
			else
			{

				factory.drawWithOptions(buf,parallelogram);

				double angle = parallelogram.getL2RtoXAxisAngle();
				parallelogram = new PicParallelogram(parallelogram);
				parallelogram.rotate(pt,-angle);
				buf.append("rotate=");
				buf.append(PEToolKit.doubleToString(toDegrees(angle)));
				buf.append(']');
			}

			pt = parallelogram.getCtrlPt(0,pt);
			buf.append(pt);
			buf.append(" rectangle ");
			pt = parallelogram.getCtrlPt(2,pt);
			buf.append(pt);
			buf.append(factory.getEOCmdMark());

		}
		else
			buf.append(
				new AbstractCurveFormatter(
					parallelogram.convertToMultiCurve(),factory).format());
		return buf.toString();
	}

}


/// PicParallelogramFormatter.java ends here
