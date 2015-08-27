// DXFCommentFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: DXFCommentFormatter.java,v 1.7 2013/03/27 07:24:22 vincentb1 Exp $
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

import jpicedt.graphic.io.formatter.CommentFormatting;
import jpicedt.graphic.io.formatter.FormatterFactory;

import jpicedt.format.output.util.BaseCommentFormatter;
import static jpicedt.format.output.dxf.DXFConstants.*;

/**
 * Permet de formatter les commentaires adaptés pour l'enfouissement du code
 * JPIC-XML à l'intérieur du fichier DXF (marque déposée).
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class DXFCommentFormatter
	extends BaseCommentFormatter
	implements CommentFormatting
{
	String strongPrefix;
	String weakPrefix;

	public DXFCommentFormatter(FormatterFactory factory){
		super(factory);
		strongPrefix = "999" + factory.getLineSeparator() + "%";
		weakPrefix = "999" + factory.getLineSeparator();
	}


	/**
	 * @since jPicEdt 1.6
	 */
	public 	String  getContentTypeCommentFormatting(){
		return "mixed JPIC-XML/DXF";
	}


	/**
	 * @since jPicEdt 1.6
	 */
	public void strongCommentFormat(String s)
		{
			commentFormat(strongPrefix,s);
		}
	/**
	 * @since jPicEdt 1.6
	 */
	public void weakCommentFormat  (String s)
		{
			commentFormat(weakPrefix,s);
		}

	public String getConcreteContentType() 
	{
		return new DXFContentType().getPresentationName();
	}
}


/// DXFCommentFormatter.java ends here
