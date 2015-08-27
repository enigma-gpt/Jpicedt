// utf8_to_escapedlatin1.cpp --- -*- coding: iso-8859-1 -*-
// Copyright 2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: utf8_to_escapedlatin1.cpp,v 1.1 2013/02/10 05:50:28 vincentb1 Exp $
// Keywords: UTF-8
// X-URL: http://www.jpicedt.org/
//
// Ce logiciel est un trancodeur de UTF-8 vers ISO-8859-1 pour fichier de propriétés Java.
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

// If anything erroneous is met, then the program will output a non zero status. You can get it with 
//
//    echo %ERRORLEVEL% 
//
// under MSDOS, or with
//
//    echo $?
//
// under BASH. The transcoded file may then contain \u0??? or \u1??? sequences in place of met erroneous code
// words.

/// Installation: 
//
// To get the console application, compile with a c++ compiler, e.g. g++ like this:
//
// g++.exe utf8_to_escapedlatin1.cpp -o utf8_to_escapedlatin1.exe
//

/// Code:
#include <iostream>
#include <fstream>
using namespace std;

int main(int ac, char const*av[])
{
  if(ac < 2)
	{
	  cerr << "USAGE: utf8_to_escapedlatin1 UTF8_FILE ESCAPEDLAINT1_FILE\n";
	  return -1;
	}

  ifstream utf8_file;
  utf8_file.open(av[1]);
  if(!utf8_file.is_open())
	{
	  cerr << "ERROR: can't open file "<<av[1]<<'\n';
	  return -2;

	}

  ofstream escapedlatin1_file;
  int error_count = 0;
  escapedlatin1_file.open(av[2]);
  if(!escapedlatin1_file.is_open())
	{
	  cerr << "ERROR: can't open file "<<av[2]<<'\n';
	  utf8_file.close();
	  return -3;

	}

  for(;;)
	{
	  char x0;
	  utf8_file.get(x0);
	  if(!utf8_file.good())
		{
		  if(!utf8_file.eof())
			error_count = -1;
		  break;
		}
	  int next_word_count = -1;
	  char x = x0;
	  while((x & 0x80))
		{
		  next_word_count++;
		  x <<= 1;
		}

	  if(next_word_count == -1)
		escapedlatin1_file<<(char)x0;
	  else if(next_word_count == 0)
		{
		  // forbidden sequence
		  ++error_count;
		  escapedlatin1_file<<"\\u0???";
		  break;
		}
	  else if(next_word_count <= 3)
		{
		  unsigned int w = x0 & ((1<<(6-next_word_count))-1);
		  int nwc = next_word_count;
		  for(;;)
			{
			  utf8_file.get(x0);
			  if(!utf8_file.good())
				{
				  error_count = -2;
				  break;
				}
			  if((x0 & 0xC0) == 0x80)
				{
				  w <<= 6;
				  w |= x0 & 0x3F;
				  if(--nwc == 0)
					{
					  escapedlatin1_file<<"\\u";
					  if(next_word_count == 3)
						escapedlatin1_file.width(6);
					  else
						escapedlatin1_file.width(4);
					  escapedlatin1_file.fill ('0');
					  escapedlatin1_file.setf ( ios::hex, ios::basefield );
					  escapedlatin1_file<<w;
					  break;
					}
				}
			  else
				{
				  // forbidden sequence
				  ++error_count;
				  escapedlatin1_file<<"\\u1???";
				  break;
				}
			}
		}
	}

  escapedlatin1_file.close();
  utf8_file.close();
  return -error_count;
}
