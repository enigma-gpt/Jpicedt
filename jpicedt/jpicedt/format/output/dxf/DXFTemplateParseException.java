// DXFTemplateParseException.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFTemplateParseException.java,v 1.5 2013/03/27 07:11:30 vincentb1 Exp $
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



/// Code:
package jpicedt.format.output.dxf;

import jpicedt.format.output.util.FormatterException;
import javax.swing.JOptionPane;
import static jpicedt.Localizer.localize;

/**
 * Exceptions se produisant lors de l'analyse d'un patron de DXF (marque d�pos�e).
 * @author Vincent Bela&iuml;che
 *	@since jPicEdt 1.6
 */
public class DXFTemplateParseException extends FormatterException
{
	Object[] infoMessage;

	DXFTemplateParseException(int lineNb, String infoMessageKey){
		String [] a = {localize(infoMessageKey),"at line: "+ Integer.toString(lineNb)};
		infoMessage = a;
	}

	public void resolve(){
		JOptionPane.showMessageDialog(
			null,
			infoMessage,
			localize("format.output.template.parse.exception"),
			JOptionPane.ERROR_MESSAGE);
	}

	//-----------------------------------------------------------------------
	//------------------------ classes statiques ----------------------------
	//-----------------------------------------------------------------------
	static public class MissingGroupValue extends DXFTemplateParseException
	{
		public MissingGroupValue(int lineNb){
			super(lineNb,"format.dxf.Tpe.expected.group.value");
		}
	}

	static public class InvalidLine extends DXFTemplateParseException
	{
		public InvalidLine(int lineNb){
			super(lineNb,"format.dxf.Tpe.invalid.line");
		}
	}

	static public class UnknownCommand extends DXFTemplateParseException
	{
		public UnknownCommand(int lineNb){
			super(lineNb,"format.dxf.Tpe.unknown.command");
		}
	}


}

/// DXFTemplateParseException.java ends here
