// AbstractCurveFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: AbstractCurveFormatter.java,v 1.8 2013/03/27 07:24:27 vincentb1 Exp $
// Keywords: AutoCAD, DXF (marque déposée)
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
import jpicedt.graphic.model.Element;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.PicPoint;
import jpicedt.Log;
import jpicedt.graphic.util.MultiCurveToArc;
import jpicedt.format.output.util.PicGroupFormatter;

import java.awt.*;
import java.io.IOException;

import static jpicedt.format.output.dxf.DXFConstants.*;
import static jpicedt.format.output.dxf.DXFConstants.DXFVersion.*;
import static jpicedt.graphic.model.StyleConstants.LineStyle.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;

/**
 * Formatteur en DXF (marque déposée) pour les AbstractCurve's. Le résultat du formattages est
 * selon les caractéristique de la AbstractCurve une/des LWPOLYLINE, des
 * LINE's, des ARC, et/ou des SPLINE.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class AbstractCurveFormatter extends AbstractFormatter
{

	/** the Element this formater acts upon */
	protected AbstractCurve curve;
	protected DXFFormatter  factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return curve;}

	/**
	 * @since jPicEdt 1.6
	 */
	public AbstractCurveFormatter(AbstractCurve curve, DXFFormatter factory){
		this.curve = curve;
		this.factory=factory;
	}

	/**
	 * @since jPicEdt 1.6
	 */
	public String format() throws IOException{
		int segmentMax = curve.getSegmentCount();
		if(segmentMax == 0)
			return "";


		DXFStringBuffer ret = new DXFStringBuffer(100, factory.getLineSeparator());

		if(factory.showJpic())
		{
			ret.comment(curve.toString());
		}

		boolean isClosed = curve.isClosed();
		boolean isPolygon;
		int segmentMin = 0;
		int segmentCur = segmentMin;
		int segmentNext;
		PicPoint pt1 = new PicPoint();
		PicPoint pt2 = new PicPoint();
		for(;;)
		{

			if(segmentCur == segmentMin &&
			   curve.isPolygon())
			{
				segmentNext = segmentMax;
				isPolygon = true;
			}
			else
			{
				segmentNext = segmentCur+1;
				if(isPolygon = curve.isStraight(segmentCur))
				{
					// étend le polygone le plus possible
					while(segmentNext < segmentMax
						  && curve.isStraight(segmentNext))
						++segmentNext;
				}
				else
				{
					/* étend la chaîne de courbes de Bézier le plus possible
					   tant que ça suit en douceur */
					while(segmentNext < segmentMax
						  && !curve.isStraight(segmentNext)
						  && curve.isSmooth(segmentNext-1))
						++segmentNext;
				}
			}
			if(isPolygon)
			{
				int plMulticurve = factory.getPlMulticurve();
				if(plMulticurve ==  POLYLINE_AS_LWPOLYLINE
				   && factory.getDXFVersion().getValue() < AUTO_CAD_RELEASE_14.getValue())
					plMulticurve =  POLYLINE_AS_LINES;
				
				switch(plMulticurve)
				{
				case POLYLINE_AS_LWPOLYLINE:
					ret.tagVal(0,"LWPOLYLINE");
					factory.commonTagVal(ret);
					ret.tagVal(100,"AcDbPolyline");
					ret.tagVal(90,segmentNext-segmentCur);
					if(isClosed &&
					   curve.getPBCBezierIndex(3*segmentCur) ==
					   curve.getPBCBezierIndex(3*segmentNext))
						ret.tagVal(70,1);
					else
						++segmentNext;
					for(int segment = segmentCur;segment < segmentNext; ++segment)
					{
						pt1 = curve.getBezierPt(3*segment,pt1);
						ret.tagPoint(0,pt1);
					}
					break;
				case POLYLINE_AS_LINES:
					if(isClosed &&
					   curve.getPBCBezierIndex(3*segmentMin) ==
					   curve.getPBCBezierIndex(3*segmentMax))
						++segmentMax;
					for(int segment = segmentCur;segment < segmentNext; ++segment)
					{
						pt1 = curve.getBezierPt(3*segment,pt1);
						pt2 = curve.getBezierPt(curve.getPBCBezierIndex(3*(segment+1)),pt2);
						factory.appendLine(ret,pt1,pt2);
					}
					break;
				default:
					Log.error("la valeur renvoyée par getPlMulticurve() était inattendue");
					break;
				}

			}
			else
			{
				switch(factory.getCurveMulticurve())
				{
				case CURVE_AS_SPLINE:
					ret.tagVal(0,"SPLINE");
					factory.commonTagVal(ret);
					if(factory.getDXFVersion().getValue() >= AUTO_CAD_RELEASE_13.getValue())
						ret.tagVal(100,"AcDbSpline");
					// vecteur normal
					ret.tagVal(210,0.0);
					ret.tagVal(220,0.0);
					ret.tagVal(230,1.0);
					{
						int flag = 8; // latte plane
						if(isClosed &&
						   curve.getPBCBezierIndex(3*segmentCur) ==
						   curve.getPBCBezierIndex(3*segmentNext)
							)
							flag |= 1;
						ret.tagVal(70,flag);
						ret.tagVal(71,3); // latte cubique
						int tangentPtCount = segmentNext-segmentCur+1;
						int ctlPtCount  = (tangentPtCount-2)*3+4;
						int knotCount = ctlPtCount+4;
						ret.tagVal(72,knotCount);
						ret.tagVal(73,ctlPtCount);
						// knots
						ret.tagVal(40,0.0);
						for(int segment = segmentCur;segment < segmentNext; ++segment)
						{
							double knotVal = (double)(segment - segmentCur)
								/(segmentNext - segmentCur);
							for(int i = 0; i < 3; ++i)
								ret.tagVal(40,knotVal);
						}
						ret.tagVal(40,1.0);
						ret.tagVal(40,1.0);
						ret.tagVal(40,1.0);
						ret.tagVal(40,1.0);
						for(int i = segmentCur*3;i <= segmentNext*3; ++i)
						{
							pt1 = curve.getBezierPt(i,pt1);
							ret.tagPoint(0,pt1);
						}
					}
					break;

				case CURVE_AS_ARC_AND_LINES:
					;
					{
						PicGroup arcs = MultiCurveToArc.convert(
							curve,
							0.01,
							100,
							segmentCur*3,
							segmentNext*3);

						PicGroupFormatter groupFormatter = new
							PicGroupFormatter(arcs,factory);

						ret.append(groupFormatter.format());
					}
					break;

				default:
					Log.error("la valeur renvoyée par getCurveMulticurve() était inattendue");
					break;
				}
			}
			if(segmentNext >= segmentMax)
				break;
			else
				segmentCur = segmentNext;
		}
		return ret.toString();
	}

};

/// AbstractCurveFormatter.java ends here
