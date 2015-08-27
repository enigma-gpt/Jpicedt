#!/usr/bin/env perl
# javaprop-re-comment.pl --- -*- coding: utf-8 -*-
# Copyright 2012 Vincent Belaïche
#
# Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
# Version: $Id: javaprop-re-comment.pl,v 1.2 2013/01/13 18:06:11 vincentb1 Exp $
# Keywords:
# X-URL: http://www.jpicedt.org/
#
# Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant les principes de
# diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
# conditions de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA sur le site
# "http://www.cecill.info".
#
# En contrepartie de l'accessibilité au code source et des droits de copie, de modification et de
# redistribution accordés par cette licence, il n'est offert aux utilisateurs qu'une garantie limitée.  Pour
# les mêmes raisons, seule une responsabilité restreinte pèse sur l'auteur du programme, le titulaire des
# droits patrimoniaux et les concédants successifs.
#
# A cet égard l'attention de l'utilisateur est attirée sur les risques associés au chargement, à
# l'utilisation, à la modification et/ou au développement et à la reproduction du logiciel par l'utilisateur
# étant donné sa spécificité de logiciel libre, qui peut le rendre complexe à manipuler et qui le réserve donc
# à des développeurs et des professionnels avertis possédant des connaissances informatiques approfondies.
# Les utilisateurs sont donc invités à charger et tester l'adéquation du logiciel à leurs besoins dans des
# conditions permettant d'assurer la sécurité de leurs systèmes et ou de leurs données et, plus généralement,
# à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
#
# Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris connaissance de la licence
# CeCILL, et que vous en avez accepté les termes.
#
## Commentary:

#

## Installation:

## Code:
use strict;
use warnings;
use feature qw(say unicode_strings);
use PerlIO;

my $version='$Id: javaprop-re-comment.pl,v 1.2 2013/01/13 18:06:11 vincentb1 Exp $';
my $error_count = 0;
my $linenb = 0;
my $noheader = 0;
my $inputfile;
my $verbose;
my $quiet;
my $valid_texi_key = '\A[A-Za-z0-9_-]*\Z';
my %flag_duplication = ();

sub usage
{
	my $retval = shift;
	print "This script obfusxate a Java .properties file, and add a header passed to command line.
Default header is:

THIS FILE IS GENERATED AUTOMATICALLY, DO NOT EDIT

    Usage:
	javaprop-re-comment.pl ARGUMENTS

--version           : show version and exit
-?, --help          : show this message and exit
-h, --header ARG    : Add a header ARG to the produced output
-n, --noheader      : Do not add any header
-i, --input  ARG    : Set input to ARG, otherwise it is STDIN
-o, --output ARG    : Set output to ARG, otherwise it is STDOUT
-q, --quiet         : Do not output any messages, supersedes --verbose
-v, --verbose       : Output some more messages
";
	exit $retval;
}


my @header = ();
my $outputfile;

my $i = 0;
while($i < @ARGV){
	if($i + 1 < @ARGV)
	{
		if($ARGV[$i] =~ /\A-(i|-input)\Z/)
		{
			$inputfile = $ARGV[$i+1];
			$i = $i +2;
		}
		elsif($ARGV[$i] =~ /\A-(o|-output)\Z/)
		{
			$outputfile = $ARGV[$i+1];
			$i = $i +2;
		}
		elsif($ARGV[$i] =~ /\A-(h|-header)\Z/)
		{
			$header[++$#header] = $ARGV[$i+1];
			$i = $i +2;
		}
		else
		{
			goto ONE_ARG;
		}
	}
	else
	{
	  ONE_ARG:
		{
			if($ARGV[$i] =~ /\A-(q|-quiet)\Z/)
			{
				$quiet = 1;
				$i ++;
			}
			elsif($ARGV[$i] =~ /\A-(n|-noheader)\Z/)
			{
				$noheader = 1;
				$i ++;
			}
			elsif($ARGV[$i] =~ /\A-(v|-verbose)\Z/)
			{
				$verbose = 1;
				$i ++;
			}
			elsif($ARGV[$i] eq "--version")
			{
				print "Version of javaprop-re-comment.pl = $version\n";
				exit 0;
			}
			elsif($ARGV[$i] =~ /\A-\?|--help\Z/)
			{
				usage(0);
				exit;
			}
			else
			{
				print "Invalid remaining arguments: @ARGV[$i .. $#ARGV]\n";
				usage(-1);
			}
		}
	}
}

my $in;
if($inputfile)
{ 
	open($in,  "< :encoding(ISO-8859-1)", $inputfile)  or die "Can't open $inputfile $!";
}
else
{
	$in = \*STDIN;
}
my $out;
if($outputfile)
{
	open($out, "> :encoding(ISO-8859-1)", $outputfile) or die "Can't open $outputfile $!";
}
else
{
	$out = \*STDOUT;
}
select $out;

my $line;
if(!$noheader)
{
	if(@header){
		foreach(@header){
			say '# ', $_;
		}
	}
	else
    {
		say '# THIS FILE IS GENERATED AUTOMATICALLY, DO NOT EDIT';
    }
}

my $folded_line = 0;
my $flagnb      = 0;

my $prop_key;
my $prop_linenb;
my $propname;
my $propval;
my $nextpropval;

LINE: while(<$in>){
	$line = $_ ;
	$linenb++ ;
	if($line =~ /\A(\s*)[#!](.*)\Z/)
	{
		next LINE;
	}
	elsif($line =~ /\A\s*\Z/)
	{
		next LINE;
	}
	elsif($line =~ /\A(\s*(.*))\Z/)
	{
		if($folded_line == 0)
		{
			if($line =~ /\A\s*((?:[a-zA-Z0-9_\.-]|\\[nr=:])+)\s*[=:](.*)\Z/)
			{
				$propval = $2;
				$prop_key = $1;
				$prop_linenb = $linenb;
				$propname = $1;
				if($propval =~ m!(\\+)$! && (length($1) & 1) == 1)
				{
					# nombre impair de contre-obliques en fin de ligne, c'est un repliement
					$folded_line = 1;
					$propval =~ s!.$!!;
				}
				else
				{
					say $propname, "=", $propval;
				}
			}
			else
			{
				die "$inputfile:$linenb: Invalid line = $line";
			}
		}
		elsif($folded_line == 1)
		{
			$nextpropval = $2;
			if($nextpropval =~ m!(\\+)$! && (length($1) & 1) == 1)
			{
				# nombre impair de contre-obliques en fin de ligne, on reste en repliement
				$nextpropval =~ s!.$!!;
				$propval = $propval . $nextpropval;
			}
			else
			{
				# le repliement est fini
				$folded_line = 0;
				$propval = $propval . $nextpropval;
				say $propname, "=", $propval;
			}

		}
	    else
		{
			die "$inputfile:$linenb: javaprop-re-comment INTERNAL BUG";
		}

		next LINE;
	}
	else
	{
		die "$inputfile:$linenb: Invalid line = $line";
	}
}


if(!$quiet)
{
	if($inputfile)
	{
		$inputfile = "file \`$inputfile\'";
	}
	else
	{
		$inputfile = "standard input";
	}
	say STDOUT "\nDone: javaprop-re-comment is finished,";
	if($verbose)
	{
		use Date::Calc qw(Today_and_Now);
		my @today_now = Today_and_Now();
		printf STDOUT "\trun on %02d-%02d-%02dT%02d:%02d:%02d\n" , @today_now;
	}
	print STDOUT "\tinput was $inputfile,\n\toutput was $outputfile,\n\t$linenb lines were processed,\n\t$error_count error(s)\n";
}

exit -$error_count;
