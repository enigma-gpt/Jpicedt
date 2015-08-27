// PicEllipseFormatter.java ---
// Copyright 2009/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: PicEllipseFormatter.java,v 1.11 2013/06/18 20:48:22 vincentb1 Exp $
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
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jpicedt 1.6
 */
public class PicEllipseFormatter  extends AbstractFormatter
{
	/** les éléments sur lesquels ce formatteur agit */
	protected PicEllipse ellipse;
	protected TikzFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return ellipse;}

	/**
	 * renvoie <code>true</code> lorsque il faut inverser les paramètres des
	 * flèches au moment du formattage.
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
	* @return une chaîne de caractères contenant le code Tikz formatant la
	* PicEllipse passée à la construction.
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

						// l'ellipse est penchée.
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
