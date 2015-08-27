/*  jPicEdt, a picture editor for LaTeX.
    Copyright (C) 1999-2006  Sylvain Reynal
*/
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
// Tel : +33 130 736 245
// Fax : +33 130 736 667
// e-mail : reynal@ensea.fr
// Version: $Id: PicEllipseFormatter.java,v 1.15 2013/03/27 07:09:45 vincentb1 Exp $
// Keywords:
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
package jpicedt.format.output.latex;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.PicPoint;

import java.awt.*;
import static jpicedt.format.output.latex.LatexConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * A drawing->LaTeX formater for PicEllipse objects
 * <p>
 * [SR:pending] rotated arc/ellipse not handled yet !
 */
public class PicEllipseFormatter extends AbstractFormatter {

	/** the Element this formater acts upon */
	protected PicEllipse ellipse;
	protected LatexFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return ellipse;}

	public PicEllipseFormatter(PicEllipse ellipse,LatexFormatter factory){
		this.ellipse = ellipse;
		this.factory=factory;;
	}

	/**
	 * @return a LaTeX-String representation of a PicEllipse (i.e. a circle, an ellipse, or an arc, never filled in the LaTeX format).
	 * @return The LaTeX string corresponding to this object
	 */
	public String format(){

		StringBuffer buf = new StringBuffer(100); // 100 as initial capacity seems to be a good guess


		factory.appendThicknessString(buf,ellipse);

		// if it's a small disk, then maybe we can use a "\\circle*" command :
		if (ellipse.isPlain() && ellipse.isCircular() && ellipse.getAttribute(FILL_STYLE)==FillStyle.SOLID
			&& ellipse.getGreatAxisLength() <= factory.maxLatexDiskDiameter) appendLatexDiskString(buf);

		// if it's a small circle, then maybe we can use a "\\circle" command : (ok, there may also be a disk here as well, if its diameter is too big)
		else if (ellipse.isPlain() && ellipse.isCircular() && ellipse.getGreatAxisLength() <= factory.maxLatexCircleDiameter)
			appendLatexCircleString(buf);

		// ok, it's a big circle or an ellipse or an arc, and however filled it may be, we emulate it with small segments
		else {
			appendEmulatedArcString(buf);
			appendArrows(buf);
		}

		buf.append(factory.getLineSeparator());
		return buf.toString();
	}

	/**
	 * <p>Create a string representation of a disk in the LaTeX format, using the \\circle* command,
	 * and append it to the given buffer. Appended string is CR-terminated.</p>
	 * @since jpicedt 1.3.2
	 */
	protected void appendLatexDiskString(StringBuffer buf){

		buf.append("\\put");
		buf.append(ellipse.getCtrlPt(PicEllipse.P_CENTER,null));
		buf.append("{\\circle*{");
		buf.append(PEToolKit.doubleToString(ellipse.getGreatAxisLength()));
		buf.append("}}");
		buf.append(factory.getLineSeparator());
	}

	/**
	 * <p>Create a string representation of a circle in the LaTeX format, using the \\circle command,
	 * and append it to the given buffer. Appended string is CR-terminated.</p>
	 * @since jpicedt 1.3.2
	 */
	protected void appendLatexCircleString(StringBuffer buf){

		buf.append("\\put");
		buf.append(ellipse.getCtrlPt(PicEllipse.P_CENTER,null));
		buf.append("{\\circle{");
		buf.append(PEToolKit.doubleToString(ellipse.getGreatAxisLength()));
		buf.append("}}");
		buf.append(factory.getLineSeparator());
	}

	/**
	 * <p>Create a string representation of a circle/ellipse in the LaTeX format, using emulated lines,
	 * and append it to the given buffer. Appended string is CR-terminated.</p>
	 * <p>Parametric equation is :</p>
	 * <ul>
	 * <li>x(t) = a cos t</li>
	 * <li>y(t) = b sin t</li>
	 * <li>where a and b are the length of 1/2 axis.</li>
	 * </ul>
	 * [SR:pending] add dash + rotated ellipse
	 * @since jpicedt 1.3.2
	 */
	protected void appendEmulatedArcString(StringBuffer buf){

		// reminder : PicEllipse.getAngleExtent() returns an angle in degrees, CCW, in (0,360]
		//            "        ".getAngleStart() returns an angle in degrees, CCW, in (-180,180]
		//            "        ".getAngleEnd() returns an angle in degrees, CCW, in (-180,180]
		// and angle-end > angle-start.

		PicVector l2r = new PicVector(ellipse.getCtrlPt(PicEllipse.P_BL,null),ellipse.getCtrlPt(PicEllipse.P_BR,null));
		PicVector b2t = new PicVector(ellipse.getCtrlPt(PicEllipse.P_BR,null),ellipse.getCtrlPt(PicEllipse.P_TR,null));
		PicPoint pt0 = ellipse.getCtrlPt(PicEllipse.P_CENTER,null);

		int nSeg; 	// nSeg = number of segment used to emulate this circle/ellipse/arc
		double deltaPhi; // angle step for the parametric equation, in radians
		double startAngle; // angle at which the parametric equation starts

		// INIT :
		if (ellipse.isPlain()){
			// it's an ELLIPSE, nSeg is always a multiple of 4, so that the shape has 2 axes of symmetry
			// it is computed by dividing the circumference by the length of a segment
			// in the case of an ellipse, its circumference is approximated by the circumference of the surrounding circle,
			// since computing the real ellipse length would involve special EllipticIntegral functions
			// hence we use Max(a,b) as an approximate radius.
			nSeg = (int)(4*Math.round(Math.PI * ellipse.getGreatAxisLength()  / factory.maxEmCircleSegmentLength / 2));
			deltaPhi = 2*Math.PI/nSeg;
			startAngle = -deltaPhi/2; // use axis of symetry
		}
		else {
			// it's an ARC, same thing except that axes of symetry don't exist "a priori" so we don't care.
			double arcAngle = ellipse.getAngleExtent() * Math.PI / 180;
			nSeg = (int)(Math.round( arcAngle * ellipse.getGreatAxisLength()  / factory.maxEmCircleSegmentLength ));
			deltaPhi = arcAngle / nSeg;
			startAngle = ellipse.getAngleStart() * Math.PI / 180;
		}

		// BUILD STRING :
		double xA,yA,xB,yB,phi;
		for (int i=0; i <= nSeg-1; i++){
			phi = (double)i * deltaPhi + startAngle;
			xA = pt0.x + l2r.x * 0.5 * Math.cos(phi) + b2t.x * 0.5 * Math.sin(phi);
			yA = pt0.y + l2r.y * 0.5 * Math.cos(phi) + b2t.y * 0.5 * Math.sin(phi);
			xB = pt0.x + l2r.x * 0.5 * Math.cos(phi + deltaPhi) + b2t.x * 0.5 * Math.sin(phi + deltaPhi);
			yB = pt0.y + l2r.y * 0.5 * Math.cos(phi + deltaPhi) + b2t.y * 0.5 * Math.sin(phi + deltaPhi);
			buf.append(factory.lineToLatexString(xA,yA,xB,yB,ArrowStyle.NONE,ArrowStyle.NONE,0));
			buf.append(factory.getLineSeparator());
		}

		// if it's not plain, possibly draw PIE or CHORD lines :
		if (!ellipse.isPlain()){
			if (ellipse.getArcType()==PicEllipse.PIE){
				PicPoint ptSt = ellipse.getCtrlPt(PicEllipse.P_ANGLE_START,null);
				PicPoint ptEnd = ellipse.getCtrlPt(PicEllipse.P_ANGLE_END,null);
				buf.append(factory.lineToLatexString(ptSt,pt0,ArrowStyle.NONE,ArrowStyle.NONE,0));
				buf.append(factory.lineToLatexString(ptEnd,pt0,ArrowStyle.NONE,ArrowStyle.NONE,0));
			}
			else if (ellipse.getArcType()==PicEllipse.CHORD){
				PicPoint ptSt = ellipse.getCtrlPt(PicEllipse.P_ANGLE_START,null);
				PicPoint ptEnd = ellipse.getCtrlPt(PicEllipse.P_ANGLE_END,null);
				buf.append(factory.lineToLatexString(ptSt,ptEnd,ArrowStyle.NONE,ArrowStyle.NONE,0));
			}
		}
	}

	/**
	 * Append arrows to the given buffer by invoking
	 *<code>arrowToLatexString()</code> on the producing factory.
	 */
	protected void appendArrows(StringBuffer buf){
		// ARROW in the ARC case :
		PicAttributeSet set = ellipse.getAttributeSet();
		if (!ellipse.isClosed()){ // i.e. OPEN, and not plain
			PicPoint loc,dir;
			if (set.getAttribute(LEFT_ARROW)!=ArrowStyle.NONE){

				loc = ellipse.getCtrlPt(PicEllipse.P_ANGLE_START,null);
				dir = ellipse.getTangentAtAngleStart(null);
				buf.append(factory.arrowToLatexString(loc, dir));
				buf.append(factory.getLineSeparator());
			}

			if (set.getAttribute(RIGHT_ARROW)!=ArrowStyle.NONE){

				loc = ellipse.getCtrlPt(PicEllipse.P_ANGLE_END,null);
				dir = ellipse.getTangentAtAngleEnd(null);
				buf.append(factory.arrowToLatexString(loc, dir));
				buf.append(factory.getLineSeparator());
			}
		}

	}

}
