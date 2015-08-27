// PicEllipseFormatter.java ---
// Copyright 2009/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: PicEllipseFormatter.java,v 1.11 2013/06/18 20:48:22 vincentb1 Exp $
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

import java.util.BitSet;

import jpicedt.graphic.model.Element;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.model.PicAttributeName;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.model.PicEllipse;
import jpicedt.graphic.model.StyleConstants.ArrowStyle;
import jpicedt.graphic.PEToolKit;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;


/**
 * Formateur de PicEllipse au format Tikz.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jpicedt 1.6
 */
public class PicEllipseFormatter  extends AbstractFormatter
{
	/** les �l�ments sur lesquels ce formatteur agit */
	protected PicEllipse ellipse;
	protected TikzFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return ellipse;}

	/**
	 * renvoie <code>true</code> lorsque il faut inverser les param�tres des
	 * fl�ches au moment du formattage.
	 *
	 * @since jPicEdt 1.6
	 */
	public boolean revertedArrowsAttribute(){
		return !ellipse.isPlain() && ellipse.getSmallAxisLength()<0;
	}

	/** */
	public PicEllipseFormatter(PicEllipse ellipse, TikzFormatter factory){
		this.ellipse = ellipse;
		this.factory=factory;
	}

   /**
	* @return une cha�ne de caract�res contenant le code Tikz formatant la
	* PicEllipse pass�e � la construction.
	* @since jPicEdt 1.6
	*/
	public String format(){
		StringBuffer buf = new StringBuffer(100);
		PicPoint pt = new PicPoint();
		if(ellipse.isPlain())
		{
			factory.draw(buf,ellipse, TikzConstants.MASK_ROTATE_BITSET);

			buf.append(ellipse.getCenter(pt));
			if(ellipse.isCircular())
			{
				buf.append(" circle [radius=");
				buf.append(PEToolKit.doubleToString(0.5*ellipse.getGreatAxisLength()));
			}
			else
			{
				buf.append(" ellipse [x radius=");
				double rotationAngle = ellipse.getRotationAngle();
				if(rotationAngle == 0.5*Math.PI || rotationAngle == -0.5*Math.PI)
				{
					// l'ellipse est verticale: greatAxis = axe vertical
					buf.append(
						PEToolKit.doubleToString(0.5*abs(ellipse.getSmallAxisLength())));
					buf.append(", y radius=");
					buf.append(PEToolKit.doubleToString(0.5*ellipse.getGreatAxisLength()));

				}	
				else
				{
					
					// l'ellipse n'est pas verticale
					buf.append(PEToolKit.doubleToString(0.5*ellipse.getGreatAxisLength()));
					buf.append(", y radius=");
					buf.append(
						PEToolKit.doubleToString(0.5*abs(ellipse.getSmallAxisLength())));
					if(rotationAngle != 0.0 && rotationAngle != Math.PI){

						// l'ellipse est pench�e.
						if(rotationAngle > 0.5*Math.PI)
							rotationAngle = rotationAngle - Math.PI;
						else if(rotationAngle < -0.5*Math.PI)
							rotationAngle = Math.PI + rotationAngle;
						buf.append(", rotate=");
						buf.append(
							PEToolKit.doubleToString(Math.toDegrees(rotationAngle)));
					}
				}
			}
			buf.append("]");
		}
		// un arc
		else {
			double startAngle, endAngle,startAngleRad;
			boolean positiveDirection;
			boolean clockwise;

			BitSet mask = new BitSet(TikzConstants.DrawFlags.SIZE.getValue());
			if(clockwise = ellipse.getSmallAxisLength() < 0)
				mask.set(TikzConstants.DrawFlags.SWAP_ARROWS.getValue());
			factory.draw(buf,ellipse, mask);

			if (clockwise){
				startAngle = -ellipse.getRotatedAngleEnd();
				endAngle = -ellipse.getRotatedAngleStart();
			}
			else {
				startAngle = ellipse.getRotatedAngleStart();
				endAngle = ellipse.getRotatedAngleEnd();
			}
			positiveDirection = endAngle >= startAngle;

			startAngle = startAngle % 360; // assure qu'on est dans [-360 360]
			endAngle = endAngle % 360;
			if(positiveDirection)
			{
				while (endAngle < startAngle) endAngle += 360;
				while(endAngle > 360){
					endAngle -= 360;
					startAngle -= 360;
				}
			}
			else
			{
				while (endAngle > startAngle) endAngle -= 360;
				while(endAngle < -360){
					endAngle += 360;
					startAngle += 360;
				}
			}
			ellipse.getCenter(pt);
			double rotateAngle;
			double halfGreatAxis = 0.5*ellipse.getGreatAxisLength();
			double halfSmallAxis = 0.5*abs(ellipse.getSmallAxisLength());
			PicVector u = PicVector.X_AXIS;
			PicVector v = PicVector.Y_AXIS;
			// Ellipse rotation
			if(ellipse.isRotated()){
				rotateAngle = ellipse.getRotationAngle();
				u = new PicVector(u);
				u.rotate(rotateAngle);
				v = new PicVector(-u.getY(),u.getX());
			}
			else
				rotateAngle = 0.0;

			startAngleRad = toRadians(startAngle);
			pt.translate(u,halfGreatAxis*cos(startAngleRad));
			pt.translate(v,halfSmallAxis*sin(startAngleRad));
			buf.append(pt);
			if(rotateAngle != 0.0){
				buf.append(" [rotate=");
				buf.append(Double.toString(toDegrees(rotateAngle)));
				buf.append("] ");
			}
			buf.append(" arc (");

			buf.append(PEToolKit.doubleToString(startAngle));
			buf.append(":");
			buf.append(PEToolKit.doubleToString(endAngle));
			buf.append(":");
			buf.append(PEToolKit.doubleToString(halfGreatAxis));
			if(!ellipse.isCircular())
			{
				buf.append(" and ");
				buf.append(PEToolKit.doubleToString(halfSmallAxis));
			}
			buf.append(")");
			switch(ellipse.getArcType())
			{
			case PicEllipse.CHORD:
				buf.append(" -- cycle");
				break;
			case PicEllipse.PIE:
				buf.append(" -- ");
				buf.append(ellipse.getCenter(pt));
				buf.append(" -- cycle");
				break;
			default:
				break;
			}

		}
		buf.append(factory.getEOCmdMark());
		return buf.toString();
	}
}


/// PicEllipseFormatter.java ends here
