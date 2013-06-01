<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:saxon="http://saxon.sf.net/" xmlns="http://www.w3.org/1999/xhtml"
	extension-element-prefixes="saxon"><xsl:output method="text" indent="no" /><xsl:template match="//units"># This file lists versions of features in the site.
#
<xsl:for-each select="unit"><xsl:sort select="@id" /><xsl:if test="contains(@id,'.feature.jar')">
<xsl:value-of select="substring-before(@id,'.feature.jar')" />.version = <xsl:value-of select="@version"/>
#
</xsl:if></xsl:for-each>
</xsl:template></xsl:stylesheet>
