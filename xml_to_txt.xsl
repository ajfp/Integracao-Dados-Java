<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text"/>
<xsl:template match = "Factos">
<xsl:text>Capitais dos paises com moeda euro:</xsl:text>
<xsl:text>&#13;</xsl:text>
<xsl:apply-templates/>
</xsl:template>

<xsl:template match = "Pais">
<xsl:if test="Moeda='Euro'">
<xsl:value-of select ="Capital"/>
</xsl:if>
</xsl:template>
</xsl:stylesheet>