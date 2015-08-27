// DXFTaggedValue.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFTaggedValue.java,v 1.5 2013/03/27 07:11:35 vincentb1 Exp $
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
import  java.lang.String;

/**
 * Couple �tiquette/valeur. Dans la terminologie DXF (marque d�pos�e) l'�tiquette correspond au "code group
 * id". Les objets <code>DXFTaggedValue</code> sont destin�s � �tre les �l�ments d'un patron de document DXF
 * (marque d�pos�e).
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jpicedt 1.6
 */
public class DXFTaggedValue
{
	static public class DXFTaggedString implements DXFInformation{
		int tag;
		String value;
		/**
		 * @return le <i>group id code</i> de l'�l�ment d'information DXF (marque d�pos�e)
		 * repr�sent� par this.
		 * @since jPicEdt 1.6
		 */
		public int getTag(){ return tag; }
		/**
		 * @return la valeur de l'�l�ment d'information DXF (marque d�pos�e) repr�sent� par this.
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
		 * @return le <i>group id code</i> de l'�l�ment d'information DXF (marque d�pos�e)
		 * repr�sent� par this.
		 * @since jPicEdt 1.6
		 */
		public int getTag(){ return tag; }
		/**
		 * @return la valeur de l'�l�ment d'information DXF (marque d�pos�e) repr�sent� par this.
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
		 * @return le <i>group id code</i> de l'�l�ment d'information DXF (marque d�pos�e)
		 * repr�sent� par this.
		 * @since jPicEdt 1.6
		 */
		public int getTag(){ return tag; }
		/**
		 * @return la valeur de l'�l�ment d'information DXF (marque d�pos�e) repr�sent� par this.
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
