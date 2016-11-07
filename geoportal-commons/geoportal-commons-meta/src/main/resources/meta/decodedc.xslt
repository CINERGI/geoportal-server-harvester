<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" xmlns:dcmiBox="http://dublincore.org/documents/2000/07/11/dcmi-box/" xmlns:ows="http://www.opengis.net/ows">
	<xsl:output method="text" version="1.0" encoding="UTF-8" indent="no"/>
	<xsl:template match="/">
identifier=<xsl:value-of select="/rdf:RDF/rdf:Description/dc:identifier"/>
title=<xsl:value-of select="/rdf:RDF/rdf:Description/dc:title"/>
description=<xsl:value-of select="/rdf:RDF/rdf:Description/dc:description"/>
modified=<xsl:value-of select="/rdf:RDF/rdf:Description/dc:date"/>
resource.url=<xsl:value-of select="/rdf:RDF/rdf:Description/dct:references"/>
resource.url.scheme=<xsl:value-of select="/rdf:RDF/rdf:Description/dct:references/@scheme"/>
bbox=<xsl:value-of select="/rdf:RDF/rdf:Description/ows:WGS84BoundingBox/ows:LowerCorner"/>,<xsl:value-of select="/rdf:RDF/rdf:Description/ows:WGS84BoundingBox/ows:UpperCorner"/>
	</xsl:template>
</xsl:stylesheet>
