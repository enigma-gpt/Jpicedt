// ColorFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ColorFormatter.java,v 1.3 2013/03/27 07:08:07 vincentb1 Exp $
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
package jpicedt.format.output.util;
import static jpicedt.Log.debug;
import java.lang.String;
import java.util.BitSet;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Arrays;

/**
 * La classe <code>ColorFormatter</code> contient des méthodes statiques pour
 * l'encodage des <code>Color</code> en TeX selon les codes prédéfinies dans
 * <code>pstricks</code> ou <code>xcolor</code> de base, ou
 * <code>xcolor</code> avec les nomS <code>SVG</code>, ou encore avec les noms
 * <code>X11</code>.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @version $Id: ColorFormatter.java,v 1.3 2013/03/27 07:08:07 vincentb1 Exp $
 * @since jPicEdt 1.6
 */
public class ColorFormatter{

	public enum ColorEncoding{ PSTRICKS(0), XCOLOR(1), SVG(2), X11(3);
		private int index;
		public int value(){ return index; }
		public static String[] keys(){
			ColorEncoding[] v = values();
			String[] ret = new String[v.length];
			int i =0;
			for(ColorEncoding e: v)
				ret[i++] = e.toString();
			return ret;
		}
		public static String[] nonPstricksKeys(){
			ColorEncoding[] v = values();
			String[] ret = new String[v.length-1];
			int i =0;
			for(ColorEncoding e: v)
				if(e != PSTRICKS)
					ret[i++] = e.toString();
			return ret;
		}
		public static ColorEncoding nonPstricksEnumOf(int i){
			ColorEncoding[] v=values();
			if(i >= PSTRICKS.index)
				return v[i+1];
			else
				return v[i];
		}
		public static ColorEncoding enumOf(int i){
			ColorEncoding[] v=values();
			return v[i];
		}
		public static ColorEncoding enumOf(String s){
			ColorEncoding[] v=values();
			for(ColorEncoding x:v){
				if(x.toString().equals(s))
					return x;
			}
			return null;
		}

		ColorEncoding(int index){ this.index = index; }
	}





	private static final HashMap<Color,Vector<ColorCode<String>>> mapColor
		= new HashMap<Color,Vector<ColorCode<String>>>(20);
	private static final HashMap<String,Vector<ColorCode<Color>>> mapName
		= new HashMap<String,Vector<ColorCode<Color>>>(20);

	private static boolean done = false;


	private static void addCE(Color c,Vector<ColorCode<String>> v){
		Vector<ColorCode<String>> v1 = mapColor.get(c);
 		if(v1 != null){
            boolean v1ToV = false;
			for(ColorCode<String> cc : v){
				boolean duplicate = false;
				for(ColorCode<String> cc1 : v1){
					if(cc1.getCode().equals(cc.getCode())){
						cc1.getEncodings().or(cc.getEncodings());
						duplicate = true;
						v1ToV = true;
						break;
					}
				}
				if(!duplicate){
					v1.add(v1.size(),cc);
					v1ToV = true;
				}
			}
			if(v1ToV)
				v = v1;
		}
		mapColor.put(c,v);
	}

	private static void addCE3(Color c,String s){
		Vector<ColorCode<String>> v = new Vector<ColorCode<String>>(1,1);
		ColorCode<String> cc = new ColorCode<String> (s,ColorEncoding.PSTRICKS,ColorEncoding.XCOLOR);
 		v.add(0, cc);
		addCE(c,v);
	}
	private static void addCE2(Color c,String s){
		Vector<ColorCode<String>> v = new Vector<ColorCode<String>>(1,1);
		v.add(0, new ColorCode<String> (s,ColorEncoding.XCOLOR));
		addCE(c,v);
	}
	private static void addCE2(float r,float g, float b,String s){
		addCE2(new Color(r,g,b),s);
	}

	private static void addCE4(Color c,String s){
		Vector<ColorCode<String>> v = new Vector<ColorCode<String>>(1,1);
		v.add(0, new ColorCode<String> (s,ColorEncoding.SVG));
		addCE(c,v);
	}
	private static void addCE4(float r,float g, float b,String s){
		addCE4(new Color(r,g,b),s);
	}

	private static void addCE6(Color c,String s2,String s4){
		Vector<ColorCode<String>> v = new Vector<ColorCode<String>>(2,1);
		v.add(0, new ColorCode<String> (s2,ColorEncoding.XCOLOR));
		v.add(1, new ColorCode<String> (s4,ColorEncoding.SVG));
		addCE(c,v);
	}

	private static void addCE6(float r,float g,float b,String s2,String s4){
		addCE6(new Color(r,g,b),s2,s4);
	}

