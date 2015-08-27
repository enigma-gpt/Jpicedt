// DXFTaggedValue.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: DXFTaggedValue.java,v 1.5 2013/03/27 07:11:35 vincentb1 Exp $
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

/// Installation:



/// Code:
package jpicedt.format.output.dxf;
import  java.lang.String;

/**
 * Couple étiquette/valeur. Dans la terminologie DXF (marque déposée) l'étiquette correspond au "code group
 * id". Les objets <code>DXFTaggedValue</code> sont destinés à être les éléments d'un patron de document DXF
 * (marque déposée).
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jpicedt 1.6
 */
public class DXFTaggedValue
{
	static public class DXFTaggedString implements DXFInformation{
		int tag;
		String value;
		/**
		 * @return le <i>group id code</i> de l'élément d'information DXF (marque déposée)
		 * représenté par this.
		 * @since jPicEdt 1.6
		 */
		public int getTag(){ return tag; }
		/**
		 * @return la valeur de l'élément d'information DXF (marque déposée) représenté par this.
		 * @since jPicEdt 1.6
		 */
		public String getValue(){ return value; }

		/**
		 * @since jPicEdt 1.6
		 */
		public DXFTaggedString(int tag,String value){
			this.tag = tag;
			this.value = value;
		}

		/**
		 * @since jPicEdt 1.6
		 */
		public void format(DXFStringBuffer buf,DXFInfoFormatter infoFormatter){
			buf.tagVal(tag, value);
		}
	};

	static public class DXFTaggedInteger implements DXFInformation{
		int tag;
		int value;
		/**
		 * @return le <i>group id code</i> de l'élément d'information DXF (marque déposée)
		 * représenté par this.
		 * @since jPicEdt 1.6
		 */
		public int getTag(){ return tag; }
		/**
		 * @return la valeur de l'élément d'information DXF (marque déposée) représenté par this.
		 * @since jPicEdt 1.6
		 */
		public int getValue(){ return value; }

		/**
		 * @since jPicEdt 1.6
		 */
		public DXFTaggedInteger(int tag,int value){
			this.tag = tag;
			this.value = value;
		}

		/**
		 * @since jPicEdt 1.6
		 */
		public void format(DXFStringBuffer buf,DXFInfoFormatter infoFormatter){
			buf.tagVal(tag, value);
		}
	};

	static public class DXFTaggedDouble implements DXFInformation{
		int tag;
		double value;
		/**
		 * @return le <i>group id code</i> de l'élément d'information DXF (marque déposée)
		 * représenté par this.
		 * @since jPicEdt 1.6
		 */
		public int getTag(){ return tag; }
		/**
		 * @return la valeur de l'élément d'information DXF (marque déposée) représenté par this.
		 * @since jPicEdt 1.6
		 */
		public double getValue(){ return value; }

		/**
		 * @since jPicEdt 1.6
		 */
		public DXFTaggedDouble(int tag, double value){
			this.tag = tag;
			this.value = value;
		}

		/**
		 * @since jPicEdt 1.6
		 */
		public void format(DXFStringBuffer buf,DXFInfoFormatter infoFormatter){
			buf.tagVal(tag, value);
		}
	};

	static public class DXFExtMinFormatter implements DXFInformation{
		/**
		 * @since jPicEdt 1.6
		 */
		public void format(DXFStringBuffer buf,DXFInfoFormatter infoFormatter){
			for(DXFInformation i : infoFormatter.getExtMin())
				i.format(buf, infoFormatter);
		}
	}

	static public class DXFExtMaxFormatter implements DXFInformation{
		/**
		 * @since jPicEdt 1.6
		 */
		public void format(DXFStringBuffer buf,DXFInfoFormatter infoFormatter){
			for(DXFInformation i : infoFormatter.getExtMax())
				i.format(buf, infoFormatter);
		}
	}


	static public class DXFEntitiesFormatter implements DXFInformation{
		/**
		 * @since jPicEdt 1.6
		 */
		public void format(DXFStringBuffer buf,DXFInfoFormatter infoFormatter){
			buf.append(infoFormatter.getEntities().toString());
		}
	}

}
/// DXFTaggedValue.java ends here
