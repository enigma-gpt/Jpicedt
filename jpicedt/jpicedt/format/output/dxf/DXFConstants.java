// DXFConstants.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFConstants.java,v 1.6 2013/03/27 07:12:00 vincentb1 Exp $
// Keywords:
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

/// Installation:


/// Code:
package jpicedt.format.output.dxf;
import jpicedt.format.output.util.FormatConstants;
import static java.lang.Math.PI;


/**
 * param�tres "Drawing eXchange Format" : ces parametr�s sont utilis� par le
 * DXFFormatter.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 */
public class DXFConstants extends FormatConstants {

	public static final double TWO_PI = 2*PI;


	//-----------------------------------------------------------------------
	// �num�rations
	//-----------------------------------------------------------------------
	public static enum DXFVersion{
		// AutoCAD(R) Release
		AUTO_CAD_RELEASE_BEFORE_13(0),
		AUTO_CAD_RELEASE_13(1),
		// LightWeight Polylines sont apparues avec la rel'14
        AUTO_CAD_RELEASE_14(2);
		static public final String PREFIX = "AUTO_CAD_RELEASE_";
		private int value;
		int getValue(){ return value; }
		String getDXFVersionId(){ return toString().substring(17); }
		DXFVersion(int value){ this.value = value; }
	};

	// repr�sentation d'une courbe vraiment courbe
	// indexe le tableau LOCAL_ID_CURVE
	public static final int  CURVE_AS_SPLINE        = 0;
	public static final int  CURVE_AS_ARC_AND_LINES = 1;
	public static final int  ELLIPSE_AS_ELLIPSE     = 2;

	// repr�sentation d'une ligne polygonale
	// indexe le tableau LOCAL_ID_POLYLINE
	public static final int  POLYLINE_AS_LWPOLYLINE = 0;
	public static final int  POLYLINE_AS_LINES      = 1;
	public static final int  POLYLINE_AS_POLYLINE   = 2; // la version lourde

	//-----------------------------------------------------------------------
	// identificateurs de label localis� pour les listes d�roulantes
	//-----------------------------------------------------------------------
	public static final String[] LOCAL_ID_CURVE= {
		"format.dxf.Formatter.curve_as_spline",
		"format.dxf.Formatter.curve_as_arc_and_lines",
		"format.dxf.Formatter.ellipse_as_ellipse"
	};
	public static final String[] LOCAL_ID_POLYLINE= {
		"format.dxf.Formatter.polyline_as_lwpolyline",
		"format.dxf.Formatter.polyline_as_lines",
		"format.dxf.Formatter.polyline_as_polyline"
	};
	//-----------------------------------------------------------------------
	// Clefs d'identification des propri�t�s
	//-----------------------------------------------------------------------
	public static final String KEY_FMT_DXF_VERSION                  ="dxf.fmt.dxf_version";
	public static final String KEY_FMT_ELLIPSE_OUTLINE_CIRCLE       ="dxf.fmt.elol.circle";
	public static final String KEY_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE	="dxf.fmt.elol.ellipse";
	public static final String KEY_FMT_MULTICURVE					="dxf.fmt.curve.multicurve";
	public static final String KEY_FMT_POLYLINE_ELLIPSE_CHORD		="dxf.fmt.pl.chord";
	public static final String KEY_FMT_POLYLINE_ELLIPSE_PIE_LINE	="dxf.fmt.pl.pie";
	public static final String KEY_FMT_POLYLINE_POLYGON_MULTICURVE	="dxf.fmt.pl.multicruve";
	public static final String KEY_FMT_POLYLINE_PARALLELOGRAM		="dxf.fmt.pl.parallelogram";

	//-----------------------------------------------------------------------
	// Choix possibles pour chaque clef
	//-----------------------------------------------------------------------
	public static final int[] OPTIONS_FMT_ELLIPSE_OUTLINE_CIRCLE        = {
		CURVE_AS_SPLINE, CURVE_AS_ARC_AND_LINES
	};
	public static final int[] OPTIONS_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE	={
		CURVE_AS_SPLINE, CURVE_AS_ARC_AND_LINES, ELLIPSE_AS_ELLIPSE
	};
	public static final int[] OPTIONS_FMT_MULTICURVE					={
		CURVE_AS_SPLINE, CURVE_AS_ARC_AND_LINES
	};
	public static final int[] OPTIONS_FMT_POLYLINE_ELLIPSE_CHORD		={
		POLYLINE_AS_LWPOLYLINE,POLYLINE_AS_LINES
	};
	public static final int[] OPTIONS_FMT_POLYLINE_ELLIPSE_PIE_LINE	    ={
		POLYLINE_AS_LWPOLYLINE,POLYLINE_AS_LINES
	};
	public static final int[] OPTIONS_FMT_POLYLINE_POLYGON_MULTICURVE	={
		POLYLINE_AS_LWPOLYLINE,POLYLINE_AS_LINES
	};
	public static final int[] OPTIONS_FMT_POLYLINE_PARALLELOGRAM		={
		POLYLINE_AS_LWPOLYLINE,POLYLINE_AS_LINES
	};


	//-----------------------------------------------------------------------
	// Valeurs par d�faut des propri�t�s
	//-----------------------------------------------------------------------
	public static final DXFVersion DFLT_FMT_DXF_VERSION           = DXFVersion.AUTO_CAD_RELEASE_BEFORE_13;
	public static final int DFLT_FMT_ELLIPSE_OUTLINE_CIRCLE       = CURVE_AS_ARC_AND_LINES;
	public static final int DFLT_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE = CURVE_AS_SPLINE;
	public static final int DFLT_FMT_MULTICURVE					  = CURVE_AS_SPLINE;
	public static final int DFLT_FMT_POLYLINE_ELLIPSE_CHORD		  = POLYLINE_AS_LINES;
	public static final int DFLT_FMT_POLYLINE_ELLIPSE_PIE_LINE	  = POLYLINE_AS_LWPOLYLINE;
	public static final int DFLT_FMT_POLYLINE_POLYGON_MULTICURVE  = POLYLINE_AS_LWPOLYLINE;
	public static final int DFLT_FMT_POLYLINE_PARALLELOGRAM		  = POLYLINE_AS_LWPOLYLINE;

	/** Patron minimal, au cas ou le fichier de ressource ne peut �tre trait� */
	public static final DXFInformation[] DXF_FALLBACK_TEMPLATE = {
		new DXFTaggedValue.DXFTaggedString(0,"SECTION"),
		new DXFTaggedValue.DXFTaggedString(2,"HEADER"),
		new DXFTaggedValue.DXFExtMinFormatter(),
		new DXFTaggedValue.DXFExtMaxFormatter(),
		new DXFTaggedValue.DXFTaggedString(0,"ENDSEC"),
		new DXFTaggedValue.DXFTaggedString(0,"SECTION"),
		new DXFTaggedValue.DXFTaggedString(2,"ENTITIES"),
		new DXFTaggedValue.DXFEntitiesFormatter(),
		new DXFTaggedValue.DXFTaggedString(0,"ENDSEC"),
		new DXFTaggedValue.DXFTaggedString(0,"EOF")};


}


/// DXFConstants.java ends here
