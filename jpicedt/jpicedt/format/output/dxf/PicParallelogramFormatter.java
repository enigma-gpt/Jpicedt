// PicParallelogramFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: PicParallelogramFormatter.java,v 1.9 2013/06/13 20:47:47 vincentb1 Exp $
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

/// Code:
package jpicedt.format.output.dxf;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.PicPoint;

import java.awt.*;

import static jpicedt.graphic.model.StyleConstants.LineStyle.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;
import static jpicedt.format.output.dxf.DXFConstants.*;
import static jpicedt.format.output.dxf.DXFConstants.DXFVersion.*;



/**
 * Formatteur de PicParallelogram au format DXF (marque déposée). Le résultat est soit un
 * LWPOLYLINE soit des LINE's selon les préférences utilisateur.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jpicedt 1.6
 */
public class PicParallelogramFormatter extends AbstractFormatter {

	/** the Element this formater acts upon */
	protected PicParallelogram parallelogram;
	protected DXFFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return parallelogram;}

	/** Construit un formatteur de PicParallelogram pour un type de contenu DXF (marque déposée).
	 * @param parallelogram l'objet de classe PicParallelogram à formatter
	 * @param factory la fabrique de formatage DXF (marque déposée) à utiliser pour le formatage.
	 * @since jPicEdt 1.6
	 */
	public PicParallelogramFormatter(PicParallelogram parallelogram, DXFFormatter factory)
		{
			this.parallelogram = parallelogram;
			this.factory = factory;
		}

	/**
	 * @since jPicEdt 1.6
	 */
	public String format()
		{
			DXFStringBuffer ret = new DXFStringBuffer(100,factory.getLineSeparator());

			if(factory.showJpic())
			{
				ret.comment(parallelogram.toString());
			}

			PicPoint pt1 = parallelogram.getCtrlPt(
				parallelogram.getFirstPointIndex(),null);
			PicPoint pt2 = parallelogram.getCtrlPt(
				parallelogram.getFirstPointIndex()+1,null);
			PicPoint pt3 = parallelogram.getCtrlPt(
				parallelogram.getFirstPointIndex()+2,null);
			PicPoint center = parallelogram.getCenter(null);
			PicPoint pt4 = PicPoint.symmetry(center,pt2);

			PicPoint[] points = { pt1, pt2, pt3, pt4 };

			int plParallelogram = factory.getPlParallelogram();
			if(plParallelogram ==  POLYLINE_AS_LWPOLYLINE
			   && factory.getDXFVersion().getValue() < AUTO_CAD_RELEASE_14.getValue())
				plParallelogram =  POLYLINE_AS_LINES;


			switch(plParallelogram)
			{
			case POLYLINE_AS_LWPOLYLINE:
				ret.tagVal(0,"LWPOLYLINE"); // light weight polyline
				factory.commonTagVal(ret);
				ret.tagVal(100,"AcDbPolyline");
				ret.tagVal(90,4); // 4 vertices
				ret.tagVal(70,1); // closed
				for(int i = 0; i < 3; ++i){
					PicPoint pt = points[i];
					ret.tagVal(10,pt.getX());
					ret.tagVal(20,pt.getY());
				}
				break;
			case POLYLINE_AS_LINES:
				for(int i = 0; i < 4; ++i){
					PicPoint ptA = points[i];
					PicPoint ptB = points[(i+1)&3];
					factory.appendLine(ret,ptA,ptB);
				}
 				break;
			case POLYLINE_AS_POLYLINE:
				ret.tagVal(0,"POLYLINE"); // light weight polyline
				factory.commonTagVal(ret);
				if(factory.getDXFVersion().getValue() >= AUTO_CAD_RELEASE_13.getValue())
					ret.tagVal(100,"AcDb2dPolyline");
				ret.tagVal(70,1); // closed
				ret.tagVal(66,1); // vertices follow flag
				for(int i = 0; i < 3; ++i){
					PicPoint pt = points[i];
					ret.tagVal(0,"VERTEX");
					ret.tagVal(10,pt.getX());
					ret.tagVal(20,pt.getY());
				}
				ret.tagVal(0,"SEQEND");
				break;
			}

			return ret.toString();

		}

};

/// PicParallelogramFormatter.java ends here
