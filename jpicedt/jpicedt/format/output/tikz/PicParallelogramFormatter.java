// PicParallelogramFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: PicParallelogramFormatter.java,v 1.9 2013/06/16 20:35:15 vincentb1 Exp $
// Keywords: Tikz, PGF
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
 * utilis�e. Sinon le parallelogramme est converti en chemin.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
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
	* @return une cha�ne de caract�res contenant le code Tikz formatant le
	* PicParallelogram pass�e � la construction.
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
