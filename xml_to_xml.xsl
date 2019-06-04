<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:template match="Factos">
	<InfoPaises>
		<xsl:apply-templates select = "Pais">
			<xsl:sort select = "Area"/>
		</xsl:apply-templates>
	</InfoPaises>	
</xsl:template>	

<xsl:template match = "Pais">
<Pais cod = "{@ISO}">
<Capital><xsl:value-of select = "Capital"/></Capital>
<Hino><xsl:value-of select = "Nome_Hino"/></Hino>
<Moeda><xsl:value-of select = "Moeda"/></Moeda>
<TotalIdiomas><xsl:value-of select = "count(Idiomas/Idioma)"/></TotalIdiomas>
</Pais>
</xsl:template>
</xsl:stylesheet>
