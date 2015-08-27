// DXFConstants.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: DXFConstants.java,v 1.6 2013/03/27 07:12:00 vincentb1 Exp $
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

/// Installation:


/// Code:
package jpicedt.format.output.dxf;
import jpicedt.format.output.util.FormatConstants;
import static java.lang.Math.PI;


/**
 * paramètres "Drawing eXchange Format" : ces parametrès sont utilisé par le
 * DXFFormatter.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class DXFConstants extends FormatConstants {

	public static final double TWO_PI = 2*PI;


	//-----------------------------------------------------------------------
	// Énumérations
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

	// représentation d'une courbe vraiment courbe
	// indexe le tableau LOCAL_ID_CURVE
	public static final int  CURVE_AS_SPLINE        = 0;
	public static final int  CURVE_AS_ARC_AND_LINES = 1;
	public static final int  ELLIPSE_AS_ELLIPSE     = 2;

	// représentation d'une ligne polygonale
	// indexe le tableau LOCAL_ID_POLYLINE
	public static final int  POLYLINE_AS_LWPOLYLINE = 0;
	public static final int  POLYLINE_AS_LINES      = 1;
	public static final int  POLYLINE_AS_POLYLINE   = 2; // la version lourde

	//-----------------------------------------------------------------------
	// identificateurs de label localisé pour les listes déroulantes
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
	// Clefs d'identification des propriétés
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
	// Valeurs par défaut des propriétés
	//-----------------------------------------------------------------------
	public static final DXFVersion DFLT_FMT_DXF_VERSION           = DXFVersion.AUTO_CAD_RELEASE_BEFORE_13;
	public static final int DFLT_FMT_ELLIPSE_OUTLINE_CIRCLE       = CURVE_AS_ARC_AND_LINES;
	public static final int DFLT_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE = CURVE_AS_SPLINE;
	public static final int DFLT_FMT_MULTICURVE					  = CURVE_AS_SPLINE;
	public static final int DFLT_FMT_POLYLINE_ELLIPSE_CHORD		  = POLYLINE_AS_LINES;
	public static final int DFLT_FMT_POLYLINE_ELLIPSE_PIE_LINE	  = POLYLINE_AS_LWPOLYLINE;
	public static final int DFLT_FMT_POLYLINE_POLYGON_MULTICURVE  = POLYLINE_AS_LWPOLYLINE;
	public static final int DFLT_FMT_POLYLINE_PARALLELOGRAM		  = POLYLINE_AS_LWPOLYLINE;

	/** Patron minimal, au cas ou le fichier de ressource ne peut être traité */
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
