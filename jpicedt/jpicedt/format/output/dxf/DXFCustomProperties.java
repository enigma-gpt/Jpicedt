// DXFCustomProperties.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFCustomProperties.java,v 1.7 2013/03/27 07:11:50 vincentb1 Exp $
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

/// Installation:

/// Code:
package jpicedt.format.output.dxf;
import jpicedt.Log;
import java.util.*;
import static jpicedt.format.output.dxf.DXFConstants.*;

/**
 * Pr�f�rences utilisateurs pour l'import/export de dessins au format DXF (marque d�pos�e).
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jpicedt 1.6
 */
public class DXFCustomProperties implements DXFCustomization
{
	private DXFConstants.DXFVersion dxfVersion;
   	private int elolCircle;
   	private int elolEllipse;
	private int	curveMulticurve;
	private int	plChord;
	private int	plPie;
	private int	plMultiCurve;
	private int plParallelogram;

	public DXFVersion getDXFVersion(){      return dxfVersion;      }
	public int getElolCircle(){      return elolCircle;      }
	public int getElolEllipse(){     return elolEllipse;	  }
	public int getCurveMulticurve(){ return curveMulticurve; }
	public int getPlChord(){         return plChord;		  }
	public int getPlPie(){           return plPie;			  }
	public int getPlMulticurve(){    return plMultiCurve;	  }
	public int getPlParallelogram(){ return plParallelogram; }

	public void setDXFVersion(DXFVersion val){ dxfVersion    = val; }
   	public void setElolCircle(int val){      elolCircle      = val; }
   	public void setElolEllipse(int val){     elolEllipse     = val; }
	public void setCurveMulticurve(int val){ curveMulticurve = val; }
	public void setPlChord(int val){         plChord         = val; }
	public void setPlPie(int val){           plPie           = val; }
	public void setPlMulticruve(int val){    plMultiCurve    = val; }
	public void setPlParallelogram(int val){ plParallelogram = val; }


	/**
	 * @since jPicEdt 1.6
	 */
	DXFCustomProperties(){}

	/**
	 * @since jPicEdt 1.6
	 */
	DXFCustomProperties(int i)
		{
			switch(i)
			{
			case 0:
				loadDefault();
				break;
			default:
				Log.error("Argument inattendu");
				break;
			}
		}

	/**
	 * Charge le contenu d'affichage des widgets avec les valeurs par d�faut
	 * prises des <code>DXFContants</code>.
	 * @since jPicEdt 1.6
	 */
	public int loadDefault() {
		dxfVersion      = DFLT_FMT_DXF_VERSION;
		elolCircle      = DFLT_FMT_ELLIPSE_OUTLINE_CIRCLE      ;
		elolEllipse     = DFLT_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE;
		curveMulticurve = DFLT_FMT_MULTICURVE                  ;
		plChord         = DFLT_FMT_POLYLINE_ELLIPSE_CHORD      ;
		plPie           = DFLT_FMT_POLYLINE_ELLIPSE_PIE_LINE   ;
		plMultiCurve    = DFLT_FMT_POLYLINE_POLYGON_MULTICURVE ;
		plParallelogram = DFLT_FMT_POLYLINE_PARALLELOGRAM      ;
		return 0;
	}

	/**
	 * Lit une pr�f�rence � valeur enti�re depuis l'objet <code>prop</code>.
	 * @param prop L'objet dans lequel la pr�f�rence est lue
	 * @param key  L'identificateur de la pr�f�rence lue
	 * @param defaultVal  La valeur par d�faut de la pr�f�rence lue.
	 * @return la pr�f�rence lue
	 * @since jPicEdt 1.6
	 */
	private int getIntegerPreference(Properties prop,String key,int defaultVal){
		return Integer.valueOf(
			prop.getProperty(key,String.valueOf(defaultVal)));
	}
	/**
	 * Charge les pr�f�rences DXF (marque d�pos�e) depuis prop vers this (op�ration inverse de store).
	 * @param prop L'objet depuis lequel on charge les pr�f�rences.
	 * @since jPicEdt 1.6
	 */
	public int load(Properties prop){
		// � partir de DXFConstants :
		dxfVersion      = DXFVersion.valueOf(DXFVersion.PREFIX
											 + prop.getProperty(KEY_FMT_DXF_VERSION,
																DFLT_FMT_DXF_VERSION.getDXFVersionId()));
		elolCircle      = getIntegerPreference(prop,KEY_FMT_ELLIPSE_OUTLINE_CIRCLE
											   ,DFLT_FMT_ELLIPSE_OUTLINE_CIRCLE);
		elolEllipse     = getIntegerPreference(prop,KEY_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE
											   ,DFLT_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE);
		curveMulticurve = getIntegerPreference(prop,KEY_FMT_MULTICURVE
											   ,DFLT_FMT_MULTICURVE                  );
		plChord         = getIntegerPreference(prop,KEY_FMT_POLYLINE_ELLIPSE_CHORD
											   ,DFLT_FMT_POLYLINE_ELLIPSE_CHORD      );
		plPie           = getIntegerPreference(prop,KEY_FMT_POLYLINE_ELLIPSE_PIE_LINE
											   ,DFLT_FMT_POLYLINE_ELLIPSE_PIE_LINE   );
		plMultiCurve    = getIntegerPreference(prop,KEY_FMT_POLYLINE_POLYGON_MULTICURVE
											   ,DFLT_FMT_POLYLINE_POLYGON_MULTICURVE );
		plParallelogram = getIntegerPreference(prop,KEY_FMT_POLYLINE_PARALLELOGRAM
											   ,DFLT_FMT_POLYLINE_PARALLELOGRAM      );
		return 0;
	}

	/**
	 * �crit une pr�f�rence � valeur enti�re dans l'objet <code>prop</code>.
	 * @param prop  objet <code>Properties</code> dans lequel on �crit la pr�f�rence.
	 * @param key   identificateur de la pr�f�rences.
	 * @param value valeur �crite.
	 * @since jPicEdt 1.6
	 */
	private void setIntegerPreference(Properties prop,String key,int value){
		prop.setProperty(key,String.valueOf(value));
	}

	/**
	 * Stocke dans l'objet <code>prop</code> les propri�t�s r�sident dans this (op�ration inverse de {@link
	 * #load(Properties prop) load}).
	 * @since jPicEdt 1.6
	 */
	 public void store(Properties prop){
		 prop.setProperty(KEY_FMT_DXF_VERSION, dxfVersion.getDXFVersionId());
		 setIntegerPreference(prop,KEY_FMT_ELLIPSE_OUTLINE_CIRCLE
							  ,elolCircle     );
		 setIntegerPreference(prop,KEY_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE
							  ,elolEllipse    );
		 setIntegerPreference(prop,KEY_FMT_MULTICURVE
							  ,curveMulticurve);
		 setIntegerPreference(prop,KEY_FMT_POLYLINE_ELLIPSE_CHORD
							  ,plChord        );
		 setIntegerPreference(prop,KEY_FMT_POLYLINE_ELLIPSE_PIE_LINE
							  ,plPie          );
		 setIntegerPreference(prop,KEY_FMT_POLYLINE_POLYGON_MULTICURVE
							  ,plMultiCurve   );
		 setIntegerPreference(prop,KEY_FMT_POLYLINE_PARALLELOGRAM
							  ,plParallelogram);
	}

}

/// DXFCustomProperties.java ends here
