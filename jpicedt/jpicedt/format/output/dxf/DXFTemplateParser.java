// DXFTemplateParser.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: DXFTemplateParser.java,v 1.5 2013/03/27 07:11:25 vincentb1 Exp $
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DXFTemplateParser
{

	ArrayList<DXFInformation> infoStack = new ArrayList<DXFInformation>(900);


	public DXFTemplateParser(String templateFileName){
		String line;
		Pattern groupCodePattern = Pattern.compile(
			"^\\p{Blank}*([0-9]+)\\p{Blank}*$");
		Pattern extMinPattern = Pattern.compile(
			"^\\\\extMin\\p{Blank}*$");
		Pattern extMaxPattern = Pattern.compile(
			"^\\\\extMax\\p{Blank}*$");
		Pattern entitiesPattern = Pattern.compile(
			"^\\\\entities\\p{Blank}*$");
		Matcher m;
		try
		{
			BufferedReader input = new BufferedReader(new FileReader(templateFileName));
			int lineNb = 0;
			for(;;)
			{
				++lineNb;
				if((line=input.readLine())!=null)
				{
					if(line.startsWith("%"))
						continue; // comment
					else if(line.startsWith("\\"))
					{
						// command
						if(extMinPattern.matcher(line).matches())
						{
							infoStack.add(new DXFTaggedValue.DXFExtMinFormatter());
						}
						else if(extMaxPattern.matcher(line).matches())
						{
							infoStack.add(new DXFTaggedValue.DXFExtMaxFormatter());
						}
						else if( entitiesPattern.matcher(line).matches())
						{
							infoStack.add(new DXFTaggedValue.DXFEntitiesFormatter());
						}
						else
							throw new DXFTemplateParseException.UnknownCommand(lineNb);
					}
					else if((m = groupCodePattern.matcher(line)).matches())
					{
						int groupCode = Integer.valueOf(m.group(1));
						line = input.readLine();
						if(line == null)
							throw new DXFTemplateParseException
								.MissingGroupValue(lineNb);
						infoStack.add(new DXFTaggedValue.DXFTaggedString(groupCode,line));
					}
					else
						throw new DXFTemplateParseException.InvalidLine(lineNb);

				}
				else
					break;
			}

		}
		catch(FileNotFoundException fnfEx)
		{
			//[todo] à localiser
			System.out.println("Fichier patron DXF non trouvé: info par défaut...");
			infoStack = null;
		}
		catch(IOException ioEx)
		{
			//[todo] à localiser
			System.out
				.println("Erreur lecteur fichier patron DXF: info par défaut...");
			infoStack = null;
		}
		catch(DXFTemplateParseException tpeEx)
		{
			tpeEx.resolve();
			infoStack = null;
		}
	}

	public DXFInformation[] getTemplate(){
		if(infoStack == null)
			return DXFConstants.DXF_FALLBACK_TEMPLATE;
		else
		{
			DXFInformation[] array = new DXFInformation[infoStack.size()];
			infoStack.toArray(array);
			infoStack = null;
			return array;
		}
	}
};


/// DXFTemplateParser.java ends here
