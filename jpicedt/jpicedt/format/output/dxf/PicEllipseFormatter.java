// PicEllipseFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: PicEllipseFormatter.java,v 1.8 2013/03/27 07:11:15 vincentb1 Exp $
// Keywords: AutoCAD, DXF (marque d�pos�e)
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

// Installation:

// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.*;
// import jpicedt.graphic.model.*;
// import java.lang.Math.*;


/// Code:
package jpicedt.format.output.dxf;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.PicPoint;
import jpicedt.Log;

import static jpicedt.format.output.dxf.DXFConstants.*;
import static jpicedt.format.output.dxf.DXFConstants.DXFVersion.*;

import static jpicedt.graphic.model.StyleConstants.LineStyle.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;

import java.awt.*;

/**
 * Formatteur de PicEllipse au format DXF (marque d�pos�e). Selon les pr�f�rences de
 * l'utilisateur le r�sultat est fait d'ELLIPSE, SPLINE, ARC LINE et/ou
 * LWPOLYLINE.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jpicedt 1.6
 */
public class PicEllipseFormatter extends AbstractFormatter {

	/** l'Element sur lequel ce formateur agit */
	protected PicEllipse ellipse;
	protected DXFFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return ellipse;}

	/** Construit un formatteur de PicEllipse pour un type de contenu DXF (marque d�pos�e).
	 * @param ellipse l'objet de classe PicEllipse � formatter
	 * @param factory la fabrique de formatage DXF (marque d�pos�e) � utiliser pour le formatage.
	 * @since jPicEdt 1.6
	 */
	public PicEllipseFormatter(PicEllipse ellipse, DXFFormatter factory)
		{
			this.ellipse = ellipse;
			this.factory = factory;
		}



   /**
	* @return une cha�ne de caract�res contenant le code DXF (marque d�pos�e) formatant la
	* PicEllipse pass�e � la construction.
	* @since jPicEdt 1.6
	*/
	public String format()
		{
			DXFStringBuffer ret = new DXFStringBuffer(100,factory.getLineSeparator());

			if(factory.showJpic())
			{
				ret.comment(ellipse.toString());
			}
			PicPoint center = ellipse.getCenter(null);

			double halfGreatAxis = 0.5*ellipse.getGreatAxisLength();

			boolean isArc = ellipse.isArc();

			/* les initialisation servent uniquement � enlever l'avertissement
			   sur la non initialisation potentielle de startAngle et endAngle */
			double startAngle = 0;
			double endAngle = TWO_PI;

			// contour du cercle ou de l'ellipse
			//---------------------------------------------------------------
			if(ellipse.isCircular())
			{
				if(isArc)
					ret.tagVal(0,"ARC");
				else
					ret.tagVal(0,"CIRCLE");
				factory.commonTagVal(ret);
				if(factory.getDXFVersion().getValue() >= AUTO_CAD_RELEASE_13.getValue())
					ret.tagVal(100,"AcDbCircle"); // sous-classe

				ret.tagVal(40,halfGreatAxis);
				ret.tagPoint(0,center);
				if(isArc)
				{
					double axisAngle = Math.toDegrees(ellipse.getRotationAngle());
					double orientation = ellipse.getSmallAxisLength() < 0 ? -1 : 1;
					if(factory.getDXFVersion().getValue() >= AUTO_CAD_RELEASE_13.getValue())
						ret.tagVal(100,"AcDbArc");	// sous-classe
					endAngle = axisAngle+orientation*ellipse.getRotatedAngleEnd();
					startAngle = axisAngle+orientation*ellipse.getRotatedAngleStart();
					if(orientation < 0)
					{
						ret.tagVal(50,endAngle);
						ret.tagVal(51,startAngle);
					}
					else
					{
						ret.tagVal(50,startAngle);
						ret.tagVal(51,endAngle);
					}
				}
			}
			else
			{

				ret.tagVal(0,"ELLIPSE");
				factory.commonTagVal(ret);
				if(factory.getDXFVersion().getValue() >= AUTO_CAD_RELEASE_13.getValue())
					ret.tagVal(100,"AcDbEllipse"); // sous-classe
				ret.tagPoint(0,center);
				PicVector centerToApogee =
					(new PicVector(PicVector.X_AXIS))
					.rotate(ellipse.getRotationAngle())
					.scale(halfGreatAxis);
				ret.tagPoint(1,centerToApogee);
				ret.tagVal(40,
						   Math.abs(ellipse.getSmallAxisLength()/ellipse.getGreatAxisLength()));
				if(isArc)
				{
					startAngle = Math.toRadians(ellipse.getRotatedAngleStart());
					endAngle = Math.toRadians(ellipse.getRotatedAngleEnd());
					// intervertit startAngle et endAngle si le sens est anti-trigo
					// et change le signe.
					if(ellipse.getSmallAxisLength() < 0)
					{
						double temp = startAngle;
						startAngle = -endAngle;
						endAngle = -temp;
					}
					if(endAngle < 0)
						endAngle += TWO_PI;
					if(startAngle < 0)
						startAngle += TWO_PI;
					ret.tagVal(41,startAngle);
					ret.tagVal(42,endAngle);

				}
			}

			// trace la corde ou les bords de coin de camembert.
			//---------------------------------------------------------------
			if(isArc && ellipse.getArcType() != PicEllipse.OPEN)
			{
				PicPoint pt1 = ellipse.getCtrlPt(ellipse.getFirstPointIndex(),null);
				PicPoint pt2 = ellipse.getCtrlPt(ellipse.getFirstPointIndex()+1,null);
				PicPoint pt3 = ellipse.getCtrlPt(ellipse.getFirstPointIndex()+2,null);

				PicVector doubleU = new PicVector(pt1,pt2);
				PicVector doubleV = new PicVector(pt2,pt3);

				double skewAngle = Math.toRadians(ellipse.getAngleStart());
				double c = Math.cos(skewAngle)*.5;
				double s = Math.sin(skewAngle)*.5;
				PicPoint angleStartPt = center.clone();
				angleStartPt.translate(
						c*doubleU.getX()+s*doubleV.getX(),
						c*doubleU.getY()+s*doubleV.getY());

				skewAngle = Math.toRadians(ellipse.getAngleEnd());
				c = Math.cos(skewAngle)*.5;
				s = Math.sin(skewAngle)*.5;

				PicPoint angleEndPt = center.clone();
				angleEndPt.translate(
						c*doubleU.getX()+s*doubleV.getX(),
						c*doubleU.getY()+s*doubleV.getY());
				switch(ellipse.getArcType())
				{
				case PicEllipse.CHORD:
					switch(factory.getPlChord())
					{
					case POLYLINE_AS_LINES:
						factory.appendLine(ret,angleStartPt,angleEndPt);
						break;
					case POLYLINE_AS_LWPOLYLINE:
						ret.tagVal(0,"LWPOLYLINE"); // light weight polyline
						factory.commonTagVal(ret);
						ret.tagVal(90,2); // 2 sommets
						ret.tagPoint(0,angleStartPt);
						ret.tagPoint(0,angleEndPt);
						break;
					default:
						Log.error("Propri�t� inattendue");
						break;
					}
					break;
				case PicEllipse.PIE:
					switch(factory.getPlChord())
					{
					case POLYLINE_AS_LINES:
						factory.appendLine(ret,angleStartPt,center);
						factory.appendLine(ret,center,angleEndPt);
						break;
					case POLYLINE_AS_LWPOLYLINE:
						ret.tagVal(0,"LWPOLYLINE"); // light weight polyline
						factory.commonTagVal(ret);
						if(factory.getDXFVersion().getValue() >= AUTO_CAD_RELEASE_13.getValue())
							ret.tagVal(100,"AcDbPolyline"); // sous-classe
						ret.tagVal(90,3); // 3 sommets
						ret.tagPoint(0,angleStartPt);
						ret.tagPoint(0,center);
						ret.tagPoint(0,angleEndPt);
						break;
					default:
						Log.error("Propri�t� inattendue");
						break;
					}
					break;
				default:
					Log.error("ellipse.getArcType() inattendu");
					break;
				}
			}

			return ret.toString();
		}

};


/// PicEllipseFormatter.java ends here