	private static void addCE7(Color c,String s3,String s4){
		Vector<ColorCode<String>> v = new Vector<ColorCode<String>>(2,1);
		v.add(0, new ColorCode<String> (s3,ColorEncoding.PSTRICKS, ColorEncoding.XCOLOR));
		v.add(1, new ColorCode<String> (s4,ColorEncoding.SVG));
		addCE(c,v);
	}

	private static void addCE8(float r,float g, float b,String s){
		Vector<ColorCode<String>> v = new Vector<ColorCode<String>>(1,1);
		v.add(0, new ColorCode<String> (s,ColorEncoding.X11));
		Color c = new Color(r,g,b);
		addCE(c,v);
	}

	private static void addCE11(Color c,String s3,String s8){
		Vector<ColorCode<String>> v = new Vector<ColorCode<String>>(2,1);
		v.add(0, new ColorCode<String> (s3,ColorEncoding.PSTRICKS, ColorEncoding.XCOLOR));
		v.add(1, new ColorCode<String> (s8,ColorEncoding.X11));
		addCE(c,v);
	}

	private static void addCE12(Color c,String s4,String s8){
		Vector<ColorCode<String>> v = new Vector<ColorCode<String>>(2,1);
		v.add(0, new ColorCode<String> (s4,ColorEncoding.SVG));
		v.add(1, new ColorCode<String> (s8,ColorEncoding.X11));
		addCE(c,v);
	}

	private static void addCE12(float r,float g,float b,String s4,String s8){
		addCE12(new Color(r,g,b),s4,s8);
	}


	private static void addCE15(Color c,String s3,String s4,String s8){
		Vector<ColorCode<String>> v = new Vector<ColorCode<String>>(3,1);
		v.add(0, new ColorCode<String> (s3,ColorEncoding.PSTRICKS, ColorEncoding.XCOLOR));
		v.add(1, new ColorCode<String> (s4,ColorEncoding.SVG));
		v.add(2, new ColorCode<String> (s8,ColorEncoding.X11));
		addCE(c,v);
	}


	private static void init(){
		if(done) return;
		done = true;

		addCE15(Color.red, "red","Red","Red1");

		addCE15(Color.green,       "green","Green","Green0");

		addCE3 (Color.blue,        "blue");

		addCE2(.75f,.5f,.25f,      "brown");
		addCE2(.75f,1f,0f,         "lime");

		addCE2(Color.orange,       "orange");
		addCE2(Color.pink,         "pink");
		addCE2(.75f,0f,.25f,      "purple");

		addCE6(0f,.5f,.5f,        "teal","Teal");

		addCE6(.5f,0f,.5f,        "violet","Purple");

		addCE2(.75f,0f,.25f,      "purple");

		addCE2(Color.cyan,        "cyan");

		addCE15(Color.magenta, "magenta","Magenta","Magenta1");
		addCE4(Color.magenta,"Fuchsia");

		addCE15(Color.yellow, "yellow","Yellow","Yellow1");

		addCE6(.5f,.5f,0f,        "olive","Olive");

		addCE6(Color.black,       "black","Black");

		addCE6(Color.gray,        "gray","Gray");
		addCE4(.5f,.5f,.5f,"Grey");

		addCE4(.5f,0f,0f,"Maroon");

		addCE8(1f,.25f,.25f,"Brown1");

		addCE2(Color.darkGray,    "darkgray");

		addCE2(Color.lightGray,   "lightgray");

		addCE6(Color.white,       "white","White");

		addCE12(0f,1f,0f,"Lime","Green1");

		addCE4(0f,1f,1f,"Aqua");
		addCE12(0f,1f,1f,"Cyan","Cyan1");


		addCE8(.064f,.305f,.545f,"DodgerBlue4");
		addCE8(.094f,.455f,.804f,"DodgerBlue3");
		addCE4(.098f,.098f,.44f,"MidnightBlue");
		addCE12(.116f,.565f,1f,"DodgerBlue","DodgerBlue1");
		addCE8(.11f,.525f,.932f,"DodgerBlue2");
		addCE4(.125f,.698f,.668f,"LightSeaGreen");
		addCE4(.132f,.545f,.132f,"ForestGreen");
		addCE8(.152f,.25f,.545f,"RoyalBlue4");
		addCE4(.185f,.31f,.31f,"DarkSlateGray");
		addCE4(.185f,.31f,.31f,"DarkSlateGrey");
		addCE12(.18f,.545f,.34f,"SeaGreen","SeaGreen4");
		addCE4(.196f,.804f,.196f,"LimeGreen");
		addCE8(.21f,.392f,.545f,"SteelBlue4");
		addCE8(.228f,.372f,.804f,"RoyalBlue3");
		addCE4(.235f,.7f,.444f,"MediumSeaGreen");
		addCE4(.255f,.41f,.884f,"RoyalBlue");
		addCE4(.25f,.88f,.815f,"Turquoise");
		addCE8(.264f,.43f,.932f,"RoyalBlue2");
		addCE8(.264f,.804f,.5f,"SeaGreen3");
		addCE4(.275f,.51f,.705f,"SteelBlue");
		addCE8(.27f,.545f,.455f,"Aquamarine4");
		addCE8(.27f,.545f,0f,"Chartreuse4");
		addCE4(.284f,.24f,.545f,"DarkSlateBlue");
		addCE8(.284f,.464f,1f,"RoyalBlue1");
		addCE4(.284f,.82f,.8f,"MediumTurquoise");
		addCE8(.28f,.235f,.545f,"SlateBlue4");
		addCE4(.294f,0f,.51f,"Indigo");
		addCE8(.29f,.44f,.545f,"SkyBlue4");
		addCE8(.305f,.932f,.58f,"SeaGreen2");
		addCE8(.31f,.58f,.804f,"SteelBlue3");
		addCE8(.325f,.525f,.545f,"CadetBlue4");
		addCE8(.32f,.545f,.545f,"DarkSlateGray4");
		addCE8(.332f,.1f,.545f,"Purple4");
		addCE4(.332f,.42f,.185f,"DarkOliveGreen");
		addCE8(.33f,.545f,.33f,"PaleGreen4");
		addCE8(.33f,1f,.624f,"SeaGreen1");
		addCE8(.365f,.28f,.545f,"MediumPurple4");
		addCE8(.36f,.675f,.932f,"SteelBlue2");
		addCE4(.372f,.62f,.628f,"CadetBlue");
		addCE8(.376f,.484f,.545f,"LightSkyBlue4");
		addCE4(.392f,.585f,.93f,"CornflowerBlue");
		addCE8(.39f,.72f,1f,"SteelBlue1");
		addCE8(.408f,.132f,.545f,"DarkOrchid4");
		addCE8(.408f,.512f,.545f,"LightBlue4");
		addCE4(.415f,.352f,.804f,"SlateBlue");
		addCE8(.41f,.35f,.804f,"SlateBlue3");
		addCE4(.41f,.41f,.41f,"DimGray");
		addCE4(.41f,.41f,.41f,"DimGrey");
		addCE8(.41f,.545f,.132f,"OliveDrab4");
		addCE8(.41f,.545f,.41f,"DarkSeaGreen4");
		addCE8(.424f,.484f,.545f,"SlateGray4");
		addCE8(.424f,.65f,.804f,"SkyBlue3");
		addCE4(.42f,.556f,.136f,"OliveDrab");
		addCE8(.43f,.484f,.545f,"LightSteelBlue4");
		addCE8(.43f,.545f,.24f,"DarkOliveGreen4");
		addCE4(.44f,.5f,.565f,"SlateGray");
		addCE4(.44f,.5f,.565f,"SlateGrey");
		addCE8(.464f,.932f,.776f,"Aquamarine2");
		addCE8(.464f,.932f,0f,"Chartreuse2");
		addCE4(.468f,.532f,.6f,"LightSlateGray");
		addCE4(.468f,.532f,.6f,"LightSlateGrey");
		addCE8(.475f,.804f,.804f,"DarkSlateGray3");
		addCE4(.484f,.408f,.932f,"MediumSlateBlue");
		addCE8(.488f,.804f,.488f,"PaleGreen3");
		addCE4(.488f,.99f,0f,"LawnGreen");
		addCE8(.48f,.215f,.545f,"MediumOrchid4");
		addCE8(.48f,.404f,.932f,"SlateBlue2");
		addCE8(.48f,.545f,.545f,"LightCyan4");
		addCE8(.48f,.772f,.804f,"CadetBlue3");
		addCE8(.494f,.752f,.932f,"SkyBlue2");
		addCE12(.498f,1f,.83f,"Aquamarine","Aquamarine1");
		addCE12(.498f,1f,0f,"Chartreuse","Chartreuse1");
		addCE8(.49f,.15f,.804f,"Purple3");
		addCE8(.4f,.545f,.545f,"PaleTurquoise4");
		addCE12(.4f,.804f,.668f,"MediumAquamarine","Aquamarine3");
		addCE8(.4f,.804f,0f,"Chartreuse3");
		addCE8(.512f,.435f,1f,"SlateBlue1");
		addCE8(.512f,.545f,.512f,"Honeydew4");
		addCE8(.512f,.545f,.545f,"Azure4");
		addCE4(.518f,.44f,1f,"LightSlateBlue");
		addCE8(.536f,.408f,.804f,"MediumPurple3");
		addCE4(.53f,.808f,.92f,"SkyBlue");
		addCE4(.53f,.808f,.98f,"LightSkyBlue");
		addCE8(.53f,.808f,1f,"SkyBlue1");
		addCE8(.545f,.04f,.312f,"DeepPink4");
		addCE8(.545f,.11f,.385f,"Maroon4");
		addCE8(.545f,.132f,.32f,"VioletRed4");
		addCE8(.545f,.136f,.136f,"Brown4");
		addCE8(.545f,.145f,0f,"OrangeRed4");
		addCE8(.545f,.1f,.1f,"Firebrick4");
		addCE8(.545f,.21f,.15f,"Tomato4");
		addCE8(.545f,.228f,.228f,"IndianRed4");
		addCE8(.545f,.228f,.385f,"HotPink4");
		addCE8(.545f,.244f,.185f,"Coral4");
		addCE12(.545f,.27f,.075f,"SaddleBrown","Chocolate4");
		addCE8(.545f,.27f,0f,"DarkOrange4");
		addCE8(.545f,.28f,.15f,"Sienna4");
		addCE8(.545f,.28f,.365f,"PaleVioletRed4");
		addCE8(.545f,.28f,.536f,"Orchid4");
		addCE8(.545f,.298f,.224f,"Salmon4");
		addCE8(.545f,.34f,.26f,"LightSalmon4");
		addCE8(.545f,.352f,.17f,"Tan4");
		addCE8(.545f,.352f,0f,"Orange4");
		addCE8(.545f,.372f,.396f,"LightPink4");
		addCE8(.545f,.396f,.03f,"DarkGoldenrod4");
		addCE8(.545f,.39f,.424f,"Pink4");
		addCE8(.545f,.41f,.08f,"Goldenrod4");
		addCE8(.545f,.41f,.41f,"RosyBrown4");
		addCE8(.545f,.45f,.332f,"Burlywood4");
		addCE8(.545f,.468f,.396f,"PeachPuff4");
		addCE8(.545f,.46f,0f,"Gold4");
		addCE8(.545f,.475f,.37f,"NavajoWhite4");
		addCE8(.545f,.484f,.545f,"Thistle4");
		addCE8(.545f,.494f,.4f,"Wheat4");
		addCE8(.545f,.49f,.42f,"Bisque4");
		addCE8(.545f,.49f,.484f,"MistyRose4");
		addCE8(.545f,.4f,.545f,"Plum4");
		addCE8(.545f,.505f,.298f,"LightGoldenrod4");
		addCE8(.545f,.512f,.47f,"AntiqueWhite4");
		addCE8(.545f,.512f,.525f,"LavenderBlush4");
		addCE8(.545f,.525f,.305f,"Khaki4");
		addCE8(.545f,.525f,.51f,"Seashell4");
		addCE8(.545f,.532f,.47f,"Cornsilk4");
		addCE8(.545f,.536f,.44f,"LemonChiffon4");
		addCE8(.545f,.536f,.536f,"Snow4");
		addCE8(.545f,.545f,.48f,"LightYellow4");
		addCE8(.545f,.545f,.512f,"Ivory4");
		addCE8(.545f,.545f,0f,"Yellow4");
		addCE12(.545f,0f,.545f,"DarkMagenta","Magenta4");
		addCE12(.545f,0f,0f,"DarkRed","Red4");
		addCE4(.54f,.17f,.888f,"BlueViolet");
		addCE8(.552f,.712f,.804f,"LightSkyBlue3");
		addCE8(.552f,.932f,.932f,"DarkSlateGray2");
		addCE8(.556f,.898f,.932f,"CadetBlue2");
		addCE12(.565f,.932f,.565f,"LightGreen","PaleGreen2");
		addCE4(.56f,.736f,.56f,"DarkSeaGreen");
		addCE4(.576f,.44f,.86f,"MediumPurple");
		addCE8(.57f,.172f,.932f,"Purple2");
		addCE4(.58f,0f,.828f,"DarkViolet");
		addCE8(.592f,1f,1f,"DarkSlateGray1");
		addCE8(.596f,.96f,1f,"CadetBlue1");
		addCE4(.596f,.985f,.596f,"PaleGreen");
		addCE8(.59f,.804f,.804f,"PaleTurquoise3");
		addCE8(.604f,.196f,.804f,"DarkOrchid3");
		addCE8(.604f,.752f,.804f,"LightBlue3");
		addCE12(.604f,.804f,.196f,"YellowGreen","OliveDrab3");
		addCE8(.604f,1f,.604f,"PaleGreen1");
		addCE8(.608f,.19f,1f,"Purple1");
		addCE8(.608f,.804f,.608f,"DarkSeaGreen3");
		addCE8(.624f,.475f,.932f,"MediumPurple2");
		addCE8(.624f,.712f,.804f,"SlateGray3");
		addCE8(.628f,.125f,.94f,"Purple0");
		addCE4(.628f,.32f,.176f,"Sienna");
		addCE8(.635f,.71f,.804f,"LightSteelBlue3");
		addCE8(.635f,.804f,.352f,"DarkOliveGreen3");
		addCE8(.644f,.828f,.932f,"LightSkyBlue2");
		addCE4(.648f,.165f,.165f,"Brown");
		addCE4(.664f,.664f,.664f,"DarkGray");
		addCE4(.664f,.664f,.664f,"DarkGrey");
		addCE8(.67f,.51f,1f,"MediumPurple1");
		addCE8(.684f,.932f,.932f,"PaleTurquoise2");
		addCE4(.688f,.932f,.932f,"PaleTurquoise");
		addCE4(.68f,.848f,.9f,"LightBlue");
		addCE4(.68f,1f,.185f,"GreenYellow");
		addCE4(.698f,.132f,.132f,"FireBrick");
		addCE8(.698f,.228f,.932f,"DarkOrchid2");
		addCE8(.698f,.875f,.932f,"LightBlue2");
		addCE8(.69f,.19f,.376f,"Maroon0");
		addCE4(.69f,.77f,.87f,"LightSteelBlue");
		addCE8(.69f,.888f,1f,"LightSkyBlue1");
		addCE4(.69f,.88f,.9f,"PowderBlue");
		addCE4(.6f,.196f,.8f,"DarkOrchid");
		addCE8(.705f,.32f,.804f,"MediumOrchid3");
		addCE8(.705f,.804f,.804f,"LightCyan3");
		addCE8(.705f,.932f,.705f,"DarkSeaGreen2");
		addCE8(.725f,.828f,.932f,"SlateGray2");
		addCE4(.72f,.525f,.044f,"DarkGoldenrod");
		addCE8(.732f,1f,1f,"PaleTurquoise1");
		addCE4(.736f,.56f,.56f,"RosyBrown");
		addCE8(.736f,.824f,.932f,"LightSteelBlue2");
		addCE8(.736f,.932f,.408f,"DarkOliveGreen2");
		addCE4(.73f,.332f,.828f,"MediumOrchid");
		addCE8(.745f,.745f,.745f,"Gray0");
		addCE8(.745f,.745f,.745f,"Grey0");
		addCE4(.74f,.716f,.42f,"DarkKhaki");
		addCE4(.752f,.752f,.752f,"Silver");
		addCE8(.752f,1f,.244f,"OliveDrab1");
		addCE8(.756f,.804f,.756f,"Honeydew3");
		addCE8(.756f,.804f,.804f,"Azure3");
		addCE8(.756f,1f,.756f,"DarkSeaGreen1");
		addCE8(.75f,.244f,1f,"DarkOrchid1");
		addCE8(.75f,.936f,1f,"LightBlue1");
		addCE8(.776f,.888f,1f,"SlateGray1");
		addCE4(.78f,.084f,.52f,"MediumVioletRed");
		addCE8(.792f,.884f,1f,"LightSteelBlue1");
		addCE8(.792f,1f,.44f,"DarkOliveGreen1");
		addCE8(.7f,.932f,.228f,"OliveDrab2");
		addCE8(.804f,.064f,.464f,"DeepPink3");
		addCE8(.804f,.15f,.15f,"Firebrick3");
		addCE8(.804f,.16f,.565f,"Maroon3");
		addCE8(.804f,.196f,.47f,"VioletRed3");
		addCE8(.804f,.215f,0f,"OrangeRed3");
		addCE8(.804f,.2f,.2f,"Brown3");
		addCE8(.804f,.31f,.224f,"Tomato3");
		addCE8(.804f,.332f,.332f,"IndianRed3");
		addCE8(.804f,.356f,.27f,"Coral3");
		addCE4(.804f,.36f,.36f,"IndianRed");
		addCE8(.804f,.376f,.565f,"HotPink3");
		addCE8(.804f,.408f,.224f,"Sienna3");
		addCE8(.804f,.408f,.536f,"PaleVioletRed3");
		addCE8(.804f,.41f,.79f,"Orchid3");
		addCE8(.804f,.44f,.33f,"Salmon3");
		addCE8(.804f,.4f,.112f,"Chocolate3");
		addCE8(.804f,.4f,0f,"DarkOrange3");
		addCE8(.804f,.505f,.385f,"LightSalmon3");
		addCE12(.804f,.52f,.248f,"Peru","Tan3");
		addCE8(.804f,.52f,0f,"Orange3");
		addCE8(.804f,.55f,.585f,"LightPink3");
		addCE8(.804f,.57f,.62f,"Pink3");
		addCE8(.804f,.585f,.048f,"DarkGoldenrod3");
		addCE8(.804f,.59f,.804f,"Plum3");
		addCE8(.804f,.608f,.112f,"Goldenrod3");
		addCE8(.804f,.608f,.608f,"RosyBrown3");
		addCE8(.804f,.668f,.49f,"Burlywood3");
		addCE8(.804f,.688f,.585f,"PeachPuff3");
		addCE8(.804f,.68f,0f,"Gold3");
		addCE8(.804f,.716f,.62f,"Bisque3");
		addCE8(.804f,.716f,.71f,"MistyRose3");
		addCE8(.804f,.71f,.804f,"Thistle3");
		addCE8(.804f,.73f,.59f,"Wheat3");
		addCE8(.804f,.745f,.44f,"LightGoldenrod3");
		addCE8(.804f,.752f,.69f,"AntiqueWhite3");
		addCE8(.804f,.756f,.772f,"LavenderBlush3");
		addCE8(.804f,.772f,.75f,"Seashell3");
		addCE8(.804f,.776f,.45f,"Khaki3");
		addCE8(.804f,.785f,.694f,"Cornsilk3");
		addCE8(.804f,.79f,.648f,"LemonChiffon3");
		addCE8(.804f,.79f,.79f,"Snow3");
		addCE8(.804f,.7f,.545f,"NavajoWhite3");
		addCE8(.804f,.804f,.705f,"LightYellow3");
		addCE8(.804f,.804f,.756f,"Ivory3");
		addCE8(.804f,.804f,0f,"Yellow3");
		addCE8(.804f,0f,.804f,"Magenta3");
		addCE8(.804f,0f,0f,"Red3");
		addCE4(.816f,.125f,.565f,"VioletRed");
		addCE4(.824f,.41f,.116f,"Chocolate");
		addCE4(.824f,.705f,.55f,"Tan");
		addCE4(.828f,.828f,.828f,"LightGray");
		addCE4(.828f,.828f,.828f,"LightGrey");
		addCE8(.82f,.372f,.932f,"MediumOrchid2");
		addCE8(.82f,.932f,.932f,"LightCyan2");
		addCE4(.848f,.75f,.848f,"Thistle");
		addCE4(.855f,.44f,.84f,"Orchid");
		addCE4(.855f,.648f,.125f,"Goldenrod");
		addCE4(.864f,.08f,.235f,"Crimson");
		addCE4(.864f,.864f,.864f,"Gainsboro");
		addCE4(.868f,.628f,.868f,"Plum");
		addCE4(.86f,.44f,.576f,"PaleVioletRed");
		addCE4(.87f,.72f,.53f,"BurlyWood");
		addCE8(.88f,.4f,1f,"MediumOrchid1");
		addCE8(.88f,.932f,.88f,"Honeydew2");
		addCE8(.88f,.932f,.932f,"Azure2");
		addCE12(.88f,1f,1f,"LightCyan","LightCyan1");
		addCE4(.912f,.59f,.48f,"DarkSalmon");
		addCE8(.932f,.07f,.536f,"DeepPink2");
		addCE8(.932f,.172f,.172f,"Firebrick2");
		addCE8(.932f,.19f,.655f,"Maroon2");
		addCE8(.932f,.228f,.55f,"VioletRed2");
		addCE8(.932f,.23f,.23f,"Brown2");
		addCE8(.932f,.25f,0f,"OrangeRed2");
		addCE8(.932f,.36f,.26f,"Tomato2");
		addCE8(.932f,.39f,.39f,"IndianRed2");
		addCE8(.932f,.415f,.312f,"Coral2");
		addCE8(.932f,.415f,.655f,"HotPink2");
		addCE8(.932f,.464f,.13f,"Chocolate2");
		addCE8(.932f,.464f,0f,"DarkOrange2");
		addCE8(.932f,.475f,.26f,"Sienna2");
		addCE8(.932f,.475f,.624f,"PaleVioletRed2");
		addCE8(.932f,.48f,.912f,"Orchid2");
		addCE8(.932f,.51f,.385f,"Salmon2");
		addCE4(.932f,.51f,.932f,"Violet");
		addCE8(.932f,.585f,.448f,"LightSalmon2");
		addCE8(.932f,.604f,.288f,"Tan2");
		addCE8(.932f,.604f,0f,"Orange2");
		addCE8(.932f,.635f,.68f,"LightPink2");
		addCE8(.932f,.664f,.72f,"Pink2");
		addCE8(.932f,.684f,.932f,"Plum2");
		addCE8(.932f,.68f,.055f,"DarkGoldenrod2");
		addCE8(.932f,.705f,.132f,"Goldenrod2");
		addCE8(.932f,.705f,.705f,"RosyBrown2");
		addCE8(.932f,.772f,.57f,"Burlywood2");
		addCE8(.932f,.796f,.68f,"PeachPuff2");
		addCE8(.932f,.79f,0f,"Gold2");
		addCE8(.932f,.81f,.63f,"NavajoWhite2");
		addCE8(.932f,.824f,.932f,"Thistle2");
		addCE8(.932f,.835f,.716f,"Bisque2");
		addCE8(.932f,.835f,.824f,"MistyRose2");
		addCE8(.932f,.848f,.684f,"Wheat2");
		addCE8(.932f,.864f,.51f,"LightGoldenrod2");
		addCE8(.932f,.875f,.8f,"AntiqueWhite2");
		addCE8(.932f,.88f,.898f,"LavenderBlush2");
		addCE8(.932f,.898f,.87f,"Seashell2");
		addCE8(.932f,.912f,.75f,"LemonChiffon2");
		addCE8(.932f,.912f,.912f,"Snow2");
		addCE4(.932f,.91f,.668f,"PaleGoldenrod");
		addCE8(.932f,.91f,.804f,"Cornsilk2");
		addCE8(.932f,.932f,.82f,"LightYellow2");
		addCE8(.932f,.932f,.88f,"Ivory2");
		addCE8(.932f,.932f,0f,"Yellow2");
		addCE8(.932f,.9f,.52f,"Khaki2");
		addCE8(.932f,0f,.932f,"Magenta2");
		addCE8(.932f,0f,0f,"Red2");
		addCE4(.933f,.867f,.51f,"LightGoldenrod");
		addCE4(.94f,.5f,.5f,"LightCoral");
		addCE4(.94f,.972f,1f,"AliceBlue");
		addCE4(.94f,.9f,.55f,"Khaki");
		addCE12(.94f,1f,.94f,"Honeydew","Honeydew1");
		addCE12(.94f,1f,1f,"Azure","Azure1");
		addCE4(.956f,.644f,.376f,"SandyBrown");
		addCE4(.96f,.87f,.7f,"Wheat");
		addCE4(.96f,.96f,.864f,"Beige");
		addCE4(.96f,.96f,.96f,"WhiteSmoke");
		addCE4(.96f,1f,.98f,"MintCream");
		addCE4(.972f,.972f,1f,"GhostWhite");
		addCE4(.98f,.5f,.448f,"Salmon");
		addCE4(.98f,.92f,.844f,"AntiqueWhite");
		addCE4(.98f,.94f,.9f,"Linen");
		addCE4(.98f,.98f,.824f,"LightGoldenrodYellow");
		addCE4(.992f,.96f,.9f,"OldLace");
		addCE4(.9f,.9f,.98f,"Lavender");
		addCE4(0f,.392f,0f,"DarkGreen");
		addCE8(0f,.408f,.545f,"DeepSkyBlue4");
		addCE8(0f,.525f,.545f,"Turquoise4");
		addCE8(0f,.545f,.27f,"SpringGreen4");
		addCE12(0f,.545f,.545f,"DarkCyan","Cyan4");
		addCE8(0f,.545f,0f,"Green4");
		addCE8(0f,.604f,.804f,"DeepSkyBlue3");
		addCE8(0f,.698f,.932f,"DeepSkyBlue2");
		addCE12(0f,.75f,1f,"DeepSkyBlue","DeepSkyBlue1");
		addCE8(0f,.772f,.804f,"Turquoise3");
		addCE8(0f,.804f,.4f,"SpringGreen3");
		addCE8(0f,.804f,.804f,"Cyan3");
		addCE8(0f,.804f,0f,"Green3");
		addCE4(0f,.808f,.82f,"DarkTurquoise");
		addCE8(0f,.898f,.932f,"Turquoise2");
		addCE8(0f,.932f,.464f,"SpringGreen2");
		addCE8(0f,.932f,.932f,"Cyan2");
		addCE8(0f,.932f,0f,"Green2");
		addCE8(0f,.96f,1f,"Turquoise1");
		addCE4(0f,.98f,.604f,"MediumSpringGreen");
		addCE12(0f,0f,.545f,"DarkBlue","Blue4");
		addCE4(0f,0f,.5f,"Navy");
		addCE4(0f,0f,.5f,"NavyBlue");
		addCE12(0f,0f,.804f,"MediumBlue","Blue3");
		addCE8(0f,0f,.932f,"Blue2");
		addCE12(0f,0f,1f,"Blue","Blue1");
		addCE12(0f,1f,.498f,"SpringGreen","SpringGreen1");
		addCE12(1f,.08f,.576f,"DeepPink","DeepPink1");
		addCE8(1f,.19f,.19f,"Firebrick1");
		addCE8(1f,.204f,.7f,"Maroon1");
		addCE8(1f,.244f,.59f,"VioletRed1");
		addCE12(1f,.27f,0f,"OrangeRed","OrangeRed1");
		addCE12(1f,.39f,.28f,"Tomato","Tomato1");
		addCE8(1f,.415f,.415f,"IndianRed1");
		addCE4(1f,.41f,.705f,"HotPink");
		addCE8(1f,.43f,.705f,"HotPink1");
		addCE8(1f,.448f,.336f,"Coral1");
		addCE8(1f,.498f,.14f,"Chocolate1");
		addCE4(1f,.498f,.312f,"Coral");
		addCE8(1f,.498f,0f,"DarkOrange1");
		addCE8(1f,.512f,.98f,"Orchid1");
		addCE8(1f,.51f,.28f,"Sienna1");
		addCE8(1f,.51f,.67f,"PaleVioletRed1");
		addCE8(1f,.55f,.41f,"Salmon1");
		addCE4(1f,.55f,0f,"DarkOrange");
		addCE12(1f,.628f,.48f,"LightSalmon","LightSalmon1");
		addCE8(1f,.648f,.31f,"Tan1");
		addCE12(1f,.648f,0f,"Orange","Orange1");
		addCE8(1f,.684f,.725f,"LightPink1");
		addCE4(1f,.712f,.756f,"LightPink");
		addCE8(1f,.71f,.772f,"Pink1");
		addCE8(1f,.725f,.06f,"DarkGoldenrod1");
		addCE8(1f,.732f,1f,"Plum1");
		addCE4(1f,.752f,.796f,"Pink");
		addCE8(1f,.756f,.145f,"Goldenrod1");
		addCE8(1f,.756f,.756f,"RosyBrown1");
		addCE8(1f,.828f,.608f,"Burlywood1");
		addCE12(1f,.844f,0f,"Gold","Gold1");
		addCE12(1f,.855f,.725f,"PeachPuff","PeachPuff1");
		addCE12(1f,.87f,.68f,"NavajoWhite","NavajoWhite1");
		addCE8(1f,.884f,1f,"Thistle1");
		addCE4(1f,.894f,.71f,"Moccasin");
		addCE12(1f,.894f,.77f,"Bisque","Bisque1");
		addCE12(1f,.894f,.884f,"MistyRose","MistyRose1");
		addCE8(1f,.905f,.73f,"Wheat1");
		addCE8(1f,.925f,.545f,"LightGoldenrod1");
		addCE4(1f,.92f,.804f,"BlanchedAlmond");
		addCE4(1f,.936f,.835f,"PapayaWhip");
		addCE8(1f,.936f,.86f,"AntiqueWhite1");
		addCE12(1f,.94f,.96f,"LavenderBlush","LavenderBlush1");
		addCE8(1f,.965f,.56f,"Khaki1");
		addCE12(1f,.96f,.932f,"Seashell","Seashell1");
		addCE12(1f,.972f,.864f,"Cornsilk","Cornsilk1");
		addCE12(1f,.98f,.804f,"LemonChiffon","LemonChiffon1");
		addCE4(1f,.98f,.94f,"FloralWhite");
		addCE12(1f,.98f,.98f,"Snow","Snow1");
		addCE12(1f,1f,.88f,"LightYellow","LightYellow1");
		addCE12(1f,1f,.94f,"Ivory","Ivory1");

		// préparation du map inverse
		for(Map.Entry<Color,Vector<ColorCode<String>>> mec : mapColor.entrySet()){
			Vector<ColorCode<String>> v = mec.getValue();
			for(ColorCode<String> cc : v){
				String name = cc.getCode();
				Vector<ColorCode<Color>> colorCodeVector = mapName.get(name);
				ColorCode<Color> dualCc = new ColorCode<Color>(mec.getKey(),cc.getEncodings());
				if(colorCodeVector == null)
				{
					colorCodeVector = new Vector<ColorCode<Color>>(1,1);
					colorCodeVector.add(0,dualCc);
					mapName.put(name,colorCodeVector);
				}
				else
				{
					colorCodeVector.add(colorCodeVector.size(),dualCc);
				}
			}
		}
	}

	private ColorFormatter(){ init(); }

	public static String format(Color color, ColorEncoding encoding){
		Vector<ColorCode<String>> v = mapColor.get(color);
		if(v != null)
			for(ColorCode<String>  cc : v){
				String code = cc.getCode(encoding);
				if(code != null)
					return code;
			}

		return null;
	}

	public static Color parse(String name, BitSet encodings){
		Vector<ColorCode<Color>> v = mapName.get(name);
		if(v != null)
			for(ColorCode<Color>  cc : v){
				Color code = cc.getCode(encodings);
				if(code != null)
					return code;
			}

		return null;
	}


	public static NamedColor[] getColors(ColorEncoding encoding){
		init();
		ArrayList<NamedColor> al = new ArrayList<NamedColor>(mapColor.size());
		for(Map.Entry<Color,Vector<ColorCode<String>>> me : mapColor.entrySet()){
			for(ColorCode<String>  cc: me.getValue()){
				String name = cc.getCode(encoding);
				if(name != null){
					Color c = me.getKey();
					NamedColor nc = NamedColor.namedColor(name,c);
					al.add(nc);
				}
			}
		}

		NamedColor[] ret = new NamedColor[al.size()];
		ret = al.toArray(ret);
		Arrays.sort(ret,0,ret.length-1,NamedColor.COMPARE_BY_NAME);
		return ret;
	}
}


/// ColorFormatter.java ends here
